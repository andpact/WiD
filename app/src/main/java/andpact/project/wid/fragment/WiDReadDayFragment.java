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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import andpact.project.wid.R;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.CircleView;
import andpact.project.wid.util.DataMaps;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDReadDayFragment extends Fragment {
    private MaterialTextView dateTextView, dayOfWeekTextView;
    private DateTimeFormatter dateFormatter, timeFormatter, timeFormatter2;
    private LinearLayout headerLinearLayout, linearLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private LocalDate currentDate;
    private ImageButton leftTriangle, rightTriangle, wiDSaveGalleryButton, wiDDeleteButton, wiDCloseButton, editDetailButton, cancelEditDetailButton,
            showEditDetailButton;
    private PieChart pieChart;
    private CircleView circleView;

    private LinearLayout wiDLinearLayout, wiDDateLinearLayout, wiDTitleLinearLayout, wiDStartTimeLinearLayout,
            wiDFinishTimeLinearLayout, wiDDurationLinearLayout, wiDDetailLinearLayout, showDetailLinearLayout, editDetailLinearLayout;
    private MaterialTextView wiDDateTextView, wiDTitleTextView, wiDDayOfWeekTextView, wiDStartTimeTextView,
            wiDFinishTimeTextView, wiDDurationTextView, wiDDetailTextView;
    private ImageView showDetailLinearLayoutImageView;
    private TextInputEditText detailInputEditText;
    private long clickedWiDId;
    private WiD clickedWiD;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_read_day, container, false);
        dateTextView = view.findViewById(R.id.dateTextView);
        dayOfWeekTextView = view.findViewById(R.id.dayOfWeekTextView);
        headerLinearLayout = view.findViewById(R.id.headerLinearLayout);
        linearLayout = view.findViewById(R.id.linearLayout);

        leftTriangle = view.findViewById(R.id.leftTriangle);
        rightTriangle = view.findViewById(R.id.rightTriangle);

        pieChart = view.findViewById(R.id.pieChart);
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
        pieChart.setCenterTextColor(Color.BLACK);

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
            dayOfWeekTextView.setTextColor(Color.BLUE);
        } else if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dayOfWeekTextView.setTextColor(Color.RED);
        } else {
            dayOfWeekTextView.setTextColor(Color.BLACK);
        }

        leftTriangle.setOnClickListener(v -> decreaseDate());
        rightTriangle.setOnClickListener(v -> increaseDate());

        wiDLinearLayout = view.findViewById(R.id.wiDLinearLayout);
        wiDTitleLinearLayout = view.findViewById(R.id.wiDTitleLinearLayout);
        wiDDateLinearLayout = view.findViewById(R.id.wiDDateLinearLayout);
        wiDStartTimeLinearLayout = view.findViewById(R.id.wiDStartTimeLinearLayout);
        wiDFinishTimeLinearLayout = view.findViewById(R.id.wiDFinishTimeLinearLayout);
        wiDDurationLinearLayout = view.findViewById(R.id.wiDDurationLinearLayout);
        wiDDetailLinearLayout = view.findViewById(R.id.wiDDetailLinearLayout);

