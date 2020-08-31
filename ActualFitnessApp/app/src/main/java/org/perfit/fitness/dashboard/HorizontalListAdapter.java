package org.perfit.fitness.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.perfit.fitness.R;

import java.util.ArrayList;

class HorizontalListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Dashboard_List_Model> imageModelArrayList;

    public HorizontalListAdapter(Context context, ArrayList<Dashboard_List_Model> imageModelArrayList) {

        this.context = context;
        this.imageModelArrayList = imageModelArrayList;
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
        return imageModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.workout_list, null, true);

            holder.iv = convertView.findViewById(R.id.workout_list_images);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.iv.setImageResource(imageModelArrayList.get(position).getList_Images());

        return convertView;
    }

    private class ViewHolder {

        private ImageView iv;

    }

}
