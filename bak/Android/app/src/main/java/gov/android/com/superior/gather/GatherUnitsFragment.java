package gov.android.com.superior.gather;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gov.android.com.superior.R;
import gov.android.com.superior.callback.JsonCallback;
import gov.android.com.superior.http.Config;

/**
 * A simple {@link Fragment} subclass.
 */
public class GatherUnitsFragment extends Fragment {

    private int parentUnitId;

    private ListView lv_units;

    private UnitAdapter unitAdapter;

    public GatherUnitsFragment() {

    }

    public static GatherUnitsFragment newInstance(int parentUnitId) {
        GatherUnitsFragment gatherUnitsFragment = new GatherUnitsFragment();
        Bundle arg = new Bundle();
        arg.putInt("parentUnitId", parentUnitId);
        gatherUnitsFragment.setArguments(arg);
        return gatherUnitsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentUnitId = getArguments().getInt("parentUnitId", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gather_units, container, false);
        lv_units = view.findViewById(R.id.lv_units);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lv_units.setOnItemClickListener(itemClickListener);

        asycGetUnitGather();
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(), GatherActivity.class);
            intent.putExtra("unitId", new Long(unitAdapter.getItemId(i)).intValue());
            intent.putExtra("unitName", unitAdapter.getItem(i).get("name").toString());
            startActivity(intent);
        }
    };

    private void asycGetUnitGather() {
        OkGo.<List>get(Config.UNIT_SELECT + "/" + parentUnitId).tag(this).execute(jsonCallback);
    }

    private JsonCallback<List> jsonCallback = new JsonCallback<List>() {
        @Override
        public void onSuccess(Response<List> response) {
            unitAdapter = new UnitAdapter(response.body());
            lv_units.setAdapter(unitAdapter);
        }
    };

    private class UnitAdapter extends BaseAdapter {

        private List units = new ArrayList();

        public UnitAdapter(List list) {
            if (list != null && list.size() > 0) units.clear();
            units.addAll(list);
        }

        @Override
        public int getCount() {
            return units.size();
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return (Map<String, Object>) units.get(i);
        }

        @Override
        public long getItemId(int i) {
            return Long.parseLong((((Map)getItem(i)).get("id")).toString());
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_unit, viewGroup, false);

            TextView textView = layout.findViewById(R.id.text1);
            Map unit = getItem(i);
            textView.setText(unit.get("name").toString());


            AvatarImageView imageView = layout.findViewById(R.id.lv_logo);

            String logo = unit.get("logo").toString();

            if (TextUtils.isEmpty(logo)) {
                String unitName = unit.get("name").toString();
                imageView.setTextAndColor(unitName.length() > 0 ? unitName.substring(0, 1) : "无", Color.parseColor("#f8f8ff"));
            } else {
                Glide.with(getActivity())
                        .load(Config.ATTACHMENT + logo)
                        .centerCrop()
                        .crossFade()
                        .into(imageView);
            }

            return layout;
        }
    }
}
