package org.vai.vari.bsiandroid;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


class ListBoxContentAdapter extends BoxContentAdapter {
    private static final int VIEW_HEAD = 0;
    private static final int VIEW_ITEM = 1;

    private List<String> mLocationKeys;

    ListBoxContentAdapter(Box box) {
        super(box);
        mLocationKeys = new ArrayList<>(box.Vials.keySet());
        Collections.sort(mLocationKeys);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_HEAD : VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.req_task_detail_row, parent, false);
        return new VH(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VH vh = (VH)holder;
        if (position == 0) { // header
            vh.position.setTypeface(null, Typeface.BOLD);
            vh.currentLabel.setTypeface(null, Typeface.BOLD);
            vh.bsiId.setTypeface(null, Typeface.BOLD);
            vh.workingId.setTypeface(null, Typeface.BOLD);
            vh.vialType.setTypeface(null, Typeface.BOLD);
            vh.vialComments.setTypeface(null, Typeface.BOLD);
            return;
        }
        Box.Vial vial = mBox.Vials.get(mLocationKeys.get(position-1)); // subtract 1 for list header
        vh.position.setText(getSlotAddress(vial.row, vial.column));
        vh.currentLabel.setText(vial.currentLabel);
        vh.bsiId.setText(vial.bsiId);
        vh.workingId.setText(vial.workingId);
        vh.vialType.setText(vial.vialType);
        vh.vialComments.setText(vial.comments);
    }

    @Override
    public int getItemCount() {
        return mLocationKeys.size()+1;
    }

    private String getSlotAddress(String rowLabel, String colLabel) {
        if (mBox.ContainerType == null) return "";

        int numRows = mBox.ContainerType.NumRows;
        int numColumns = mBox.ContainerType.NumColumns;
        String address = "";
        if (numRows > 1) address = rowLabel;
        if (numRows > 1 && numColumns > 1) address = address + "-";
        if (numColumns > 1) address = address + colLabel;
        return address;
    }

    private class VH extends RecyclerView.ViewHolder {

        TextView position;
        TextView currentLabel;
        TextView bsiId;
        TextView workingId;
        TextView vialType;
        TextView vialComments;

        VH(View itemView) {
            super(itemView);

            position = (TextView) itemView.findViewById(R.id.location);
            currentLabel = (TextView) itemView.findViewById(R.id.currentLabel);
            bsiId = (TextView) itemView.findViewById(R.id.bsiId);
            workingId = (TextView) itemView.findViewById(R.id.workingId);
            vialType = (TextView) itemView.findViewById(R.id.vialType);
            vialComments = (TextView) itemView.findViewById(R.id.vialComments);
        }
    }
}

