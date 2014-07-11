package com.shortstack.hackertracker.Speakers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.DialogUtil;

public class SpeakerPagerFragment extends Fragment {

    static ViewPager pager;
    private Context context;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static SpeakerPagerFragment newInstance(int position) {
        SpeakerPagerFragment frag=new SpeakerPagerFragment();
        Bundle args=new Bundle();

        args.putInt(ARG_SECTION_NUMBER, position);
        frag.setArguments(args);

        return(frag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.pager, container, false);

        // get context
        context = inflater.getContext();

        pager=(ViewPager)result.findViewById(R.id.pager);
        pager.setAdapter(buildAdapter());
        pager.setOffscreenPageLimit(4);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) result.findViewById(R.id.pager_title_strip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.green));
        pagerTabStrip.setDrawFullUnderline(false);

        return(result);
    }

    private PagerAdapter buildAdapter() {
        return(new SpeakerPagerAdapter(getActivity(), getChildFragmentManager()));
    }

}