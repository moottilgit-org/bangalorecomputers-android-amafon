package com.atoz.johnnysapp.store.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static Context mContext;

    public static void init(Context context) {
        mContext= context;
    }

    public interface AfterPick {
        void picked(final String oldText, final String newText);
    }

    public static void pickDate(final Context context, final String inDate, final AfterPick afterPick) {
        try {
            DatePickerDialog mDatePicker= getDatePicker(context, inDate, afterPick);
            mDatePicker.show();
        } catch(Exception e) {
            Log.e("pickDate", e.toString());
        }
    }
    public static DatePickerDialog getDatePicker(final Context context, final String inDate, final AfterPick afterPick) {
        try {
            final String oldText= TextUtils.isEmpty(inDate)?DateUtils.getLocalDate():inDate;
            String sDate= localToDate(oldText);
            sDate= sDate.contains("-") ? sDate : DateUtils.getDateStr();
            String[] tmp1= sDate.split("-");
            int mYear= Integer.parseInt(tmp1[0]);
            int mMonth= Integer.parseInt(tmp1[1]) - 1;
            int mDay= Integer.parseInt(tmp1[2]);

            DatePickerDialog mDatePicker;
            mDatePicker= new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                month= month + 1;
                String newText= year + "-" + String.format(Locale.getDefault(),"%02d", month) + "-" + String.format(Locale.getDefault(),"%02d", dayOfMonth);
                newText= getLocalDate(newText);
                if(afterPick != null) {
                    afterPick.picked(oldText, newText);
                }
            }, mYear, mMonth, mDay);
            if(PermissionManager.is21()) {
                mDatePicker.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
            }
            mDatePicker.setTitle("Select Date");
            return mDatePicker;
        } catch(Exception e) {
            Log.e("getDatePicker", e.toString());
            return null;
        }
    }

    public static void pickTime(final Context context, final String inTime, final AfterPick afterPick) {
        pickTime(context, "Select time", inTime, afterPick);
    }

    public static void pickTime(final Context context, final String title, final String inTime, final AfterPick afterPick) {
        try {
            TimePickerDialog mTimePicker= getTimePicker(context, title, inTime, afterPick);
            mTimePicker.show();
        } catch(Exception e) {
            Log.e("pickTime", e.toString());
        }
    }

    public static TimePickerDialog getTimePicker(final Context context, final String title, final String inTime, final AfterPick afterPick) {
        try {
            final String oldText= TextUtils.isEmpty(inTime)?DateUtils.getLocalTime():inTime;
            String sTime= localToTime(oldText);
            sTime= sTime.contains(":") ? sTime : DateUtils.getTimeStr();
            String[] tmp2= sTime.split(":");
            int hour= Integer.parseInt(tmp2[0]);
            int minute= Integer.parseInt(tmp2[1]);
            TimePickerDialog mTimePicker;
            mTimePicker= new TimePickerDialog(context, (view, hourOfDay, minute1) -> {
                String newText= String.format(Locale.getDefault(),"%02d", hourOfDay) + ":" + String.format(Locale.getDefault(),"%02d", minute1) + ":00";
                newText= getLocalTime(newText);
                if(afterPick!=null) {
                    afterPick.picked(oldText, newText);
                }
            }, hour, minute, false);
            mTimePicker.setTitle(title);
            mTimePicker.setMessage(title);
            return mTimePicker;
        } catch(Exception e) {
            Log.e("getTimePicker", e.toString());
            return null;
        }
    }

    public static Date getDateTime() {
        Calendar cal= Calendar.getInstance();
        return cal.getTime();
    }
    public static Date getDateTime(Date dt) {
        Calendar cal= Calendar.getInstance();
        cal.setTime(dt);
        return cal.getTime();
    }
    public static Date getDateTime(int plusMinutes) {
        return getDateTime(plusMinutes, "M");
    }
    public static Date getDateTime(int plusDelay, String mode) {
        Calendar cal= Calendar.getInstance();
        switch (mode) {
            case "S":
                cal.add(Calendar.SECOND, plusDelay);
            break;
            case "M":
                cal.add(Calendar.MINUTE, plusDelay);
            break;
            case "H":
                cal.add(Calendar.HOUR_OF_DAY, plusDelay);
            break;
            case "D":
                cal.add(Calendar.DAY_OF_MONTH, plusDelay);
            break;
        }
        return cal.getTime();
    }
    public static Date getDateTime(String str) {
        try {
            return getDateTime(str, true, false);
        } catch(Exception e) {
            Log.e("getDateTime", e.toString());
            return null;
        }
    }
    public static Date getDateTime(String str, boolean seconds, boolean tried) {
        try {
            SimpleDateFormat dfTime= new SimpleDateFormat("yyyy-MM-dd HH:mm" + (seconds?":ss":""));
            return dfTime.parse(str);
        } catch(Exception e) {
            Log.e("getDateTime", e.toString());
            return tried?null:getDateTime(str, !seconds, true);
        }
    }

    public static Date getDate(String date) {
        try {SimpleDateFormat dfTime= new SimpleDateFormat("yyyy-MM-dd");
            return dfTime.parse(date);
        } catch(Exception e) {
            Log.e("getDate", e.toString());
            return null;
        }
    }

    public static Date getDate() {
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getTime(String str) {
        try {
            return getTime(str, true, false);
        } catch(Exception e) {
            Log.e("strToTime", e.toString());
            return null;
        }
    }
    public static Date getTime(String str, boolean seconds, boolean tried) {
        try {
            SimpleDateFormat dfTime= new SimpleDateFormat("HH:mm" + (seconds?":ss":""));
            return dfTime.parse(str);
        } catch(Exception e) {
            Log.e("strToTime", e.toString());
            return tried?null:getTime(str, !seconds, true);
        }
    }


    public static String toDateTimeStr(long time) {
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return getDateTimeStr(calendar.getTime());
    }
    public static String getLocalDate() {
        final Calendar t= Calendar.getInstance();
        return getLocalDate(t.getTime());
    }
    public static String getLocalDate(String date) {
        try {
            SimpleDateFormat dfDate= new SimpleDateFormat("yyyy-MM-dd");
            Date date1= dfDate.parse(date);
            Calendar cal= Calendar.getInstance();
            cal.setTime(date1);
            return getLocalDate(cal.getTime());
        } catch(Exception e) {
            Log.e("getLocalDate", e.toString());
            return date;
        }
    }
    public static String getLocalDate(Date date) {
        return DateFormat.getDateFormat(mContext).format(date);
    }
    public static String getDayName(Date date) {
        try {
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(date);
            return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        } catch(Exception e) {
            Log.e("getDayName", e.toString());
            return "";
        }
    }
    public static String localToDate(String date) {
        try {
            Date date1= DateFormat.getDateFormat(mContext).parse(date);
            SimpleDateFormat dfDate= new SimpleDateFormat("yyyy-MM-dd");
            return dfDate.format(date1);
        } catch(Exception e) {
            Log.e("localToDate", e.toString());
            return date;
        }
    }

    public static String localToDateTime(String string) {
        try {
            String dateStr= string;
            String timeStr= string;
            if(dateStr.contains(" ")) {
                String[] temp= dateStr.split(" ");
                dateStr= temp[0];
                timeStr= (temp[1] + " " + (temp.length>2?temp[2]:"")).trim();
            }
            Date date= DateFormat.getDateFormat(mContext).parse(dateStr);
            SimpleDateFormat dfDate= new SimpleDateFormat("yyyy-MM-dd");
            Date time= DateFormat.getTimeFormat(mContext).parse(timeStr);
            SimpleDateFormat dfTime= new SimpleDateFormat("HH:mm:ss");
            return dfDate.format(date) + " " + dfTime.format(time);
        } catch(Exception e) {
            Log.e("localToDateTime", e.toString());
            return string;
        }
    }

    public static String getLocalTime() {
        final Calendar t= Calendar.getInstance();
        return getLocalTime(t.getTime());
    }
    public static String getLocalTime(String time) {
        return getLocalTime(time, true, false);
    }
    public static String getLocalTime(String time, boolean seconds, boolean tried) {
        try {
            SimpleDateFormat dfTime= new SimpleDateFormat("HH:mm" + (seconds?":ss":""));
            Date date= dfTime.parse(time);
            Calendar cal= Calendar.getInstance();
            cal.setTime(date);
            return getLocalTime(cal.getTime());
        } catch(Exception e) {
            Log.e("getLocalTime", e.toString());
            return tried?time:getLocalTime(time, !seconds, true);
        }
    }
    public static String getLocalTime(Date date, boolean seconds) {
        String res= DateFormat.getTimeFormat(mContext).format(date);
        if(seconds) {
            res= res.replace(" ", ":" + String.format("%02d", date.getSeconds()) + " ");
        }
        return res;
    }
    public static String getLocalTime(Date date) {
        return DateFormat.getTimeFormat(mContext).format(date);
    }
    public static String localToTime(String time) {
        try {
            Date date= DateFormat.getTimeFormat(mContext).parse(time);
            SimpleDateFormat dfTime= new SimpleDateFormat("HH:mm:ss");
            return dfTime.format(date);
        } catch(Exception e) {
            Log.e("localToTime", e.toString());
            return time;
        }
    }

    public static String clearSec(String time) {
        return getDateTimeStr(clearSec(getDateTime(time)));
    }

    public static Date clearSec(Date time) {
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static String getLocalDateTime() {
        final Calendar t= Calendar.getInstance();
        return getLocalDateTime(t.getTime());
    }
    public static String getLocalDateTime(String date) {
        return getLocalDateTime(date, false);
    }
    public static String getLocalDateTime(String date, boolean seconds) {
        return getLocalDateTime(getDateTime(date), seconds);
    }
    public static String getLocalDateTime(Date date) {
        return getLocalDateTime(date, false);
    }
    public static String getLocalDateTime(Date date, boolean seconds) {
        return getLocalDate(date) + " " + getLocalTime(date, seconds);
    }

    public static String getLocalDateTime(boolean seconds) {
        final Calendar t= Calendar.getInstance();
        return getLocalDate(t.getTime()) + " " + getLocalTime(t.getTime(), seconds);
    }

    public static String getDateStr() {
        Calendar cal= Calendar.getInstance();
        return getDateStr(cal.getTime());
    }
    public static String getDateStr(Date date) {
        Calendar cal= Calendar.getInstance();
        cal.setTime(date);
        return String.valueOf(cal.get(Calendar.YEAR)) + "-" +
                String.format("%02d", cal.get(Calendar.MONTH) + 1) + "-" +
                String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
    }

    public static String getTimeStr() {
        Calendar cal= Calendar.getInstance();
        return getTimeStr(cal.getTime());
    }
    public static String getTimeStr(Date date) {
        Calendar cal= Calendar.getInstance();
        cal.setTime(date);
        return String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.format("%02d", cal.get(Calendar.MINUTE)) + ":" +
                String.format("%02d", cal.get(Calendar.SECOND));
    }

    public static String getDateTimeStr(Date date) {
        return getDateStr(date) + " " + getTimeStr(date);
    }
    public static String getDateTimeStr() {
        return getDateStr() + " " + getTimeStr();
    }
    public static String getDateTimeStr(int plusMinutes) {
        return getDateTimeStr(plusMinutes, "M");
    }
    public static String getDateTimeStr(int plusDelay, String mode) {
        Calendar cal= Calendar.getInstance();
        switch (mode) {
            case "M":
                cal.add(Calendar.MINUTE, plusDelay);
            break;
            case "H":
                cal.add(Calendar.HOUR_OF_DAY, plusDelay);
            break;
            case "D":
                cal.add(Calendar.DAY_OF_MONTH, plusDelay);
            break;
        }
        return getDateTimeStr(cal.getTime());
    }

    public static String getDateMonth(String date) {
        return getDateMonth(DateUtils.getDate(date));
    }
    public static String getDateMonth(Date date) {
        try {
            Calendar cal= Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        } catch (Exception e) {
            Log.e("getDateMonth", e.toString());
            return DateUtils.getLocalDate(date);
        }
    }
    public static String getDayMonth() {
        try {
            return getDayMonth(getDate());
        } catch (Exception e) {
            return DateUtils.getLocalDate();
        }
    }
    public static String getDayMonth(Date date) {
        try {
            Calendar cal= Calendar.getInstance();
            cal.setTime(date);
            return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) + ", " +
                    cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + " " +
                    cal.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            Log.e("getDateMonth", e.toString());
            return DateUtils.getLocalDate(date);
        }
    }
    public static String getDayMonthBig(Date date) {
        try {
            Calendar cal= Calendar.getInstance();
            cal.setTime(date);
            return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " +
                    cal.get(Calendar.DAY_OF_MONTH) + " " +
                    cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        } catch (Exception e) {
            Log.e("getDateMonth", e.toString());
            return DateUtils.getLocalDate(date);
        }
    }
    public static String getDayMonthYear(Date date) {
        try {
            Calendar cal= Calendar.getInstance();
            cal.setTime(date);
            return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) + ", " +
                    cal.get(Calendar.DAY_OF_MONTH) + " " +
                    cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + " " +
                    cal.get(Calendar.YEAR);
        } catch (Exception e) {
            Log.e("getDateMonth", e.toString());
            return DateUtils.getLocalDate(date);
        }
    }
    public static Date getFirstDay(Date date) {
        try {
            Calendar cal= Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return cal.getTime();
        } catch (Exception e) {
            Log.e("getFirstDay", e.toString());
            return date;
        }
    }
    public static Date getLastDay(Date date) {
        try {
            Calendar cal= Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            return cal.getTime();
        } catch (Exception e) {
            Log.e("getFirstDay", e.toString());
            return date;
        }
    }
    public static long diffInSecondsAbs(Date d1, Date d2) {
        d1= d1==null?DateUtils.getDateTime():d1;
        d2= d2==null?DateUtils.getDateTime():d2;
        return Math.abs(diffInSeconds(d1, d2));
    }
    public static long diffInSeconds(Date d1, Date d2) {
        return TimeUnit.MILLISECONDS.toSeconds(d1.getTime()) -
                TimeUnit.MILLISECONDS.toSeconds(d2.getTime());
    }
    public static long diffInMinutesAbs(Date d1, Date d2) {
        return Math.abs(diffInMinutes(d1, d2));
    }
    public static long diffInMinutes(Date d1, Date d2) {
        d1= d1==null?DateUtils.getDateTime():d1;
        d2= d2==null?DateUtils.getDateTime():d2;
        return TimeUnit.MILLISECONDS.toMinutes(d1.getTime()) -
               TimeUnit.MILLISECONDS.toMinutes(d2.getTime());
    }
    public static long diffInDays(Date d1, Date d2) {
        return TimeUnit.MILLISECONDS.toDays(d1.getTime()) -
                TimeUnit.MILLISECONDS.toDays(d2.getTime());
    }

    public static String timeFormat(Date cTime) {
        long iDifference,iDay,iHr,iMin;
        long time1,time2;
        String resultTime = "";

        Calendar cal= Calendar.getInstance();
        time1=cal.getTimeInMillis()/1000;
        cal.setTime(cTime);
        time2=cal.getTimeInMillis()/1000;
        iDifference=time1-time2;

        iDay= Math.abs((int)(iDifference/86400));
        iHr= Math.abs((int)(iDifference/3600));
        iMin= Math.abs((int)(iDifference/60));

        if(iDay>0)
            resultTime= String.valueOf(iDay)+"Day ";
        else if(iHr>0)
            resultTime= String.valueOf(iHr)+"Hr ";
        else //if(iMin>0)
            resultTime= String.valueOf(iMin)+"Min ";

        return resultTime;
    }

    public static Date getNextDay(int day) {
        try {
            Calendar cal= Calendar.getInstance();
            int curDay= cal.get(Calendar.DAY_OF_WEEK);
            int date= cal.get(Calendar.DAY_OF_MONTH);
            int diff= day==curDay?0:(day>curDay?day-curDay:7-(curDay-day));
            cal.add(Calendar.DAY_OF_MONTH, diff);
            return cal.getTime();
        } catch (Exception e) {}
        return Calendar.getInstance().getTime();
    }
    public static Date firstDate() {
        Calendar cal= Calendar.getInstance();
        return firstDate(cal.getTime());
    }
    public static Date firstDate(Date date) {
        try {
            Calendar cal= Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return cal.getTime();
        } catch (Exception e) {}
        return date;
    }
    public static Date addDateTime(String dateStr, int plusDelay, String mode) {
        return addDateTime(DateUtils.getDateTime(dateStr), plusDelay, mode);
    }
    public static Date addDateTime(Date date, int plusDelay, String mode) {
        Calendar cal= Calendar.getInstance();
        cal.setTime(date);
        switch (mode) {
            case "S":
                cal.add(Calendar.SECOND, plusDelay);
                break;
            case "M":
                cal.add(Calendar.MINUTE, plusDelay);
                break;
            case "H":
                cal.add(Calendar.HOUR_OF_DAY, plusDelay);
                break;
            case "D":
                cal.add(Calendar.DAY_OF_MONTH, plusDelay);
                break;
            case "W":
                cal.add(Calendar.WEEK_OF_MONTH, plusDelay);
                break;
            case "m":
                cal.add(Calendar.MONTH, plusDelay);
                break;
            case "Y":
                cal.add(Calendar.YEAR, plusDelay);
                break;
        }
        return cal.getTime();
    }
    public static Date startOfDay() {
        try {
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(DateUtils.getDate());
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        } catch (Exception e) {
            return DateUtils.getDate();
        }
    }
    public static Date endOfDay() {
        try {
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(DateUtils.getDate());
            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return calendar.getTime();
        } catch (Exception e) {
            return DateUtils.getDate();
        }
    }

    public static boolean sameDay(Date d1, Date d2) {
        return nextDay(d1, d2, 0);
    }
    public static boolean nextDay(Date d1, Date d2) {
        return nextDay(d1, d2, 1);
    }
    public static boolean nextDay(Date d1, Date d2, int diff) {
        boolean result= false;
        try {
            Calendar calendar1= Calendar.getInstance();
            calendar1.setTime(d1);
            Calendar calendar2= Calendar.getInstance();
            calendar2.setTime(d2);
            calendar1.set(Calendar.HOUR_OF_DAY, 0);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            result= DateUtils.diffInDays(calendar1.getTime(), calendar2.getTime()) == (diff * -1);
        } catch (Exception e) {}
        return result;
    }

    public static boolean sameMonth(Date d1, Date d2) {
        boolean result= false;
        try {
            Calendar calendar1= Calendar.getInstance();
            calendar1.setTime(d1);
            Calendar calendar2= Calendar.getInstance();
            calendar2.setTime(d2);
            result= calendar1.get(Calendar.MONTH)==calendar2.get(Calendar.MONTH) &&
                    calendar1.get(Calendar.YEAR)==calendar2.get(Calendar.YEAR);
        } catch (Exception e) {}
        return result;
    }

    public static Date nextMonthFirstDate(int factor) {
        try {
            return nextMonthFirstDate(DateUtils.getDate(), factor);
        } catch (Exception e) {
            return DateUtils.getDate();
        }
    }
    public static Date nextMonthFirstDate(Date d1, int factor) {
        try {
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(d1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, factor);
            return clearSec(calendar.getTime());
        } catch (Exception e) {
            return d1;
        }
    }

    public static Date nextMonthLastDate(int factor) {
        try {
            return nextMonthLastDate(DateUtils.getDate(), factor);
        } catch (Exception e) {
            return DateUtils.getDate();
        }
    }
    public static Date nextMonthLastDate(Date d1, int factor) {
        try {
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(nextMonthFirstDate(d1, factor));
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            return clearSec(calendar.getTime());
        } catch (Exception e) {
            return d1;
        }
    }


    public static String MonthYear(String date) {
        try {
            SimpleDateFormat dfDate= new SimpleDateFormat("yyyy-MM-dd");
            Date date1= dfDate.parse(date);
            Calendar cal= Calendar.getInstance();
            cal.setTime(date1);
            return String.valueOf(cal.get(Calendar.MONTH) + 1) + "/" +
                    String.valueOf(cal.get(Calendar.YEAR));
        } catch(Exception e) {
            Log.e("getLocalDate", e.toString());
            return date;
        }
    }

    public static String dateRangeStr(String date1, String date2, String split, boolean local) {
        try {
            Date dt1= DateUtils.getDateTime(date1);
            Date dt2= DateUtils.getDateTime(date2);
            if(date1.equals(date2)) {
                return local?DateUtils.getLocalDateTime(date1):date1;
            } else if(DateUtils.sameDay(dt1, dt2)) {
                return local?DateUtils.getLocalDateTime(dt1)+" " + split + " " + DateUtils.getLocalTime(dt2):
                        date1 + " " + split + " " + DateUtils.getTimeStr(dt2);
            } else {
                return local?DateUtils.getLocalDateTime(dt1)+" " + split + " " + DateUtils.getLocalDateTime(dt2):
                        date1 + " " + split + " " + date2;
            }
        } catch(Exception e) {
            Log.e("dateRangeStr", e.toString());
            return local?DateUtils.getLocalDateTime(date1)+" " + split + " " + DateUtils.getLocalDateTime(date2):
                    date1 + " " + split + " " + date2;
        }
    }

    public static String dateToStr(Date date) {
        try {
            Date today= DateUtils.getDateTime();
            if(DateUtils.sameDay(today, date)) {
                return DateFormat.getTimeFormat(mContext).format(date);
            } else if (DateUtils.nextDay(today, date)){
                return "tomorrow " + DateFormat.getTimeFormat(mContext).format(date);
            } else {
                return DateUtils.getDayMonthBig(date) + " " + DateFormat.getTimeFormat(mContext).format(date);
            }
        } catch (Exception e) {
            return DateUtils.getLocalDateTime(date);
        }
    }

    public static String dateToStrEx(Date date) {
        try {
            Date today= DateUtils.getDateTime();
            if(DateUtils.sameDay(today, date)) {
                return getLocalTime(date);
            } else {
                return getDateMonth(date) + " " + getLocalTime(date);
            }
        } catch (Exception e) {
            return DateUtils.getLocalDateTime(date);
        }
    }

    public static boolean inRange(String from, String to) {
        try {
            Date now= DateUtils.getDate();
            return now.after(DateUtils.getDate(from)) && now.before(DateUtils.getDate(to));
        } catch(Exception e) {
            Log.e("inRange", e.toString());
            return false;
        }
    }

    public static int Age(String DOB) {
        try {
            Calendar calendar= Calendar.getInstance();
            int nowYear= calendar.get(Calendar.YEAR);
            calendar.setTime(DateUtils.getDate(DOB));
            int dobYear= calendar.get(Calendar.YEAR);
            return nowYear - dobYear;
        } catch(Exception e) {
            Log.e("Age", e.toString());
            return 0;
        }
    }

    public static String modeToScaled(String mode) {
        return "M".equals(mode)?"m":mode;
        /*
        switch (mode) {            case "H": mode= "M"; break;
            case "D": mode= "H"; break;
            case "W": mode= "D"; break;
            case "M": mode= "W"; break;
            case "Y": mode= "m"; break;
        }
        return mode;*/
    }/*
    public static int modeToDays(String mode, int after) {
        switch (mode+"X") {
            case "H": after= after / 24; break;
            case "D": break;
            case "W": after= after * 7; break;
            case "M": after= after * 30; break;
            case "Y": after= after * 365; break;
        }
        return after;
    }*/

    public static String timeStr(long seconds, boolean round) {
        int sec= 0, min= 0, hr= 0;
        if(seconds >= 3600) {
            hr= (int)seconds / 3600;
            seconds= seconds % 3600;
        }
        if(seconds >= 60) {
            min= (int)seconds / 60;
            seconds= seconds % 60;
        }
        sec= (int) seconds;
        if(round) {
            min = min > 0 ? min + (sec > 0 ? 1 : 0) : min;
            if (min >= 60) {
                hr += 1;
                min = 0;
                sec = 0;
            }
        }
        String str= (hr>0? String.valueOf(hr)+ "hr ":"") +
                (min>0? String.valueOf(min)+ "min ":"") +
                (min==0&&sec>0? String.valueOf(sec)+ "sec ":"");
        return str;
    }

    public static String getNearRoundLocalTime(Date date) {
        try {
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(date);
            int min= calendar.get(Calendar.MINUTE);

            if(min > 55) {
                calendar.set(Calendar.MINUTE, 0);
                calendar.add(Calendar.HOUR_OF_DAY, 1);
            } else {
                int init= 50;
                while(init >= 0) {
                    if(min > init) {
                        calendar.set(Calendar.MINUTE, init + 5);
                        break;
                    }
                    init-= 5;
                }
            }

            if(sameDay(calendar.getTime(), DateUtils.getDate())) {
                return DateUtils.getLocalTime(calendar.getTime());
            } else {
                return DateUtils.getLocalDateTime(calendar.getTime());
            }
        } catch (Exception e) {
            return DateUtils.getLocalDateTime(date);
        }
    }

    public static String timeDifference(String inTime, boolean tinyMode, boolean addAgo) {
        String secondsStr= (tinyMode?" sec(s)":" second(s)") + (addAgo?" ago":"");
        String minutesStr= (tinyMode?" min(s)":" minute(s)") + (addAgo?" ago":"");
        String hoursStr= (tinyMode?" hr(s)":" hour(s)") + (addAgo?" ago":"");
        String daysStr= (tinyMode?" day(s)":" day(s)") + (addAgo?" ago":"");
        String monthsStr= (tinyMode?" month(s)":" month(s)") + (addAgo?" ago":"");
        String yearsStr= (tinyMode?" year(s)":" year(s)") + (addAgo?" ago":"");
        String approx= tinyMode?"":"approximately ";

        long msPerMinute = 60 * 1000;
        long msPerHour = msPerMinute * 60;
        long msPerDay = msPerHour * 24;
        long msPerMonth = msPerDay * 30;
        long msPerYear = msPerDay * 365;

        Date current= new Date();
        Date previous= getDateTime(inTime);
        long elapsed = current.getTime() - previous.getTime();

        if (elapsed < msPerMinute) {
            return "Just now";
            //var temp= Math.round(elapsed/1000);
            //return temp + dt.cleanS(temp, secondsStr);
        }
        else if (elapsed < msPerHour) {
            long temp= Math.round(elapsed/msPerMinute);
            return temp + cleanS(temp, minutesStr);
        }
        else if (elapsed < msPerDay ) {
            long temp= Math.round(elapsed/msPerHour);
            return temp + cleanS(temp, hoursStr);
        }
        else if (elapsed < msPerMonth) {
            long temp= Math.round(elapsed/msPerDay);
            return approx + temp + cleanS(temp, daysStr);
        }
        else if (elapsed < msPerYear) {
            long temp= Math.round(elapsed/msPerMonth);
            return approx + temp + cleanS(temp, monthsStr);
        }
        else {
            long temp= Math.round(elapsed/msPerYear);
            return approx + temp + cleanS(temp, yearsStr);
        }
    }
    private static String cleanS(long value, String string) {
        return string.replace("(s)", value>1?"s":"");
    }
}
