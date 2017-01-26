package org.vai.vari.bsiandroid;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Locale;

class ReqTaskDetailAdapter extends BaseAdapter {
    private ReqTaskItem task;

    ReqTaskDetailAdapter(ReqTaskItem task) {
        this.task = task;
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
        return task.Boxes.size();
    }

    @Override
    public ReqTaskItem.Box getItem(int position) {
        return task.Boxes.get(position);
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

        final ReqTaskItem.Box box = getItem(position);
        TextView boxlabel = (TextView)boxView.findViewById(R.id.boxlabel);
        String boxLabel = box.Freezer.isEmpty() ? "" : box.Freezer;
        if (!box.Rack.isEmpty()) {
            if (!boxLabel.isEmpty()) boxLabel = boxLabel + " > ";
            boxLabel = boxLabel + box.Rack;
        }
        if (!box.BoxLabel.isEmpty()) {
            if (!boxLabel.isEmpty()) boxLabel = boxLabel + " > ";
            boxLabel = boxLabel + box.BoxLabel;
        }
        boxlabel.setText(boxLabel);

        @SuppressWarnings("unchecked")
        RecyclerView boxContents = (RecyclerView) boxView.findViewById(R.id.boxContents);
        RecyclerView.LayoutManager lm;
        RecyclerView.Adapter adapter;
        int slots = box.NumRows * box.NumColumns;
        if (slots <= 144) {
            int numColumns = box.NumColumns;
            if (box.NumRows == 1) {
                switch (numColumns) {
                    case 25:
                    case 81:
                    case 144:
                        numColumns = (byte) Math.sqrt(numColumns);
                        break;
                }
            }
            lm = new GridLayoutManager(boxView.getContext(), numColumns);
            adapter = new SlotAdapter(boxView, box);
            boxContents.setHasFixedSize(true);
        } else {
            lm = new LinearLayoutManager(boxView.getContext());
            adapter = new FlatAdapter(boxView, box);
        }

        boxContents.setLayoutManager(lm);
        boxContents.setAdapter(adapter);
        return boxView;
    }

    private static class SlotAdapter extends RecyclerView.Adapter<SlotVH> {
        private View boxView;
        private ReqTaskItem.Box box;

        SlotAdapter(View view, ReqTaskItem.Box box) {
            this.boxView = view;
            this.box = box;
        }

        @Override
        public SlotVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View slotView = LayoutInflater.from(boxView.getContext())
                    .inflate(R.layout.req_task_detail_slot, parent, false);
            return new SlotVH(slotView, new OnVHClickedListener() {
                @Override
                public void onVHClicked(SlotVH vh) {

                }
            });
        }

        @Override
        public void onBindViewHolder(SlotVH holder, int position) {
            String row = box.NumRows == 1 ? "" :
                    box.RowFormat == 1 ? (char)('A' + position/box.NumColumns) + "-" :
                            String.format(Locale.US, box.NumColumns == 1 ? "%1$03d" : "%1$d", position/box.NumColumns+1) + "-";
            String col = box.NumColumns == 1 ? "" :
                    String.format(Locale.US, box.NumRows == 1 ? "%1$03d" : "%1$d", position%box.NumColumns+1);
            holder.row.setText(row);
            holder.col.setText(col);
            String key = row + col;
            ReqTaskItem.Vial vial = box.Vials.get(key);
            holder.vialLabel.setText(vial == null ? "" : vial.current_label);
        }

        @Override
        public int getItemCount() {
            int slots = box.NumRows * box.NumColumns;
            if (slots > 144) return box.Vials.size();
            return slots;
        }
    }

    interface OnVHClickedListener {
        void onVHClicked(SlotVH vh);
    }

    private static class SlotVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OnVHClickedListener mListener;

        TextView row;
        TextView col;
        TextView vialLabel;

        SlotVH(View itemView, OnVHClickedListener listener) {
            super(itemView);
            itemView.setOnClickListener(this);
            mListener = listener;

            row = (TextView)itemView.findViewById(R.id.boxRow);
            col = (TextView)itemView.findViewById(R.id.boxColumn);
            vialLabel = (TextView)itemView.findViewById(R.id.vialLabel);
        }

        @Override
        public void onClick(View v) {
            mListener.onVHClicked(this);
        }
    }

    private static class FlatAdapter extends RecyclerView.Adapter {
        private View boxView;
        private ReqTaskItem.Box box;

        FlatAdapter(View view, ReqTaskItem.Box box) {
            this.boxView = view;
            this.box = box;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new TextView(boxView.getContext());
            return new RecyclerView.ViewHolder(view) {};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ReqTaskItem.Vial vial = box.Vials.valueAt(position);
            TextView text = (TextView)holder.itemView;
            text.setText(vial.current_label);
        }

        @Override
        public int getItemCount() {
            int slots = box.NumRows * box.NumColumns;
            if (slots > 144) return box.Vials.size();
            return slots;
        }
    }
}