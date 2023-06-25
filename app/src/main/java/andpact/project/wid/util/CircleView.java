package andpact.project.wid.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {
    private Paint paint;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD)); // bold 형태로 설정
        // 원의 스타일 등 다른 속성을 설정할 수 있습니다.
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        canvas.drawCircle(width / 2, height / 2, radius, paint);

        int centerX = width / 2;
//        int centerY = height / 2;
        int centerY = height / 2 + 8; // 원의 위치를 파이 차트와 맞추기 위해 살짝 아래로 내림.
        float textRadius = radius * 0.9f; // 글자는 원의 안쪽에 표시되는게 원칙.
//        float textSize = radius * 0.1f;
        float textSize = radius * 0.07f; // 원의 둘레에 표시되는 숫자의 글자 크기를 줄임.

        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < 24; i++) {
            float angle = (float) (2 * Math.PI * i / 24);
            float x = centerX + textRadius * (float) Math.sin(angle);
            float y = centerY - textRadius * (float) Math.cos(angle);
            if (i == 0) {
                canvas.drawText(String.valueOf(24), x, y, paint);
                continue;
            }
            canvas.drawText(String.valueOf(i), x, y, paint);
        }
    }
}