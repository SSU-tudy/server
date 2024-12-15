package com.example.ssuwap.ui.profile.calander;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.example.ssuwap.data.calendar.SubjectData;

import java.util.List;

public class CustomLineSpan implements LineBackgroundSpan {
    private List<SubjectData> subjects;

    public CustomLineSpan(List<SubjectData> subject) {
        this.subjects = subject;
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lineNum) {
        int lineHeight = 10; // 줄 간격
        int currentBottom = bottom + 10; // 첫 줄의 시작 위치
        int maxWidth = right - left; // 캘린더 셀의 최대 너비

        for (SubjectData subject : subjects) {
            float hours = subject.getTotalDuration() / 3600000f; // 최대 5시간 기준 밀리세컨이라 3600으로 나누기
            float ratio = Math.min(hours / 3f, 1.0f); // 최대 3시간 기준 비율 계산 테스트용으로 맥스 1분 ㄱㄱ
            int lineLength = (int) (maxWidth * ratio); // 선의 길이 계산

            // 선의 색상 설정
            paint.setColor(subject.getColor());
            paint.setStrokeWidth(5); // 선 두께 설정

            // 선 그리기
            canvas.drawLine(left, currentBottom, left + lineLength, currentBottom, paint);

            // 다음 선 위치로 이동
            currentBottom += lineHeight;
        }
    }
}
