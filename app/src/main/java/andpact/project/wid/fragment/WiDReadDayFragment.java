package andpact.project.wid.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
    private MaterialTextView dateTextView, dayOfWeekTextView, setDateTodayButton;
    private DateTimeFormatter dateFormatter, timeFormatter, timeFormatter2;
    private LinearLayout dateLayout, totalDurationLayout, totalDurationHolderLayout, wiDLayout, wiDHolderLayout;
    private ShapeableImageView titleColorCircle;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private LocalDate currentDate;
    private ImageButton decreaseDateButton, increaseDateButton, clickedWiDSaveGalleryButton, clickedWiDDeleteButton, clickedWiDCloseButton;
    private MaterialTextView clickedWiDShowEditDetailButton, clickedWiDEditDetailButton;
    private PieChart pieChart;
    private CircleView circleView;
    private LinearLayout clickedWiDLayout, clickedWiDDetailLayout, showClickedWiDetailLayout;
    private MaterialTextView clickedWiDDateTextView, clickedWiDDayOfWeekTextView, clickedWiDTitleTextView, clickedWiDStartTextView,
            clickedWiDFinishTextView, clickedWiDDurationTextView, clickedWiDDetailTextView;
    private ImageView showClickedWiDDetailLayoutImageView;
    private TextInputLayout clickedWiDTextInputLayout;
    private TextInputEditText clickedWiDDetailInputEditText;
    private long clickedWiDId;
    private WiD clickedWiD;
    private Map<String, Duration> totalDurationForDayMap;
    private Map<String, Integer> colorMap;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_read_day, container, false);

        dateTextView = view.findViewById(R.id.dateTextView);
        dayOfWeekTextView = view.findViewById(R.id.dayOfWeekTextView);
        setDateTodayButton = view.findViewById(R.id.setDateTodayButton);
        setDateTodayButton.setOnClickListener(v -> {
            currentDate = LocalDate.now();
            updateWiDLayout();
        });

        dateLayout = view.findViewById(R.id.dateLayout);
        totalDurationLayout = view.findViewById(R.id.totalDurationLayout);
        totalDurationHolderLayout = view.findViewById(R.id.totalDurationHolderLayout);
        wiDLayout = view.findViewById(R.id.wiDLayout);
        wiDHolderLayout = view.findViewById(R.id.wiDHolderLayout);

        titleColorCircle = view.findViewById(R.id.titleColorCircle);

        colorMap = DataMaps.getColorMap(getContext());

        decreaseDateButton = view.findViewById(R.id.decreaseDateButton);
        increaseDateButton = view.findViewById(R.id.increaseDateButton);

        pieChart = view.findViewById(R.id.pieChart);
//        pieChart.setElevation(2);
        pieChart.setUsePercentValues(false); // 상대 값(퍼센트)이 아닌 절대 값 사용
        pieChart.setDrawEntryLabels(false); // 엔트리 라벨 표시 X
        pieChart.getDescription().setEnabled(false); // 설명 비활성화
        pieChart.getLegend().setEnabled(false); // 각주(범례) 표시 X
//        pieChart.setDrawHoleEnabled(false); // 가운데 원 표시 X
        pieChart.setHoleRadius(70); // 가운데 원의 반지름은 큰 원의 70%
        pieChart.setHoleColor(Color.TRANSPARENT);

        // 가운데 텍스트 설정
        pieChart.setDrawCenterText(true);


        // 텍스트 스타일 설정 (옵션)
        pieChart.setCenterTextSize(15f);
