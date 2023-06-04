package andpact.project.wid.fragment;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import andpact.project.wid.R;
import andpact.project.wid.model.WiD;
import andpact.project.wid.service.WiDService;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDReadFragment extends Fragment {
    private TextView dateTextView;
    private LinearLayout linearLayout;
    private WiDDatabaseHelper wiDDatabaseHelper;
    private LocalDate currentDate;

    private ImageButton leftTriangle;
    private ImageButton rightTriangle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_read, container, false);
        dateTextView = view.findViewById(R.id.dateTextView);
        linearLayout = view.findViewById(R.id.linearLayout);

        leftTriangle = view.findViewById(R.id.leftTriangle);
        rightTriangle = view.findViewById(R.id.rightTriangle);

        wiDDatabaseHelper = new WiDDatabaseHelper(getContext());
        currentDate = LocalDate.now();

        dateTextView.setText(currentDate.toString());

        updateWiDList();

        leftTriangle.setOnClickListener(v -> decreaseDate());
        rightTriangle.setOnClickListener(v -> increaseDate());

        return view;
    }

    private void decreaseDate() {
        currentDate = currentDate.minusDays(1);
        dateTextView.setText(currentDate.toString());
        updateWiDList();
    }

    private void increaseDate() {
        currentDate = currentDate.plusDays(1);
        dateTextView.setText(currentDate.toString());
        updateWiDList();
    }

    private void updateWiDList() {
        linearLayout.removeAllViews();
        List<WiD> wiDList = wiDDatabaseHelper.getWiDByDate(currentDate.toString());

        if (wiDList.isEmpty()) {

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
            textView.setText("No data");
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            emptyLayout.addView(textView);

            // Create and add the text view
            TextView textView2 = new TextView(getContext());
            textView2.setText("Register now");
            textView2.setTypeface(null, Typeface.BOLD);
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            textView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            emptyLayout.addView(textView2);

            // Add the empty state layout to the main linear layout
            linearLayout.addView(emptyLayout);
        } else {
            // WiD 리스트를 텍스트 뷰에 표현하기
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
                long seconds = (wiD.getDuration().getSeconds() % 60);
                String durationText;

                if (hours > 0 && minutes == 0) {
                    durationText = String.format("%d시간", hours);
                } else if (hours > 0) {
                    durationText = String.format("%d시간 %d분", hours, minutes);
                } else if (minutes > 0) {
                    durationText = String.format("%d분", minutes);
                } else {
                    durationText = String.format("%d초", seconds);
                }

                durationTextView.setText(durationText);
                durationTextView.setTypeface(null, Typeface.BOLD);
                durationTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                durationTextView.setGravity(Gravity.CENTER);
                itemLayout.addView(durationTextView);

                // Create and add the image button
                ImageButton imageButton = new ImageButton(getContext());
                imageButton.setImageResource(R.drawable.baseline_more_horiz_24);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Update Detail");

                        // Add an input field for the "detail" field
                        EditText detailEditText = new EditText(getContext());
                        detailEditText.setHint(clickedWiD.getDetail() == null ? "Enter detail" : clickedWiD.getDetail());
                        builder.setView(detailEditText);

                        // Display the fields of the clickedWiD object in the dialog
                        builder.setMessage("Title: " + clickedWiD.getTitle() + "\n" +
                                "Start: " + clickedWiD.getStart().format(timeFormatter) + "\n" +
                                "Finish: " + clickedWiD.getFinish().format(timeFormatter) + "\n" +
                                "Duration: " + clickedWiD.getDuration().toString());

                        builder.setPositiveButton("Update", (dialog, which) -> {
                            // Retrieve the new detail value entered by the user
                            String newDetail = detailEditText.getText().toString();

                            // Call the updateWiDDetailById method from WiDDatabaseHelper to update the WiD object
                            wiDDatabaseHelper.updateWiDDetailById(widId, newDetail);

                            // Update the clickedWiD object's detail field
                            clickedWiD.setDetail(newDetail);

                            // Display a toast message to indicate successful update
                            Toast.makeText(getContext(), "Detail updated successfully", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                        });

                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                        // Show the dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
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
    }
}