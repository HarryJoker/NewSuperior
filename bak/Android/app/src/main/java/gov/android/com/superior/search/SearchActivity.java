package gov.android.com.superior.search;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.R;
import gov.android.com.superior.view.searchview.ICallBack;
import gov.android.com.superior.view.searchview.SearchListView;
import gov.android.com.superior.view.searchview.SearchView;
import gov.android.com.superior.view.searchview.bCallBack;

public class SearchActivity extends BaseActivity {

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        setTitle("搜索");

        // 3. 绑定组件
        searchView = (SearchView) findViewById(R.id.search_view);

        // 4. 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                System.out.println("我收到了" + string);
                if (string != null && !string.trim().isEmpty()) {
                    Intent intent = new Intent(SearchActivity.this, SearchListActivity.class);
                    intent.putExtra("keyword", string);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SearchActivity.this, "请输入查询关键字", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
//        searchView.setOnClickBack(new );
        searchView.setOnClickBack(new bCallBack(){
            @Override
            public void BackAciton() {
                finish();
            }
        });
    }
}
