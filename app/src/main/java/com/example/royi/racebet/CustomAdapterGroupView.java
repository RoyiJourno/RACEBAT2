package com.example.royi.racebet;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import static android.content.ContentValues.TAG;


public class CustomAdapterGroupView  extends BaseAdapter {

    private Context context;



    CustomAdapterGroupView(Context context) {

        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return GroupView.modelArrayList.size();
    }

    @Override
    public Object getItem(int position) { return GroupView.modelArrayList.get(position); }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try {
                convertView = inflater.inflate(R.layout.group_view_custom_adapter, null, true);
            }catch (Exception e){
                Log.e(TAG, "getView: ",e.getCause() );
            }



            holder.nameShowList = (TextView) convertView.findViewById(R.id.nameListShow);
            holder.scoreShowList = (TextView) convertView.findViewById(R.id.scoreShow);




            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.nameShowList.setText(GroupView.modelArrayList.get(position).getFullname());
        holder.scoreShowList.setText(String.valueOf(GroupView.modelArrayList.get(position).getScore()));


        return convertView;
    }

    private class ViewHolder {

        private TextView nameShowList, scoreShowList;
    }

}
