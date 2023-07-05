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
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import andpact.project.wid.R;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.DataMaps;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDSearchFragment extends Fragment {
    private SearchView searchView;
    private MaterialTextView wiDCountTextView;
    private DateTimeFormatter dateFormatter, timeFormatter, timeFormatter2;
    private LinearLayout wiDLayout;
    private ShapeableImageView titleColorCircle;
    private LinearLayout clickedWiDLayout, clickedWiDDetailLayout, showClickedWiDetailLayout;
    private MaterialTextView clickedWiDDateTextView, clickedWiDDayOfWeekTextView, clickedWiDTitleTextView, clickedWiDStartTextView,
            clickedWiDFinishTextView, clickedWiDDurationTextView, clickedWiDDetailTextView;
    private ImageView showClickedWiDDetailLayoutImageView;
    private MaterialTextView clickedWiDShowEditDetailButton, clickedWiDEditDetailButton;
    private TextInputLayout clickedWiDTextInputLayout;
    private TextInputEditText clickedWiDDetailInputEditText;
    private ImageButton clickedWiDSaveGalleryButton, clickedWiDDeleteButton, clickedWiDCloseButton;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private long clickedWiDId;
    private WiD clickedWiD;
    private String searchViewText;
    private Map<String, Integer> colorMap;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_search, container, false);

        searchView = view.findViewById(R.id.searchView);

        wiDCountTextView = view.findViewById(R.id.wiDCountTextView);

        wiDLayout = view.findViewById(R.id.wiDLayout);
        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

        dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.d ");
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeFormatter2 = DateTimeFormatter.ofPattern("HH:mm");

        titleColorCircle = view.findViewById(R.id.titleColorCircle);

        colorMap = DataMaps.getColorMap(getContext());

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

            updateWiDLayout(searchViewText);

            showSnackbar("세부 사항이 수정되었습니다.");
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

                updateWiDLayout(searchViewText);

                searchView.setAlpha(1f);
                searchView.setEnabled(true);

                wiDLayout.setAlpha(1.0f);
                wiDLayout.setEnabled(true);

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

            searchView.setAlpha(1f);
            searchView.setEnabled(true);

            wiDLayout.setAlpha(1.0f);
            wiDLayout.setEnabled(true);

            for (int i = 0; i < wiDLayout.getChildCount(); i++) {
                View childView = wiDLayout.getChildAt(i);
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

        // "세부 사항으로 검색해 보세요." 텍스트 뷰 생성 및 설정
        MaterialTextView noContextTextView = new MaterialTextView(getContext());
        noContextTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
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
                    wiDCountTextView.setVisibility(View.INVISIBLE);

                    wiDLayout.removeAllViews();
                    wiDLayout.addView(noContextTextView);
                } else {
                    searchViewText = newText;
                    updateWiDLayout(searchViewText);
                }
                return false;
            }
        });
        return view;
    }
    private void updateWiDLayout(String newText) {
        List<WiD> wiDList = wiDDatabaseHelper.getWiDListByDetail(newText);

        wiDCountTextView.setVisibility(View.VISIBLE);
        wiDCountTextView.setText("검색 결과 " + wiDList.size() + "개");

        wiDLayout.removeAllViews();

        LocalDate date = null; // LocalDate 변수 초기화

        for (WiD wiD : wiDList) {
            if (date == null || !date.equals(wiD.getDate())) {
                LinearLayout dateLayout = new LinearLayout(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 16, 0, 0);
                dateLayout.setLayoutParams(layoutParams);
                dateLayout.setOrientation(LinearLayout.HORIZONTAL);
                wiDLayout.addView(dateLayout);

                // 새로운 날짜인 경우에만 dateTextView를 생성하고 추가
                date = wiD.getDate();

                LocalDate yesterday = LocalDate.now().minusDays(1);
                LocalDate today = LocalDate.now();

                MaterialTextView dateTextView = new MaterialTextView(getContext());
                if (date.equals(yesterday)) {
                    dateTextView.setText("어제");
                    dateLayout.addView(dateTextView);
                } else if (date.equals(today)) {
                    dateTextView.setText("오늘");
                    dateLayout.addView(dateTextView);
                } else {
                    String formattedDate = date.format(dateFormatter);

                    dateTextView.setText(formattedDate);
                    dateLayout.addView(dateTextView);

                    MaterialTextView dayOfWeekTextView = new MaterialTextView(getContext());
                    String koreanDayOfWeek = DataMaps.getDayOfWeekMap().get(date.getDayOfWeek());
                    dayOfWeekTextView.setText(koreanDayOfWeek);
                    dateLayout.addView(dayOfWeekTextView);

                    if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                        dayOfWeekTextView.setTextColor(Color.BLUE);
                    } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        dayOfWeekTextView.setTextColor(Color.RED);
                    } else {
                        dayOfWeekTextView.setTextColor(Color.BLACK);
                    }
                }
            }

            LinearLayout itemHolderLayout = new LinearLayout(getContext());
            itemHolderLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 16, 0, 0);
            itemHolderLayout.setLayoutParams(layoutParams);
            itemHolderLayout.setBackgroundResource(R.drawable.bg_white);
            itemHolderLayout.setElevation(4);
            itemHolderLayout.setTag(wiD.getId());

            LinearLayout itemLayout = new LinearLayout(getContext());
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setGravity(Gravity.CENTER_VERTICAL);

            MaterialTextView titleTextView = new MaterialTextView(getContext());
            titleTextView.setText(DataMaps.getTitleMap(getContext()).get(wiD.getTitle()));
            titleTextView.setTypeface(null, Typeface.BOLD);
            titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            titleTextView.setGravity(Gravity.CENTER);
            itemLayout.addView(titleTextView);

            MaterialTextView startTimeTextView = new MaterialTextView(getContext());
            startTimeTextView.setText(wiD.getStart().format(timeFormatter2));
            startTimeTextView.setTypeface(null, Typeface.BOLD);
            startTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            startTimeTextView.setGravity(Gravity.CENTER);
            itemLayout.addView(startTimeTextView);

            MaterialTextView finishTimeTextView = new MaterialTextView(getContext());
            finishTimeTextView.setText(wiD.getFinish().format(timeFormatter2));
            finishTimeTextView.setTypeface(null, Typeface.BOLD);
            finishTimeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            finishTimeTextView.setGravity(Gravity.CENTER);
            itemLayout.addView(finishTimeTextView);

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

            durationTextView.setText(formattedDuration);
            durationTextView.setTypeface(null, Typeface.BOLD);
            durationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            durationTextView.setGravity(Gravity.CENTER);
            itemLayout.addView(durationTextView);

            LinearLayout itemLayout2 = new LinearLayout(getContext());
            itemLayout2.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams itemLayout2LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 16, 0, 0);
            itemLayout2.setLayoutParams(itemLayout2LayoutParams);

            MaterialTextView detailText = new MaterialTextView(getContext());
            detailText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            detailText.setTypeface(null, Typeface.BOLD);
            detailText.setText("세부 사항");

            MaterialTextView detailTextView = new MaterialTextView(getContext());
            detailTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            detailTextView.setPadding(16, 0, 0, 0);
            detailTextView.setText(wiD.getDetail());
            detailTextView.setMaxLines(3);

            itemLayout2.addView(detailText);
            itemLayout2.addView(detailTextView);

            itemHolderLayout.addView(itemLayout);
            itemHolderLayout.addView(itemLayout2);
            itemHolderLayout.setOnClickListener(v -> {

                searchView.setAlpha(0.2f);
                searchView.setEnabled(false);

                wiDLayout.setAlpha(0.2f);
                wiDLayout.setEnabled(false);

                for (int i = 0; i < wiDLayout.getChildCount(); i++) {
                    View childView = wiDLayout.getChildAt(i);
                    if (childView instanceof LinearLayout) {
                        LinearLayout tmpItemLayout = (LinearLayout) childView;
                        tmpItemLayout.setEnabled(false);
                    }
                }

                clickedWiDLayout.setVisibility(View.VISIBLE);
                clickedWiDId = (Long) itemHolderLayout.getTag();
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

                clickedWiDDetailTextView.setText(clickedWiD.getDetail());
            });
            wiDLayout.addView(itemHolderLayout);
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