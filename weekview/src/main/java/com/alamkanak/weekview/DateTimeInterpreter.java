package com.alamkanak.weekview;

import java.util.Calendar;

/**
 * Created by Raquib on 1/6/2015.
 */
public interface DateTimeInterpreter {
    //zyfadd
    String interpretDay(Calendar date);

    String interpretDate(Calendar date);
    String interpretTime(int hour);
}
