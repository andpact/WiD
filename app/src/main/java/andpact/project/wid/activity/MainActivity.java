package andpact.project.wid.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import andpact.project.wid.R;
import andpact.project.wid.fragment.WiDCreateFragment;
import andpact.project.wid.fragment.WiDReadFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    if (!(currentFragment instanceof WiDReadFragment)) {
                        WiDReadFragment wiDReadFragment = new WiDReadFragment();
                        fragmentTransaction1.replace(R.id.frame_container, wiDReadFragment);
                        currentFragment = wiDReadFragment;
                    }
                    break;
            }

            fragmentTransaction1.commit();
            return true;
        });
    }
}