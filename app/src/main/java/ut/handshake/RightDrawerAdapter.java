package ut.handshake;

/**
 * Created by Aurelius on 12/13/14.
 */

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;


import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aurelius on 12/13/14.
 */
public class RightDrawerAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<String>> listDataChild;

    private static final String TAG = Handshake.class.getSimpleName();

    private LayoutInflater inflater;
    private Activity parentActivity;
    public RightDrawerAdapter(Activity parent, ArrayList<String> listDataHeader,
                                 HashMap<String, ArrayList<String>> listChildData) {
        parentActivity = parent;
        inflater = LayoutInflater.from(parent);
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        //Log.d(TAG, "getChildView");
        final ViewHolder holder;
        View view = convertView;
        String childText = (String) getChild(groupPosition, childPosition);

        if (view == null) {
            /*LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);*/
            view = inflater.inflate(R.layout.right_drawer_adapter, parent, false);
            holder = new ViewHolder();
            holder.statusIcon = (ImageView) view.findViewById(R.id.status_icon);
            holder.routeName = (TextView) view.findViewById(R.id.route_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        int count = 0;
        for (Route route: Handshake.routes) {
            //Log.d(TAG, "Checking route: "+route.getDisplayName()+" count: "+count);
            if(route.getDisplayName().equals(childText)) {
                if (route.isOnline()) {
                    holder.routeName.setTextColor(parentActivity.getResources().getColor(R.color.text_primary));
                    holder.statusIcon.setImageDrawable(parentActivity.getResources().getDrawable(R.drawable.presence_online));
                } else {
                    holder.routeName.setTextColor(parentActivity.getResources().getColor(R.color.actual_gray));
                    holder.statusIcon.setImageDrawable(parentActivity.getResources().getDrawable(R.drawable.presence_busy));
                    //ExpandableListView elv = (ExpandableListView) parent;
                    //elv.setChildIndicator(parentActivity.getResources().getDrawable(R.drawable.presence_busy));
                }
            }
            count++;
        }
        holder.routeName.setText(childText);

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        /*String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;*/
        //Log.d(TAG, "getGroupView");
        final ViewHolder holder;
        View view = convertView;
        String headerTitle = (String) getGroup(groupPosition);

        if (view == null) {
            /*LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);*/
            Log.d(TAG, "trying to check parent");
            Log.d(TAG, parent.toString());
            view = inflater.inflate(R.layout.right_drawer_adapter, parent, false);
            holder = new ViewHolder();
            holder.statusIcon = (ImageView) view.findViewById(R.id.status_icon);
            holder.routeName = (TextView) view.findViewById(R.id.route_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (isExpanded) {
            holder.statusIcon.setImageDrawable(parentActivity.getResources().getDrawable(R.drawable.ic_action_collapse));
        } else {
            if (holder.statusIcon == null) {
                Log.d(TAG, "oh no!");
            }
            if (parentActivity == null) {
                Log.d(TAG, "oh no!2");
            }
            if (parentActivity.getResources() == null) {
                Log.d(TAG, "oh no!3");
            }
            holder.statusIcon.setImageDrawable(parentActivity.getResources().getDrawable(R.drawable.ic_action_expand));
        }

        holder.routeName.setTypeface(null, Typeface.BOLD);
        holder.routeName.setText(headerTitle);
        //Log.d(TAG, "finished");
        return view;
    }

    public void update(HashMap<String, ArrayList<String>> listDataChild) {
        this.listDataChild = listDataChild;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ViewHolder {
        ImageView statusIcon;
        TextView routeName;
    }
}


