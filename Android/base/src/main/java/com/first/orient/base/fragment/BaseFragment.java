package com.first.orient.base.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

    protected String TAG = "";


    public BaseFragment() {
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,   "onCreate------------------------->" + "\n" + "Fragment Arguments:" + makeBundleToJson(getArguments()));
    }

    public void updateBundle(Bundle bundle) {

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG,   "onPause------------------------->");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,   "onResume------------------------->");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG,   "onActivityCreated------------------------->");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,   "onStart------------------------->");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String msg = "Intent: null";
        if (data != null) {
            msg = "Action: " + data.getAction() + "\n" +
                    "Intent Uri: " + data.getData() + "\n" +
                    "RequestCode: " + requestCode + "\n" +
                    "ResultCode: " + resultCode + "\n" +
                    "Intent Data:" + makeBundleToJson(data.getExtras());
        }
        Log.i(TAG,   "onActivityResult--------------------->" + "\n" + msg);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG,   "onDetach------------------------->");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,   "onAttach------------------------->");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,   "onStop------------------------->");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,   "onDestroyView------------------------->");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG,   "onLowMemory------------------------->");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,   "onDestroy------------------------->");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG,   "onViewCreated------------------------->");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.i(TAG,   "onViewStateRestored------------------------->");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG,   "onHiddenChanged: " + hidden + "------------------------->");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG,   "onSaveInstanceState------------------------->");
    }

    public static JSONObject makeBundleToJson(Bundle bundle) {
        JSONObject json = new JSONObject();
        if (bundle == null) return json;
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                json.put(key, bundle.get(key));
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }
}
