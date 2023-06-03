package andpact.project.wid.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import andpact.project.wid.R;
import andpact.project.wid.model.WiD;
import andpact.project.wid.service.WiDService;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDReadFragment extends Fragment {
    private TextView dateTextView;
    private LinearLayout linearLayout;
    private WiDService wiDService;
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

        wiDService = new WiDService();
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
        List<WiD> wiDList = wiDService.getWiDByDate(getContext(), currentDate.toString());

        // WiD 리스트를 텍스트 뷰에 표현하기
        for (WiD wiD : wiDList) {
            TextView textView = new TextView(getContext());
            textView.setText(wiD.toString());
            linearLayout.addView(textView);
        }
    }
}