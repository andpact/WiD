package andpact.project.wid.fragment;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import andpact.project.wid.R;
import andpact.project.wid.Title;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDCreateFragment extends Fragment {

    // Fields referencing text views and buttons for each Title
    private TextView studyDurationTextView;
    private MaterialButton studyStartButton;
    private MaterialButton studyFinishButton;

    private TextView workDurationTextView;
    private MaterialButton workStartButton;
    private MaterialButton workFinishButton;

    private TextView readingDurationTextView;
    private MaterialButton readingStartButton;
    private MaterialButton readingFinishButton;

    private TextView exerciseDurationTextView;
    private MaterialButton exerciseStartButton;
    private MaterialButton exerciseFinishButton;

    private TextView sleepDurationTextView;
    private MaterialButton sleepStartButton;
    private MaterialButton sleepFinishButton;

    private TextView travelDurationTextView;
    private MaterialButton travelStartButton;
    private MaterialButton travelFinishButton;

    private TextView hobbyDurationTextView;
    private MaterialButton hobbyStartButton;
    private MaterialButton hobbyFinishButton;

    private TextView otherDurationTextView;
    private MaterialButton otherStartButton;
    private MaterialButton otherFinishButton;
    private WiD currentWiD;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_create, container, false);

        // Assigning views based on Title
        studyDurationTextView = view.findViewById(R.id.studyDurationTextView);
        studyStartButton = view.findViewById(R.id.studyStartButton);
        studyFinishButton = view.findViewById(R.id.studyFinishButton);

        workDurationTextView = view.findViewById(R.id.workDurationTextView);
        workStartButton = view.findViewById(R.id.workStartButton);
        workFinishButton = view.findViewById(R.id.workFinishButton);

        readingDurationTextView = view.findViewById(R.id.readingDurationTextView);
        readingStartButton = view.findViewById(R.id.readingStartButton);
        readingFinishButton = view.findViewById(R.id.readingFinishButton);

        exerciseDurationTextView = view.findViewById(R.id.exerciseDurationTextView);
        exerciseStartButton = view.findViewById(R.id.exerciseStartButton);
        exerciseFinishButton = view.findViewById(R.id.exerciseFinishButton);

        sleepDurationTextView = view.findViewById(R.id.sleepDurationTextView);
        sleepStartButton = view.findViewById(R.id.sleepStartButton);
        sleepFinishButton = view.findViewById(R.id.sleepFinishButton);

        travelDurationTextView = view.findViewById(R.id.travelDurationTextView);
        travelStartButton = view.findViewById(R.id.travelStartButton);
        travelFinishButton = view.findViewById(R.id.travelFinishButton);

        hobbyDurationTextView = view.findViewById(R.id.hobbyDurationTextView);
        hobbyStartButton = view.findViewById(R.id.hobbyStartButton);
        hobbyFinishButton = view.findViewById(R.id.hobbyFinishButton);

        otherDurationTextView = view.findViewById(R.id.otherDurationTextView);
        otherStartButton = view.findViewById(R.id.otherStartButton);
        otherFinishButton = view.findViewById(R.id.otherFinishButton);

        studyStartButton.setOnClickListener(v -> createWiD(Title.STUDY, studyDurationTextView, studyStartButton, studyFinishButton));
        workStartButton.setOnClickListener(v -> createWiD(Title.WORK, workDurationTextView, workStartButton, workFinishButton));
        readingStartButton.setOnClickListener(v -> createWiD(Title.READING, readingDurationTextView, readingStartButton, readingFinishButton));
        exerciseStartButton.setOnClickListener(v -> createWiD(Title.EXERCISE, exerciseDurationTextView, exerciseStartButton, exerciseFinishButton));
        sleepStartButton.setOnClickListener(v -> createWiD(Title.SLEEP, sleepDurationTextView, sleepStartButton, sleepFinishButton));
        travelStartButton.setOnClickListener(v -> createWiD(Title.TRAVEL, travelDurationTextView, travelStartButton, travelFinishButton));
        hobbyStartButton.setOnClickListener(v -> createWiD(Title.HOBBY, hobbyDurationTextView, hobbyStartButton, hobbyFinishButton));
        otherStartButton.setOnClickListener(v -> createWiD(Title.OTHER, otherDurationTextView, otherStartButton, otherFinishButton));

        studyFinishButton.setOnClickListener(v -> finishWiD(studyDurationTextView, studyStartButton, studyFinishButton));
        workFinishButton.setOnClickListener(v -> finishWiD(workDurationTextView, workStartButton, workFinishButton));
        readingFinishButton.setOnClickListener(v -> finishWiD(readingDurationTextView, readingStartButton, readingFinishButton));
        exerciseFinishButton.setOnClickListener(v -> finishWiD(exerciseDurationTextView, exerciseStartButton, exerciseFinishButton));
        sleepFinishButton.setOnClickListener(v -> finishWiD(sleepDurationTextView, sleepStartButton, sleepFinishButton));
        travelFinishButton.setOnClickListener(v -> finishWiD(travelDurationTextView, travelStartButton, travelFinishButton));
        hobbyFinishButton.setOnClickListener(v -> finishWiD(hobbyDurationTextView, hobbyStartButton, hobbyFinishButton));
        otherFinishButton.setOnClickListener(v -> finishWiD(otherDurationTextView, otherStartButton, otherFinishButton));


        return view;
    }

    private void createWiD(Title title, TextView durationTextView, MaterialButton startButton, MaterialButton finishButton) {

        studyStartButton.setEnabled(false);
//        studyStartButton.setBackgroundColor(Color.GRAY);

        workStartButton.setEnabled(false);
//        workStartButton.setBackgroundColor(Color.GRAY);

        readingStartButton.setEnabled(false);
//        readingStartButton.setBackgroundColor(Color.GRAY);

        exerciseStartButton.setEnabled(false);
//        exerciseStartButton.setBackgroundColor(Color.GRAY);

        sleepStartButton.setEnabled(false);
//        sleepStartButton.setBackgroundColor(Color.GRAY);

        travelStartButton.setEnabled(false);
//        travelStartButton.setBackgroundColor(Color.GRAY);

        hobbyStartButton.setEnabled(false);
//        hobbyStartButton.setBackgroundColor(Color.GRAY);

        otherStartButton.setEnabled(false);
//        otherStartButton.setBackgroundColor(Color.GRAY);

        // Create a new WiD object with the provided title, current date, and start time
        currentWiD = new WiD();
        currentWiD.setTitle(title.name());
        currentWiD.setDate(LocalDate.now());
        currentWiD.setStart(LocalTime.now());

        // Hide the start button and show the finish button
        startButton.setVisibility(View.GONE);
        finishButton.setVisibility(View.VISIBLE);

        // Initialize the stopwatch variables
        long startTime = SystemClock.uptimeMillis();
        TextView stopwatch = durationTextView;

        // Start the stopwatch runnable
        handler.post(new Runnable() {
            @Override
            public void run() {
                long timeMillis = SystemClock.uptimeMillis() - startTime;
                long seconds = (timeMillis / 1000) % 60;
                long minutes = (timeMillis / (1000 * 60)) % 60;
                long hours = (timeMillis / (1000 * 60 * 60)) % 24;

                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                stopwatch.setText(formattedTime);

                handler.postDelayed(this, 1000); // Update every 1 second
            }
        });
    }

    private void finishWiD(TextView durationTextView, MaterialButton startButton, MaterialButton finishButton) {
        if (currentWiD != null) {
            // Assign the finish time to the current WiD object
            currentWiD.setFinish(LocalTime.now());

            // Calculate the duration
            Duration duration = Duration.between(currentWiD.getStart(), currentWiD.getFinish());
            currentWiD.setDuration(duration);

            // Store the WiD object in the database
            WiDDatabaseHelper databaseHelper = new WiDDatabaseHelper(getActivity());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues values = currentWiD.toContentValues();
            db.insert("wid_table", null, values);
            db.close();

            // Reset the current WiD object
            currentWiD = null;

            // Re-enable and restore the original color of other start buttons
            studyStartButton.setEnabled(true);
//            studyStartButton.setBackgroundColor(getResources().getColor(R.color.original_start_button_color));

            workStartButton.setEnabled(true);
//            workStartButton.setBackgroundColor(getResources().getColor(R.color.original_start_button_color));

            readingStartButton.setEnabled(true);
//            readingStartButton.setBackgroundColor(getResources().getColor(R.color.original_start_button_color));

            exerciseStartButton.setEnabled(true);
//            exerciseStartButton.setBackgroundColor(getResources().getColor(R.color.original_start_button_color));

            sleepStartButton.setEnabled(true);
//            sleepStartButton.setBackgroundColor(getResources().getColor(R.color.original_start_button_color));

            travelStartButton.setEnabled(true);
//            travelStartButton.setBackgroundColor(getResources().getColor(R.color.original_start_button_color));

            hobbyStartButton.setEnabled(true);
//            hobbyStartButton.setBackgroundColor(getResources().getColor(R.color.original_start_button_color));

            otherStartButton.setEnabled(true);
//            otherStartButton.setBackgroundColor(getResources().getColor(R.color.original_start_button_color));


            // Stop the stopwatch and set the durationTextView to "00:00:00" after 3 seconds
            handler.postDelayed(() -> {
                // Stop the stopwatch
                handler.removeCallbacksAndMessages(null);

                // Blink the durationTextView three times
                for (int i = 0; i < 3; i++) {
                    handler.postDelayed(() -> {
                        durationTextView.setVisibility(View.INVISIBLE);
                        handler.postDelayed(() -> durationTextView.setVisibility(View.VISIBLE), 500); // Visible after 500 milliseconds
                    }, i * 1000); // Invisible after 1 second, repeated three times
                }

                // Set the durationTextView to "00:00:00" after blinking
                handler.postDelayed(() -> durationTextView.setText(R.string.zero_duration), 3000); // Set after 3 seconds

                // Hide the finish button and show the start button
                finishButton.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
            }, 0); // Start immediately
        }
    }
}