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
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import andpact.project.wid.R;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.DataMaps;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDSearchFragment extends Fragment {
    private SearchView searchView;
    private DateTimeFormatter dateFormatter, timeFormatter, timeFormatter2;
    private LinearLayout wiDLayout, wiDLinearLayout;
    private MaterialTextView wiDDateTextView, wiDTitleTextView, wiDDayOfWeekTextView, wiDStartTimeTextView,
            wiDFinishTimeTextView, wiDDurationTextView, wiDDetailTextView;
    private ImageButton wiDSaveGalleryButton, wiDDeleteButton, wiDCloseButton, editDetailButton, cancelEditDetailButton,
            showEditDetailButton;
    private LinearLayout wiDDetailLinearLayout, showDetailLinearLayout, editDetailLinearLayout;
    private ImageView showDetailLinearLayoutImageView;
    private TextInputEditText detailInputEditText;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private long clickedWiDId;
    private WiD clickedWiD;
    private String searchViewText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        wiDLayout = view.findViewById(R.id.wiDLayout);
        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

        dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.d ");
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeFormatter2 = DateTimeFormatter.ofPattern("HH:mm");

        wiDLinearLayout = view.findViewById(R.id.wiDLinearLayout);

        wiDTitleTextView = view.findViewById(R.id.wiDTitleTextView);
        wiDDateTextView = view.findViewById(R.id.wiDDateTextView);
        wiDDayOfWeekTextView = view.findViewById(R.id.wiDDayOfWeekTextView);
        wiDStartTimeTextView = view.findViewById(R.id.wiDStartTimeTextView);
        wiDFinishTimeTextView = view.findViewById(R.id.wiDFinishTimeTextView);
        wiDDurationTextView = view.findViewById(R.id.wiDDurationTextView);
        wiDDetailTextView = view.findViewById(R.id.wiDDetailTextView);

        wiDDetailLinearLayout = view.findViewById(R.id.wiDDetailLinearLayout);

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

                } else {
                    showDetailLinearLayoutImageView.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
                    wiDDetailLinearLayout.setVisibility(View.VISIBLE);

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

                updateLinearLayout(searchViewText);
                wiDLayout.setVisibility(View.VISIBLE);

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

            wiDLayout.setVisibility(View.VISIBLE);

            wiDDetailLinearLayout.setVisibility(View.GONE);

            showDetailLinearLayoutImageView.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);

            updateLinearLayout(searchViewText);
        });

        // "세부 사항으로 검색해 보세요." 텍스트 뷰 생성 및 설정
        MaterialTextView noContextTextView = new MaterialTextView(getContext());
        noContextTextView.setText("세부 사항으로 WiD를 검색해 보세요.");
        noContextTextView.setGravity(Gravity.CENTER);

        // 리니어 레이아웃에 텍스트 뷰 추가
        wiDLayout.addView(noContextTextView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // 검색어가 비어있을 때, 리니어 레이아웃 초기화
                    wiDLayout.removeAllViews();

                    // 리니어 레이아웃에 텍스트 뷰 추가
                    wiDLayout.addView(noContextTextView);
                } else {
                    searchViewText = newText;
                    updateLinearLayout(searchViewText);
//                    List<WiD> wiDList = wiDDatabaseHelper.getWiDListByDetail(newText);
//
//                    // 기존에 표시되어있던 뷰들을 모두 제거
//                    linearLayout.removeAllViews();
//
//                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//
//                    LocalDate date = null; // LocalDate 변수 초기화
//
//                    int count = 1; // Initialize the counter variable
//
//                    for (WiD wiD : wiDList) {
//                        if (date == null || !date.equals(wiD.getDate())) {
//                            LinearLayout dateLinearLayout = new LinearLayout(getContext());
//                            dateLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//                            dateLinearLayout.setPadding(0, 32, 0, 0);
//                            linearLayout.addView(dateLinearLayout);
//
//                            // 새로운 날짜인 경우에만 dateTextView를 생성하고 추가
//                            date = wiD.getDate();
//
//                            LocalDate yesterday = LocalDate.now().minusDays(1);
//                            LocalDate today = LocalDate.now();
//
//                            MaterialTextView dateTextView = new MaterialTextView(getContext());
//                            if (date.equals(yesterday)) {
//                                dateTextView.setText("어제");
//                                dateLinearLayout.addView(dateTextView);
//                            } else if (date.equals(today)) {
//                                dateTextView.setText("오늘");
//                                dateLinearLayout.addView(dateTextView);
//                            } else {
//                                String formattedDate = date.format(dateFormatter);
//
//                                dateTextView.setText(formattedDate);
//                                dateLinearLayout.addView(dateTextView);
//
//                                MaterialTextView dayOfWeekTextView = new MaterialTextView(getContext());
//                                String koreanDayOfWeek = DataMaps.getDayOfWeekMap().get(date.getDayOfWeek());
//                                dayOfWeekTextView.setText(koreanDayOfWeek);
//                                dateLinearLayout.addView(dayOfWeekTextView);
//
//                                if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
//                                    dayOfWeekTextView.setTextColor(Color.BLUE);
//                                } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
//                                    dayOfWeekTextView.setTextColor(Color.RED);
//                                } else {
//                                    dayOfWeekTextView.setTextColor(Color.BLACK);
//                                }
//                            }
//                        }
//
//                        LinearLayout itemLayout = new LinearLayout(getContext());
//                        itemLayout.setPadding(0, 16, 0, 0);
//                        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
//                        itemLayout.setGravity(Gravity.CENTER_VERTICAL);
//                        itemLayout.setTag(wiD.getId());
//
//                        // Create and add the numberTextView
//                        MaterialTextView numberTextView = new MaterialTextView(getContext());
//                        numberTextView.setText(String.valueOf(count++)); // Set the current count as the text
//                        numberTextView.setTypeface(null, Typeface.BOLD);
//                        numberTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
//                        numberTextView.setGravity(Gravity.CENTER);
//                        itemLayout.addView(numberTextView);
//
//                        // Create and add the title TextView
//                        MaterialTextView titleTextView = new MaterialTextView(getContext());
//                        titleTextView.setText(DataMaps.getTitleMap(getContext()).get(wiD.getTitle()));
//                        titleTextView.setTypeface(null, Typeface.BOLD);
//                        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
//                        titleTextView.setGravity(Gravity.CENTER);
//                        itemLayout.addView(titleTextView);
//
//                        // Create and add the start time TextView
//                        MaterialTextView startTimeTextView = new MaterialTextView(getContext());
//                        startTimeTextView.setText(wiD.getStart().format(timeFormatter2));
//                        startTimeTextView.setTypeface(null, Typeface.BOLD);
//                        startTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f));
//                        startTimeTextView.setGravity(Gravity.CENTER);
//                        itemLayout.addView(startTimeTextView);
//
//                        // Create and add the finish time TextView
//                        MaterialTextView finishTimeTextView = new MaterialTextView(getContext());
//                        finishTimeTextView.setText(wiD.getFinish().format(timeFormatter2));
//                        finishTimeTextView.setTypeface(null, Typeface.BOLD);
//                        finishTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f));
//                        finishTimeTextView.setGravity(Gravity.CENTER);
//                        itemLayout.addView(finishTimeTextView);
//
//                        // Create and add the duration TextView
//                        MaterialTextView durationTextView = new MaterialTextView(getContext());
//                        long hours = wiD.getDuration().toHours();
//                        long minutes = (wiD.getDuration().toMinutes() % 60);
//                        String durationText;
//
//                        if (hours > 0 && minutes == 0) {
//                            durationText = String.format("%d시간", hours);
//                        } else if (hours > 0) {
//                            durationText = String.format("%d시간 %d분", hours, minutes);
//                        } else {
//                            durationText = String.format("%d분", minutes);
//                        }
//
//                        durationTextView.setText(durationText);
//                        durationTextView.setTypeface(null, Typeface.BOLD);
//                        durationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
//                        durationTextView.setGravity(Gravity.CENTER);
//                        itemLayout.addView(durationTextView);
//
//                        ImageView detailImageView = new ImageView(getContext());
//                        if (TextUtils.isEmpty(wiD.getDetail())) {
//                            detailImageView.setImageResource(R.drawable.baseline_more_horiz_24);
//                        } else {
//                            detailImageView.setImageResource(R.drawable.outline_description_24);
//                        }
//                        detailImageView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
//                        itemLayout.addView(detailImageView);
//
//                        itemLayout.setOnClickListener(v -> {
//                            linearLayout.setVisibility(View.GONE);
//
//                            wiDLinearLayout.setVisibility(View.VISIBLE);
//                            clickedWiDId = (Long) itemLayout.getTag();
//                            clickedWiD = wiDDatabaseHelper.getWiDById(clickedWiDId);
//
//                            wiDTitleTextView.setText(DataMaps.getTitleMap(getContext()).get(clickedWiD.getTitle()));
//                            wiDDateTextView.setText(clickedWiD.getDate().format(dateFormatter));
//
//                            String wiDKoreanDayOfWeek = DataMaps.getDayOfWeekMap().get(clickedWiD.getDate().getDayOfWeek());
//                            wiDDayOfWeekTextView.setText(wiDKoreanDayOfWeek);
//
//                            if (clickedWiD.getDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
//                                wiDDayOfWeekTextView.setTextColor(Color.BLUE);
//                            } else if (clickedWiD.getDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
//                                wiDDayOfWeekTextView.setTextColor(Color.RED);
//                            } else {
//                                wiDDayOfWeekTextView.setTextColor(Color.BLACK);
//                            }
//
//                            wiDStartTimeTextView.setText(clickedWiD.getStart().format(timeFormatter));
//                            wiDFinishTimeTextView.setText(clickedWiD.getFinish().format(timeFormatter));
//
//                            long clickedWiDHours = clickedWiD.getDuration().toHours();
//                            long clickedWiDMinutes = (clickedWiD.getDuration().toMinutes() % 60);
//                            long clickedWiDSeconds = (clickedWiD.getDuration().getSeconds() % 60);
//                            String clickedWiDDurationText;
//
//                            if (0 < clickedWiDHours && 0 == clickedWiDMinutes && 0 == clickedWiDSeconds) {
//                                clickedWiDDurationText = String.format("%d시간", clickedWiDHours);
//                            } else if (0 < clickedWiDHours && 0 < clickedWiDMinutes && 0 == clickedWiDSeconds) {
//                                clickedWiDDurationText = String.format("%d시간 %d분", clickedWiDHours, clickedWiDMinutes);
//                            } else if (0 < clickedWiDHours && 0 == clickedWiDMinutes && 0 < clickedWiDSeconds) {
//                                clickedWiDDurationText = String.format("%d시간 %d초", clickedWiDHours, clickedWiDSeconds);
//                            } else if (0 < clickedWiDHours) {
//                                clickedWiDDurationText = String.format("%d시간 %d분 %d초", clickedWiDHours, clickedWiDMinutes, clickedWiDSeconds);
//                            } else if (0 < clickedWiDMinutes && 0 == clickedWiDSeconds) {
//                                clickedWiDDurationText = String.format("%d분", clickedWiDMinutes);
//                            } else if (0 < clickedWiDMinutes) {
//                                clickedWiDDurationText = String.format("%d분 %d초", clickedWiDMinutes, clickedWiDSeconds);
//                            } else {
//                                clickedWiDDurationText = String.format("%d초", clickedWiDSeconds);
//                            }
//
//                            wiDDurationTextView.setText(clickedWiDDurationText);
//
//                            wiDDetailTextView.setText(clickedWiD.getDetail());
//
//                        });
//
//                        itemLayout.setOnClickListener(v -> {
//                            linearLayout.setVisibility(View.GONE);
//
//                            wiDLinearLayout.setVisibility(View.VISIBLE);
//                            clickedWiDId = (Long) itemLayout.getTag();
//                            clickedWiD = wiDDatabaseHelper.getWiDById(clickedWiDId);
//
//                            wiDTitleTextView.setText(DataMaps.getTitleMap(getContext()).get(clickedWiD.getTitle()));
//                            wiDDateTextView.setText(clickedWiD.getDate().format(dateFormatter));
//
//                            String wiDKoreanDayOfWeek = DataMaps.getDayOfWeekMap().get(clickedWiD.getDate().getDayOfWeek());
//                            wiDDayOfWeekTextView.setText(wiDKoreanDayOfWeek);
//
//                            if (clickedWiD.getDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
//                                wiDDayOfWeekTextView.setTextColor(Color.BLUE);
//                            } else if (clickedWiD.getDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
//                                wiDDayOfWeekTextView.setTextColor(Color.RED);
//                            } else {
//                                wiDDayOfWeekTextView.setTextColor(Color.BLACK);
//                            }
//
//                            wiDStartTimeTextView.setText(clickedWiD.getStart().format(timeFormatter));
//                            wiDFinishTimeTextView.setText(clickedWiD.getFinish().format(timeFormatter));
//
//                            long clickedWiDHours = clickedWiD.getDuration().toHours();
//                            long clickedWiDMinutes = (clickedWiD.getDuration().toMinutes() % 60);
//                            long clickedWiDSeconds = (clickedWiD.getDuration().getSeconds() % 60);
//                            String clickedWiDDurationText;
//
//                            if (0 < clickedWiDHours && 0 == clickedWiDMinutes && 0 == clickedWiDSeconds) {
//                                clickedWiDDurationText = String.format("%d시간", clickedWiDHours);
//                            } else if (0 < clickedWiDHours && 0 < clickedWiDMinutes && 0 == clickedWiDSeconds) {
//                                clickedWiDDurationText = String.format("%d시간 %d분", clickedWiDHours, clickedWiDMinutes);
//                            } else if (0 < clickedWiDHours && 0 == clickedWiDMinutes && 0 < clickedWiDSeconds) {
//                                clickedWiDDurationText = String.format("%d시간 %d초", clickedWiDHours, clickedWiDSeconds);
//                            } else if (0 < clickedWiDHours) {
//                                clickedWiDDurationText = String.format("%d시간 %d분 %d초", clickedWiDHours, clickedWiDMinutes, clickedWiDSeconds);
//                            } else if (0 < clickedWiDMinutes && 0 == clickedWiDSeconds) {
//                                clickedWiDDurationText = String.format("%d분", clickedWiDMinutes);
//                            } else if (0 < clickedWiDMinutes) {
//                                clickedWiDDurationText = String.format("%d분 %d초", clickedWiDMinutes, clickedWiDSeconds);
//                            } else {
//                                clickedWiDDurationText = String.format("%d초", clickedWiDSeconds);
//                            }
//
//                            wiDDurationTextView.setText(clickedWiDDurationText);
//
//                            wiDDetailTextView.setText(clickedWiD.getDetail());
//
//                        });
//                        linearLayout.addView(itemLayout);
//                    }
                }
                return false;
            }
        });
        return view;
    }
    private void updateLinearLayout(String newText) {
        List<WiD> wiDList = wiDDatabaseHelper.getWiDListByDetail(newText);

        // 기존에 표시되어있던 뷰들을 모두 제거
        wiDLayout.removeAllViews();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = null; // LocalDate 변수 초기화

        int count = 1; // Initialize the counter variable

        for (WiD wiD : wiDList) {
            if (date == null || !date.equals(wiD.getDate())) {
                LinearLayout dateLinearLayout = new LinearLayout(getContext());
                dateLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                dateLinearLayout.setPadding(0, 32, 0, 0);
                wiDLayout.addView(dateLinearLayout);

                // 새로운 날짜인 경우에만 dateTextView를 생성하고 추가
                date = wiD.getDate();

                LocalDate yesterday = LocalDate.now().minusDays(1);
                LocalDate today = LocalDate.now();

                MaterialTextView dateTextView = new MaterialTextView(getContext());
                if (date.equals(yesterday)) {
                    dateTextView.setText("어제");
                    dateLinearLayout.addView(dateTextView);
                } else if (date.equals(today)) {
                    dateTextView.setText("오늘");
                    dateLinearLayout.addView(dateTextView);
                } else {
                    String formattedDate = date.format(dateFormatter);

                    dateTextView.setText(formattedDate);
                    dateLinearLayout.addView(dateTextView);

                    MaterialTextView dayOfWeekTextView = new MaterialTextView(getContext());
                    String koreanDayOfWeek = DataMaps.getDayOfWeekMap().get(date.getDayOfWeek());
                    dayOfWeekTextView.setText(koreanDayOfWeek);
                    dateLinearLayout.addView(dayOfWeekTextView);

                    if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                        dayOfWeekTextView.setTextColor(Color.BLUE);
                    } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        dayOfWeekTextView.setTextColor(Color.RED);
                    } else {
                        dayOfWeekTextView.setTextColor(Color.BLACK);
                    }
                }
            }

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
                wiDLayout.setVisibility(View.GONE);

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

            itemLayout.setOnClickListener(v -> {
                wiDLayout.setVisibility(View.GONE);

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
            wiDLayout.addView(itemLayout);
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
