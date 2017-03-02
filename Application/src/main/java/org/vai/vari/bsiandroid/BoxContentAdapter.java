package org.vai.vari.bsiandroid;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Locale;

class BoxContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Box mBox;
    private int mSlotSize;
    private RecyclerView mRecyclerView;

    // memoize slot size for performance
    private int getSlotSize() {
        if (mSlotSize == 0) {
            GridLayoutManager lm = (GridLayoutManager)mRecyclerView.getLayoutManager();
            int spanCount = lm.getSpanCount();
            mSlotSize = spanCount > 12 ? 235 : 2436/spanCount;
        }
        return mSlotSize;
    }

    BoxContentAdapter(Box box) {
        this.mBox = box;
        mSlotSize = 0;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View slotView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.req_task_detail_slot, parent, false);
        slotView.getLayoutParams().width =
            slotView.getLayoutParams().height = getSlotSize();
        return new VH(slotView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VH vh = (VH)holder;
        vh.slotLabel.setText(getSlotAddress(position));
        int numColumns = mBox.ContainerType.NumColumns;
        String rowKey = mBox.RowFormat == 1 ? Character.toString((char)('A' + position / numColumns)) :
                String.format(Locale.US, "%1$03d", position / numColumns+1);
        String colKey = mBox.ColumnFormat == 1 ? Character.toString((char)('A' + position % numColumns)) :
                String.format(Locale.US, "%1$03d", position % numColumns+1);
        Box.Vial vial = mBox.Vials.get(rowKey + "-" + colKey);
        if (vial != null) {
            String vialText = vial.currentLabel.isEmpty() ? vial.bsiId : vial.currentLabel;
            if (!vial.workingId.isEmpty()) vialText = vialText+" ("+vial.workingId+")";
            vh.vialLabel.setText(vialText);
        }
        // Stupid RecyclerView calling this for first item before layout,
        // so make him redraw it.
        if (position == 1) {
            holder.itemView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(0);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBox.ContainerType.NumRows * mBox.ContainerType.NumColumns;
    }

    private String getSlotAddress(int position) {
        if (mBox.ContainerType == null) return "";

        int numRows = mBox.ContainerType.NumRows;
        int numColumns = mBox.ContainerType.NumColumns;
        String rowLabel = mBox.RowFormat == 1 ? Character.toString((char)('A' + position / numColumns)) :
                String.format(Locale.US, numColumns == 1 ? "%1$03d" : "%1$d", position / numColumns+1);
        String colLabel = mBox.ColumnFormat == 1 ? Character.toString((char)('A' + position % numColumns)) :
                String.format(Locale.US, numRows == 1 ? "%1$03d" : "%1$d", position % numColumns+1);
        String address = "";
        if (numRows > 1) address = rowLabel;
        if (numRows > 1 && numColumns > 1) address = address + "-";
        if (numColumns > 1) address = address + colLabel;
        return address;
    }

    private class VH extends RecyclerView.ViewHolder {

        TextView slotLabel;
        TextView vialLabel;


        VH(View itemView) {
            super(itemView);

            slotLabel = (TextView) itemView.findViewById(R.id.slotLabel);
            vialLabel = (TextView) itemView.findViewById(R.id.vialLabel);
        }
    }
}