package info.arybin.fearnotwords.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import info.arybin.fearnotwords.Config;
import info.arybin.fearnotwords.Constants;
import info.arybin.fearnotwords.activity.BaseActivity;


public abstract class BaseFragment extends Fragment implements Constants {
    protected BaseActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = ((BaseActivity) getActivity());
        activity.initializeViews(getClass(), this);
    }


    /**
     * @return true if the back press event is consumed, false otherwise.
     */
    abstract public boolean onBackPressed();


    protected String readConfig(Config config) {
        if (null != activity) {
            return activity.readConfig(config);
        }
        return null;
    }

    public Fragment loadFragment(int container, Class<? extends BaseFragment> fragment, Bundle args) {
        if (null != activity) {
            return activity.loadFragment(container, fragment, args);
        }
        return null;
    }

    public boolean playSound(String name) {
        if (null != activity) {
            return activity.playSound(name);
        }
        return false;
    }


}
