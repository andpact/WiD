package andpact.project.wid.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import andpact.project.wid.fragment.WiDCreateFragment2;
import andpact.project.wid.fragment.WiDReadAllFragment;
import andpact.project.wid.fragment.WiDReadFragment;
import andpact.project.wid.fragment.WiDSearchFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_FRAGMENTS = 4;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WiDCreateFragment2();
            case 1:
                return new WiDReadAllFragment();
            case 2:
                return new WiDReadFragment();
            case 3:
                return new WiDSearchFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_FRAGMENTS;
    }
}