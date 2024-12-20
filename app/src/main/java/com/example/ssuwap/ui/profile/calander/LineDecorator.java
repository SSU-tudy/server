package com.example.ssuwap.ui.profile.calander;

import android.util.Log;

import com.example.ssuwap.data.calendar.SubjectData;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;


import java.util.List;

public class LineDecorator implements DayViewDecorator {



    private CalendarDay day;
    private List<SubjectData> subject;
    public LineDecorator(CalendarDay day, List<SubjectData> dataMap) {
        this.subject = dataMap;
        this.day = day;
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Log.d("cal", String.valueOf(day));
        return this.day.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CustomLineSpan(subject));
    }
}
