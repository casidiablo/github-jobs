package com.github.jobs.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.jobs.R;

public class DummyFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.dummy, container, false);
        TextView viewById = (TextView) inflate.findViewById(R.id.foo);
        viewById.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
        return inflate;
    }
}
