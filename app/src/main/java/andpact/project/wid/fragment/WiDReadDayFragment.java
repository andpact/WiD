package andpact.project.wid.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.time.DayOfWeek;
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
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.CircleView;
import andpact.project.wid.util.DataMaps;
import andpact.project.wid.util.Title;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDReadDayFragment extends Fragment {
    private MaterialTextView dateTextView, dayOfWeekTextView;
    private DateTimeFormatter dateFormatter, timeFormatter, timeFormatter2;
    private LinearLayout dateLayout, totalDurationLayout, totalDurationHolderLayout, wiDLayout, wiDHolderLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private LocalDate currentDate;
    private ImageButton decreaseDateButton, increaseDateButton, clickedWiDSaveGalleryButton, clickedWiDDeleteButton, clickedWiDCloseButton, clickedWiDEditDetailButton, clickedWiDCancelEditDetailButton,
            clickedWiDShowEditDetailButton;
    private PieChart pieChart;
    private CircleView circleView;

    private LinearLayout clickedWiDLayout, clickedWiDDetailLayout, showClickedWiDetailLayout, clickedWiDEditDetailLayout;
    private MaterialTextView clickedWiDDateTextView, clickedWiDDayOfWeekTextView, clickedWiDTitleTextView, clickedWiDStartTextView,
            clickedWiDFinishTextView, clickedWiDDurationTextView, clickedWiDDetailTextView;
    private ImageView showClickedWiDDetailLayoutImageView;
    private TextInputEditText clickedWiDDetailInputEditText;
    private long clickedWiDId;
    private WiD clickedWiD;
    private Map<String, Duration> totalDurationForDayMap;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_read_day, container, false);
        dateTextView = view.findViewById(R.id.dateTextView);
        dayOfWeekTextView = view.findViewById(R.id.dayOfWeekTextView);
        dateLayout = view.findViewById(R.id.dateLayout);
        totalDurationLayout = view.findViewById(R.id.totalDurationLayout);
        totalDurationHolderLayout = view.findViewById(R.id.totalDurationHolderLayout);
        wiDLayout = view.findViewById(R.id.wiDLayout);
        wiDHolderLayout = view.findViewById(R.id.wiDHolderLayout);

        decreaseDateButton = view.findViewById(R.id.decreaseDateButton);
        increaseDateButton = view.findViewById(R.id.increaseDateButton);

        pieChart = view.findViewById(R.id.pieChart);
//        pieChart.setElevation(2);
        pieChart.setUsePercentValues(false); // 상대 값(퍼센트)이 아닌 절대 값 사용
        pieChart.setDrawEntryLabels(false); // 엔트리 라벨 표시 X
        pieChart.getDescription().setEnabled(false); // 설명 비활성화
        pieChart.getLegend().setEnabled(false); // 각주(범례) 표시 X
