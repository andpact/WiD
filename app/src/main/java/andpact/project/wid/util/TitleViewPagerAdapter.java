package andpact.project.wid.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import andpact.project.wid.fragment.TitleTextViewFragment;

public class TitleViewPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 8;
    public TitleViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 각각의 텍스트 뷰에 해당하는 Fragment를 생성합니다.
        return TitleTextViewFragment.newInstance(position);
    }
    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}