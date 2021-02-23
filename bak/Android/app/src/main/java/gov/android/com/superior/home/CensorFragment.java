package gov.android.com.superior.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gov.android.com.superior.BaseFragment;
import gov.android.com.superior.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CensorFragment extends BaseFragment {

    private int category = 0;

    public CensorFragment() {
        // Required empty public constructor
    }

    public static CensorFragment newInstance(int category) {
        CensorFragment fragment =  new CensorFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("category", category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt("category", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_censor, container, false);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
