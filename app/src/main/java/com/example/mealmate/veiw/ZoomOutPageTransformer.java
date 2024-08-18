package com.example.mealmate.veiw;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        if (position < -1) {
            // [-Infinity,-1)
            page.setAlpha(0);
        } else if (position <= 1) {
            // [-1,1]
            page.setAlpha(1);
            page.setScaleX(Math.max(MIN_SCALE, 1 - Math.abs(position)));
            page.setScaleY(Math.max(MIN_SCALE, 1 - Math.abs(position)));
            page.setTranslationX(-position * pageWidth);
        } else {
            // (1,+Infinity]
            page.setAlpha(0);
        }
    }
}