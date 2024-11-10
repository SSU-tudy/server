package com.example.ssuwap.ui.book.upload.isbn;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NaverBookInfoFetcher {
    private static final String CLIENT_ID = "9_qEqvvsxwJiw0NxBMKq"; // 네이버 클라이언트 ID
    private static final String CLIENT_SECRET = "6T1RqrqI12"; // 네이버 클라이언트 시크릿
    private static final String TAG = "NaverBookInfoFetcher";
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final OnBookInfoFetchedListener listener;

    public NaverBookInfoFetcher(OnBookInfoFetchedListener listener) {
        this.listener = listener;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void fetchBookInfo(String isbn) {
        executorService.execute(() -> {
            String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + isbn;
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // API 인증 헤더 추가
                connection.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
                connection.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);

                // 응답 읽기
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // JSON 파싱 및 결과 전달
                mainHandler.post(() -> parseAndReturnBookInfo(response.toString()));

            } catch (Exception e) {
                Log.e(TAG, "Error fetching book info", e);
                mainHandler.post(() -> listener.onError(e));
            }
        });
    }

    private void parseAndReturnBookInfo(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray items = jsonObject.optJSONArray("items");

            if (items != null && items.length() > 0) {
                JSONObject bookInfo = items.getJSONObject(0);
                String title = bookInfo.optString("title", "Title not available");
                String authors = bookInfo.optString("author", "Authors not available");

                String publisher = bookInfo.optString("publisher", "Publisher not available");
                String publishedDate = bookInfo.optString("pubdate", "Date not available");
                String imageUrl = bookInfo.optString("image", "");

                String authorsWithComma = authors
                        .replaceAll("[\\^,;/\\s]+", ", ")
                        .replaceAll(",\\s*,", ", ")
                        .replaceAll("^,\\s*", "")
                        .replaceAll(",\\s*$", "");

                Log.d("BookInfo", title);
                Log.d("BookInfo", authors);
                Log.d("BookInfo", authorsWithComma);
                Log.d("BookInfo", publisher);

                listener.onBookInfoFetched(title, authorsWithComma, publisher, publishedDate, imageUrl);
            } else {
                listener.onError(new Exception("해당 ISBN에 대한 도서 정보를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing book info", e);
            listener.onError(e);
        }
    }

    public interface OnBookInfoFetchedListener {
        void onBookInfoFetched(String title, String authors, String publisher, String publishedDate, String imageUrl);
        void onError(Exception e);
    }
}
