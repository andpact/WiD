package andpact.project.wid.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import andpact.project.wid.R;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.DataMaps;
import andpact.project.wid.util.Title;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDReadWeekFragment extends Fragment {
    private TextView dateTextView, weekOfYearTextView, setDateTodayButton;
    private ImageButton decreaseDateButton, increaseDateButton;
    private LocalDate currentDate;
    private GridLayout gridLayout;
    private DateTimeFormatter formatter;
    private LinearLayout wiDHolderLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private Map<String, Duration> totalDurationMap;
    private Map<String, Integer> bestDayMap;
    private Map<String, Duration> bestDurationMap;
    private Map<String, Duration> totalDurationForDayMap;
    private Map<String, Integer> colorMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_read_week, container, false);

        dateTextView = view.findViewById(R.id.dateTextView);
        weekOfYearTextView = view.findViewById(R.id.weekOfYearTextView);
        setDateTodayButton = view.findViewById(R.id.setDateTodayButton);
        setDateTodayButton.setOnClickListener(v -> {
            currentDate = LocalDate.now();
            updateWiDLayout();
        });

        decreaseDateButton = view.findViewById(R.id.decreaseDateButton);
        increaseDateButton = view.findViewById(R.id.increaseDateButton);

        colorMap = DataMaps.getColorMap(getContext());

        currentDate = LocalDate.now();

        formatter = DateTimeFormatter.ofPattern("yyyy.M W번째 '주'");

        gridLayout = view.findViewById(R.id.gridLayout);
        wiDHolderLayout = view.findViewById(R.id.wiDHolderLayout);

        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

        // Set click listeners for the left and right arrows
        decreaseDateButton.setOnClickListener(v -> decreaseWeek());
        increaseDateButton.setOnClickListener(v -> increaseWeek());

        updateWiDLayout();

        return view;
    }
    private void decreaseWeek() {
        currentDate = currentDate.minusDays(7);
        updateWiDLayout();
    }

    private void increaseWeek() {
        currentDate = currentDate.plusDays(7);
        updateWiDLayout();
    }
    private void updateWiDLayout() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeek = currentDate.get(weekFields.weekOfYear());
        int todayWeek = today.get(weekFields.weekOfYear());

        if (currentWeek == todayWeek) {
            increaseDateButton.setEnabled(false);
            increaseDateButton.setAlpha(0.2f);
        } else {
            increaseDateButton.setEnabled(true);
            increaseDateButton.setAlpha(1f);
        }

        boolean hasData = false;

        gridLayout.removeAllViews();
        wiDHolderLayout.removeAllViews();

        totalDurationMap = new HashMap<>();
        bestDayMap = new HashMap<>();
        bestDurationMap = new HashMap<>();

        for (Title title : Title.values()) { // "STUDY"
            totalDurationMap.put(title.toString(), Duration.ZERO);
            bestDayMap.put(title.toString(), 0);
            bestDurationMap.put(title.toString(), Duration.ZERO);
        }

        LocalDate firstDayOfWeek = currentDate;
        while (firstDayOfWeek.getDayOfWeek() != DayOfWeek.MONDAY) {
            firstDayOfWeek = firstDayOfWeek.minusDays(1);
        }

        for (int i = 0; i < 14; i++) {
            ArrayList<PieEntry> entries = new ArrayList<>(); // 빈 엔트리 셋 생성
            entries.add(new PieEntry(1, ""));
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.light_gray));
            PieData data = new PieData(dataSet);
            data.setDrawValues(false); // 엔트리 값 표시 X
            PieChart pieChart = new PieChart(getContext());
            pieChart.setUsePercentValues(false); // 상대 값(퍼센트)이 아닌 절대 값 사용
            pieChart.setDrawEntryLabels(false); // 엔트리 라벨 표시 X
            pieChart.getDescription().setEnabled(false); // 설명 비활성화
            pieChart.getLegend().setEnabled(false); // 각주(범례) 표시 X
            pieChart.setHoleRadius(70); // 가운데 원의 반지름은 큰 원의 70%
            pieChart.setHoleColor(Color.TRANSPARENT);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            if (i < 7) {
                pieChart.setAlpha(0.2f);
            } else {
                pieChart.setAlpha(0.5f);
            }

            pieChart.setLayoutParams(layoutParams);
            pieChart.setData(data);
            pieChart.invalidate();

            gridLayout.addView(pieChart);
        }

        for (int i = 0; i < 7; i++) { // 일주일 파이 차트 그리기
            ArrayList<PieEntry> entries;
            PieDataSet dataSet;
            PieData data;

            PieChart pieChart = new PieChart(getContext());
            pieChart.setUsePercentValues(false); // 상대 값(퍼센트)이 아닌 절대 값 사용
            pieChart.setDrawEntryLabels(false); // 엔트리 라벨 표시 X
            pieChart.getDescription().setEnabled(false); // 설명 비활성화
            pieChart.getLegend().setEnabled(false); // 각주(범례) 표시 X
            pieChart.setHoleRadius(70); // 가운데 원의 반지름은 큰 원의 70%
            pieChart.setHoleColor(Color.TRANSPARENT);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            pieChart.setLayoutParams(layoutParams);

            pieChart.setDrawCenterText(true);
            pieChart.setCenterText(firstDayOfWeek.getDayOfMonth() + "");
            pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);

            if (firstDayOfWeek.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                pieChart.setCenterTextColor(ContextCompat.getColor(getContext(), R.color.red));
            } else if (firstDayOfWeek.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                pieChart.setCenterTextColor(ContextCompat.getColor(getContext(), R.color.blue));
            } else {
                pieChart.setCenterTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }

            List<WiD> wiDList = wiDDatabaseHelper.getWiDByDate(firstDayOfWeek.toString());

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
                        bestDayMap.put(title.toString(), firstDayOfWeek.getDayOfMonth());
                    }
                }
            }
            gridLayout.addView(pieChart);
            firstDayOfWeek = firstDayOfWeek.plusDays(1);
        }

        for (int i = 0; i < 14; i++) {
            ArrayList<PieEntry> entries = new ArrayList<>(); // 빈 엔트리 셋 생성
            entries.add(new PieEntry(1, ""));
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.light_gray));
            PieData data = new PieData(dataSet);
            data.setDrawValues(false); // 엔트리 값 표시 X
            PieChart pieChart = new PieChart(getContext());
            pieChart.setUsePercentValues(false); // 상대 값(퍼센트)이 아닌 절대 값 사용
            pieChart.setDrawEntryLabels(false); // 엔트리 라벨 표시 X
            pieChart.getDescription().setEnabled(false); // 설명 비활성화
            pieChart.getLegend().setEnabled(false); // 각주(범례) 표시 X
            pieChart.setHoleRadius(70); // 가운데 원의 반지름은 큰 원의 70%
            pieChart.setHoleColor(Color.TRANSPARENT);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            if (i < 7) {
                pieChart.setAlpha(0.5f);
            } else {
                pieChart.setAlpha(0.2f);
            }

            pieChart.setLayoutParams(layoutParams);
            pieChart.setData(data);
            pieChart.invalidate();

            gridLayout.addView(pieChart);
        }

        String formattedDate = currentDate.format(formatter);
        dateTextView.setText(formattedDate);

        int weekOfYear = currentDate.get(WeekFields.ISO.weekOfWeekBasedYear());
        weekOfYearTextView.setText(" " + weekOfYear + "/52");

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
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 16, 0, 0); // 위쪽 마진을 16으로 설정
                itemLinearLayout.setLayoutParams(layoutParams);
