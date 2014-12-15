package ut.handshake;

/**
 * Created by Aurelius on 12/14/14.
 */

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;



/**
 * Created by Aurelius on 12/14/14.
 */

import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;


public class JoinRoute extends android.support.v4.app.Fragment {

    private static final String TAG = JoinRoute.class.getSimpleName();

    //private static final String SHOW_SUBSCRIBE = "show_subscribe";
    //private static final String SEARCH_TEXT = "search_text";

    //boolean showSubscribe;
    //String searchText;
    //Button mSubscribedButton;
    //Button mNearbyButton;
    //Button mSearchButton;
    //Button mTopButton;
    //EditText mSearchText;

    EditText routeNameToJoin;
    Button confirmButton;
    private OnJoinRouteListener mListener;

    public static JoinRoute newInstance() {
        JoinRoute fragment = new JoinRoute();
        Bundle args = new Bundle();
        //args.putBoolean(SHOW_SUBSCRIBE, showSubscribe);
        //args.putString(SEARCH_TEXT, searchText);
        fragment.setArguments(args);
        return fragment;
    }
    public JoinRoute() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //showSubscribe = getArguments().getBoolean(SHOW_SUBSCRIBE);
            //searchText = getArguments().getString(SEARCH_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.join_route, container, false);

        routeNameToJoin = (EditText) view.findViewById(R.id.join_route_text);
        confirmButton = (Button) view.findViewById(R.id.confirm_join_button);


        confirmButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String routeName = routeNameToJoin.getText().toString();

                mListener.joinRouteSelected(routeName);
            }
        });
        /*mSubscribedButton = (Button) view.findViewById(R.id.view_subscribed_button);
        mSubscribedButton.setVisibility(showSubscribe ? View.VISIBLE : View.GONE);

        mNearbyButton = (Button) view.findViewById(R.id.nearby_button);
        mSearchButton = (Button) view.findViewById(R.id.search_button);
        mTopButton = (Button) view.findViewById(R.id.top_button);
        mSearchText = (EditText) view.findViewById(R.id.search_text);

        mSubscribedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.subscribedButtonHandler();
            }
        });

        mNearbyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mListener.nearbyButtonHandler();
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mListener.searchButtonHandler(mSearchText.getText().toString());
            }
        });

        mTopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mListener.topButtonHandler();
            }
        });
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mSearchText.setText(searchText);
        mSearchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.d(TAG, "Focus change");
                if(view.getId() == R.id.search_text && !hasFocus) {
                    Log.d(TAG, "Focus change inside");
                    InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                }
            }
        });*/

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnJoinRouteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCreateRouteListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    public interface OnJoinRouteListener {
        public void joinRouteSelected(String routeName);
        //public void subscribedButtonHandler();
        //public void nearbyButtonHandler();
        //public void searchButtonHandler(String searchText);
        //public void topButtonHandler();
    }

}


