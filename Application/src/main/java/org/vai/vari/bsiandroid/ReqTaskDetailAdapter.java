package org.vai.vari.bsiandroid;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;


class ReqTaskDetailAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
    private static final int VIEW_LIST = 0;
    private static final int VIEW_SLOT = 1;

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
        Box box = getItem(position);
        if (box.ContainerType == null || box.ContainerType.NumColumns == 1
                || box.ContainerType.NumRows * box.ContainerType.NumColumns > 144) {
            return VIEW_LIST;
        }
        return VIEW_SLOT;
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
        if (convertView == null) {
            boxView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.req_task_detail_box, parent, false);
        }
        else {
            boxView = convertView;
        }
        SwitchCompat listSwitch = (SwitchCompat)boxView.findViewById(R.id.listSwitch);
        listSwitch.setOnCheckedChangeListener(this);

        Box box = getItem(position);
        TextView locationView = (TextView)boxView.findViewById(R.id.boxlabel);
        String loc = box.ContainerLabel;
        if (box.Workbench != null && !box.Workbench.isEmpty()) loc = loc+" ("+box.Workbench+")";
        locationView.setText(loc);
        TextView numVials = (TextView)boxView.findViewById(R.id.numVialsInBox);
        numVials.setText("vials: " + box.Vials.size());

        @SuppressWarnings("unchecked")
        RecyclerView boxContents = (RecyclerView) boxView.findViewById(R.id.boxContents);
        boolean asList = getItemViewType(position) == VIEW_LIST;
        if (asList) {
            listSwitch.setVisibility(View.INVISIBLE);
        } else {
            listSwitch.setChecked(false);
        }
        setUpBoxContents(asList, box, boxContents);
        return boxView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ViewGroup constraintView = (ViewGroup)buttonView.getParent().getParent();
        switch (buttonView.getId()) {
            case R.id.listSwitch:
                RecyclerView boxContents = (RecyclerView) constraintView.findViewById(R.id.boxContents);
                BoxContentAdapter adapter = (BoxContentAdapter)boxContents.getAdapter();
                Box box = adapter.mBox;
                setUpBoxContents(buttonView.isChecked(), box, boxContents);
                break;
        }
    }

    private void setUpBoxContents(boolean asList, Box box, RecyclerView boxContents) {
        RecyclerView.LayoutManager lm;
        BoxContentAdapter adapter;
        if (asList) {
            lm = new LinearLayoutManager(boxContents.getContext());
            adapter = new ListBoxContentAdapter(box);
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
            lm = new GridLayoutManager(boxContents.getContext(), numColumns);
            adapter = new BoxContentAdapter(box);
            boxContents.setHasFixedSize(true);
            boxContents.setNestedScrollingEnabled(false);
        }
        boxContents.setLayoutManager(lm);
        boxContents.setAdapter(adapter);
    }
}