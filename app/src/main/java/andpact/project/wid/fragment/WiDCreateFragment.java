package andpact.project.wid.fragment;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import andpact.project.wid.R;
import andpact.project.wid.Title;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDCreateFragment extends Fragment {
    private MaterialTextView studyTextView;
    private MaterialTextView workTextView;
    private MaterialTextView readingTextView;
    private MaterialTextView exerciseTextView;
    private MaterialTextView sleepTextView;
    private MaterialTextView travelTextView;
    private MaterialTextView hobbyTextView;
    private MaterialTextView otherTextView;
    private MaterialTextView dateTextView;
    private MaterialTextView dayOfWeekTextView;
    private MaterialTextView parenthesisTextView;
    private MaterialTextView startTimeTextView;
    private MaterialTextView finishTimeTextView;
    private MaterialTextView durationTextView;
    private MaterialButton startButton;
    private MaterialButton finishButton;
    private MaterialButton resetButton;
    private Map<DayOfWeek, String> dayOfWeekMap;
    private Map<String, String> titleMap;
    private String clickedTitle;
    private WiD wiD;
    private LocalDate currentDate = LocalDate.now();
    private LocalTime currentTime;
    private Handler startHandler;
    private Runnable startTimeRunnable;
    private Handler finishHandler;
    private Runnable finishTimeRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_create, container, false);

        studyTextView = view.findViewById(R.id.studyTextView);
        workTextView = view.findViewById(R.id.workTextView);
        readingTextView = view.findViewById(R.id.readingTextView);
        exerciseTextView = view.findViewById(R.id.exerciseTextView);
        sleepTextView = view.findViewById(R.id.sleepTextView);
        travelTextView = view.findViewById(R.id.travelTextView);
        hobbyTextView = view.findViewById(R.id.hobbyTextView);
        otherTextView = view.findViewById(R.id.otherTextView);

        dateTextView = view.findViewById(R.id.dateTextView);
        dayOfWeekTextView = view.findViewById(R.id.dayOfWeekTextView);
        parenthesisTextView = view.findViewById(R.id.parenthesisTextView);
        startTimeTextView = view.findViewById(R.id.startTimeTextView);
        finishTimeTextView = view.findViewById(R.id.finishTimeTextView);
        finishTimeTextView.setTextColor(Color.LTGRAY); // 시작 전 회색으로
        durationTextView = view.findViewById(R.id.durationTextView);

        dayOfWeekMap = new HashMap<>();
        dayOfWeekMap.put(DayOfWeek.MONDAY, "월");
        dayOfWeekMap.put(DayOfWeek.TUESDAY, "화");
        dayOfWeekMap.put(DayOfWeek.WEDNESDAY, "수");
        dayOfWeekMap.put(DayOfWeek.THURSDAY, "목");
        dayOfWeekMap.put(DayOfWeek.FRIDAY, "금");
        dayOfWeekMap.put(DayOfWeek.SATURDAY, "토");
        dayOfWeekMap.put(DayOfWeek.SUNDAY, "일");

        titleMap = new HashMap<>();
        titleMap.put(Title.STUDY.toString(), getString(R.string.title_1));
        titleMap.put(Title.WORK.toString(), getString(R.string.title_2));
        titleMap.put(Title.READING.toString(), getString(R.string.title_3));
        titleMap.put(Title.EXERCISE.toString(), getString(R.string.title_4));
        titleMap.put(Title.SLEEP.toString(), getString(R.string.title_5));
        titleMap.put(Title.TRAVEL.toString(), getString(R.string.title_6));
        titleMap.put(Title.HOBBY.toString(), getString(R.string.title_7));
        titleMap.put(Title.OTHER.toString(), getString(R.string.title_8));

        // 현재 날짜를 표시
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 ("));
        dateTextView.setText(formattedDate);
        String koreanDayOfWeek = dayOfWeekMap.get(currentDate.getDayOfWeek());
        dayOfWeekTextView.setText(koreanDayOfWeek);

        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            dayOfWeekTextView.setTextColor(Color.BLUE);
        } else if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dayOfWeekTextView.setTextColor(Color.RED);
        } else {
            dayOfWeekTextView.setTextColor(Color.BLACK);
        }

        parenthesisTextView.setText(")");

        // 현재 시간을 표시하고 시간이 흐르는 것을 업데이트
        startHandler = new Handler();
        startTimeRunnable = new Runnable() {
            @Override
            public void run() {
                currentTime = LocalTime.now(); // 시간이 1초 단위로 계속 현재 시간으로 할당됨.
                String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH시 mm분 ss초"));
                startTimeTextView.setText(formattedTime);
                finishTimeTextView.setText(startTimeTextView.getText()); // 시작시간의 텍스트만 가져옴. 러너블 업이

                startHandler.postDelayed(this, 1000); // 1초마다 업데이트
            }
        };
        startHandler.postDelayed(startTimeRunnable, 0);

        startButton = view.findViewById(R.id.startButton);
        finishButton = view.findViewById(R.id.finishButton);
        resetButton = view.findViewById(R.id.resetButton);

        resetButton.setOnClickListener(v -> {
            startButton.setVisibility(View.VISIBLE);
            startButton.setBackgroundColor(Color.LTGRAY);
            startButton.setEnabled(false);
            resetButton.setVisibility(View.GONE);
            startHandler.postDelayed(startTimeRunnable, 0);
            startTimeTextView.setTextColor(Color.BLACK);
            durationTextView.setText("0초");
            clickedTitle = null;
        });

        studyTextView.setOnClickListener(v -> setTextViewClickLogic(Title.STUDY));
        workTextView.setOnClickListener(v -> setTextViewClickLogic(Title.WORK));
        readingTextView.setOnClickListener(v -> setTextViewClickLogic(Title.READING));
        exerciseTextView.setOnClickListener(v -> setTextViewClickLogic(Title.EXERCISE));
        sleepTextView.setOnClickListener(v -> setTextViewClickLogic(Title.SLEEP));
        travelTextView.setOnClickListener(v -> setTextViewClickLogic(Title.TRAVEL));
        hobbyTextView.setOnClickListener(v -> setTextViewClickLogic(Title.HOBBY));
        otherTextView.setOnClickListener(v -> setTextViewClickLogic(Title.OTHER));

        startButton.setOnClickListener(v -> startWiD());
        startButton.setEnabled(false);
        startButton.setBackgroundColor(Color.LTGRAY);
        finishButton.setOnClickListener(v -> finishWiD());

        return view;
    }
    private void startWiD() {
        wiD = new WiD();
        wiD.setTitle(clickedTitle);
        wiD.setDate(currentDate);

        wiD.setStart(currentTime);

        startTimeTextView.setTextColor(Color.LTGRAY);
        finishTimeTextView.setTextColor(Color.BLACK);
        durationTextView.setTextColor(Color.BLACK);

        startButton.setVisibility(View.GONE);
        finishButton.setVisibility(View.VISIBLE);

        // 시작 시간 흐름 멈추기.
        startHandler.removeCallbacks(startTimeRunnable);

        // Start updating the duration TextView
        updateDurationTextView();
    }
    private void updateDurationTextView() {
        finishHandler = new Handler();
        finishTimeRunnable = new Runnable() {
            @Override
            public void run() {
                // Calculate the elapsed time
                currentTime = LocalTime.now();
                Duration elapsedDuration = Duration.between(wiD.getStart(), currentTime);

                String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH시 mm분 ss초"));
                finishTimeTextView.setText(formattedTime);

                // Format the elapsed time
                long elapsedSeconds = elapsedDuration.getSeconds();
                long hours = elapsedSeconds / 3600;
                long minutes = (elapsedSeconds % 3600) / 60;
                long seconds = elapsedSeconds % 60;

                String formattedDuration;

                // Add hours if elapsed time has hours
                if (hours > 0 && minutes == 0 && seconds == 0) {
                    formattedDuration = String.format("%d시간", hours);
                } else if (hours > 0 && minutes > 0 && seconds == 0) {
                    formattedDuration = String.format("%d시간 %d분", hours, minutes);
                } else if (hours > 0 && minutes == 0 && seconds > 0) {
                    formattedDuration = String.format("%d시간 %d초", hours, seconds);
                } else if (hours > 0) {
                    formattedDuration = String.format("%d시간 %d분 %d초", hours, minutes, seconds);
                } else if (minutes > 0 && seconds == 0) {
                    formattedDuration = String.format("%d분", minutes);
                } else if (minutes > 0) { // Add minutes if elapsed time has minutes
                    formattedDuration = String.format("%d분 %d초", minutes, seconds);
                } else { // Display seconds only
                    formattedDuration = String.format("%d초", seconds);
                }

                // Update the duration TextView
                durationTextView.setText(formattedDuration);

                finishHandler.postDelayed(this, 1000); // 1 second delay
            }
        };
        finishHandler.postDelayed(finishTimeRunnable, 0);
    }
    private void finishWiD() {
        if (wiD != null) {
            wiD.setFinish(currentTime);
            Duration duration = Duration.between(wiD.getStart(), wiD.getFinish());

            finishTimeTextView.setTextColor(Color.LTGRAY);
            durationTextView.setTextColor(Color.LTGRAY);

            finishHandler.removeCallbacks(finishTimeRunnable);
            finishButton.setVisibility(View.GONE);
            resetButton.setVisibility(View.VISIBLE);

            // Check if the duration is at least 1 minute
            if (duration.toMinutes() >= 1) {
                wiD.setDuration(duration);

                // Store the WiD object in the database
                WiDDatabaseHelper databaseHelper = new WiDDatabaseHelper(getActivity());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues values = wiD.toContentValues();
                db.insert(databaseHelper.getTableWID(), null, values);
                db.close();

                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "WiD가 기록되었습니다.", Snackbar.LENGTH_SHORT);

                View snackbarView = snackbar.getView();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 16 * 15);
                snackbarView.setLayoutParams(params);

                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "1분 미만의 WiD는 기록되지 않아요.", Snackbar.LENGTH_SHORT);

                View snackbarView = snackbar.getView();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 16 * 15);
                snackbarView.setLayoutParams(params);

                snackbar.show();
            }
        }
        wiD = null;
    }
    private void setTextViewClickLogic(Title title) {
        clickedTitle = title.toString();
        startButton.setEnabled(true);
        startButton.setBackgroundColor(Color.RED);
    }
}