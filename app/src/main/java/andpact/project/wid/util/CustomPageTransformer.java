package andpact.project.wid.util;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class CustomPageTransformer implements ViewPager2.PageTransformer {

    private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        int pageHeight = page.getHeight();
        int pageWidth = page.getWidth();

        // 페이지의 좌우에 위치한 뷰의 가시성을 설정합니다.
        if (position < -1 || 1 < position) {
            page.setVisibility(View.INVISIBLE);
        } else {
            page.setVisibility(View.VISIBLE);
        }

//        // 현재 페이지를 중심으로 좌우의 뷰의 크기와 위치를 조정합니다.
        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
        float vertMargin = pageHeight * (1 - scaleFactor) / 2;
        float horzMargin = pageWidth * (1 - scaleFactor) / 2;

        if (position < 0) {
            page.setTranslationX(horzMargin - vertMargin / 2);
        } else {
            page.setTranslationX(-horzMargin + vertMargin / 2);
        }

        // 뷰의 크기 조정
        page.setScaleX(scaleFactor);
        page.setScaleY(scaleFactor);
    }
}