package andpact.project.wid.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import andpact.project.wid.R;
import andpact.project.wid.util.ViewPagerAdapter;

public class TmpActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Set up ViewPager2 adapter
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Handle bottom navigation item selection
        bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_1:
                    viewPager.setCurrentItem(0, true);
                    return true;
                case R.id.item_2:
                    viewPager.setCurrentItem(1, true);
                    return true;
                case R.id.item_3:
                    viewPager.setCurrentItem(2, true);
                    return true;
                case R.id.item_4:
                    viewPager.setCurrentItem(3, true);
                    return true;
                default:
                    return false;
            }
        });

        // Update bottom navigation when ViewPager2 page changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bottomNavigation.getMenu().getItem(position).setChecked(true);
            }
        });
    }
}