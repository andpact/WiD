package andpact.project.wid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import andpact.project.wid.R;
import andpact.project.wid.fragment.WiDCreateFragment;
import andpact.project.wid.fragment.WiDReadHolderFragment;
import andpact.project.wid.fragment.WiDReadMonthFragment;
import andpact.project.wid.fragment.WiDReadDayFragment;
import andpact.project.wid.fragment.WiDSearchFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // 다크 모드 활성화
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // 다크 모드 비활성화

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        currentFragment = new WiDCreateFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frame_container, currentFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();

            switch (menuItem.getItemId()) {
                case R.id.item_1:
                    if (!(currentFragment instanceof WiDCreateFragment)) {
                        WiDCreateFragment wiDCreateFragment = new WiDCreateFragment();
                        fragmentTransaction1.replace(R.id.frame_container, wiDCreateFragment);
                        currentFragment = wiDCreateFragment;
                    }
                    break;
                case R.id.item_2:
                    if (!(currentFragment instanceof WiDReadHolderFragment)) {
                        WiDReadHolderFragment wiDReadHolderFragment = new WiDReadHolderFragment();
                        fragmentTransaction1.replace(R.id.frame_container, wiDReadHolderFragment);
                        currentFragment = wiDReadHolderFragment;
                    }
                    break;
                case R.id.item_3:
                    if (!(currentFragment instanceof WiDSearchFragment)) {
                        WiDSearchFragment wiDSearchFragment = new WiDSearchFragment();
                        fragmentTransaction1.replace(R.id.frame_container, wiDSearchFragment);
                        currentFragment = wiDSearchFragment;
                    }
                    break;
            }
            fragmentTransaction1.commit();
            return true;
        });
    }

    public void disableBottomNavigation() {
        bottomNavigationView.setAlpha(0.5f);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setEnabled(false);
        }
    }
    public void enableBottomNavigation() {
        bottomNavigationView.setAlpha(1);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setEnabled(true);
        }
    }
}