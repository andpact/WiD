package andpact.project.wid.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import andpact.project.wid.R;
import andpact.project.wid.Title;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDSearchFragment extends Fragment {
    private SearchView searchView;
    private LinearLayout linearLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private Map<String, String> titleMap;
    private Map<DayOfWeek, String> dayOfWeekMap;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        linearLayout = view.findViewById(R.id.linearLayout);
        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

        // "세부 사항으로 검색해 보세요." 텍스트 뷰 생성 및 설정
        MaterialTextView noContextTextView = new MaterialTextView(getContext());
        noContextTextView.setText("세부 사항으로 WiD를 검색해 보세요.");
        noContextTextView.setGravity(Gravity.CENTER);

        // 리니어 레이아웃에 텍스트 뷰 추가
        linearLayout.addView(noContextTextView);

        titleMap = new HashMap<>();
        titleMap.put(Title.STUDY.toString(), getResources().getString(R.string.title_1));
        titleMap.put(Title.WORK.toString(), getResources().getString(R.string.title_2));
        titleMap.put(Title.READING.toString(), getResources().getString(R.string.title_3));
        titleMap.put(Title.EXERCISE.toString(), getResources().getString(R.string.title_4));
        titleMap.put(Title.SLEEP.toString(), getResources().getString(R.string.title_5));
        titleMap.put(Title.TRAVEL.toString(), getResources().getString(R.string.title_6));
        titleMap.put(Title.HOBBY.toString(), getResources().getString(R.string.title_7));
        titleMap.put(Title.OTHER.toString(), getResources().getString(R.string.title_8));

        dayOfWeekMap = new HashMap<>();
        dayOfWeekMap.put(DayOfWeek.MONDAY, "월");
        dayOfWeekMap.put(DayOfWeek.TUESDAY, "화");
        dayOfWeekMap.put(DayOfWeek.WEDNESDAY, "수");
        dayOfWeekMap.put(DayOfWeek.THURSDAY, "목");
        dayOfWeekMap.put(DayOfWeek.FRIDAY, "금");
        dayOfWeekMap.put(DayOfWeek.SATURDAY, "토");
        dayOfWeekMap.put(DayOfWeek.SUNDAY, "일");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // 검색어가 비어있을 때, 리니어 레이아웃 초기화
                    linearLayout.removeAllViews();

                    // 리니어 레이아웃에 텍스트 뷰 추가
                    linearLayout.addView(noContextTextView);
                } else {
                    List<WiD> wiDList = wiDDatabaseHelper.getWiDListByDetail(newText);

                    // 기존에 표시되어있던 뷰들을 모두 제거
                    linearLayout.removeAllViews();

                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                    LocalDate date = null; // LocalDate 변수 초기화

                    int count = 1; // Initialize the counter variable

                    for (WiD wiD : wiDList) {
                        if (date == null || !date.equals(wiD.getDate())) {
                            LinearLayout dateLinearLayout = new LinearLayout(getContext());
                            dateLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            dateLinearLayout.setPadding(0, 32, 0, 0);
                            linearLayout.addView(dateLinearLayout);

                            // 새로운 날짜인 경우에만 dateTextView를 생성하고 추가
                            date = wiD.getDate();

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (");
                            String formattedDate = date.format(formatter);

                            MaterialTextView dateTextView = new MaterialTextView(getContext());
                            dateTextView.setText(formattedDate);
                            dateLinearLayout.addView(dateTextView);

                            MaterialTextView dayOfWeekTextView = new MaterialTextView(getContext());
                            String koreanDayOfWeek = dayOfWeekMap.get(date.getDayOfWeek());
                            dayOfWeekTextView.setText(koreanDayOfWeek);
                            dateLinearLayout.addView(dayOfWeekTextView);

                            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                                dayOfWeekTextView.setTextColor(Color.BLUE);
                            } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                                dayOfWeekTextView.setTextColor(Color.RED);
                            } else {
                                dayOfWeekTextView.setTextColor(Color.BLACK);
                            }

                            MaterialTextView parenthesisTextView = new MaterialTextView(getContext());
                            parenthesisTextView.setText(")");
                            dateLinearLayout.addView(parenthesisTextView);
                        };

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
                        titleTextView.setText(titleMap.get(wiD.getTitle()));
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

                        // Create and add the "세부사항 입력칸" TextView
                        MaterialTextView detailTextView = new MaterialTextView(getContext());
                        detailTextView.setId(View.generateViewId());
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

                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "WiD의 세부 사항이 수정되었어요.", Snackbar.LENGTH_SHORT);

                                View snackbarView = snackbar.getView();
                                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                                params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 16 * 15);
                                snackbarView.setLayoutParams(params);

                                snackbar.show();
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
                            builder.setMessage("해당 WiD를 삭제하시겠습니까?");
                            builder.setPositiveButton("삭제", (dialog, which) -> {
                                // Get the ID from the mainLayout's tag
                                Long id = (Long) mainLayout.getTag();

                                // Call the deleteWiDById method with the retrieved ID
                                wiDDatabaseHelper.deleteWiDById(id);

                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "WiD가 삭제되었어요.", Snackbar.LENGTH_SHORT);

                                View snackbarView = snackbar.getView();
                                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                                params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 16 * 15);
                                snackbarView.setLayoutParams(params);

                                snackbar.show();
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
                    }
                }
                return false;
            }
        });
        return view;
    }
}
