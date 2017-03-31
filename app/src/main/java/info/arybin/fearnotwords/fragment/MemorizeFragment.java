package info.arybin.fearnotwords.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import info.arybin.fearnotwords.R;
import info.arybin.fearnotwords.core.OperableQueue;
import info.arybin.fearnotwords.core.SimpleOperableQueue;
import info.arybin.fearnotwords.model.Memorable;

public class MemorizeFragment extends BaseFragment {

    private OperableQueue<? extends Memorable> memorableQueue;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memorize, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public boolean onBackPressed() {

        return false;
    }

    private void initialize() {
        initializedViews();

        ArrayList<? extends Memorable> tmp = getArguments().getParcelableArrayList(KEY_LOADED_MEMORABLE);
        memorableQueue = SimpleOperableQueue.buildFrom(tmp);
    }

    private void initializedViews() {

    }


}
