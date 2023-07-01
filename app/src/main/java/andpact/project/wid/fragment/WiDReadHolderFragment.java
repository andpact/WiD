package andpact.project.wid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import andpact.project.wid.R;
import andpact.project.wid.util.ReadFragmentPagerAdapter;

public class WiDReadHolderFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabItem dayTab, weekTab, monthTab;
//    private TabItem yearTab;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wid_read_holder, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.viewPager2);

        viewPager2.setAdapter(new ReadFragmentPagerAdapter(getChildFragmentManager(), getLifecycle()));

        dayTab = view.findViewById(R.id.dayTab);
        weekTab = view.findViewById(R.id.weekTab);
        monthTab = view.findViewById(R.id.monthTab);
//        yearTab = view.findViewById(R.id.yearTab);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager2.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed for this implementation
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed for this implementation
            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tabLayout.getTabAt(position);
                if (tab != null) {
                    tab.select();
                }
            }
        });

        return view;
    }
}