//        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
//        pieChart.setCenterTextColor(ContextCompat.getColor(getContext(), R.color.black));

        circleView = view.findViewById(R.id.circleView);

        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

        currentDate = LocalDate.now();
        dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.d ");
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeFormatter2 = DateTimeFormatter.ofPattern("HH:mm");

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

        clickedWiDTextInputLayout = view.findViewById(R.id.clickedWiDTextInputLayout);
        clickedWiDDetailInputEditText = view.findViewById(R.id.clickedWiDDetailInputEditText);
        clickedWiDDetailInputEditText.setGravity(Gravity.NO_GRAVITY);
        clickedWiDDetailInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 이전 텍스트 변경 전에 호출됩니다.
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.length();
                if (100 < currentLength) {
                    clickedWiDTextInputLayout.setError("100자 이하로 작성해주세요.");
                    clickedWiDEditDetailButton.setEnabled(false);
                    clickedWiDEditDetailButton.setAlpha(0.2f);
                } else {
                    clickedWiDTextInputLayout.setError("");
                    clickedWiDEditDetailButton.setEnabled(true);
                    clickedWiDEditDetailButton.setAlpha(1f);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // 텍스트 변경이 완료된 후에 호출됩니다.
            }
        });

        clickedWiDShowEditDetailButton = view.findViewById(R.id.clickedWiDShowEditDetailButton);
        clickedWiDShowEditDetailButton.setOnClickListener(v -> {
            clickedWiDDetailTextView.setVisibility(View.GONE);
            clickedWiDTextInputLayout.setVisibility(View.VISIBLE);
            clickedWiDShowEditDetailButton.setVisibility(View.GONE);
            clickedWiDEditDetailButton.setVisibility(View.VISIBLE);
            clickedWiDDetailInputEditText.setText(clickedWiDDetailTextView.getText());
        });

        clickedWiDEditDetailButton = view.findViewById(R.id.clickedWiDEditDetailButton);
        clickedWiDEditDetailButton.setOnClickListener(v -> {
            clickedWiDDetailTextView.setVisibility(View.VISIBLE);
            clickedWiDTextInputLayout.setVisibility(View.GONE);
            clickedWiDShowEditDetailButton.setVisibility(View.VISIBLE);
            clickedWiDEditDetailButton.setVisibility(View.GONE);

            String newDetail = clickedWiDDetailInputEditText.getText().toString();

            wiDDatabaseHelper.updateWiDDetailById(clickedWiDId, newDetail);

            clickedWiDDetailTextView.setText(newDetail);

            updateWiDLayout();

            showSnackbar("세부 사항이 수정되었습니다.");
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

                updateWiDLayout();

                decreaseDateButton.setEnabled(true);
                increaseDateButton.setEnabled(true);

                dateLayout.setAlpha(1.0f);
                totalDurationLayout.setAlpha(1.0f);
                pieChart.setAlpha(1.0f);
                circleView.setAlpha(1.0f);
                wiDLayout.setAlpha(1.0f);

                showSnackbar("WiD가 삭제되었습니다.");
            });
            builder.setNegativeButton("취소", (dialog, which) -> {
                // Dismiss the dialog
                dialog.dismiss();
            });
            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.getWindow().setWindowAnimations(0); // 애니메이션 없앰.
            dialog.show();
        });

        clickedWiDCloseButton = view.findViewById(R.id.clickedWiDCloseButton);
        clickedWiDCloseButton.setOnClickListener(v -> {
            clickedWiDLayout.setVisibility(View.GONE);
            clickedWiDId = 0;
            clickedWiD = null;

            decreaseDateButton.setEnabled(true);
            increaseDateButton.setEnabled(true);

            dateLayout.setAlpha(1.0f);
            totalDurationLayout.setAlpha(1.0f);
            pieChart.setAlpha(1.0f);
            circleView.setAlpha(1.0f);
            wiDLayout.setAlpha(1.0f);

            for (int i = 0; i < wiDHolderLayout.getChildCount(); i++) {
                View childView = wiDHolderLayout.getChildAt(i);
                if (childView instanceof LinearLayout) {
                    LinearLayout tmpItemLayout = (LinearLayout) childView;
                    tmpItemLayout.setEnabled(true);
                }
            }

            clickedWiDDetailLayout.setVisibility(View.GONE);
            clickedWiDDetailTextView.setVisibility(View.VISIBLE);
            clickedWiDDetailTextView.setText("");
            clickedWiDDetailTextView.setHint("세부 사항 입력..");
            clickedWiDTextInputLayout.setVisibility(View.GONE);
            clickedWiDDetailInputEditText.setText("");
            clickedWiDDetailInputEditText.setHint("세부 사항 입력..");
            clickedWiDShowEditDetailButton.setVisibility(View.VISIBLE);
            clickedWiDEditDetailButton.setVisibility(View.GONE);

            showClickedWiDDetailLayoutImageView.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
        });

        updateWiDLayout();

        return view;
    }
    private void decreaseDate() {
        currentDate = currentDate.minusDays(1);
        updateWiDLayout();
    }
    private void increaseDate() {
        currentDate = currentDate.plusDays(1);
        updateWiDLayout();
    }
    private void updateWiDLayout() {

        LocalDate today = LocalDate.now();
        if (currentDate.equals(today)) {
            increaseDateButton.setEnabled(false);
            increaseDateButton.setAlpha(0.2f);
        } else {
            increaseDateButton.setEnabled(true);
            increaseDateButton.setAlpha(1f);
        }

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
            pieChart.setCenterText("표시할 WiD 없음");
            pieChart.setData(data);
            pieChart.invalidate();

            MaterialTextView noDataTextView = new MaterialTextView(getContext());
            noDataTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            noDataTextView.setText("표시할 WiD가 없어요.");
            noDataTextView.setGravity(Gravity.CENTER);
            wiDHolderLayout.addView(noDataTextView);

            MaterialTextView noTotalDataTextView = new MaterialTextView(getContext());
            noTotalDataTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            noTotalDataTextView.setText("표시할 정보가 없어요.");
            noTotalDataTextView.setGravity(Gravity.CENTER);
            totalDurationHolderLayout.addView(noTotalDataTextView);

        } else {
            pieChart.setCenterText("오후 | 오전");

            entries = new ArrayList<>();

            // 시작 시간 초기화
            int startMinutes = 0;

            int count = 1; // Initialize the counter variable

            for (WiD wiD : wiDList) {

                Duration durationForDay = totalDurationForDayMap.get(wiD.getTitle()).plus(wiD.getDuration());
                totalDurationForDayMap.put(wiD.getTitle(), durationForDay);

                LinearLayout itemLayout = new LinearLayout(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 16, 0, 0); // 위쪽 마진을 16으로 설정
                itemLayout.setLayoutParams(layoutParams);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setGravity(Gravity.CENTER_VERTICAL);
                itemLayout.setBackgroundResource(R.drawable.bg_white);
//                itemLayout.setPadding(0, 16, 0, 16);
                itemLayout.setElevation(4);
                itemLayout.setTag(wiD.getId());

                ShapeableImageView imageView = new ShapeableImageView(getContext());
                imageView.setBackgroundResource(R.drawable.rectangle);
                GradientDrawable wiDDrawable = (GradientDrawable) imageView.getBackground();
                wiDDrawable.setColor(colorMap.get(wiD.getTitle()));
                itemLayout.addView(imageView);

                // Create and add the numberTextView
                MaterialTextView numberTextView = new MaterialTextView(getContext());
                numberTextView.setText(String.valueOf(count++)); // Set the current count as the text
                numberTextView.setTypeface(null, Typeface.BOLD);
                numberTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
                numberTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(numberTextView);

                // Create and add the title TextView
                MaterialTextView titleTextView = new MaterialTextView(getContext());
                titleTextView.setText(DataMaps.getTitleMap(getContext()).get(wiD.getTitle()));
                titleTextView.setTypeface(null, Typeface.BOLD);
                titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
                titleTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(titleTextView);

                // Create and add the start time TextView
                MaterialTextView startTimeTextView = new MaterialTextView(getContext());
                startTimeTextView.setText(wiD.getStart().format(timeFormatter2));
                startTimeTextView.setTypeface(null, Typeface.BOLD);
                startTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f));
                startTimeTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(startTimeTextView);

                // Create and add the finish time TextView
                MaterialTextView finishTimeTextView = new MaterialTextView(getContext());
                finishTimeTextView.setText(wiD.getFinish().format(timeFormatter2));
                finishTimeTextView.setTypeface(null, Typeface.BOLD);
                finishTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f));
                finishTimeTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(finishTimeTextView);

                // Create and add the duration TextView
                MaterialTextView durationTextView = new MaterialTextView(getContext());
                long hours = wiD.getDuration().toHours();
                long minutes = (wiD.getDuration().toMinutes() % 60);
                long seconds = (wiD.getDuration().getSeconds() % 60);
                String formattedDuration;

                if (0 < hours && 0 == minutes && 0 == seconds) {
                    formattedDuration = String.format("%d시간", hours);
                } else if (0 < hours && 0 < minutes && 0 == seconds) {
                    formattedDuration = String.format("%d시간 %d분", hours, minutes);
                } else if (0 < hours && 0 == minutes && 0 < seconds) {
                    formattedDuration = String.format("%d시간 %d초", hours, seconds);
                } else if (0 < hours) {
                    formattedDuration = String.format("%d시간 %d분", hours, minutes);
                } else if (0 < minutes && 0 == seconds) {
                    formattedDuration = String.format("%d분", minutes);
                } else if (0 < minutes) {
                    formattedDuration = String.format("%d분 %d초", minutes, seconds);
                } else {
                    formattedDuration = String.format("%d초", seconds);
                }

                Duration elapsedDuration = wiD.getDuration();
                long totalSeconds = elapsedDuration.getSeconds(); // 총 경과한 초 수

                double percentage = ((double) totalSeconds / (24 * 60 * 60)) * 100; // 일(day) 비율을 퍼센트로 계산
                double roundedPercentage = Math.floor(percentage * 10.0) / 10.0;

                String roundedPercentageText;
                if (roundedPercentage == 0.0) {
                    roundedPercentageText = "0";
                } else {
                    roundedPercentageText = String.valueOf(roundedPercentage);
                }

                durationTextView.setText(formattedDuration);
                durationTextView.append(" (" + roundedPercentageText + ")");
                durationTextView.setTypeface(null, Typeface.BOLD);
                durationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                durationTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(durationTextView);

                MaterialTextView detailTextView = new MaterialTextView(getContext());
                detailTextView.setGravity(Gravity.CENTER);
                detailTextView.setTypeface(null, Typeface.BOLD);
                if (TextUtils.isEmpty(wiD.getDetail())) {
                    detailTextView.setText("0");
                    detailTextView.setTextColor(Color.GRAY);
                } else {
                    detailTextView.setText(String.valueOf(wiD.getDetail().length()));
                    detailTextView.setTextColor(Color.BLACK);
                }
                detailTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
                itemLayout.addView(detailTextView);

                itemLayout.setOnClickListener(v -> {

                    decreaseDateButton.setEnabled(false);
                    increaseDateButton.setEnabled(false);

                    dateLayout.setAlpha(0.2f);
                    totalDurationLayout.setAlpha(0.2f);
                    pieChart.setAlpha(0.2f);
                    circleView.setAlpha(0.2f);
                    wiDLayout.setAlpha(0.2f);

                    for (int i = 0; i < wiDHolderLayout.getChildCount(); i++) {
                        View childView = wiDHolderLayout.getChildAt(i);
                        if (childView instanceof LinearLayout) {
                            LinearLayout tmpItemLayout = (LinearLayout) childView;
                            tmpItemLayout.setEnabled(false);
                        }
                    }

                    clickedWiDLayout.setVisibility(View.VISIBLE);
                    clickedWiDId = (Long) itemLayout.getTag();
                    clickedWiD = wiDDatabaseHelper.getWiDById(clickedWiDId);

                    GradientDrawable clickedWiDDrawable = (GradientDrawable) titleColorCircle.getBackground();
                    clickedWiDDrawable.setColor(colorMap.get(clickedWiD.getTitle()));

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

//                    Duration clickedWiDElapsedDuration = clickedWiD.getDuration();
//                    long clickedWiDTotalSeconds = clickedWiDElapsedDuration.getSeconds(); // 총 경과한 초 수
//
//                    double clickedWiDPercentage = ((double) clickedWiDTotalSeconds / (24 * 60 * 60)) * 100; // 일(day) 비율을 퍼센트로 계산
//                    double clickedWiDRoundedPercentage = Math.floor(clickedWiDPercentage * 10.0) / 10.0;
//
//                    clickedWiDDurationTextView.append(" (" + clickedWiDRoundedPercentage + "%)");

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
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 16, 0, 0); // 위쪽 마진을 16으로 설정
                totalDurationItemLayout.setLayoutParams(layoutParams);
                totalDurationItemLayout.setOrientation(LinearLayout.HORIZONTAL);
                totalDurationItemLayout.setBackgroundResource(R.drawable.bg_white);
//                totalDurationItemLayout.setPadding(0, 16, 0, 16);
                totalDurationItemLayout.setElevation(4);

                ShapeableImageView imageView = new ShapeableImageView(getContext());
                imageView.setBackgroundResource(R.drawable.rectangle);
                GradientDrawable wiDDrawable = (GradientDrawable) imageView.getBackground();
                wiDDrawable.setColor(colorMap.get(key.toString()));
                totalDurationItemLayout.addView(imageView);

                MaterialTextView titleTextView = new MaterialTextView(getContext());
                titleTextView.setText(DataMaps.getTitleMap(getContext()).get(key.toString()));
                titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f));
                titleTextView.setGravity(Gravity.CENTER);
//                titleTextView.setTextSize(20);
                titleTextView.setTypeface(null, Typeface.BOLD);
                totalDurationItemLayout.addView(titleTextView);

                MaterialTextView totalDurationTextView = new MaterialTextView(getContext());
                Duration totalDuration = totalDurationForDayMap.get(key.toString());

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

                Duration elapsedDuration = totalDuration;
                long totalSeconds = elapsedDuration.getSeconds(); // 총 경과한 초 수

                double percentage = ((double) totalSeconds / (24 * 60 * 60)) * 100; // 일(day) 비율을 퍼센트로 계산
                double roundedPercentage = Math.round(percentage * 10.0) / 10.0;

                totalDurationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                totalDurationTextView.setText(totalDurationText);
                totalDurationTextView.append(" (" + roundedPercentage + ")");
//                totalDurationTextView.setTextSize(18);
                totalDurationTextView.setTypeface(null, Typeface.BOLD);
                totalDurationTextView.setGravity(Gravity.CENTER);

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