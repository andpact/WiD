package andpact.project.wid.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import andpact.project.wid.R;
import andpact.project.wid.Title;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.WiDDatabaseHelper;

import android.widget.ImageButton;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.textview.MaterialTextView;

public class WiDReadAllFragment extends Fragment {
    private TextView dateTextView;
    private ImageButton leftTriangle, rightTriangle;
    private LocalDate currentDate, firstOfMonth;
    private DateTimeFormatter formatter;
    private GridLayout gridLayout;
    private LinearLayout statisticsTabLinearLayout;
    private LinearLayout statisticsLinearLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private Map<String, Integer> colorMap;
    private Map<String, String> titleMap;
    private Map<String, Duration> totalDurationMap;
    private Map<String, Integer> bestDayMap;
    private Map<String, Duration> bestDurationMap;
    private Map<String, Duration> totalDurationForDayMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_read_all, container, false);

        dateTextView = view.findViewById(R.id.dateTextView);
        leftTriangle = view.findViewById(R.id.leftTriangle);
        rightTriangle = view.findViewById(R.id.rightTriangle);

        gridLayout = view.findViewById(R.id.gridLayout);
        statisticsTabLinearLayout = view.findViewById(R.id.statisticsTabLinearLayout);
        statisticsLinearLayout = view.findViewById(R.id.statisticsLinearLayout);
        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

        // Initialize the date formatter
        formatter = DateTimeFormatter.ofPattern("yyyy년 M월");

        // Get the current date
        currentDate = LocalDate.now();

        colorMap = new HashMap<>();
        colorMap.put(Title.STUDY.toString(), getContext().getColor(R.color.study_color));
        colorMap.put(Title.WORK.toString(), getContext().getColor(R.color.work_color));
        colorMap.put(Title.READING.toString(), getContext().getColor(R.color.reading_color));
        colorMap.put(Title.EXERCISE.toString(), getContext().getColor(R.color.exercise_color));
        colorMap.put(Title.SLEEP.toString(), getContext().getColor(R.color.sleep_color));
        colorMap.put(Title.TRAVEL.toString(), getContext().getColor(R.color.travel_color));
        colorMap.put(Title.HOBBY.toString(), getContext().getColor(R.color.hobby_color));
        colorMap.put(Title.OTHER.toString(), getContext().getColor(R.color.other_color));

        titleMap = new HashMap<>();
        titleMap.put(Title.STUDY.toString(), getString(R.string.title_1));
        titleMap.put(Title.WORK.toString(), getString(R.string.title_2));
        titleMap.put(Title.READING.toString(), getString(R.string.title_3));
        titleMap.put(Title.EXERCISE.toString(), getString(R.string.title_4));
        titleMap.put(Title.SLEEP.toString(), getString(R.string.title_5));
        titleMap.put(Title.TRAVEL.toString(), getString(R.string.title_6));
        titleMap.put(Title.HOBBY.toString(), getString(R.string.title_7));
        titleMap.put(Title.OTHER.toString(), getString(R.string.title_8));

        // Set the formatted date to the TextView
        updateDateTextView();

        // Set click listeners for the left and right arrows
        leftTriangle.setOnClickListener(v -> decreaseMonth());
        rightTriangle.setOnClickListener(v -> increaseMonth());

        return view;
    }

    private void decreaseMonth() {
        currentDate = currentDate.minusMonths(1);
        updateDateTextView();
    }

    private void increaseMonth() {
        currentDate = currentDate.plusMonths(1);
        updateDateTextView();
    }

    private void updateDateTextView() {
        boolean hasData = false;

        gridLayout.removeAllViews();
        statisticsLinearLayout.removeAllViews();
        firstOfMonth = currentDate.withDayOfMonth(1);
        int startDay = firstOfMonth.getDayOfWeek().getValue() % 7; // 0 to 6, Sun to Mon
        int daysInMonth = currentDate.lengthOfMonth();

        totalDurationMap = new HashMap<>();
        bestDayMap = new HashMap<>();
        bestDurationMap = new HashMap<>();

        for (Title title : Title.values()) { // "STUDY"
            totalDurationMap.put(title.toString(), Duration.ZERO);
            bestDayMap.put(title.toString(), 0);
            bestDurationMap.put(title.toString(), Duration.ZERO);
        }

        for (int i = 0; i < startDay + daysInMonth; i++) { // 1일 부터 마지막 날까지 파이 차트를 그림. 반복 한 번이 하루를 의미함.
            ArrayList<PieEntry> entries;
            PieDataSet dataSet;
            PieData data;

            PieChart pieChart = new PieChart(getContext());
            pieChart.setUsePercentValues(false); // 상대 값(퍼센트)이 아닌 절대 값 사용
            pieChart.setDrawEntryLabels(false); // 엔트리 라벨 표시 X
            pieChart.getDescription().setEnabled(false); // 설명 비활성화
            pieChart.getLegend().setEnabled(false); // 각주(범례) 표시 X
            pieChart.setHoleRadius(70); // 가운데 원의 반지름은 큰 원의 70%
//            pieChart.setPadding(0, 0, 0, 0);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
//            파이 차트 크기 키우기 왜 안됨;;
//            layoutParams.setMargins(0, 0, 0, 0);
//            layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
//            layoutParams.width = 140;
//            layoutParams.height = 140;

            pieChart.setLayoutParams(layoutParams);

            if (i < startDay) { // 1일 이전의 빈 파이 차트를 표시
                entries = new ArrayList<>(); // 빈 엔트리 셋 생성
                entries.add(new PieEntry(1, ""));
                dataSet = new PieDataSet(entries, "");
                dataSet.setColor(Color.TRANSPARENT);
                data = new PieData(dataSet);
                data.setDrawValues(false); // 엔트리 값 표시 X
                pieChart.setData(data);
                pieChart.invalidate();

                gridLayout.addView(pieChart);
            } else { // 1일 부터의 파이 차트를 표시
                pieChart.setDrawCenterText(true);
                pieChart.setCenterText(i - startDay + 1 + "");
                pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);

                // Set the center text color based on the day of the week
                if (i % 7 == 0) {
                    // Sunday (0th day of the week)
//                    pieChart.setCenterTextColor(R.color.sunday_red); // 왜 안됨 이거
                    pieChart.setCenterTextColor(Color.RED);
                } else if (i % 7 == 6) {
                    // Saturday (6th day of the week)
//                    pieChart.setCenterTextColor(R.color.saturday_blue);
                    pieChart.setCenterTextColor(Color.BLUE);
                } else {
                    // Other days of the week
                    pieChart.setCenterTextColor(R.color.black);
                }

                List<WiD> wiDList = wiDDatabaseHelper.getWiDByDate(firstOfMonth.toString());

                if (wiDList.isEmpty()) { // 해당 날짜에 WiD가 없을 때 빈 파이 차트를 그림.
                    entries = new ArrayList<>(); // 빈 엔트리 셋 생성
                    entries.add(new PieEntry(1, ""));
                    dataSet = new PieDataSet(entries, "");
                    dataSet.setColor(Color.LTGRAY);
                    data = new PieData(dataSet);
                    data.setDrawValues(false); // 엔트리 값 표시 X
                    pieChart.setData(data);
                    pieChart.invalidate();
                } else {
                    hasData = !hasData;

                    entries = new ArrayList<>();

                    // 시작 시간 초기화
                    int startMinutes = 0;

                    totalDurationForDayMap = new HashMap<>();

                    for (Title title : Title.values()) { // "STUDY"
                        totalDurationForDayMap.put(title.toString(), Duration.ZERO);
                    }

                    for (WiD wiD : wiDList) {
                        int finishMinutes = wiD.getFinish().getHour() * 60 + wiD.getFinish().getMinute();

                        // 비어 있는 시간대의 엔트리 추가
                        if (wiD.getStart().getHour() * 60 + wiD.getStart().getMinute() > startMinutes) {
                            int emptyMinutes = wiD.getStart().getHour() * 60 + wiD.getStart().getMinute() - startMinutes;
                            entries.add(new PieEntry(emptyMinutes, ""));
                        }

                        // 시작 시간 업데이트
                        startMinutes = wiD.getStart().getHour() * 60 + wiD.getStart().getMinute();

                        // 엔트리 셋에 해당 WiD 객체의 시간대를 추가
                        entries.add(new PieEntry(finishMinutes - startMinutes, wiD.getTitle()));

                        // 시작 시간 업데이트
                        startMinutes = wiD.getFinish().getHour() * 60 + wiD.getFinish().getMinute();

                        // totalDurationMap에 값을 할당
                        String title = wiD.getTitle(); // "STUDY"
                        Duration durationForTotal = totalDurationMap.get(title).plus(wiD.getDuration());
                        totalDurationMap.put(title, durationForTotal);

                        // totalDurationForDayMap에 오늘 duration을 할당.
                        Duration durationForDay = totalDurationForDayMap.get(title).plus(wiD.getDuration());
                        totalDurationForDayMap.put(title, durationForDay);
                    }
                    // 마지막 WiD 객체 이후의 비어 있는 시간대의 엔트리 추가
                    if (startMinutes < 24 * 60) {
                        int emptyMinutes = 24 * 60 - startMinutes;
                        entries.add(new PieEntry(emptyMinutes, ""));
                    }

                    // 파이 데이터셋 생성
                    dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(entries.stream()
                            .map(entry -> colorMap.getOrDefault(entry.getLabel(), Color.LTGRAY))
                            .collect(Collectors.toList()));

                    // 파이 데이터셋 생성
                    data = new PieData(dataSet);
                    data.setDrawValues(false); // 엔트리 값 표시 X

                    // 파이 차트에 데이터 설정
                    pieChart.setData(data);
                    pieChart.invalidate(); // 차트 갱신

                    for (Title title : Title.values()) {
                        Duration totalDurationForDay = totalDurationForDayMap.get(title.toString());
                        Duration bestDuration = bestDurationMap.get(title.toString());

                        if (totalDurationForDay != Duration.ZERO && 0 < totalDurationForDay.compareTo(bestDuration)) {
                            bestDurationMap.put(title.toString(), totalDurationForDay);
                            bestDayMap.put(title.toString(), i - startDay + 1);
                        }
                    }
                }
                gridLayout.addView(pieChart);

                firstOfMonth = firstOfMonth.plusDays(1);
            }
        }

        String formattedDate = currentDate.format(formatter);
        dateTextView.setText(formattedDate);

        if (hasData) {
            // Create a list to store the Title values and their corresponding totalDurations
            List<Title> sortedTitles = new ArrayList<>(Arrays.asList(Title.values()));

            // Sort the titles based on totalDuration in descending order
            Collections.sort(sortedTitles, (t1, t2) -> {
                Duration duration1 = totalDurationMap.get(t1.toString());
                Duration duration2 = totalDurationMap.get(t2.toString());
                return duration2.compareTo(duration1);
            });

            int count = 1;

            for (Title key : sortedTitles) {
                LinearLayout itemLinearLayout = new LinearLayout(getContext());
                itemLinearLayout.setPadding(0, 16, 0, 0);
                itemLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                itemLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                MaterialTextView numberTextView = new MaterialTextView(getContext());
                numberTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0.5f));
                numberTextView.setText(count + "");
                numberTextView.setTypeface(null, Typeface.BOLD);
                numberTextView.setGravity(Gravity.CENTER);

                MaterialTextView titleTextView = new MaterialTextView(getContext());
                titleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0.5f));
                titleTextView.setText(titleMap.get(key.toString()));
                titleTextView.setTypeface(null, Typeface.BOLD);
                titleTextView.setGravity(Gravity.CENTER);

                MaterialTextView totalDurationTextView = new MaterialTextView(getContext());
                Duration totalDuration = totalDurationMap.get(key.toString());

                // 총 소요 시간 자체가 없으면 표시를 안함.
                if (totalDuration == Duration.ZERO) {
                    continue;
                }

                count++;

                long totalDurationHours = totalDuration.toHours();
                long totalDurationMinutes = (totalDuration.toMinutes() % 60);
                String totalDurationText;
                if (totalDurationHours > 0 && totalDurationMinutes == 0) {
                    totalDurationText = String.format("%d시간", totalDurationHours);
                } else if (totalDurationHours > 0) {
                    totalDurationText = String.format("%d시간 %d분", totalDurationHours, totalDurationMinutes);
                } else {
                    totalDurationText = String.format("%d분", totalDurationMinutes);
                }
                totalDurationTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f));
                totalDurationTextView.setText(totalDurationText);

                totalDurationTextView.setTypeface(null, Typeface.BOLD);
                totalDurationTextView.setGravity(Gravity.CENTER);
                MaterialTextView bestDayTextView = new MaterialTextView(getContext());
                int bestDayValue = bestDayMap.get(key.toString());
                bestDayTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0.7f));
                bestDayTextView.setText(bestDayValue + "일");
                bestDayTextView.setTypeface(null, Typeface.BOLD);
                bestDayTextView.setGravity(Gravity.CENTER);

                MaterialTextView bestDurationTextView = new MaterialTextView(getContext());
                Duration bestDuration = bestDurationMap.get(key.toString());
                long bestDurationHours = bestDuration.toHours();
                long bestDurationMinutes = (bestDuration.toMinutes() % 60);
                String bestDurationText;
                if (bestDurationHours > 0 && bestDurationMinutes == 0) {
                    bestDurationText = String.format("%d시간", bestDurationHours);
                } else if (bestDurationHours > 0) {
                    bestDurationText = String.format("%d시간 %d분", bestDurationHours, bestDurationMinutes);
                } else {
                    bestDurationText = String.format("%d분", bestDurationMinutes);
                }
                bestDurationTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0.7f));
                bestDurationTextView.setText(bestDurationText);
                bestDurationTextView.setTypeface(null, Typeface.BOLD);
                bestDurationTextView.setGravity(Gravity.CENTER);

                itemLinearLayout.addView(numberTextView);
                itemLinearLayout.addView(titleTextView);
                itemLinearLayout.addView(totalDurationTextView);
                itemLinearLayout.addView(bestDayTextView);
                itemLinearLayout.addView(bestDurationTextView);

                statisticsLinearLayout.addView(itemLinearLayout);
            }
        } else {
            MaterialTextView noDataTextView = new MaterialTextView(getContext());
            noDataTextView.setText("표시할 WiD가 없어요.");
            noDataTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            noDataTextView.setGravity(Gravity.CENTER);
            statisticsTabLinearLayout.setVisibility(View.GONE);
            statisticsLinearLayout.addView(noDataTextView);
        }
    }
}