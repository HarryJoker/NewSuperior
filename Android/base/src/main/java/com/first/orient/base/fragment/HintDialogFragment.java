package com.first.orient.base.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.first.orient.base.R;

public class HintDialogFragment extends DialogFragment {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String REQUEST_CODE = "request_code";

    private HintDialogFragmentCallback mCallback;

    public static HintDialogFragment newInstance(int title, int message, int requestCode) {
        HintDialogFragment frag = new HintDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TITLE, title);
        args.putInt(MESSAGE, message);
        args.putInt(REQUEST_CODE, requestCode);
        frag.setArguments(args);
        return frag;
    }

    public void setCallback(HintDialogFragmentCallback callback) {
        mCallback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt(TITLE);
        int message = getArguments().getInt(MESSAGE);
        final int requestCode = getArguments().getInt(REQUEST_CODE);
        if (mCallback == null) {
            if (getActivity() != null && getActivity() instanceof HintDialogFragmentCallback) {
                mCallback = (HintDialogFragmentCallback)getActivity();
            }
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (mCallback != null) {
                                    mCallback.doPositiveClick(requestCode);
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (mCallback != null) {
                                    mCallback.doNegativeClick(requestCode);
                                }
                            }
                        }
                )
                .create();
    }

    public interface HintDialogFragmentCallback {

        void doPositiveClick(int requestCode);

        void doNegativeClick(int requestCode);
    }

}
