package gov.android.com.superior;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.lzy.okgo.OkGo;

import gov.android.com.superior.home.UnitFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    public String TAG = "Fragment";

    public BaseFragment() {
        TAG = this.getClass().getSimpleName();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate------------------------->");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG,"onPause------------------------->");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume------------------------->");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG,"onActivityCreated------------------------->");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"onStart------------------------->");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG,"onActivityResult------------------------->");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG,"onDetach------------------------->");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,"onAttach------------------------->");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,"onStop------------------------->");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"onDestroyView------------------------->");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG,"onLowMemory------------------------->");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy------------------------->");
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG,"onViewCreated------------------------->");
    }

    public void showLoading() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity)getActivity()).showLoading();
        }
    }

    public void hideLoading() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity)getActivity()).hideLoading();
        }
    }
}
