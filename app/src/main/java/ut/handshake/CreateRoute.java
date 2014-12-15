package ut.handshake;

/**
 * Created by Aurelius on 12/14/14.
 */

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class CreateRoute extends android.support.v4.app.Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = CreateRoute.class.getSimpleName();

    private static final String POSSIBLE_EMAILS = "possible_emails";
    private static final String POSSIBLE_PHONES = "possible_phones";

    //boolean showSubscribe;
    //String searchText;
    //Button mSubscribedButton;
    //Button mNearbyButton;
    //Button mSearchButton;
    //Button mTopButton;
    //EditText mSearchText;

    private OnCreateRouteListener mListener;


    public static CreateRoute newInstance(JSONArray emails, JSONArray phoneNumbers) {
        CreateRoute fragment = new CreateRoute();
        Bundle args = new Bundle();
        args.putString(POSSIBLE_EMAILS, emails.toString());
        args.putString(POSSIBLE_PHONES, phoneNumbers.toString());
        fragment.setArguments(args);
        return fragment;
    }
    public CreateRoute() {
        // Required empty public constructor
    }

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    public static final String DATEPICKER_TAG2 = "datepicker2";
    public static final String TIMEPICKER_TAG2 = "timepicker2";

    public static final int DIALOG_FRAGMENT = 1;

    public static String possibleEmails;
    public static String possiblePhoneNumbers;
    //public static ArrayList<String> selectedEmails;
    //public static ArrayList<String> selectedNumbers;
    public static JSONArray selectedEmails;
    public static JSONArray selectedNumbers;
    public static ArrayList<Range> ranges;
    public static ListView selectMediums;

    public static GregorianCalendar start;
    public static GregorianCalendar end;

    MyDateListener mDateListener;
    MyTimeListener mTimeListener;

    Button mConfirmButton;
    MyCustomAdapter dataAdapter;

    private Route newRoute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            possibleEmails = getArguments().getString(POSSIBLE_EMAILS);
            possiblePhoneNumbers = getArguments().getString(POSSIBLE_PHONES);
        }
        //selectedEmails = new ArrayList<>();
        //selectedNumbers = new ArrayList<>();
        selectedEmails = new JSONArray();
        selectedNumbers = new JSONArray();

        ranges = new ArrayList<>();

        start = new GregorianCalendar();
        end = new GregorianCalendar();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case DIALOG_FRAGMENT:

                if (resultCode == Activity.RESULT_OK) {
                    // After Ok code.
                } else if (resultCode == Activity.RESULT_CANCELED){
                    // After Cancel code.
                }

                break;
        }
    }

    public void findSelected() {
        StringBuffer responseText = new StringBuffer();
        responseText.append("The following were selected...\n");

        ArrayList<Medium> mediumList = dataAdapter.mediumList;
        String result_emails = "[";
        String result_numbers = "[";
        String emails_prefix = "";
        String numbers_prefix = "";
        for(int i=0;i<mediumList.size();i++){
            Medium medium = mediumList.get(i);
            if(!medium.getSelected()){
                continue;
            }

            if(medium.getIsEmail()){
                result_emails += emails_prefix + "'" + medium.getContent() + "'";
                emails_prefix = ",";
            } else {
                result_numbers += numbers_prefix + "'" + medium.getContent() + "'";
                numbers_prefix = ",";
            }
        }
        result_emails += "]";
        result_numbers += "]";

        try {
            selectedEmails = new JSONArray(result_emails);
            selectedNumbers = new JSONArray(result_numbers);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(getActivity().getApplicationContext(),
                result_emails + result_numbers, Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.create_route, container, false);

        final Calendar calendar = Calendar.getInstance();

        mConfirmButton = (Button) view.findViewById(R.id.confirm_create_button);


        String numbers_src = possiblePhoneNumbers;
        String emails_src = possibleEmails;

        JSONArray numbers = null;
        JSONArray emails = null;
        ArrayList<Medium> mediumList = null;
        try {
            numbers = new JSONArray(numbers_src);
            emails = new JSONArray(emails_src);

            mediumList = new ArrayList<Medium>();
            for(int i = 0; i < numbers.length(); i++){
                String entry = (String) numbers.get(i);
                mediumList.add(new Medium(entry, false));
            }
            for(int i = 0; i < emails.length(); i++){
                String entry = (String) emails.get(i);
                mediumList.add(new Medium(entry, true));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(getActivity().getApplicationContext(),
                R.layout.medium_info, mediumList);
        ListView listView = (ListView) view.findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        mConfirmButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GregorianCalendar cutoff = new GregorianCalendar();
                cutoff.add(Calendar.YEAR, 5);
                Range newRange = new Range(start, end, cutoff, cutoff);
                ranges.add(newRange);
                findSelected();
                mListener.newRouteSelected(selectedEmails, selectedNumbers, ranges);
            }
        });



        mDateListener = new MyDateListener();
        mTimeListener = new MyTimeListener();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        final DatePickerDialog datePickerDialog2 = DatePickerDialog.newInstance(mDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        final TimePickerDialog timePickerDialog2 = TimePickerDialog.newInstance(mTimeListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

        view.findViewById(R.id.start_date_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                android.support.v4.app.Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                datePickerDialog.setVibrate(false);
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.setTargetFragment(CreateRoute.this, DIALOG_FRAGMENT);
                datePickerDialog.show(getActivity().getSupportFragmentManager().beginTransaction(), DATEPICKER_TAG);
            }
        });

        view.findViewById(R.id.start_time_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.setVibrate(false);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.setTargetFragment(CreateRoute.this, DIALOG_FRAGMENT);
                timePickerDialog.show(getActivity().getSupportFragmentManager().beginTransaction(), TIMEPICKER_TAG);
            }
        });

        view.findViewById(R.id.end_date_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                android.support.v4.app.Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG2);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                datePickerDialog2.setVibrate(false);
                datePickerDialog2.setYearRange(1985, 2028);
                datePickerDialog2.setCloseOnSingleTapDay(false);
                datePickerDialog2.setTargetFragment(CreateRoute.this, DIALOG_FRAGMENT);
                datePickerDialog2.show(getActivity().getSupportFragmentManager().beginTransaction(), DATEPICKER_TAG2);
            }
        });

        view.findViewById(R.id.end_time_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog2.setVibrate(false);
                timePickerDialog2.setCloseOnSingleTapMinute(false);
                timePickerDialog2.setTargetFragment(CreateRoute.this, DIALOG_FRAGMENT);
                timePickerDialog2.show(getActivity().getSupportFragmentManager().beginTransaction(), TIMEPICKER_TAG2);
            }
        });

        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getActivity().getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }

            TimePickerDialog tpd = (TimePickerDialog) getActivity().getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }

            DatePickerDialog dpd2 = (DatePickerDialog) getActivity().getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG2);
            if (dpd2 != null) {
                dpd2.setOnDateSetListener(mDateListener);
            }

            TimePickerDialog tpd2 = (TimePickerDialog) getActivity().getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG2);
            if (tpd2 != null) {
                tpd2.setOnTimeSetListener(mTimeListener);
            }

        }

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
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        start.set(year, month, day, start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE));
        Toast.makeText(getActivity().getApplicationContext(), "new start date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH),
                hourOfDay, minute);
        Toast.makeText(getActivity().getApplicationContext(), "new start time:" + hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();

    }


        @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCreateRouteListener) activity;
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

    public interface OnCreateRouteListener {
        public void newRouteSelected(JSONArray emails, JSONArray phones, ArrayList<Range> ranges);
        //public void subscribedButtonHandler();
        //public void nearbyButtonHandler();
        //public void searchButtonHandler(String searchText);
        //public void topButtonHandler();
    }

    public class MyDateListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
            end.set(year, month, day, end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE));
            Toast.makeText(getActivity().getApplicationContext(), "new end date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
        }
    }

    public class MyTimeListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
            end.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH),
                    hourOfDay, minute);
            Toast.makeText(getActivity().getApplicationContext(), "new end time:" + hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<Medium> {

        private ArrayList<Medium> mediumList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Medium> mediumList) {
            super(context, textViewResourceId, mediumList);
            this.mediumList = new ArrayList<Medium>();
            this.mediumList.addAll(mediumList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.medium_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Medium Medium = (Medium) cb.getTag();
                        Medium.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Medium Medium = mediumList.get(position);
            holder.code.setText(" (" +  Medium.getType() + ")");
            holder.name.setText(Medium.getContent());
            holder.name.setChecked(Medium.getSelected());
            holder.name.setTag(Medium);

            return convertView;

        }

    }

}

