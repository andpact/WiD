package andpact.project.wid.fragment;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import andpact.project.wid.R;
import andpact.project.wid.activity.MainActivity;
import andpact.project.wid.util.Title;
import andpact.project.wid.model.WiD;
import andpact.project.wid.util.CustomPageTransformer;
import andpact.project.wid.util.DataMaps;
import andpact.project.wid.util.TitleViewPagerAdapter;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDCreateFragment extends Fragment {
    private MaterialTextView timeLeftTextView, dateTextView, dayOfWeekTextView, startTimeTextView, finishTimeTextView, durationTextView;
    private ShapeableImageView titleColorCircle;
    private DateTimeFormatter dateFormatter, timeFormatter;
    private ImageButton titleLeftButton, titleRightButton;
    private ViewPager2 viewPager2;
    private ImageButton startButton, finishButton, resetButton;
    private String clickedTitle;
    private WiD wiD;
    private LocalDate currentDate;
    private LocalTime currentTime;
    private Timer leftTimer;
    private int timeLeftCurrentIndex;
    private Handler timeLeftHandler, startHandler, finishHandler;
    private Runnable startTimeRunnable, finishTimeRunnable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_create, container, false);

        dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.d ");
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        titleColorCircle = view.findViewById(R.id.titleColorCircle);

        timeLeftTextView = view.findViewById(R.id.timeLeftTextView);

        timeLeftHandler = new Handler();
        leftTimer = new Timer();
        timeLeftCurrentIndex = 0;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timeLeftHandler.post(() -> updateTImeLeft());
            }
        };

        leftTimer.schedule(timerTask, 0, 3000);

        dateTextView = view.findViewById(R.id.dateTextView);
        dayOfWeekTextView = view.findViewById(R.id.dayOfWeekTextView);
        startTimeTextView = view.findViewById(R.id.startTimeTextView);
        finishTimeTextView = view.findViewById(R.id.finishTimeTextView);
        finishTimeTextView.setTextColor(Color.LTGRAY); // 시작 전 회색으로
        durationTextView = view.findViewById(R.id.durationTextView);

        currentDate = LocalDate.now();

        titleLeftButton = view.findViewById(R.id.titleLeftButton);
        titleRightButton = view.findViewById(R.id.titleRightButton);

        viewPager2 = view.findViewById(R.id.viewPager2);
        viewPager2.setAdapter(new TitleViewPagerAdapter(getActivity()));
        viewPager2.setPageTransformer(new CustomPageTransformer());

        titleLeftButton.setOnClickListener(v -> {
            int currentPosition = viewPager2.getCurrentItem();
            if (0 < currentPosition) {
                viewPager2.setCurrentItem(currentPosition - 1);
            }
        });

        titleRightButton.setOnClickListener(v -> {
            int currentPosition = viewPager2.getCurrentItem();
            int totalItems = viewPager2.getAdapter().getItemCount();
            if (currentPosition < totalItems - 1) {
                viewPager2.setCurrentItem(currentPosition + 1);
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Title[] titles = Title.values();

                if (0 <= position && position < titles.length) {
                    Title selectedTitle = titles[position];
                    clickedTitle = selectedTitle.toString();

                    Map<String, Integer> colorMap = DataMaps.getColorMap(getContext());
                    GradientDrawable drawable = (GradientDrawable) titleColorCircle.getBackground();
                    drawable.setColor(colorMap.get(clickedTitle));
                }
                super.onPageSelected(position);
            }
        });

        // 현재 날짜를 표시
        String formattedDate = currentDate.format(dateFormatter);
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

        // 현재 시간을 표시하고 시간이 흐르는 것을 업데이트
        startHandler = new Handler();
        startTimeRunnable = new Runnable() {
            @Override
            public void run() {
                currentTime = LocalTime.now(); // 시간이 1초 단위로 계속 현재 시간으로 할당됨.
                String formattedTime = currentTime.format(timeFormatter);
                startTimeTextView.setText(formattedTime);
                finishTimeTextView.setText(startTimeTextView.getText()); // 시작시간의 텍스트만 가져옴. 러너블 없이

                startHandler.postDelayed(this, 1000); // 1초마다 업데이트
            }
        };
        startHandler.postDelayed(startTimeRunnable, 0);

        startButton = view.findViewById(R.id.startButton);
        finishButton = view.findViewById(R.id.finishButton);
        resetButton = view.findViewById(R.id.resetButton);

        finishButton.setEnabled(false);
        finishButton.setColorFilter(Color.LTGRAY);
        resetButton.setEnabled(false);
        resetButton.setColorFilter(Color.LTGRAY);

        startButton.setOnClickListener(v -> startWiD());
        finishButton.setOnClickListener(v -> finishWiD());
        resetButton.setOnClickListener(v -> resetWiD());

        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        leftTimer.cancel();
    }
    private void updateTImeLeft() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.with(LocalTime.MAX);
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).with(LocalTime.MAX);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        Duration duration;
        long months;
        long days;
        long hours;
        long minutes;

        switch (timeLeftCurrentIndex) {
            case 0: // Today
                duration = Duration.between(now, endOfDay);
                hours = duration.toHours();
                minutes = duration.toMinutes() % 60;
                if (hours == 0) {
                    timeLeftTextView.setText(String.format("오늘이 %d분 남았습니다.", minutes));
                } else if (minutes == 0) {
                    timeLeftTextView.setText(String.format("오늘이 %d시간 남았습니다.", hours));
                } else {
                    timeLeftTextView.setText(String.format("오늘이 %d시간 %d분 남았습니다.", hours, minutes));
                }
                break;
            case 1: // This week
                duration = Duration.between(now, endOfWeek);
                days = duration.toDays();
                hours = duration.toHours() % 24;
                if (days == 0) {
                    timeLeftTextView.setText(String.format("이번 주가 %d시간 남았습니다.", hours));
                } else if (hours == 0) {
                    timeLeftTextView.setText(String.format("이번 주가 %d일 남았습니다.", days));
                } else {
                    timeLeftTextView.setText(String.format("이번 주가 %d일 %d시간 남았습니다.", days, hours));
                }
                break;
            case 2: // This month
                duration = Duration.between(now, endOfMonth);
                days = duration.toDays();
                hours = duration.toHours() % 24;
                if (days == 0) {
                    timeLeftTextView.setText(String.format("이번 달이 %d시간 남았습니다.", hours));
                } else if (hours == 0) {
                    timeLeftTextView.setText(String.format("이번 달이 %d일 남았습니다.", days));
                } else {
                    timeLeftTextView.setText(String.format("이번 달이 %d일 %d시간 남았습니다.", days, hours));
                }
                break;
            case 3: // This year
                months = 12 - now.getMonthValue();
                days = Duration.between(now, endOfMonth).toDays();
                if (months == 0) {
                    timeLeftTextView.setText(String.format("이번 년도가 %d일 남았습니다.", days));
                } else if (days == 0) {
                    timeLeftTextView.setText(String.format("이번 년도가 %d개월 남았습니다.", months));
                } else {
                    timeLeftTextView.setText(String.format("이번 년도가 %d개월 %d일 남았습니다.", months, days));
                }
                break;
        }
        timeLeftCurrentIndex = (timeLeftCurrentIndex + 1) % 4;
    }
    private void startWiD() {
        wiD = new WiD();
        wiD.setTitle(clickedTitle);
        wiD.setDate(currentDate);

        wiD.setStart(currentTime);

        titleRightButton.setVisibility(View.INVISIBLE);
        titleRightButton.setEnabled(false);
        viewPager2.setUserInputEnabled(false);
        titleLeftButton.setVisibility(View.INVISIBLE);
        titleLeftButton.setEnabled(false);

        startTimeTextView.setTextColor(Color.LTGRAY);
        finishTimeTextView.setTextColor(Color.BLACK);
        durationTextView.setTextColor(Color.BLACK);

        startButton.setEnabled(false);
        startButton.setColorFilter(Color.LTGRAY);
        finishButton.setEnabled(true);
        finishButton.setColorFilter(Color.BLACK);

        titleLeftButton.setVisibility(View.GONE);
        titleRightButton.setVisibility(View.GONE);

        // 시작 시간 흐름 멈추기.
        startHandler.removeCallbacks(startTimeRunnable);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.disableBottomNavigation();
        }

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

                String formattedTime = currentTime.format(timeFormatter);
                finishTimeTextView.setText(formattedTime);

                // Format the elapsed time
                long elapsedSeconds = elapsedDuration.getSeconds();
                long hours = elapsedSeconds / 3600;
                long minutes = (elapsedSeconds % 3600) / 60;
                long seconds = elapsedSeconds % 60;

                double percentage = ((double) elapsedSeconds / (24 * 60 * 60)) * 100;
                double roundedPercentage = Math.round(percentage * 10.0) / 10.0;

                String formattedDuration;

                // Add hours if elapsed time has hours
                if (0 < hours && 0 == minutes && 0 == seconds) {
                    formattedDuration = String.format("%d시간", hours);
                } else if (0 < hours && 0 < minutes && 0 == seconds) {
                    formattedDuration = String.format("%d시간 %d분", hours, minutes);
                } else if (0 < hours && 0 == minutes && 0 < seconds) {
                    formattedDuration = String.format("%d시간 %d초", hours, seconds);
                } else if (0 < hours) {
                    formattedDuration = String.format("%d시간 %d분 %d초", hours, minutes, seconds);
                } else if (0 < minutes && 0 == seconds) {
                    formattedDuration = String.format("%d분", minutes);
                } else if (0 < minutes) { // Add minutes if elapsed time has minutes
                    formattedDuration = String.format("%d분 %d초", minutes, seconds);
                } else { // Display seconds only
                    formattedDuration = String.format("%d초", seconds);
                }

                if (0.1 <= roundedPercentage) {
                    formattedDuration = String.format("%s (%.1f%%)", formattedDuration, roundedPercentage);
                } else {
                    formattedDuration = String.format("%s (0%%)", formattedDuration);
                }

                // Update the duration TextView
                durationTextView.setText(formattedDuration);

                // Check if duration exceeds 12 hours and call finishWiD() method
                if (12 <= hours) {
                    finishWiD();
                    showSnackbar("12시간이 초과되어 WiD가 자동으로 등록되었습니다.");
                }

                finishHandler.postDelayed(this, 1000); // 1 second delay
            }
        };
        finishHandler.postDelayed(finishTimeRunnable, 0);
    }
    private void finishWiD() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.enableBottomNavigation();
        }

        resetButton.setEnabled(true);
        resetButton.setColorFilter(Color.BLACK);
        finishButton.setEnabled(false);
        finishButton.setColorFilter(Color.LTGRAY);

        if (wiD != null) {
            wiD.setFinish(currentTime);

            finishTimeTextView.setTextColor(Color.LTGRAY);
            durationTextView.setTextColor(Color.LTGRAY);

            finishHandler.removeCallbacks(finishTimeRunnable);

            if (wiD.getStart().isAfter(wiD.getFinish())) { // 이틀에 걸친 WiD
                // Calculate the duration for the first part of the WiD
                Duration firstDuration = Duration.between(wiD.getStart(), LocalTime.MAX);

                // Calculate the duration for the second part of the WiD
                Duration secondDuration = Duration.between(LocalTime.MIDNIGHT, wiD.getFinish());

                // Calculate the total duration across days
                Duration totalDuration = firstDuration.plus(secondDuration);

                // Check if the duration is at least 1 minute
                if (totalDuration.toMinutes() < 1) {
                    showSnackbar("1분 이상의 WiD를 기록해 주세요.");
                    return;
                }

                // 두번째 WiD를 위해 시간을 미리 빼놓음.
                LocalTime finishTimeForSecondWiD = wiD.getFinish();
                // Calculate the duration for the first part of the WiD
                wiD.setFinish(LocalTime.MAX);
                wiD.setDuration(firstDuration);

                // Store the first WiD object in the database
                WiDDatabaseHelper databaseHelper = new WiDDatabaseHelper(getActivity());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues values = wiD.toContentValues();
                db.insert(databaseHelper.getTableWID(), null, values);

                // Create a new WiD object for the second part of the duration
                WiD secondWiD = new WiD();
                secondWiD.setTitle(wiD.getTitle());
                secondWiD.setDate(wiD.getDate().plusDays(1)); // Add one day to the date
                secondWiD.setStart(LocalTime.MIDNIGHT);
                secondWiD.setFinish(finishTimeForSecondWiD);
                secondWiD.setDuration(secondDuration);

                // Store the second WiD object in the database
                ContentValues secondValues = secondWiD.toContentValues();
                db.insert(databaseHelper.getTableWID(), null, secondValues);
                db.close();

            } else { // 하루 내의 WiD
                Duration duration = Duration.between(wiD.getStart(), wiD.getFinish());

                // Check if the duration is at least 1 minute
                if (duration.toMinutes() < 1) {
                    showSnackbar("1분 이상의 WiD를 기록해 주세요.");
                    return;
                }

                wiD.setDuration(duration);

                // Store the WiD object in the database
                WiDDatabaseHelper databaseHelper = new WiDDatabaseHelper(getActivity());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues values = wiD.toContentValues();
                db.insert(databaseHelper.getTableWID(), null, values);
                db.close();
            }

            showSnackbar("WiD가 기록되었습니다.");

            wiD = null;
        }
    }
    private void resetWiD() {
        resetButton.setEnabled(false);
        resetButton.setColorFilter(Color.LTGRAY);
        startButton.setEnabled(true);
        startButton.setColorFilter(Color.BLACK);

        titleLeftButton.setVisibility(View.VISIBLE);
        titleRightButton.setVisibility(View.VISIBLE);

        startHandler.postDelayed(startTimeRunnable, 0);
        startTimeTextView.setTextColor(Color.BLACK);
        durationTextView.setText("");
        durationTextView.setHint("최소 1분..");

        titleRightButton.setVisibility(View.VISIBLE);
        titleRightButton.setEnabled(true);
        viewPager2.setUserInputEnabled(true);
        titleLeftButton.setVisibility(View.VISIBLE);
        titleLeftButton.setEnabled(true);

        showSnackbar("초기화가 완료되었습니다.");
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