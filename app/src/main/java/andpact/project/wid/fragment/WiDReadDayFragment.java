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

public class WiDReadDayFragment extends Fragment {
    private MaterialTextView dateTextView, dayOfWeekTextView;
    private LinearLayout headerLinearLayout, linearLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private LocalDate currentDate;
    private ImageButton leftTriangle, rightTriangle;
    private PieChart pieChart;

    private LinearLayout wiDLinearLayout, wiDTitleLinearLayout, wiDDateLinearLayout, wiDStartTimeLinearLayout,
            wiDFinishTimeLinearLayout, wiDDurationLinearLayout, wiDDetailLinearLayout, wiDOtherButtonLinearLayout, wiDDetailAddCancelButtonLinearLayout;
    private MaterialTextView wiDTitleTextView, wiDDateTextView, wiDDayOfWeekTextView, wiDParenthesisTextView, wiDStartTimeTextView,
            wiDFinishTimeTextView, wiDDurationTextView, wiDDetailTextView;
    private TextInputLayout wiDDetailTextInputLayout;
    private TextInputEditText wiDDetailInputEditText;
    private MaterialButton wiDShowDetailInputTextButton, wiDAddDetailButton, wiDCancelDetailButton, wiDDeleteButton, wiDBackButton;
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

        wiDLinearLayout = view.findViewById(R.id.wiDLinearLayout);
        wiDTitleLinearLayout = view.findViewById(R.id.wiDTitleLinearLayout);
        wiDDateLinearLayout = view.findViewById(R.id.wiDDateLinearLayout);
        wiDStartTimeLinearLayout = view.findViewById(R.id.wiDStartTimeLinearLayout);
        wiDFinishTimeLinearLayout = view.findViewById(R.id.wiDFinishTimeLinearLayout);
        wiDDurationLinearLayout = view.findViewById(R.id.wiDDurationLinearLayout);
        wiDDetailLinearLayout = view.findViewById(R.id.wiDDetailLinearLayout);

        wiDTitleTextView = view.findViewById(R.id.wiDTitleTextView);
        wiDDateTextView = view.findViewById(R.id.wiDDateTextView);
        wiDDayOfWeekTextView = view.findViewById(R.id.wiDDayOfWeekTextView);
        wiDParenthesisTextView = view.findViewById(R.id.wiDParenthesisTextView);
        wiDStartTimeTextView = view.findViewById(R.id.wiDStartTimeTextView);
        wiDFinishTimeTextView = view.findViewById(R.id.wiDFinishTimeTextView);
        wiDDurationTextView = view.findViewById(R.id.wiDDurationTextView);
        wiDDetailTextView = view.findViewById(R.id.wiDDetailTextView);
        wiDDetailTextView.setOnClickListener(v -> {
            wiDDetailTextInputLayout.setVisibility(View.VISIBLE);
            wiDDetailTextView.setVisibility(View.GONE);
            wiDDetailInputEditText.setText(wiDDetailTextView.getText());
        });

        wiDDetailTextInputLayout = view.findViewById(R.id.wiDDetailTextInputLayout);
        wiDDetailInputEditText = view.findViewById(R.id.wiDDetailInputEditText);

        wiDOtherButtonLinearLayout = view.findViewById(R.id.wiDOtherButtonLinearLayout);
        wiDDetailAddCancelButtonLinearLayout = view.findViewById(R.id.wiDDetailAddCancelButtonLinearLayout);

