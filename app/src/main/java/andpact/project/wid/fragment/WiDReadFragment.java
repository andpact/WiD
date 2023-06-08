package andpact.project.wid.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import andpact.project.wid.R;
import andpact.project.wid.Title;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDReadFragment extends Fragment {
    private TextView dateTextView;
    private TextView dayOfWeekTextView;
    private LinearLayout linearLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private LocalDate currentDate;

    private ImageButton leftTriangle;
    private ImageButton rightTriangle;

    private PieChart pieChart;
    private Map<DayOfWeek, String> dayOfWeekMap;
    private HashMap<String, Integer> colorMap;
    private Map<String, String> titleMap;

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
//        pieChart.setDrawHoleEnabled(false); // 가운데 원 표시

        // 가운데 텍스트 설정
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("오후  오전");

        // 텍스트 스타일 설정 (옵션)
        pieChart.setCenterTextSize(15f);
//        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setCenterTextColor(Color.BLACK);

        dayOfWeekMap = new HashMap<>();
        dayOfWeekMap.put(DayOfWeek.MONDAY, " 월");
        dayOfWeekMap.put(DayOfWeek.TUESDAY, " 화");
        dayOfWeekMap.put(DayOfWeek.WEDNESDAY, " 수");
        dayOfWeekMap.put(DayOfWeek.THURSDAY, " 목");
        dayOfWeekMap.put(DayOfWeek.FRIDAY, " 금");
        dayOfWeekMap.put(DayOfWeek.SATURDAY, " 토");
        dayOfWeekMap.put(DayOfWeek.SUNDAY, " 일");

        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

        currentDate = LocalDate.now();
        dateTextView.setText(currentDate.toString());
        String koreanDayOfWeek = dayOfWeekMap.get(currentDate.getDayOfWeek());
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
        titleMap.put(Title.STUDY.toString(), getResources().getString(R.string.title_1));
        titleMap.put(Title.WORK.toString(), getResources().getString(R.string.title_2));
        titleMap.put(Title.READING.toString(), getResources().getString(R.string.title_3));
        titleMap.put(Title.EXERCISE.toString(), getResources().getString(R.string.title_4));
        titleMap.put(Title.SLEEP.toString(), getResources().getString(R.string.title_5));
        titleMap.put(Title.TRAVEL.toString(), getResources().getString(R.string.title_6));
        titleMap.put(Title.HOBBY.toString(), getResources().getString(R.string.title_7));
        titleMap.put(Title.OTHER.toString(), getResources().getString(R.string.title_8));

        updateWiDList();

        return view;
    }

    private void decreaseDate() {
        currentDate = currentDate.minusDays(1);

        dateTextView.setText(currentDate.toString());
        String koreanDayOfWeek = dayOfWeekMap.get(currentDate.getDayOfWeek());
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

        dateTextView.setText(currentDate.toString());
        String koreanDayOfWeek = dayOfWeekMap.get(currentDate.getDayOfWeek());
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

            // Create a LinearLayout for the empty state
            LinearLayout emptyLayout = new LinearLayout(getContext());
            emptyLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            emptyLayout.setGravity(Gravity.CENTER);
            emptyLayout.setOrientation(LinearLayout.VERTICAL);

            // Create and add the image view
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.baseline_event_busy_192);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            emptyLayout.addView(imageView);

            // Create and add the text view
            TextView textView = new TextView(getContext());
            textView.setText("표시할 정보가 없어요!");
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            emptyLayout.addView(textView);

            // Create and add the text view
            TextView textView2 = new TextView(getContext());
            textView2.setText("지금 등록해보세요.");
            textView2.setTypeface(null, Typeface.BOLD);
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            textView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            emptyLayout.addView(textView2);

            // Add the empty state layout to the main linear layout
            linearLayout.addView(emptyLayout);
        } else {
            // WiD 리스트를 텍스트 뷰에 표현하기
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            entries = new ArrayList<>();

            // 시작 시간 초기화
            int startMinutes = 0;

            for (WiD wiD : wiDList) {

                LinearLayout itemLayout = new LinearLayout(getContext());
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setGravity(Gravity.CENTER_VERTICAL);

                // Create and add the title TextView
                TextView titleTextView = new TextView(getContext());
                titleTextView.setText(titleMap.get(wiD.getTitle()));
                titleTextView.setTypeface(null, Typeface.BOLD);
                titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                titleTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(titleTextView);

                // Create and add the start time TextView
                TextView startTimeTextView = new TextView(getContext());
                startTimeTextView.setText(wiD.getStart().format(timeFormatter));
                startTimeTextView.setTypeface(null, Typeface.BOLD);
                startTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                startTimeTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(startTimeTextView);

                // Create and add the tilde TextView
                TextView tildeTextView = new TextView(getContext());
                tildeTextView.setText("~");
                tildeTextView.setTypeface(null, Typeface.BOLD);
                LinearLayout.LayoutParams tildeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tildeLayoutParams.weight = 0;
                tildeTextView.setLayoutParams(tildeLayoutParams);
                tildeTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(tildeTextView);

                // Create and add the finish time TextView
                TextView finishTimeTextView = new TextView(getContext());
                finishTimeTextView.setText(wiD.getFinish().format(timeFormatter));
                finishTimeTextView.setTypeface(null, Typeface.BOLD);
                finishTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                finishTimeTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(finishTimeTextView);

                // Create and add the duration TextView
                TextView durationTextView = new TextView(getContext());
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
                durationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                durationTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(durationTextView);

                // Create and add the image button
                ImageButton imageButton = new ImageButton(getContext());
                imageButton.setImageResource(R.drawable.baseline_edit_24);
                imageButton.setBackground(null);
                imageButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                itemLayout.addView(imageButton);
                imageButton.setTag(wiD.getId());

                imageButton.setOnClickListener(v -> {
                    // Retrieve the WiD id from the image button's tag
                    Long widId = (Long) v.getTag();

                    // Retrieve the WiD object by its ID
                    WiD clickedWiD = wiDDatabaseHelper.getWiDById(widId);

                    if (clickedWiD != null) {
                        // Create and configure the dialog
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

                        // Set the date as the dialog's title with center alignment and bold text
                        TextView dialogTitle = new TextView(getContext());
                        dialogTitle.setText(R.string.app_name);
                        dialogTitle.setTextSize(20);
                        dialogTitle.setGravity(Gravity.CENTER);
                        dialogTitle.setTypeface(null, Typeface.BOLD);
                        dialogTitle.setPadding(0, 16, 0, 8);
                        builder.setCustomTitle(dialogTitle);

                        // Create a custom layout for the dialog
                        LinearLayout customLayout = new LinearLayout(getContext());
                        customLayout.setOrientation(LinearLayout.VERTICAL);
                        customLayout.setPadding(32, 16, 32, 16);

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

                        // Create and configure the TextView for displaying start, finish, title, and duration
                        TextView infoTextView = new TextView(getContext());
                        String start = clickedWiD.getStart().format(timeFormatter);
                        String finish = clickedWiD.getFinish().format(timeFormatter);
                        String title = clickedWiD.getTitle();
                        String boldInfoText = String.format("<b><font size='20sp'>%s</font></b>부터<b><font size='20sp'>%s</font></b>까지<br><b><font size='20sp'>%s</font></b>동안<b><font size='20sp'>%s</font></b>을 했습니다.<br>아래에 더 자세히 기록해 보세요.", start, finish, clickedWiDDurationText, title);
                        infoTextView.setText(Html.fromHtml(boldInfoText, Html.FROM_HTML_MODE_LEGACY));
                        infoTextView.setGravity(Gravity.CENTER);
                        customLayout.addView(infoTextView);

                        // Create a TextView for displaying the detail
                        TextView detailLabelTextView = new TextView(getContext());
                        detailLabelTextView.setText("세부 사항");
                        detailLabelTextView.setGravity(Gravity.CENTER);
                        detailLabelTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        customLayout.addView(detailLabelTextView);

                        // Create a TextView for displaying the detail
                        TextView detailTextView = new TextView(getContext());
                        detailTextView.setText(clickedWiD.getDetail());
                        customLayout.addView(detailTextView);

                        // Create an EditText for editing the detail
                        EditText detailEditText = new EditText(getContext());
                        detailEditText.setVisibility(View.GONE); // Initially hide the EditText
                        customLayout.addView(detailEditText);

                        // Set the custom layout for the dialog
                        builder.setView(customLayout);

                        // Handle the detailTextView click to show the detailEditText
                        detailTextView.setOnClickListener(view -> {
                            detailTextView.setVisibility(View.GONE);
                            detailEditText.setVisibility(View.VISIBLE);
                            detailEditText.setText(clickedWiD.getDetail());
                        });

                        builder.setPositiveButton("수정", (dialog, which) -> {
                            String newDetail = detailEditText.getText().toString();
                            clickedWiD.setDetail(newDetail);
                            detailTextView.setText(newDetail);

                            // Update the detail in the database
                            wiDDatabaseHelper.updateWiDDetailById(widId, newDetail);

                            Toast.makeText(getContext(), "세부 사항이 수정되었습니다.", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                        });

                        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

                        // Show the dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        // Adjust the dialog width (optional)
                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        layoutParams.copyFrom(dialog.getWindow().getAttributes());
                        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.getWindow().setAttributes(layoutParams);
                    }
                });

                // Create and add the image button
                ImageButton imageButton2 = new ImageButton(getContext());
                imageButton2.setImageResource(R.drawable.baseline_cancel_24);
                imageButton2.setBackground(null);
                imageButton2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                itemLayout.addView(imageButton2);
                imageButton2.setTag(wiD.getId());

                imageButton2.setOnClickListener(v -> {
                    // Retrieve the WiD id from the image button's tag
                    Long widId = (Long) v.getTag();
                    // Call the deleteWiDById method from WiDDatabaseHelper to delete the WiD object
                    wiDDatabaseHelper.deleteWiDById(widId);
                    // Remove the corresponding item layout from the parent LinearLayout
                    linearLayout.removeView((View) v.getParent());
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
                    .map(entry -> colorMap.getOrDefault(entry.getLabel(), Color.LTGRAY))
                    .collect(Collectors.toList()));

            // 파이 데이터셋 생성
            data = new PieData(dataSet);
            data.setDrawValues(false); // 엔트리 값 표시 X

            // 파이 차트에 데이터 설정
            pieChart.setData(data);
            pieChart.invalidate(); // 차트 갱신
        }
    }
}