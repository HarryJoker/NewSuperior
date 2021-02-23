package gov.android.com.superior.gather;

import android.os.Bundle;

import gov.android.com.superior.BaseLoadActivity;
import gov.android.com.superior.R;

public class GatherActivity extends BaseLoadActivity {

    private int unitId;
    private String unitName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gather);

        setTitle("数据统计");

        unitId = getIntent().getIntExtra("unitId", 0);
        unitName = getIntent().getStringExtra("unitName");

        //步骤一：添加一个FragmentTransaction的实例
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();

         //步骤二：用add()方法加上Fragment的对象rightFragment
         GatherFragment gatherFragment = GatherFragment.newInstance(unitId, unitName);
         transaction.add(R.id.container, gatherFragment);

         //步骤三：调用commit()方法使得FragmentTransaction实例的改变生效
         transaction.commit();
    }
}
