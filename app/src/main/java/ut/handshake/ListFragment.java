package ut.handshake;

/**
 * Created by Aurelius on 12/13/14.
 */

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Date;


public class ListFragment extends android.support.v4.app.Fragment {

    private static final String TAG = ListFragment.class.getSimpleName();

    private static final String ARG_IMAGES = "ImageList";
    private static final String ARG_TEXT = "MainTextList";
    private static final String ARG_TIMES = "TimestampList";
    private static final String ARG_ALLOW_MESSAGE = "AllowMessage";
    private static final String ARG_ALLOW_CLICKS = "AllowClicks";

    private OnListListener mListener;
    //DisplayImageOptions options;

    ListView listView;

    private ArrayList<String> imageList;
    private ArrayList<String> mainTextList;
    private ArrayList<String> timestampList;
    private boolean allowMessages;
    private boolean allowClicks;

    Button sendButton;
    EditText messageContents;

    public boolean isAllowClicks() {
        return allowClicks;
    }

    public void setAllowClicks(boolean allowClicks) {
        this.allowClicks = allowClicks;
    }

    public boolean isAllowMessages() {
        return allowMessages;
    }

    public void setAllowMessages(boolean allowMessages) {
        this.allowMessages = allowMessages;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public ArrayList<String> getMainTextList() {
        return mainTextList;
    }

    public void setMainTextList(ArrayList<String> mainTextList) {
        this.mainTextList = mainTextList;
    }

    public ArrayList<String> getTimestampList() {
        return timestampList;
    }

    public void setTimestampList(ArrayList<String> timestampList) {
        this.timestampList = timestampList;
    }

    public static ListFragment newInstance(ArrayList<String> imageList, ArrayList<String> mainTextList,
                                           ArrayList<String> timestampList, boolean allowMessages,
                                           boolean allowClicks) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_IMAGES, imageList);
        args.putStringArrayList(ARG_TEXT, mainTextList);
        args.putStringArrayList(ARG_TIMES, timestampList);
        args.putBoolean(ARG_ALLOW_MESSAGE, allowMessages);
        args.putBoolean(ARG_ALLOW_CLICKS, allowClicks);

        fragment.setArguments(args);
        return fragment;
    }
    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            imageList = getArguments().getStringArrayList(ARG_IMAGES);
            mainTextList = getArguments().getStringArrayList(ARG_TEXT);
            timestampList = getArguments().getStringArrayList(ARG_TIMES);
            allowMessages = getArguments().getBoolean(ARG_ALLOW_MESSAGE);
            allowClicks = getArguments().getBoolean(ARG_ALLOW_CLICKS);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        // Inflate the layout for the center fragment
        View rootView = inflater.inflate(R.layout.center_fragment, container, false);

        ListView listview = (ListView) rootView.findViewById(R.id.center_list);
        messageContents = (EditText) rootView.findViewById(R.id.message_contents);

        sendButton = (Button) rootView.findViewById(R.id.send_message_button);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // build the message
                String messageBody = messageContents.getText().toString();
                if (messageBody==null || messageBody.isEmpty()) {
                    return;
                }
                Message messageToSend = new Message(messageBody, Handshake.userId,
                        Handshake.activeRoute, true, "", new Date());
                mListener.messageSendHandler(messageToSend);

            }
        });

        // setup listView for convos and messages respectively
        if (allowClicks) {

        } else {
            if (listview == null) {
                Log.d(TAG, "listView is null when it should not be..");
            }
            listview.setSelector(getActivity().getResources().getDrawable(android.R.color.transparent));
            listview.setDivider(getActivity().getResources().getDrawable(android.R.color.transparent));
            listview.setDividerHeight(0);
            listview.setCacheColorHint(Color.TRANSPARENT);
            listview.setChoiceMode(0);
            int numberMessages = Handshake.activeMessages.size() - 1;
            Log.d(TAG, "Number of messages: "+numberMessages);
            //listview.setSelection(numberMessages);
            listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            listview.setStackFromBottom(true);
            //listview.setAdapter(new MessageViewAdapter());
        }

        listview.setAdapter(new CenterViewAdapter());


        if (allowMessages) {
            LinearLayout bottomRightBar = (LinearLayout) rootView.findViewById(R.id.bottom_write_bar);
            bottomRightBar.setVisibility(View.VISIBLE);
        }

        // Check if we want to have a click listener
        if (allowClicks) {
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Log.d(TAG, "Position: " + position);
                    mListener.conversationClickHandler(mainTextList.get(position));
                }
            });
        }
        return rootView;
    }

    public class CenterViewAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        CenterViewAdapter() {
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return mainTextList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;

            if (view == null) {
                view = inflater.inflate(R.layout.center_view_adapter, parent, false);
                holder = new ViewHolder();
                holder.relView = (RelativeLayout) view.findViewById(R.id.single_entry);
                holder.profileIcon = (ImageView) holder.relView.findViewById(R.id.profile_icon);
                holder.mainText = (TextView) holder.relView.findViewById(R.id.main_text);
                holder.subText = (TextView) holder.relView.findViewById(R.id.sub_text);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }


            // Conversation List vs Single Conversation settings
            if (allowClicks) {

            } else {
                holder.profileIcon.getLayoutParams().width = 0;
                Message curMessage = Handshake.activeMessages.get(position);

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.relView.getLayoutParams();
                //lp.gravity = Gravity.RIGHT;
                lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                //holder.relView.setBackgroundResource(R.drawable.send_first);

                if (curMessage.isMine()) {
                    lp.gravity = Gravity.RIGHT;
                    holder.relView.setBackgroundResource(R.drawable.send_first);
                }
                else {
                    lp.gravity = Gravity.LEFT;
                    holder.relView.setBackgroundResource(R.drawable.receive_first);
                    holder.mainText.setTextColor(getActivity().getResources().getColor(R.color.almost_white));
                    holder.subText.setTextColor(getActivity().getResources().getColor(R.color.almost_white));
                }
                holder.relView.setLayoutParams(lp);
                holder.subText.setText(timestampList.get(position));
            }

            holder.mainText.setText(mainTextList.get(position));

            return view;

        }
    }

    static class ViewHolder {
        RelativeLayout relView;
        ImageView profileIcon;
        TextView mainText;
        TextView subText;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListListener {
        public void conversationClickHandler(String conversationName);
        public void messageSendHandler(Message messageToSend);
    }

}

