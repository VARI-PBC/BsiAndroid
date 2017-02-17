package org.vai.vari.bsiandroid;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


class ReqTaskDetailAdapter extends RecyclerView.Adapter {
    private static final int VIEW_LIST = 0;
    private static final int VIEW_SLOT = 1;
    private static final int VIEW_PROG = 2;
    private static final int VIEW_HEAD = 3;

    private List<Box> mBoxes;
    private ReqTaskItem mTask;

    ReqTaskDetailAdapter(ReqTaskItem task) {
        mTask = task;
        mBoxes = new ArrayList<>();
    }

    void addBoxes(List<Box> boxes) {
        int positionStart = mBoxes.size();
        int itemCount = boxes.size();
        mBoxes.addAll(boxes);
         notifyItemRangeInserted(positionStart, itemCount);
    }

    void addBox(Box box) {
        mBoxes.add(box);
        notifyItemInserted(mBoxes.size() - 1);
    }

    Box removeBox(int position) {
        Box box = mBoxes.remove(position);
        notifyItemRemoved(position);
        return box;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder vh;
        switch (viewType) {
            case VIEW_HEAD:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.req_task_detail_header, parent, false);
                vh = new HeaderVH(view);
                break;
            case VIEW_PROG:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.req_task_footer, parent, false);
                vh = new RecyclerView.ViewHolder(view) {};
                break;
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.req_task_detail_box, parent, false);
                vh = new BoxVH(view);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderVH) {
            HeaderVH vh = (HeaderVH)holder;
            vh.requisitionId.setText(mTask.RequisitionId + " (" + mTask.TaskId + ")");
            vh.reqInstructions.setText(mTask.ReqInstructions);
            String[] dateParts = (mTask.TaskEndTime).split(" ");
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            if (dateParts[0].equals(currentDate)) {
                vh.dateCompleted.setText(dateParts[1].substring(0, 5));
            } else {
                vh.dateCompleted.setText(dateParts[0].substring(5, 10).replace('-', '/'));
            }
            vh.completedBy.setText(mTask.Technician);
            vh.numVials.setText("vials: " + mTask.VialCount);
            vh.reqNotes.setText(mTask.Notes);
            if (mTask.Notes.isEmpty()) vh.reqNotes.setHeight(0);
            vh.taskInstructions.setText(mTask.TaskInstructions);
            if (mTask.TaskInstructions.isEmpty()) vh.taskInstructions.setHeight(0);

        } else if (holder instanceof BoxVH) {
            BoxVH vh = (BoxVH)holder;
            vh.listSwitch.setOnCheckedChangeListener(vh);

            Box box = mBoxes.get(position-1);
            String loc = box.ContainerLabel;
            if (box.Workbench != null && !box.Workbench.isEmpty()) loc = loc+" ("+box.Workbench+")";
            vh.locationView.setText(loc);
            vh.numVials.setText("vials: " + box.Vials.size());

            boolean asList = getItemViewType(position) == VIEW_LIST;
            if (asList) {
                vh.listSwitch.setVisibility(View.INVISIBLE);
            } else {
                vh.listSwitch.setChecked(false);
            }
            vh.setUpBoxContents(asList, box);
        }
    }

    // Get the type of View that will be created by getView(int, View, ViewGroup)
    // for the specified item.
    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_HEAD;
        Box box = mBoxes.get(position-1);
        if (box == null)
            return VIEW_PROG;
        if (box.ContainerType == null || box.ContainerType.NumColumns == 1
                || box.ContainerType.NumRows * box.ContainerType.NumColumns > 144) {
            return VIEW_LIST;
        }
        return VIEW_SLOT;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mBoxes.size()+1;
    }


    private class HeaderVH extends RecyclerView.ViewHolder {
        TextView requisitionId;
        TextView reqInstructions;
        TextView dateCompleted;
        TextView completedBy;
        TextView numVials;
        TextView reqNotes;
        TextView taskInstructions;

        HeaderVH(View view) {
            super(view);

            requisitionId = (TextView) view.findViewById(R.id.requisition_id);
            reqInstructions = (TextView) view.findViewById(R.id.instructions);
            dateCompleted = (TextView) view.findViewById(R.id.date_completed);
            completedBy = (TextView) view.findViewById(R.id.completed_by);
            numVials = (TextView) view.findViewById(R.id.num_vials);
            reqNotes = (TextView)view.findViewById(R.id.reqNotes);
            taskInstructions = (TextView)view.findViewById(R.id.taskInstructions);
        }
    }

    private class BoxVH extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        SwitchCompat listSwitch;
        TextView locationView;
        TextView numVials;
        RecyclerView boxContents;

        BoxVH(View view) {
            super(view);

            listSwitch = (SwitchCompat)view.findViewById(R.id.listSwitch);
            locationView = (TextView)view.findViewById(R.id.boxLabel);
            numVials = (TextView)view.findViewById(R.id.numVialsInBox);
            boxContents = (RecyclerView)view.findViewById(R.id.boxContents);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ViewGroup constraintView = (ViewGroup)buttonView.getParent().getParent();
            switch (buttonView.getId()) {
                case R.id.listSwitch:
                    BoxContentAdapter adapter = (BoxContentAdapter)boxContents.getAdapter();
                    Box box = adapter.mBox;
                    setUpBoxContents(buttonView.isChecked(), box);
                    break;
            }
        }

        private void setUpBoxContents(boolean asList, Box box) {
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
}