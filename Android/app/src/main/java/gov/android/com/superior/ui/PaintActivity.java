package gov.android.com.superior.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.first.orient.base.activity.BaseToolBarActivity;
import com.lht.paintview.PaintView;
import com.lht.paintview.pojo.DrawShape;

import java.util.ArrayList;

import gov.android.com.superior.R;
import gov.android.com.superior.tools.ImageUtils;

public class PaintActivity extends BaseToolBarActivity {

    private PaintView mPaintView;

    final static int WIDTH_PAINT = 20;


    @Override
    public void onInitParams() {

    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        barTitle.setText("手签");
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_paint;
    }

    @Override
    protected void onFindViews() {
        mPaintView = findViewById(R.id.view_paint);

        mPaintView.setColorFromRes(R.color.black);
        mPaintView.setTextColorFromRes(R.color.black);
        mPaintView.setBgColor(Color.WHITE);
        mPaintView.setStrokeWidth(WIDTH_PAINT);
        mPaintView.setOnDrawListener(mOnDrawListener);
    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {

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