//                itemLinearLayout.setPadding(0, 16, 0, 16);
                itemLinearLayout.setElevation(4);
                itemLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLinearLayout.setBackgroundResource(R.drawable.bg_white);

                ShapeableImageView imageView = new ShapeableImageView(getContext());
                imageView.setBackgroundResource(R.drawable.rectangle);
                GradientDrawable wiDDrawable = (GradientDrawable) imageView.getBackground();
                wiDDrawable.setColor(colorMap.get(key.toString()));
                itemLinearLayout.addView(imageView);

                MaterialTextView titleTextView = new MaterialTextView(getContext());
                titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
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
                long bestDurationSeconds = (bestDuration.getSeconds() % 60);
                String formattedBestDuration;

                if (0 < bestDurationHours && 0 == bestDurationMinutes && 0 == bestDurationSeconds) {
                    formattedBestDuration = String.format("%d시간", bestDurationHours);
                } else if (0 < bestDurationHours && 0 < bestDurationMinutes && 0 == bestDurationSeconds) {
                    formattedBestDuration = String.format("%d시간 %d분", bestDurationHours, bestDurationMinutes);
                } else if (0 < bestDurationHours && 0 == bestDurationMinutes && 0 < bestDurationSeconds) {
                    formattedBestDuration = String.format("%d시간 %d초", bestDurationHours, bestDurationSeconds);
                } else if (0 < bestDurationHours) {
                    formattedBestDuration = String.format("%d시간 %d분", bestDurationHours, bestDurationMinutes);
                } else if (0 < bestDurationMinutes && 0 == bestDurationSeconds) {
                    formattedBestDuration = String.format("%d분", bestDurationMinutes);
                } else if (0 < bestDurationMinutes) {
                    formattedBestDuration = String.format("%d분 %d초", bestDurationMinutes, bestDurationSeconds);
                } else {
                    formattedBestDuration = String.format("%d초", bestDurationSeconds);
                }

                double bestDurationPercentage = ((double) bestDurationSeconds / (24 * 60 * 60 * 7)) * 100; // 주(week) 비율을 퍼센트로 계산
                double bestDurationRoundedPercentage = Math.floor(bestDurationPercentage * 10.0) / 10.0;

                String bestDurationRoundedPercentageText;
                if (bestDurationRoundedPercentage == 0.0) {
                    bestDurationRoundedPercentageText = "0";
                } else {
                    bestDurationRoundedPercentageText = String.valueOf(bestDurationRoundedPercentage);
                }

                bestDayAndDurationTextView.setText(bestDay + "일 / " + formattedBestDuration + " (" + bestDurationRoundedPercentageText + ")");

                MaterialTextView totalDurationTextView = new MaterialTextView(getContext());
                Duration totalDuration = totalDurationMap.get(key.toString());

                // 총 소요 시간 자체가 없으면 표시를 안함.
                if (totalDuration == Duration.ZERO) {
                    continue;
                }

                long totalDurationHours = totalDuration.toHours();
                long totalDurationMinutes = (totalDuration.toMinutes() % 60);
                long totalDurationSeconds = (totalDuration.getSeconds() % 60);

                String totalDurationText;

                if (0 < totalDurationHours && 0 == totalDurationMinutes && 0 == totalDurationSeconds) {
                    totalDurationText = String.format("%d시간", totalDurationHours);
                } else if (0 < totalDurationHours && 0 < totalDurationMinutes && 0 == totalDurationSeconds) {
                    totalDurationText = String.format("%d시간 %d분", totalDurationHours, totalDurationMinutes);
                } else if (0 < totalDurationHours && 0 == totalDurationMinutes && 0 < totalDurationSeconds) {
                    totalDurationText = String.format("%d시간 %d초", totalDurationHours, totalDurationSeconds);
                } else if (0 < totalDurationHours) {
                    totalDurationText = String.format("%d시간 %d분", totalDurationHours, totalDurationMinutes);
                } else if (0 < totalDurationMinutes && 0 == totalDurationSeconds) {
                    totalDurationText = String.format("%d분", totalDurationMinutes);
                } else if (0 < totalDurationMinutes) {
                    totalDurationText = String.format("%d분 %d초", totalDurationMinutes, totalDurationSeconds);
                } else {
                    totalDurationText = String.format("%d초", totalDurationSeconds);
                }

                double totalDurationPercentage = ((double) totalDurationSeconds / (24 * 60 * 60 * 7)) * 100; // 주(week) 비율을 퍼센트로 계산
                double totalDurationRoundedPercentage = Math.floor(totalDurationPercentage * 10.0) / 10.0;

                String totalDurationRoundedPercentageText;
                if (totalDurationRoundedPercentage == 0.0) {
                    totalDurationRoundedPercentageText = "0";
                } else {
                    totalDurationRoundedPercentageText = String.valueOf(totalDurationRoundedPercentage);
                }

                totalDurationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                totalDurationTextView.setText(totalDurationText);
                totalDurationTextView.append(" (" + totalDurationRoundedPercentageText + ")");
                totalDurationTextView.setTypeface(null, Typeface.BOLD);
                totalDurationTextView.setGravity(Gravity.CENTER);

                itemLinearLayout.addView(titleTextView);
                itemLinearLayout.addView(bestDayAndDurationTextView);
                itemLinearLayout.addView(totalDurationTextView);

                wiDHolderLayout.addView(itemLinearLayout);
            }
        } else {
            MaterialTextView noDataTextView = new MaterialTextView(getContext());
            noDataTextView.setText("표시할 데이터가 없어요.");
            noDataTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            noDataTextView.setGravity(Gravity.CENTER);
            wiDHolderLayout.addView(noDataTextView);
        }
    }
}