//        LinearLayout.LayoutParams layoutParams10 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        layoutParams10.setMargins(0, 10, 0, 0);
//
//        LinearLayout.LayoutParams layoutParams20 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        layoutParams20.setMargins(0, 20, 0, 0);

        wiDTitleTextView = view.findViewById(R.id.wiDTitleTextView);
        wiDDateTextView = view.findViewById(R.id.wiDDateTextView);
        wiDDayOfWeekTextView = view.findViewById(R.id.wiDDayOfWeekTextView);
        wiDStartTimeTextView = view.findViewById(R.id.wiDStartTimeTextView);
        wiDFinishTimeTextView = view.findViewById(R.id.wiDFinishTimeTextView);
        wiDDurationTextView = view.findViewById(R.id.wiDDurationTextView);
        wiDDetailTextView = view.findViewById(R.id.wiDDetailTextView);

        showDetailLinearLayout = view.findViewById(R.id.showDetailLinearLayout);

        editDetailLinearLayout = view.findViewById(R.id.editDetailLinearLayout);

        showDetailLinearLayoutImageView = view.findViewById(R.id.showDetailLinearLayoutImageView);
        showDetailLinearLayout.setOnClickListener(new View.OnClickListener() {
            boolean isExpanded = false;

            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    showDetailLinearLayoutImageView.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
                    wiDDetailLinearLayout.setVisibility(View.GONE);

//                    wiDDateLinearLayout.setLayoutParams(layoutParams10);
//                    wiDTitleLinearLayout.setLayoutParams(layoutParams10);
//                    wiDStartTimeLinearLayout.setLayoutParams(layoutParams10);
//                    wiDFinishTimeLinearLayout.setLayoutParams(layoutParams10);
//                    wiDDurationLinearLayout.setLayoutParams(layoutParams10);
                } else {
                    showDetailLinearLayoutImageView.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
                    wiDDetailLinearLayout.setVisibility(View.VISIBLE);

//                    wiDDateLinearLayout.setLayoutParams(layoutParams20);
//                    wiDTitleLinearLayout.setLayoutParams(layoutParams20);
//                    wiDStartTimeLinearLayout.setLayoutParams(layoutParams20);
//                    wiDFinishTimeLinearLayout.setLayoutParams(layoutParams20);
//                    wiDDurationLinearLayout.setLayoutParams(layoutParams20);
                }
                isExpanded = !isExpanded;
            }
        });

        detailInputEditText = view.findViewById(R.id.detailInputEditText);

        showEditDetailButton = view.findViewById(R.id.showEditDetailButton);
        showEditDetailButton.setBackgroundColor(Color.TRANSPARENT);
        showEditDetailButton.setOnClickListener(v -> {
            detailInputEditText.setText(wiDDetailTextView.getText());
            editDetailLinearLayout.setVisibility(View.VISIBLE);
            wiDLinearLayout.setVisibility(View.GONE);
        });

        editDetailButton = view.findViewById(R.id.editDetailButton);
        editDetailButton.setBackgroundColor(Color.TRANSPARENT);
        editDetailButton.setOnClickListener(v -> {
            String newDetail = detailInputEditText.getText().toString();

            wiDDatabaseHelper.updateWiDDetailById(clickedWiDId, newDetail);

            wiDDetailTextView.setText(newDetail);
            editDetailLinearLayout.setVisibility(View.GONE);

            wiDLinearLayout.setVisibility(View.VISIBLE);

            updateWiDList();

            showSnackbar("세부 사항이 수정되었습니다.");
        });

        cancelEditDetailButton = view.findViewById(R.id.cancelEditDetailButton);
        cancelEditDetailButton.setBackgroundColor(Color.TRANSPARENT);
        cancelEditDetailButton.setOnClickListener(v -> {
            editDetailLinearLayout.setVisibility(View.GONE);

            wiDLinearLayout.setVisibility(View.VISIBLE);
        });

        wiDSaveGalleryButton = view.findViewById(R.id.wiDSaveGalleryButton);

        wiDDeleteButton = view.findViewById(R.id.wiDDeleteButton);
        wiDDeleteButton.setOnClickListener(v -> {
            // Create and show the confirmation dialog
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setMessage("WiD를 삭제하시겠습니까?");
            builder.setPositiveButton("삭제", (dialog, which) -> {

                // Call the deleteWiDById method with the retrieved ID
                wiDDatabaseHelper.deleteWiDById(clickedWiDId);

                wiDLinearLayout.setVisibility(View.GONE);

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

        wiDCloseButton = view.findViewById(R.id.wiDCloseButton);
        wiDCloseButton.setOnClickListener(v -> {
            wiDLinearLayout.setVisibility(View.GONE);
            clickedWiDId = 0;
            clickedWiD = null;

            headerLinearLayout.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            circleView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);

            wiDDetailLinearLayout.setVisibility(View.GONE);

            showDetailLinearLayoutImageView.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);

//            왜 안되냐?;;
//            headerLinearLayout.setEnabled(true);
//            headerLinearLayout.setClickable(true);
//            linearLayout.setEnabled(true);
//            linearLayout.setClickable(true);
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
        linearLayout.removeAllViews();
        List<WiD> wiDList = wiDDatabaseHelper.getWiDByDate(currentDate.toString());

        ArrayList<PieEntry> entries;
        PieDataSet dataSet;
        PieData data;

        if (wiDList.isEmpty()) {
            entries = new ArrayList<>(); // 빈 엔트리 셋 생성
            entries.add(new PieEntry(1, ""));
            dataSet = new PieDataSet(entries, "");
            dataSet.setColor(Color.LTGRAY);
            data = new PieData(dataSet);
            data.setDrawValues(false); // 엔트리 값 표시 X
            pieChart.setData(data);
            pieChart.invalidate();

            // "세부 사항으로 검색해 보세요." 텍스트 뷰 생성 및 설정
            MaterialTextView noContextTextView = new MaterialTextView(getContext());
            noContextTextView.setText("표시할 WiD가 없어요.");
            noContextTextView.setGravity(Gravity.CENTER);

            // 리니어 레이아웃에 텍스트 뷰 추가
            linearLayout.addView(noContextTextView);

        } else {
            entries = new ArrayList<>();

            // 시작 시간 초기화
            int startMinutes = 0;

            int count = 1; // Initialize the counter variable

            for (WiD wiD : wiDList) {

                LinearLayout itemLayout = new LinearLayout(getContext());
                itemLayout.setPadding(0, 16, 0, 0);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setGravity(Gravity.CENTER_VERTICAL);
                itemLayout.setTag(wiD.getId());
                int color = DataMaps.getColorMap(getContext()).get(wiD.getTitle());
                itemLayout.setBackgroundColor(color);

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
                    headerLinearLayout.setVisibility(View.GONE);
                    pieChart.setVisibility(View.GONE);
                    circleView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);

                    wiDLinearLayout.setVisibility(View.VISIBLE);
                    clickedWiDId = (Long) itemLayout.getTag();
                    clickedWiD = wiDDatabaseHelper.getWiDById(clickedWiDId);

                    wiDTitleTextView.setText(DataMaps.getTitleMap(getContext()).get(clickedWiD.getTitle()));
                    wiDDateTextView.setText(clickedWiD.getDate().format(dateFormatter));

                    String wiDKoreanDayOfWeek = DataMaps.getDayOfWeekMap().get(clickedWiD.getDate().getDayOfWeek());
                    wiDDayOfWeekTextView.setText(wiDKoreanDayOfWeek);

                    if (clickedWiD.getDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
                        wiDDayOfWeekTextView.setTextColor(Color.BLUE);
                    } else if (clickedWiD.getDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
                        wiDDayOfWeekTextView.setTextColor(Color.RED);
                    } else {
                        wiDDayOfWeekTextView.setTextColor(Color.BLACK);
                    }

                    wiDStartTimeTextView.setText(clickedWiD.getStart().format(timeFormatter));
                    wiDFinishTimeTextView.setText(clickedWiD.getFinish().format(timeFormatter));

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

                    wiDDurationTextView.setText(clickedWiDDurationText);

                    wiDDetailTextView.setText(clickedWiD.getDetail());

                });

                linearLayout.addView(itemLayout);

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
                    .map(entry -> DataMaps.getColorMap(getContext()).getOrDefault(entry.getLabel(), Color.LTGRAY))
                    .collect(Collectors.toList()));

            // 파이 데이터셋 생성
            data = new PieData(dataSet);
            data.setDrawValues(false); // 엔트리 값 표시 X

            // 파이 차트에 데이터 설정
            pieChart.setData(data);
            pieChart.invalidate(); // 차트 갱신
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