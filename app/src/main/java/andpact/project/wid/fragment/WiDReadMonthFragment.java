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

import androidx.core.content.ContextCompat;
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
import andpact.project.wid.util.Title;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.DataMaps;
import andpact.project.wid.util.WiDDatabaseHelper;

import android.widget.ImageButton;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.textview.MaterialTextView;

public class WiDReadMonthFragment extends Fragment {
    private TextView dateTextView;
    private ImageButton decreaseDateButton, increaseDateButton;
    private LocalDate currentDate, firstOfMonth;
    private DateTimeFormatter formatter;
    private GridLayout gridLayout;
    private LinearLayout wiDHolderLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private Map<String, Duration> totalDurationMap;
    private Map<String, Integer> bestDayMap;
    private Map<String, Duration> bestDurationMap;
    private Map<String, Duration> totalDurationForDayMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_read_month, container, false);

        dateTextView = view.findViewById(R.id.dateTextView);
        decreaseDateButton = view.findViewById(R.id.decreaseDateButton);
        increaseDateButton = view.findViewById(R.id.increaseDateButton);

        gridLayout = view.findViewById(R.id.gridLayout);
        wiDHolderLayout = view.findViewById(R.id.wiDHolderLayout);
        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

        // Initialize the date formatter
        formatter = DateTimeFormatter.ofPattern("yyyy.M");

        // Get the current date
        currentDate = LocalDate.now();

        // Set the formatted date to the TextView
        updateWiDLayout();

        // Set click listeners for the left and right arrows
        decreaseDateButton.setOnClickListener(v -> decreaseMonth());
        increaseDateButton.setOnClickListener(v -> increaseMonth());

        return view;
    }

    private void decreaseMonth() {
        currentDate = currentDate.minusMonths(1);
        updateWiDLayout();
    }
    private void increaseMonth() {
        currentDate = currentDate.plusMonths(1);
        updateWiDLayout();
    }
    private void updateWiDLayout() {
        LocalDate today = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int todayMonth = today.getMonthValue();

        if (currentMonth == todayMonth) {
            increaseDateButton.setEnabled(false);
            increaseDateButton.setAlpha(0.2f);
        } else {
            increaseDateButton.setEnabled(true);
            increaseDateButton.setAlpha(1f);
        }

        boolean hasData = false;

        gridLayout.removeAllViews();
        wiDHolderLayout.removeAllViews();
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
                dataSet.setColor(ContextCompat.getColor(getContext(), R.color.transparent));
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
                    pieChart.setCenterTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else if (i % 7 == 6) {
                    // Saturday (6th day of the week)
                    pieChart.setCenterTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                } else {
                    // Other days of the week
                    pieChart.setCenterTextColor(ContextCompat.getColor(getContext(), R.color.black));
                }

                List<WiD> wiDList = wiDDatabaseHelper.getWiDByDate(firstOfMonth.toString());

                if (wiDList.isEmpty()) { // 해당 날짜에 WiD가 없을 때 빈 파이 차트를 그림.
                    entries = new ArrayList<>(); // 빈 엔트리 셋 생성
                    entries.add(new PieEntry(1, ""));
                    dataSet = new PieDataSet(entries, "");
                    dataSet.setColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                    data = new PieData(dataSet);
                    data.setDrawValues(false); // 엔트리 값 표시 X
                    pieChart.setData(data);
                    pieChart.invalidate();
                } else {
                    hasData = true;

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
                            .map(entry -> DataMaps.getColorMap(getContext()).getOrDefault(entry.getLabel(), ContextCompat.getColor(getContext(), R.color.light_gray)))
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

            for (Title key : sortedTitles) {
                LinearLayout itemLinearLayout = new LinearLayout(getContext());
                itemLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                itemLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                MaterialTextView titleTextView = new MaterialTextView(getContext());
                titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                titleTextView.setText(DataMaps.getTitleMap(getContext()).get(key.toString()));
                titleTextView.setTypeface(null, Typeface.BOLD);
                titleTextView.setGravity(Gravity.CENTER);

                MaterialTextView bestDayAndDurationTextView = new MaterialTextView(getContext());
                int bestDay = bestDayMap.get(key.toString());
                bestDayAndDurationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                bestDayAndDurationTextView.setTypeface(null, Typeface.BOLD);
                bestDayAndDurationTextView.setGravity(Gravity.CENTER);

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

                bestDayAndDurationTextView.setText(bestDay + "일(" + bestDurationText + ")");

                MaterialTextView totalDurationTextView = new MaterialTextView(getContext());
                Duration totalDuration = totalDurationMap.get(key.toString());

                // 총 소요 시간 자체가 없으면 표시를 안함.
                if (totalDuration == Duration.ZERO) {
                    continue;
                }

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
                totalDurationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                totalDurationTextView.setText(totalDurationText);
                totalDurationTextView.setTypeface(null, Typeface.BOLD);
                totalDurationTextView.setGravity(Gravity.CENTER);

                itemLinearLayout.addView(titleTextView);
                itemLinearLayout.addView(bestDayAndDurationTextView);
                itemLinearLayout.addView(totalDurationTextView);

                wiDHolderLayout.addView(itemLinearLayout);
            }
        } else {
            MaterialTextView noDataTextView = new MaterialTextView(getContext());
            noDataTextView.setTextSize(30);
            noDataTextView.setText("표시할 데이터가 없어요.");
            noDataTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            noDataTextView.setGravity(Gravity.CENTER);
            noDataTextView.setPadding(0, 20, 0, 20);
            wiDHolderLayout.addView(noDataTextView);
        }
    }
}