package andpact.project.wid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import andpact.project.wid.R;

public class WiDReadYearFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_read_year, container, false);

        return view;
    }
    private void updateWiDLayout() {
//        LocalDate today = LocalDate.now();
//        int currentYear = currentDate.getYear();
//        int todayYear = today.getYear();
//
//        if (currentYear == todayYear) {
//            increaseDateButton.setEnabled(false);
//            increaseDateButton.setAlpha(0.5f);
//        } else {
//            increaseDateButton.setEnabled(true);
//            increaseDateButton.setAlpha(1f);
//        }
    }
}