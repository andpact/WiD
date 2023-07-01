package andpact.project.wid.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import andpact.project.wid.fragment.WiDReadDayFragment;
import andpact.project.wid.fragment.WiDReadMonthFragment;
import andpact.project.wid.fragment.WiDReadWeekFragment;
import andpact.project.wid.fragment.WiDReadYearFragment;

public class ReadFragmentPagerAdapter extends FragmentStateAdapter {
    private final int NUM_FRAGMENTS = 3;

    public ReadFragmentPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WiDReadDayFragment();
            case 1:
                return new WiDReadWeekFragment();
            case 2:
                return new WiDReadMonthFragment();
//            case 3:
//                return new WiDReadYearFragment();
            default:
                return null;
        }
    }
    @Override
    public int getItemCount() {
        return NUM_FRAGMENTS;
    }
}