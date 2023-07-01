package andpact.project.wid.util;

import android.content.Context;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

import andpact.project.wid.R;

public class DataMaps {
    private static Map<DayOfWeek, String> dayOfWeekMap;
    private static Map<String, Integer> colorMap;
    private static Map<String, String> titleMap;

    public static Map<DayOfWeek, String> getDayOfWeekMap() {
        if (dayOfWeekMap == null) {
            dayOfWeekMap = new HashMap<>();
            dayOfWeekMap.put(DayOfWeek.MONDAY, "월");
            dayOfWeekMap.put(DayOfWeek.TUESDAY, "화");
            dayOfWeekMap.put(DayOfWeek.WEDNESDAY, "수");
            dayOfWeekMap.put(DayOfWeek.THURSDAY, "목");
            dayOfWeekMap.put(DayOfWeek.FRIDAY, "금");
            dayOfWeekMap.put(DayOfWeek.SATURDAY, "토");
            dayOfWeekMap.put(DayOfWeek.SUNDAY, "일");
        }
        return dayOfWeekMap;
    }
    public static Map<String, Integer> getColorMap(Context context) {
        if (colorMap == null) {
            colorMap = new HashMap<>();
            colorMap.put(Title.STUDY.toString(), context.getColor(R.color.study_color));
            colorMap.put(Title.WORK.toString(), context.getColor(R.color.work_color));
            colorMap.put(Title.READING.toString(), context.getColor(R.color.reading_color));
            colorMap.put(Title.EXERCISE.toString(), context.getColor(R.color.exercise_color));
            colorMap.put(Title.HOBBY.toString(), context.getColor(R.color.hobby_color));
            colorMap.put(Title.MEAL.toString(), context.getColor(R.color.meal_color));
            colorMap.put(Title.SHOWER.toString(), context.getColor(R.color.shower_color));
            colorMap.put(Title.TRAVEL.toString(), context.getColor(R.color.travel_color));
            colorMap.put(Title.SLEEP.toString(), context.getColor(R.color.sleep_color));
            colorMap.put(Title.OTHER.toString(), context.getColor(R.color.other_color));
        }
        return colorMap;
    }
    public static Map<String, String> getTitleMap(Context context) {
        if (titleMap == null) {
            titleMap = new HashMap<>();
            titleMap.put(Title.STUDY.toString(), context.getString(R.string.title_1));
            titleMap.put(Title.WORK.toString(), context.getString(R.string.title_2));
            titleMap.put(Title.READING.toString(), context.getString(R.string.title_3));
            titleMap.put(Title.EXERCISE.toString(), context.getString(R.string.title_4));
            titleMap.put(Title.HOBBY.toString(), context.getString(R.string.title_5));
            titleMap.put(Title.MEAL.toString(), context.getString(R.string.title_6));
            titleMap.put(Title.SHOWER.toString(), context.getString(R.string.title_7));
            titleMap.put(Title.TRAVEL.toString(), context.getString(R.string.title_8));
            titleMap.put(Title.SLEEP.toString(), context.getString(R.string.title_9));
            titleMap.put(Title.OTHER.toString(), context.getString(R.string.title_10));
        }
        return titleMap;
    }

}