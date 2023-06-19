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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import andpact.project.wid.R;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.DataMaps;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDReadFragment extends Fragment {
    private MaterialTextView dateTextView, dayOfWeekTextView;
    private LinearLayout linearLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private LocalDate currentDate;
    private ImageButton leftTriangle, rightTriangle;
    private PieChart pieChart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_read, container, false);
        dateTextView = view.findViewById(R.id.dateTextView);
        dayOfWeekTextView = view.findViewById(R.id.dayOfWeekTextView);
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

        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

        currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (");
        String formattedDate = currentDate.format(formatter);
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

        updateWiDList();

        return view;
    }

    private void decreaseDate() {
        currentDate = currentDate.minusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (");
        String formattedDate = currentDate.format(formatter);
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (");
        String formattedDate = currentDate.format(formatter);
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
            // WiD 리스트를 텍스트 뷰에 표현하기
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            entries = new ArrayList<>();

            // 시작 시간 초기화
            int startMinutes = 0;

            int count = 1; // Initialize the counter variable

            for (WiD wiD : wiDList) {

                LinearLayout mainLayout = new LinearLayout(getContext());
                mainLayout.setOrientation(LinearLayout.VERTICAL);
                mainLayout.setTag(wiD.getId());

                LinearLayout itemLayout = new LinearLayout(getContext());
                itemLayout.setPadding(0, 16, 0, 0);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setGravity(Gravity.CENTER_VERTICAL);

                // Create and add the numberTextView
                MaterialTextView numberTextView = new MaterialTextView(getContext());
                numberTextView.setText(String.valueOf(count++)); // Set the current count as the text
                numberTextView.setTypeface(null, Typeface.BOLD);
                numberTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
                numberTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(numberTextView);

                // Create and add the title TextView
                MaterialTextView titleTextView = new MaterialTextView(getContext());
//                titleTextView.setText(titleMap.get(wiD.getTitle()));
                titleTextView.setText(DataMaps.getTitleMap(getContext()).get(wiD.getTitle()));
                titleTextView.setTypeface(null, Typeface.BOLD);
                titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
                titleTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(titleTextView);

                // Create and add the start time TextView
                MaterialTextView startTimeTextView = new MaterialTextView(getContext());
                startTimeTextView.setText(wiD.getStart().format(timeFormatter));
                startTimeTextView.setTypeface(null, Typeface.BOLD);
                startTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f));
                startTimeTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(startTimeTextView);

                // Create and add the finish time TextView
                MaterialTextView finishTimeTextView = new MaterialTextView(getContext());
                finishTimeTextView.setText(wiD.getFinish().format(timeFormatter));
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

                ImageView arrowImageView = new ImageView(getContext());
                arrowImageView.setImageResource(R.drawable.baseline_keyboard_arrow_right_24);
                arrowImageView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
                arrowImageView.setTag("arrow_right"); // Set a tag to track the state of the button
                itemLayout.addView(arrowImageView);

                LinearLayout itemLayout2 = new LinearLayout(getContext());
                itemLayout2.setOrientation(LinearLayout.VERTICAL);
                itemLayout2.setVisibility(View.GONE); // Initially set to invisible

                ConstraintLayout constraintLayout = new ConstraintLayout(getContext());
                constraintLayout.setPadding(32, 16, 32, 0);
                // Set LayoutParams
//                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
//                        ConstraintLayout.LayoutParams.MATCH_PARENT,
//                        ConstraintLayout.LayoutParams.WRAP_CONTENT
//                );
//                constraintLayout.setLayoutParams(layoutParams);

                // Create and add the "세부사항 입력칸" TextView
                MaterialTextView detailTextView = new MaterialTextView(getContext());
                detailTextView.setId(View.generateViewId());
//                detailTextView.setTextSize(16);
                detailTextView.setTypeface(null, Typeface.BOLD); // Set the text to bold
                detailTextView.setGravity(Gravity.CENTER_VERTICAL);
                detailTextView.setMaxLines(3); // 최대 3줄까지 표시
                detailTextView.setEllipsize(TextUtils.TruncateAt.END); // 글자가 생략될 경우 "..."로 표시

                ConstraintLayout.LayoutParams detailParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                detailTextView.setLayoutParams(detailParams);

                MaterialButton showMoreButton = new MaterialButton(getContext());
                showMoreButton.setId(View.generateViewId());
                showMoreButton.setText("..자세히");
                showMoreButton.setTextColor(Color.BLUE); // 버튼 텍스트 색상을 파란색으로 설정
                showMoreButton.setBackgroundColor(Color.TRANSPARENT); // 배경색을 투명으로 설정
                showMoreButton.setPadding(0, 0, 0, 0);

                ConstraintLayout.LayoutParams buttonParams = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                );
                buttonParams.setMargins(0, 0, 0, 0);

                showMoreButton.setLayoutParams(buttonParams);
                showMoreButton.setGravity(Gravity.END | Gravity.BOTTOM); // 버튼을 텍스트 뷰의 오른쪽 아래에 위치하도록 설정
                showMoreButton.setOnClickListener(v -> {
                    // "자세히 보기" 버튼 클릭 시 전체 디테일을 표시하는 기능 구현
                    detailTextView.setMaxLines(Integer.MAX_VALUE); // 최대 줄 수 제한을 해제하여 전체 디테일 표시
                    showMoreButton.setVisibility(View.GONE); // "자세히 보기" 버튼 감추기
                });

                constraintLayout.addView(detailTextView);
                constraintLayout.addView(showMoreButton);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(detailTextView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
//                constraintSet.connect(detailTextView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
//                constraintSet.connect(detailTextView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
//                constraintSet.connect(detailTextView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                constraintSet.connect(showMoreButton.getId(), ConstraintSet.END, detailTextView.getId(), ConstraintSet.END);
                constraintSet.connect(showMoreButton.getId(), ConstraintSet.BOTTOM, detailTextView.getId(), ConstraintSet.BOTTOM);

                constraintSet.applyTo(constraintLayout);

                if (TextUtils.isEmpty(wiD.getDetail())) {
                    detailTextView.setHint("세부 사항 입력...");
                    showMoreButton.setVisibility(View.GONE);
                } else {
                    detailTextView.setText(wiD.getDetail());
                    showMoreButton.setVisibility(View.VISIBLE);
                }

//                if (TextUtils.isEmpty(wiD.getDetail())) {
//                    detailTextView.setHint("세부 사항 입력...");
//                    showMoreButton.setVisibility(View.GONE);
//                } else {
//                    detailTextView.setText(wiD.getDetail());
//                    if (detailTextView.getLineCount() > 3) {
//                        showMoreButton.setVisibility(View.VISIBLE);
//                    } else {
//                        showMoreButton.setVisibility(View.GONE);
//                    }
//                }

                itemLayout2.addView(constraintLayout);

                // Set OnClickListener for the TextView
                detailTextView.setOnClickListener(v -> {
                    // Create and show the dialog
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    MaterialTextView dialogTitle = new MaterialTextView(getContext());
                    dialogTitle.setPadding(0, 64, 0, 64);
                    dialogTitle.setText("세부 사항");
                    dialogTitle.setTextSize(20);
                    dialogTitle.setGravity(Gravity.CENTER);
                    dialogTitle.setTypeface(null, Typeface.BOLD);
                    builder.setCustomTitle(dialogTitle);

                    // Create and add the Material TextInputLayout with EditText
                    TextInputLayout textInputLayout = new TextInputLayout(getContext());
                    textInputLayout.setPadding(64, 0, 64, 0);

                    textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE); // 왜 설정이 안되냐;;;
                    textInputLayout.setCounterEnabled(true);
                    textInputLayout.setCounterMaxLength(200);

                    TextInputEditText textInputEditText = new TextInputEditText(getContext());
                    textInputEditText.setText(detailTextView.getText()); // Set the text from detailTextView to TextInputEditText
                    textInputLayout.addView(textInputEditText);

                    // Set minimum lines to 5
                    textInputEditText.setMinLines(10);

                    builder.setView(textInputLayout);

                    // Set positive button action
                    builder.setPositiveButton("확인", (dialog, which) -> {
                        // Get the ID from the mainLayout's tag
                        Long id = (Long) mainLayout.getTag();

                        // Get the new detail from the TextInputEditText
                        String newDetail = textInputEditText.getText().toString();

                        // Call the updateWiDDetailById method with the retrieved values
                        wiDDatabaseHelper.updateWiDDetailById(id, newDetail);

                        // 새 디테일 적용
                        detailTextView.setText(newDetail);

                        showSnackbar("WiD의 세부 사항이 수정되었어요.");
                    });

                    // Set negative button action
                    builder.setNegativeButton("취소", (dialog, which) -> {
                        // Dismiss the dialog
                        dialog.dismiss();
                    });

                    // Show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });

                // Create and add the "WiD 삭제" Button
                MaterialButton deleteButton = new MaterialButton(getContext());
                deleteButton.setText("WiD 삭제");
                deleteButton.setBackgroundColor(Color.RED);
                deleteButton.setCornerRadius(15);
                LinearLayout.LayoutParams deleteLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                deleteLayoutParams.setMargins(32, 16, 32, 0);
                deleteButton.setLayoutParams(deleteLayoutParams);

                deleteButton.setOnClickListener(v -> {
                    // Create and show the confirmation dialog
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setMessage("삭제하시겠습니까?");
                    builder.setPositiveButton("삭제", (dialog, which) -> {
                        // Get the ID from the mainLayout's tag
                        Long id = (Long) mainLayout.getTag();

                        // Call the deleteWiDById method with the retrieved ID
                        wiDDatabaseHelper.deleteWiDById(id);

                        // 화면 업데이트
                        updateWiDList();

                        showSnackbar("WiD가 삭제되었어요.");
                    });
                    builder.setNegativeButton("취소", (dialog, which) -> {
                        // Dismiss the dialog
                        dialog.dismiss();
                    });
                    // Show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });

                itemLayout2.addView(deleteButton);

                itemLayout.setOnClickListener(v -> {
                    if (itemLayout2.getVisibility() == View.GONE) {
                        itemLayout2.setVisibility(View.VISIBLE);
                        arrowImageView.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                        arrowImageView.setTag("arrow_down");
                    } else {
                        itemLayout2.setVisibility(View.GONE);
                        arrowImageView.setImageResource(R.drawable.baseline_keyboard_arrow_right_24);
                        arrowImageView.setTag("arrow_right");
                    }
                });

                // Add itemLayout and itemLayout2 to mainLayout
                mainLayout.addView(itemLayout);
                mainLayout.addView(itemLayout2);

                linearLayout.addView(mainLayout);

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
}