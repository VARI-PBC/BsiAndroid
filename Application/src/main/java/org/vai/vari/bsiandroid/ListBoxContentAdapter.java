package org.vai.vari.bsiandroid;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;


class ListBoxContentAdapter extends BoxContentAdapter {
    private static final int VIEW_HEAD = 0;
    private static final int VIEW_ITEM = 1;

    private Box.Vial[] mVials;

    ListBoxContentAdapter(Box box) {
        super(box);
        mVials = box.Vials.values().toArray(new Box.Vial[box.Vials.size()]);
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
        vh.position.setText(getVialAddress(position));
        Box.Vial vial = mVials[position-1];
        vh.currentLabel.setText(vial.currentLabel);
        vh.bsiId.setText(vial.bsiId);
        vh.workingId.setText(vial.workingId);
        vh.vialType.setText(vial.vialType);
        vh.vialComments.setText(vial.comments);
    }

    @Override
    public int getItemCount() {
        return mVials.length+1;
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

