package com.sevencats.movelist20.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sevencats.movelist20.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomAnalyticsFragment extends Fragment {


    public BottomAnalyticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_analytics, container, false);
    }

}
