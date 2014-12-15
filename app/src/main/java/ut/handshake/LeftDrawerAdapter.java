package ut.handshake;

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

/**
 * Created by Aurelius on 12/13/14.
 */
public class LeftDrawerAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<String> textList;
    LeftDrawerAdapter(Activity parent, ArrayList<String> textList) {
        inflater = LayoutInflater.from(parent);
        this.textList = textList;
    }

    @Override
    public int getCount() {
        return textList.size();
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
            view = inflater.inflate(R.layout.left_drawer_adapter, parent, false);
            holder = new ViewHolder();

            //holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.textLabel = (TextView) view.findViewById(R.id.left_item_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.textLabel.setText(textList.get(position));

        return view;
    }

    static class ViewHolder {
        //ImageView imageView;
        TextView textLabel;
    }
}


