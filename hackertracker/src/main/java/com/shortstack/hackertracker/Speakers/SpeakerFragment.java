package com.shortstack.hackertracker.Speakers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shortstack.hackertracker.Activity.HomeActivity;
import com.shortstack.hackertracker.Adapter.DefaultAdapter;
import com.shortstack.hackertracker.Api.ApiException;
import com.shortstack.hackertracker.Api.Impl.SpeakerServiceImpl;
import com.shortstack.hackertracker.Api.SpeakerService;
import com.shortstack.hackertracker.Listener.AsyncTaskCompleteListener;
import com.shortstack.hackertracker.Fragment.HackerTrackerFragment;
import com.shortstack.hackertracker.Model.ApiBase;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Model.SpeakerList;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.ApiResponseUtil;
import com.shortstack.hackertracker.Utils.DialogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpeakerFragment extends HackerTrackerFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_DATE = "date";
    private View rootView;
    private DefaultAdapter adapter;
    private ListView list;
    private SpeakerService speakerService;
    private static int date;
    private Context context;
    private int mPage;
    private int mDate;

    public static SpeakerFragment newInstance(int position,int date) {
        SpeakerFragment frag=new SpeakerFragment();
        Bundle args=new Bundle();

        args.putInt(ARG_SECTION_NUMBER, position);
        args.putInt(ARG_DATE, date);

        frag.setArguments(args);

        return(frag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mPage = args.getInt(ARG_SECTION_NUMBER);
        mDate = args.getInt(ARG_DATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {

        context = inflater.getContext();

        if (rootView != null) {
          ViewGroup parent = (ViewGroup) rootView.getParent();
          if (parent != null)
              parent.removeView(rootView);
        }
        try {
          rootView = inflater.inflate(R.layout.fragment_speakers, container, false);
        } catch (InflateException e) {
        }

        date = mDate+1;

        list = (ListView) rootView.findViewById(R.id.list_speakers);
        speakerService = new SpeakerServiceImpl();

        // check internet connectivity
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        /*if (isConnected) {

            // get speakers from API
            fillSpeakerList();

        } else { */

            // get speakers from database
            List<Default> speakers = getItemByDate(String.valueOf(date), Constants.TYPE_SPEAKER);
            if (speakers.size() > 0) {

                adapter = new DefaultAdapter(context, R.layout.row, speakers);

                list.setAdapter(adapter);

            }

        //}

        return rootView;
    }

    protected void fillSpeakerList() {

        try {
            speakerService.getAllSpeakers(context, new GetSpeakersListener());
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    private class GetSpeakersListener implements AsyncTaskCompleteListener<ApiBase> {

        @Override
        public void onTaskComplete(ApiBase result) {

            // get speakers
            SpeakerList speakers;
            try {
                speakers = (SpeakerList) ApiResponseUtil.parseResponse(result, SpeakerList.class);
            } catch (ApiException e) {
                return;
            }

            List<Default> speakersArray = new ArrayList(Arrays.asList(speakers.getSpeakers()));

            // populate adapter and attached it to the list view
            adapter = new DefaultAdapter(context, R.layout.row, speakersArray);

            if (speakersArray.size()!=0) {
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                // no results found
            }
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (adapter!=null)
            adapter.notifyDataSetChanged();
    }
}