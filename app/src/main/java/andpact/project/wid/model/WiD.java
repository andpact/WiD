package andpact.project.wid.model;


import android.content.ContentValues;
import android.database.Cursor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class WiD {
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DETAIL = "detail";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_FINISH = "finish";
    public static final String COLUMN_DURATION = "duration";
    private String title;
    private String detail;
    private LocalDate date;
    private LocalTime start;
    private LocalTime finish;
    private Duration duration;

    public WiD() {
    }

    public WiD(String title, String detail, LocalDate date, LocalTime start, LocalTime finish, Duration duration) {
        this.title = title;
        this.detail = detail;
        this.date = date;
        this.start = start;
        this.finish = finish;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getFinish() {
        return finish;
    }

    public void setFinish(LocalTime finish) {
        this.finish = finish;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    // Method to convert WiD object to ContentValues
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DETAIL, detail);
        values.put(COLUMN_DATE, date.toString());
        values.put(COLUMN_START, start.toString());
        values.put(COLUMN_FINISH, finish.toString());
        values.put(COLUMN_DURATION, duration.toString());
        return values;
    }

    // Static method to create a WiD object from a database cursor
    public static WiD fromCursor(Cursor cursor) {
        WiD wid = new WiD();
        wid.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        wid.setDetail(cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL)));
        wid.setDate(LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE))));
        wid.setStart(LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_START))));
        wid.setFinish(LocalTime.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FINISH))));
        wid.setDuration(Duration.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DURATION))));
        return wid;
    }
}
