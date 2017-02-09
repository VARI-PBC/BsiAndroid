package org.vai.vari.bsiandroid;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


class ReqTaskDetailAdapter extends BaseAdapter {
    private Box[] mBoxes;

    public void setData(Box[] boxes) {
        mBoxes = boxes;
        notifyDataSetChanged();
    }

    // Returns the number of types of Views that will be created by getView(int, View, ViewGroup)
    @Override
    public int getViewTypeCount() {
        // Returns the number of types of Views that will be created by this adapter
        // Each type represents a set of views that can be converted
        return 1;
    }

    // Get the type of View that will be created by getView(int, View, ViewGroup)
    // for the specified item.
    @Override
    public int getItemViewType(int position) {
        // Return an integer here representing the type of View.
        // Note: Integers must be in the range 0 to getViewTypeCount() - 1
        return 0;
    }

    @Override
    public int getCount() {
        return mBoxes == null ? 0 : mBoxes.length;
    }

    @Override
    public Box getItem(int position) {
        return mBoxes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View boxView;
        //int viewType = getItemViewType(position);
        if (convertView == null) {
            boxView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.req_task_detail_box, parent, false);
        }
        else {
            boxView = convertView;
        }

        final Box box = getItem(position);
        TextView locationView = (TextView)boxView.findViewById(R.id.boxlabel);
        String loc = box.ContainerLabel;
        if (box.Workbench != null && !box.Workbench.isEmpty()) loc = loc+" ("+box.Workbench+")";
        locationView.setText(loc);

        @SuppressWarnings("unchecked")
        RecyclerView boxContents = (RecyclerView) boxView.findViewById(R.id.boxContents);
        RecyclerView.LayoutManager lm;
        RecyclerView.Adapter adapter;
        SwitchCompat listSwitch = (SwitchCompat)boxView.findViewById(R.id.listSwitch);
        if (box.ContainerType == null || box.ContainerType.NumColumns == 1
                || box.ContainerType.NumRows * box.ContainerType.NumColumns > 144) {
            lm = new LinearLayoutManager(parent.getContext());
            adapter = new ListBoxContentAdapter(box);
            listSwitch.setVisibility(View.INVISIBLE);
        } else {
            int numColumns = box.ContainerType.NumColumns;
            if (box.ContainerType.NumRows == 1) {
                switch (numColumns) {
                    case 25:
                    case 81:
                    case 144:
                        numColumns = (byte) Math.sqrt(numColumns);
                        break;
                }
            }
            lm = new GridLayoutManager(parent.getContext(), numColumns);
            adapter = new BoxContentAdapter(box);
            boxContents.setHasFixedSize(true);
            listSwitch.setChecked(false);
        }

        boxContents.setLayoutManager(lm);
        boxContents.setAdapter(adapter);
        return boxView;
    }

}