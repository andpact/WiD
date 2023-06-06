package andpact.project.wid.fragment;

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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.format.DateTimeFormatter;
import java.util.List;

import andpact.project.wid.R;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDSearchFragment extends Fragment {
    private SearchView searchView;
    private LinearLayout linearLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        linearLayout = view.findViewById(R.id.linearLayout);
        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());

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
                } else {
                    List<WiD> wiDList = wiDDatabaseHelper.getWiDListByDetail(newText);

                    // 기존에 표시되어있던 뷰들을 모두 제거
                    linearLayout.removeAllViews();

                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    for (WiD wiD : wiDList) {
                        LinearLayout itemLayout = new LinearLayout(getContext());
                        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                        itemLayout.setGravity(Gravity.CENTER_VERTICAL);

                        // Create and add the title TextView
                        TextView titleTextView = new TextView(getContext());
                        titleTextView.setText(wiD.getTitle());
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

                                builder.setPositiveButton("Update", (dialog, which) -> {
                                    String newDetail = detailEditText.getText().toString();
                                    clickedWiD.setDetail(newDetail);
                                    detailTextView.setText(newDetail);

                                    // Update the detail in the database
                                    wiDDatabaseHelper.updateWiDDetailById(widId, newDetail);

                                    Toast.makeText(getContext(), "Detail updated successfully", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                });

                                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

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
                    }

                }

                return false;
            }
        });


        return view;
    }
}