        wiDShowDetailInputTextButton = view.findViewById(R.id.wiDShowDetailInputTextButton);
        wiDShowDetailInputTextButton.setOnClickListener(v -> {
            wiDDetailLinearLayout.setVisibility(View.VISIBLE);
            wiDOtherButtonLinearLayout.setVisibility(View.GONE);
            wiDDetailAddCancelButtonLinearLayout.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(clickedWiD.getDetail())) {
                wiDDetailTextView.setVisibility(View.GONE);
                wiDDetailTextInputLayout.setVisibility(View.VISIBLE);
            } else {
                wiDDetailTextView.setVisibility(View.VISIBLE);
                wiDDetailTextView.setText(clickedWiD.getDetail());
            }
        });

        wiDAddDetailButton = view.findViewById(R.id.wiDAddDetailButton);
        wiDAddDetailButton.setOnClickListener(v -> {
            String newDetail = wiDDetailInputEditText.getText().toString();
            wiDDatabaseHelper.updateWiDDetailById(clickedWiDId, newDetail);
            showSnackbar("WiD의 세부 사항이 수정되었습니다.");
            wiDDetailTextView.setText(newDetail);
            wiDDetailTextView.setVisibility(View.VISIBLE);
            wiDDetailLinearLayout.setVisibility(View.GONE);
            wiDDetailTextInputLayout.setVisibility(View.GONE);
            wiDOtherButtonLinearLayout.setVisibility(View.VISIBLE);
            wiDDetailAddCancelButtonLinearLayout.setVisibility(View.GONE);
        });

        wiDCancelDetailButton = view.findViewById(R.id.wiDCancelDetailButton);
        wiDCancelDetailButton.setOnClickListener(v -> {
            wiDDetailLinearLayout.setVisibility(View.GONE);
            wiDDetailTextInputLayout.setVisibility(View.GONE);
            wiDOtherButtonLinearLayout.setVisibility(View.VISIBLE);
            wiDDetailAddCancelButtonLinearLayout.setVisibility(View.GONE);
        });

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

                showSnackbar("WiD가 삭제되었습니다..");
            });
            builder.setNegativeButton("취소", (dialog, which) -> {
                // Dismiss the dialog
                dialog.dismiss();
            });
            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        wiDBackButton = view.findViewById(R.id.wiDBackButton);

        wiDBackButton.setOnClickListener(v -> {
            wiDLinearLayout.setVisibility(View.GONE);
            clickedWiDId = 0;
            clickedWiD = null;

            headerLinearLayout.setEnabled(true);
            linearLayout.setEnabled(true);
        });

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

                LinearLayout itemLayout = new LinearLayout(getContext());
                itemLayout.setPadding(0, 16, 0, 0);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setGravity(Gravity.CENTER_VERTICAL);
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
//                if (wiD.getDetail().isEmpty()) {
//
//                } else {
//
//                }
                arrowImageView.setImageResource(R.drawable.baseline_keyboard_arrow_right_24);
                arrowImageView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
                itemLayout.addView(arrowImageView);

                itemLayout.setOnClickListener(v -> {
                    headerLinearLayout.setEnabled(false);
                    linearLayout.setEnabled(false);

                    wiDLinearLayout.setVisibility(View.VISIBLE);
                    clickedWiDId = (Long) itemLayout.getTag();
                    clickedWiD = wiDDatabaseHelper.getWiDById(clickedWiDId);

                    wiDTitleTextView.setText(DataMaps.getTitleMap(getContext()).get(clickedWiD.getTitle()));
                    wiDDateTextView.setText(clickedWiD.getDate().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 (")));

                    String wiDKoreanDayOfWeek = DataMaps.getDayOfWeekMap().get(clickedWiD.getDate().getDayOfWeek());
                    wiDDayOfWeekTextView.setText(wiDKoreanDayOfWeek);

                    if (clickedWiD.getDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
                        wiDDayOfWeekTextView.setTextColor(Color.BLUE);
                    } else if (clickedWiD.getDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
                        wiDDayOfWeekTextView.setTextColor(Color.RED);
                    } else {
                        wiDDayOfWeekTextView.setTextColor(Color.BLACK);
                    }

                    wiDParenthesisTextView.setText(")");
                    wiDStartTimeTextView.setText(clickedWiD.getStart().format(DateTimeFormatter.ofPattern("HH시 mm분 ss초")));
                    wiDFinishTimeTextView.setText(clickedWiD.getFinish().format(DateTimeFormatter.ofPattern("HH시 mm분 ss초")));

                    long clickedWiDHours = clickedWiD.getDuration().toHours();
                    long clickedWiDMinutes = (clickedWiD.getDuration().toMinutes() % 60);
                    String clickedWiDDurationText;

                    if (clickedWiDHours > 0 && clickedWiDMinutes == 0) {
                        clickedWiDDurationText = String.format("%d시간", clickedWiDHours);
                    } else if (clickedWiDHours > 0) {
                        clickedWiDDurationText = String.format("%d시간 %d분", clickedWiDHours, clickedWiDMinutes);
                    } else {
                        clickedWiDDurationText = String.format("%d분", clickedWiDMinutes);
                    }

                    wiDDurationTextView.setText(clickedWiDDurationText);

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
}