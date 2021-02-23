package gov.android.com.superior;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lht.paintview.PaintView;
import com.lht.paintview.pojo.DrawShape;

import java.util.ArrayList;

import gov.android.com.superior.tools.ImageUtil;
import gov.android.com.superior.tools.ImageUtils;

public class PaintActivity extends BaseActivity {

    private PaintView mPaintView;

    final static int WIDTH_PAINT = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        setTitle("手签批阅");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPaintView = findViewById(R.id.view_paint);

        mPaintView.setColorFromRes(R.color.black);
        mPaintView.setTextColorFromRes(R.color.black);
        mPaintView.setBgColor(Color.WHITE);
        mPaintView.setStrokeWidth(WIDTH_PAINT);
        mPaintView.setOnDrawListener(mOnDrawListener);
    }

    private PaintView.OnDrawListener mOnDrawListener = new PaintView.OnDrawListener() {
        @Override
        public void afterPaintInit(int viewWidth, int viewHeight) {
            System.out.println(viewWidth + "*" + viewHeight);
        }

        @Override
        public void afterEachPaint(ArrayList<DrawShape> drawShapes) {
            System.out.println(drawShapes);
        }
    };

    public void undoClick(View v) {
        mPaintView.undo();
    }

    public void saveClick(View v) {
        Bitmap bitmap = mPaintView.getBitmap(true);
        String filePath = ImageUtils.saveImage(this, mPaintView.getBitmap(true), true);
        Intent intent = new Intent();
        intent.putExtra("filePath", filePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void clearClick(View v) {
        mPaintView.clear();
    }

}
