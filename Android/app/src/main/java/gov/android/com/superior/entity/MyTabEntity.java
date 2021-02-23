package gov.android.com.superior.entity;

import android.os.Bundle;

import com.first.orient.base.fragment.BaseFragment;
import com.first.orient.base.utils.JokerLog;
import com.flyco.tablayout.listener.CustomTabEntity;

/**
 * @author HarryJoker
 * @date :2020-11-30 15:39
 * @description:
 */
public class MyTabEntity extends TabEntity {

    private int mPosition;
    private Class mClazz;
    private BaseFragment fragment;

    public MyTabEntity(String title, int selectedIcon, int unSelectedIcon, int position, Class clazz) {
        super(title, selectedIcon, unSelectedIcon);
        this.mPosition = position;
        this.mClazz = clazz;
    }

    public int getmPosition() {
        return mPosition;
    }

    public BaseFragment getFragment() {
        if (fragment == null) {
            try {
                JokerLog.e("position: " + mPosition + " newInstance Fragment ------------------->");
                fragment = (BaseFragment) Class.forName(mClazz.getName()).newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("category", 1);
                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JokerLog.e("position: " + mPosition + " cache Fragment ------------------->");
        }
        return fragment;
    }
}