//        pieChart.setDrawHoleEnabled(false); // 가운데 원 표시 X
//        pieChart.setHoleColor(Color.TRANSPARENT);// 가운데 원 색
        pieChart.setHoleRadius(70); // 가운데 원의 반지름은 큰 원의 70%

        // 가운데 텍스트 설정
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("오후 | 오전");

        // 텍스트 스타일 설정 (옵션)
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setCenterTextColor(ContextCompat.getColor(getContext(), R.color.black));

        circleView = view.findViewById(R.id.circleView);

        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

        currentDate = LocalDate.now();
        dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.d ");
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeFormatter2 = DateTimeFormatter.ofPattern("HH:mm");

        String formattedDate = currentDate.format(dateFormatter);
        dateTextView.setText(formattedDate);
        String koreanDayOfWeek = DataMaps.getDayOfWeekMap().get(currentDate.getDayOfWeek());
        dayOfWeekTextView.setText(koreanDayOfWeek);

        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            dayOfWeekTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        } else if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dayOfWeekTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        } else {
            dayOfWeekTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }

        decreaseDateButton.setOnClickListener(v -> decreaseDate());
        increaseDateButton.setOnClickListener(v -> increaseDate());

        clickedWiDLayout = view.findViewById(R.id.clickedWiDLayout);
        clickedWiDDetailLayout = view.findViewById(R.id.clickedWiDDetailLayout);

        clickedWiDDateTextView = view.findViewById(R.id.clickedWiDDateTextView);
        clickedWiDDayOfWeekTextView = view.findViewById(R.id.clickedWiDDayOfWeekTextView);
        clickedWiDTitleTextView = view.findViewById(R.id.clickedWiDTitleTextView);
        clickedWiDStartTextView = view.findViewById(R.id.clickedWiDStartTextView);
        clickedWiDFinishTextView = view.findViewById(R.id.clickedWiDFinishTextView);
        clickedWiDDurationTextView = view.findViewById(R.id.clickedWiDDurationTextView);
        clickedWiDDetailTextView = view.findViewById(R.id.clickedWiDDetailTextView);

        showClickedWiDetailLayout = view.findViewById(R.id.showClickedWiDetailLayout);

        clickedWiDEditDetailLayout = view.findViewById(R.id.clickedWiDEditDetailLayout);

        showClickedWiDDetailLayoutImageView = view.findViewById(R.id.showClickedWiDDetailLayoutImageView);
        showClickedWiDetailLayout.setOnClickListener(new View.OnClickListener() {
            boolean isExpanded = false;
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    showClickedWiDDetailLayoutImageView.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
                    clickedWiDDetailLayout.setVisibility(View.GONE);
                } else {
                    showClickedWiDDetailLayoutImageView.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
                    clickedWiDDetailLayout.setVisibility(View.VISIBLE);
                }
                isExpanded = !isExpanded;
            }
        });

        clickedWiDDetailInputEditText = view.findViewById(R.id.clickedWiDDetailInputEditText);

        clickedWiDShowEditDetailButton = view.findViewById(R.id.clickedWiDShowEditDetailButton);
        clickedWiDShowEditDetailButton.setOnClickListener(v -> {
            clickedWiDDetailInputEditText.setText(clickedWiDDetailTextView.getText());
            clickedWiDEditDetailLayout.setVisibility(View.VISIBLE);
            clickedWiDLayout.setVisibility(View.GONE);
        });

        clickedWiDEditDetailButton = view.findViewById(R.id.clickedWiDEditDetailButton);
        clickedWiDEditDetailButton.setOnClickListener(v -> {
            String newDetail = clickedWiDDetailInputEditText.getText().toString();

            wiDDatabaseHelper.updateWiDDetailById(clickedWiDId, newDetail);

            clickedWiDDetailTextView.setText(newDetail);
            clickedWiDEditDetailLayout.setVisibility(View.GONE);

            clickedWiDLayout.setVisibility(View.VISIBLE);

            updateWiDList();

            showSnackbar("세부 사항이 수정되었습니다.");
        });

        clickedWiDCancelEditDetailButton = view.findViewById(R.id.clickedWiDCancelEditDetailButton);
        clickedWiDCancelEditDetailButton.setOnClickListener(v -> {
            clickedWiDEditDetailLayout.setVisibility(View.GONE);

            clickedWiDLayout.setVisibility(View.VISIBLE);
        });

        clickedWiDSaveGalleryButton = view.findViewById(R.id.clickedWiDSaveGalleryButton);

        clickedWiDDeleteButton = view.findViewById(R.id.clickedWiDDeleteButton);
        clickedWiDDeleteButton.setOnClickListener(v -> {
            // Create and show the confirmation dialog
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setMessage("WiD를 삭제하시겠습니까?");
            builder.setPositiveButton("삭제", (dialog, which) -> {

                // Call the deleteWiDById method with the retrieved ID
                wiDDatabaseHelper.deleteWiDById(clickedWiDId);

                clickedWiDLayout.setVisibility(View.GONE);

                updateWiDList();

                showSnackbar("WiD가 삭제되었습니다.");
            });
            builder.setNegativeButton("취소", (dialog, which) -> {
                // Dismiss the dialog
                dialog.dismiss();
            });
            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        clickedWiDCloseButton = view.findViewById(R.id.clickedWiDCloseButton);
        clickedWiDCloseButton.setOnClickListener(v -> {
            clickedWiDLayout.setVisibility(View.GONE);
            clickedWiDId = 0;
            clickedWiD = null;

            dateLayout.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            circleView.setVisibility(View.VISIBLE);
            totalDurationLayout.setVisibility(View.VISIBLE);
            wiDLayout.setVisibility(View.VISIBLE);

            clickedWiDDetailLayout.setVisibility(View.GONE);

            showClickedWiDDetailLayoutImageView.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
        });

        updateWiDList();

        return view;
    }
    private void decreaseDate() {
        currentDate = currentDate.minusDays(1);

        String formattedDate = currentDate.format(dateFormatter);
        dateTextView.setText(formattedDate);

        String koreanDayOfWeek = DataMaps.getDayOfWeekMap().get(currentDate.getDayOfWeek());
        dayOfWeekTextView.setText(koreanDayOfWeek);

        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            dayOfWeekTextView.setTextColor(Color.BLUE);
        } else if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dayOfWeekTextView.setTextColor(Color.RED);
        } else {
            dayOfWeekTextView.setTextColor(Color.BLACK);
        }
        updateWiDList();
    }
    private void increaseDate() {
        currentDate = currentDate.plusDays(1);

        String formattedDate = currentDate.format(dateFormatter);
        dateTextView.setText(formattedDate);

        String koreanDayOfWeek = DataMaps.getDayOfWeekMap().get(currentDate.getDayOfWeek());
        dayOfWeekTextView.setText(koreanDayOfWeek);

        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            dayOfWeekTextView.setTextColor(Color.BLUE);
        } else if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dayOfWeekTextView.setTextColor(Color.RED);
        } else {
            dayOfWeekTextView.setTextColor(Color.BLACK);
        }

        updateWiDList();
    }
    private void updateWiDList() {
        wiDHolderLayout.removeAllViews();
        totalDurationHolderLayout.removeAllViews();

        totalDurationForDayMap = new HashMap<>();

        for (Title title : Title.values()) { // "STUDY"
            totalDurationForDayMap.put(title.toString(), Duration.ZERO);
        }

        List<WiD> wiDList = wiDDatabaseHelper.getWiDByDate(currentDate.toString());

        ArrayList<PieEntry> entries;
        PieDataSet dataSet;
        PieData data;

        if (wiDList.isEmpty()) {
            entries = new ArrayList<>(); // 빈 엔트리 셋 생성
            entries.add(new PieEntry(1, ""));
            dataSet = new PieDataSet(entries, "");
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.light_gray));
            data = new PieData(dataSet);
            data.setDrawValues(false); // 엔트리 값 표시 X
            pieChart.setData(data);
            pieChart.invalidate();

            totalDurationLayout.setVisibility(View.INVISIBLE);

            // "세부 사항으로 검색해 보세요." 텍스트 뷰 생성 및 설정
            MaterialTextView noDataTextView = new MaterialTextView(getContext());
            noDataTextView.setText("표시할 WiD가 없어요.");
            noDataTextView.setGravity(Gravity.CENTER);
            noDataTextView.setTextSize(30);
            noDataTextView.setPadding(0, 20, 0, 20);

            // 리니어 레이아웃에 텍스트 뷰 추가
            wiDHolderLayout.addView(noDataTextView);

        } else {
            totalDurationLayout.setVisibility(View.VISIBLE);

            entries = new ArrayList<>();

            // 시작 시간 초기화
            int startMinutes = 0;

            int count = 1; // Initialize the counter variable

            for (WiD wiD : wiDList) {

                Duration durationForDay = totalDurationForDayMap.get(wiD.getTitle()).plus(wiD.getDuration());
                totalDurationForDayMap.put(wiD.getTitle(), durationForDay);

                LinearLayout itemLayout = new LinearLayout(getContext());
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setGravity(Gravity.CENTER_VERTICAL);
                itemLayout.setBackgroundResource(R.drawable.bg_light_gray);
                itemLayout.setTag(wiD.getId());

                // Create and add the numberTextView
                MaterialTextView numberTextView = new MaterialTextView(getContext());
                numberTextView.setText(String.valueOf(count++)); // Set the current count as the text
                numberTextView.setTypeface(null, Typeface.BOLD);
                numberTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
                numberTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(numberTextView);

                // Create and add the title TextView
                MaterialTextView titleTextView = new MaterialTextView(getContext());
                titleTextView.setText(DataMaps.getTitleMap(getContext()).get(wiD.getTitle()));
                titleTextView.setTypeface(null, Typeface.BOLD);
                titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
                titleTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(titleTextView);

                // Create and add the start time TextView
                MaterialTextView startTimeTextView = new MaterialTextView(getContext());
                startTimeTextView.setText(wiD.getStart().format(timeFormatter2));
                startTimeTextView.setTypeface(null, Typeface.BOLD);
                startTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f));
                startTimeTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(startTimeTextView);

                // Create and add the finish time TextView
                MaterialTextView finishTimeTextView = new MaterialTextView(getContext());
                finishTimeTextView.setText(wiD.getFinish().format(timeFormatter2));
                finishTimeTextView.setTypeface(null, Typeface.BOLD);
                finishTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f));
                finishTimeTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(finishTimeTextView);

                // Create and add the duration TextView
                MaterialTextView durationTextView = new MaterialTextView(getContext());
                long hours = wiD.getDuration().toHours();
                long minutes = (wiD.getDuration().toMinutes() % 60);
                String durationText;

                if (hours > 0 && minutes == 0) {
                    durationText = String.format("%d시간", hours);
                } else if (hours > 0) {
                    durationText = String.format("%d시간 %d분", hours, minutes);
                } else {
                    durationText = String.format("%d분", minutes);
                }

                durationTextView.setText(durationText);
                durationTextView.setTypeface(null, Typeface.BOLD);
                durationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
                durationTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(durationTextView);

                ImageView detailImageView = new ImageView(getContext());
                if (TextUtils.isEmpty(wiD.getDetail())) {
                    detailImageView.setImageResource(R.drawable.baseline_more_horiz_24);
                } else {
                    detailImageView.setImageResource(R.drawable.outline_description_24);
                }
                detailImageView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
                itemLayout.addView(detailImageView);

                itemLayout.setOnClickListener(v -> {
                    dateLayout.setVisibility(View.GONE);
                    pieChart.setVisibility(View.GONE);
                    circleView.setVisibility(View.GONE);
                    totalDurationLayout.setVisibility(View.GONE);
                    wiDLayout.setVisibility(View.GONE);

                    clickedWiDLayout.setVisibility(View.VISIBLE);
                    clickedWiDId = (Long) itemLayout.getTag();
                    clickedWiD = wiDDatabaseHelper.getWiDById(clickedWiDId);

                    clickedWiDDateTextView.setText(clickedWiD.getDate().format(dateFormatter));
                    clickedWiDTitleTextView.setText(DataMaps.getTitleMap(getContext()).get(clickedWiD.getTitle()));

                    String wiDKoreanDayOfWeek = DataMaps.getDayOfWeekMap().get(clickedWiD.getDate().getDayOfWeek());
                    clickedWiDDayOfWeekTextView.setText(wiDKoreanDayOfWeek);

                    if (clickedWiD.getDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
                        clickedWiDDayOfWeekTextView.setTextColor(Color.BLUE);
                    } else if (clickedWiD.getDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
                        clickedWiDDayOfWeekTextView.setTextColor(Color.RED);
                    } else {
                        clickedWiDDayOfWeekTextView.setTextColor(Color.BLACK);
                    }

                    clickedWiDStartTextView.setText(clickedWiD.getStart().format(timeFormatter));
                    clickedWiDFinishTextView.setText(clickedWiD.getFinish().format(timeFormatter));

                    long clickedWiDHours = clickedWiD.getDuration().toHours();
                    long clickedWiDMinutes = (clickedWiD.getDuration().toMinutes() % 60);
                    long clickedWiDSeconds = (clickedWiD.getDuration().getSeconds() % 60);
                    String clickedWiDDurationText;

                    if (0 < clickedWiDHours && 0 == clickedWiDMinutes && 0 == clickedWiDSeconds) {
                        clickedWiDDurationText = String.format("%d시간", clickedWiDHours);
                    } else if (0 < clickedWiDHours && 0 < clickedWiDMinutes && 0 == clickedWiDSeconds) {
                        clickedWiDDurationText = String.format("%d시간 %d분", clickedWiDHours, clickedWiDMinutes);
                    } else if (0 < clickedWiDHours && 0 == clickedWiDMinutes && 0 < clickedWiDSeconds) {
                        clickedWiDDurationText = String.format("%d시간 %d초", clickedWiDHours, clickedWiDSeconds);
                    } else if (0 < clickedWiDHours) {
                        clickedWiDDurationText = String.format("%d시간 %d분 %d초", clickedWiDHours, clickedWiDMinutes, clickedWiDSeconds);
                    } else if (0 < clickedWiDMinutes && 0 == clickedWiDSeconds) {
                        clickedWiDDurationText = String.format("%d분", clickedWiDMinutes);
                    } else if (0 < clickedWiDMinutes) {
                        clickedWiDDurationText = String.format("%d분 %d초", clickedWiDMinutes, clickedWiDSeconds);
                    } else {
                        clickedWiDDurationText = String.format("%d초", clickedWiDSeconds);
                    }

                    clickedWiDDurationTextView.setText(clickedWiDDurationText);

                    clickedWiDDetailTextView.setText(clickedWiD.getDetail());

                });

                wiDHolderLayout.addView(itemLayout);

                // WiD 객체의 시작 시간과 종료 시간을 분 단위로 계산
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

            List<Title> sortedTitles = new ArrayList<>(Arrays.asList(Title.values()));

            Collections.sort(sortedTitles, (t1, t2) -> {
                Duration duration1 = totalDurationForDayMap.get(t1.toString());
                Duration duration2 = totalDurationForDayMap.get(t2.toString());
                return duration2.compareTo(duration1);
            });

            for (Title key : sortedTitles) {
                LinearLayout totalDurationItemLayout = new LinearLayout(getContext());
                totalDurationItemLayout.setOrientation(LinearLayout.HORIZONTAL);
                totalDurationItemLayout.setBackgroundResource(R.drawable.bg_light_gray);
                totalDurationItemLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                MaterialTextView titleTextView = new MaterialTextView(getContext());
                titleTextView.setText(DataMaps.getTitleMap(getContext()).get(key.toString()));
                titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                titleTextView.setGravity(Gravity.CENTER);
                titleTextView.setTextSize(20);
                titleTextView.setTypeface(null, Typeface.BOLD);

                MaterialTextView totalDurationTextView = new MaterialTextView(getContext());
                totalDurationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                totalDurationTextView.setGravity(Gravity.CENTER);
                Duration totalDuration = totalDurationForDayMap.get(key.toString());

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
                totalDurationTextView.setTextSize(18);
                totalDurationTextView.setTypeface(null, Typeface.BOLD);
                totalDurationTextView.setGravity(Gravity.CENTER);

                totalDurationItemLayout.addView(titleTextView);
                totalDurationItemLayout.addView(totalDurationTextView);

                totalDurationHolderLayout.addView(totalDurationItemLayout);
            }
        }
    }
    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);

        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 16 * 15);
        snackbarView.setLayoutParams(params);

        snackbar.show();
    }
    private void saveGallery() {
        // showDetailLinearLayout 없애고 저장 ㄱㄱ
    }
}