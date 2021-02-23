package gov.android.com.superior.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by wanghua on 17/8/11.
 */

public class NestExpandableListView extends ExpandableListView {

    public NestExpandableListView(Context context) {
        super(context);
    }

    public NestExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //重新设置高度
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
