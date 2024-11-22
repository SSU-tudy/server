package com.example.ssuwap.ui.login;

import android.util.Log;
import okhttp3.*;
import java.io.IOException;

public class UsaintAuthService {
    public interface AuthCallback {
        void onAuthSuccess(String token); // 로그인 성공 시 토큰 전달
        void onAuthFailure(String message); // 로그인 실패 시 메시지 전달
    }

    private final OkHttpClient client = new OkHttpClient();
    private static final String SMARTID_LOGIN_URL = "https://smartid.ssu.ac.kr/Symtra_sso/smln.asp";
    private static final String SMARTID_LOGIN_FORM_REQUEST_URL = "https://smartid.ssu.ac.kr/Symtra_sso/smln_pcs.asp";
    private static final String SSU_USAINT_SSO_URL = "https://saint.ssu.ac.kr/webSSO/sso.jsp";


    public void authenticate(String studentId, String password, AuthCallback callback) {
        Log.d("UsaintAuthService", "Starting authenticate with studentId: " + studentId);
        // Step 1: Obtain SSO Token by logging in with studentId and password
        Request loginRequest = new Request.Builder()
                .url(SMARTID_LOGIN_URL)
                .build();

        client.newCall(loginRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("UsaintAuthService", "Login request successful");
                    // Get login form parameters in response
                    String loginFormHtml = response.body().string();
                    String inTpBit = parseParameter(loginFormHtml, "in_tp_bit");
                    String rqstCausCd = parseParameter(loginFormHtml, "rqst_caus_cd");

                    if (inTpBit != null && rqstCausCd != null) {
                        sendLoginForm(studentId, password, inTpBit, rqstCausCd, callback);
                    } else {
                        callback.onAuthFailure("로그인 폼 파라미터를 찾을 수 없습니다.");
                    }
                } else {
                    Log.d("UsaintAuthService", "Login request failed with message: " + response.message());
                    callback.onAuthFailure("SMARTID 로그인 페이지 접근 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("UsaintAuthService", "Login request failed with error: " + e.getMessage());
                callback.onAuthFailure("네트워크 요청 실패: " + e.getMessage());
            }
        });
    }

    private void sendLoginForm(String studentId, String password, String inTpBit, String rqstCausCd, AuthCallback callback) {
        FormBody formBody = new FormBody.Builder()
                .add("in_tp_bit", inTpBit)
                .add("rqst_caus_cd", rqstCausCd)
                .add("userid", studentId)
                .add("pwd", password)
                .build();

        Request formRequest = new Request.Builder()
                .url(SMARTID_LOGIN_FORM_REQUEST_URL)
                .post(formBody)
                .build();

        client.newCall(formRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String token = getTokenFromCookies(response);
                    if (token != null) {
                        authenticateWithToken(studentId, token, callback);
                    } else {
                        callback.onAuthFailure("SSO 토큰을 찾을 수 없습니다.");
                    }
                } else {
                    callback.onAuthFailure("로그인 폼 전송 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onAuthFailure("네트워크 요청 실패: " + e.getMessage());
            }
        });
    }

    private void authenticateWithToken(String studentId, String token, AuthCallback callback) {
        HttpUrl ssoUrl = HttpUrl.parse(SSU_USAINT_SSO_URL).newBuilder()
                .addQueryParameter("sToken", token)
                .addQueryParameter("sIdno", studentId)
                .build();

        Request ssoRequest = new Request.Builder()
                .url(ssoUrl)
                .get()
                .build();

        client.newCall(ssoRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    callback.onAuthSuccess(token); // 인증 성공 시 토큰 반환
                } else {
                    callback.onAuthFailure("SSO 인증 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onAuthFailure("네트워크 요청 실패: " + e.getMessage());
            }
        });
    }


    private String parseParameter(String html, String parameterName) {
        String regex = "name=\"" + parameterName + "\" value=\"([^\"]*)\"";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(html);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String getTokenFromCookies(Response response) {
        for (Cookie cookie : Cookie.parseAll(response.request().url(), response.headers())) {
            if (cookie.name().equals("sToken") && !cookie.value().isEmpty()) {
                return cookie.value();
            }
        }
        return null;
    }
}