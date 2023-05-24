package com.safra.utilities;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.safra.R;

//public class EventDecorator implements DayViewDecorator {
//    private final int color;
//    private final CalendarDay date;
//
//    public EventDecorator(Context context, CalendarDay date) {
//        this.date = date;
//        this.color = ContextCompat.getColor(context, R.color.black); // Change to your desired color
//    }
//
//    @Override
//    public boolean shouldDecorate(CalendarDay day) {
//        return day.equals(date);
//    }
//
//    @Override
//    public void decorate(DayViewFacade view) {
//        view.setBackgroundDrawable(new ColorDrawable(color));
//        view.addSpan(new DotSpan(5, color)); // Optional: Display a colored dot on the date
//    }
//}
