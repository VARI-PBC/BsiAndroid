package org.vai.vari.bsiandroid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


class ReqTasksMasterAdapter extends RecyclerView.Adapter {

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_PROG = 1;

    private List<ReqTaskItem> mTasks;

    List<ReqTaskItem> getTasks() {
        return mTasks;
    }

    void addTasks(Collection<ReqTaskItem> tasks) {
        int positionStart = mTasks.size();
        int itemCount = tasks.size();
        this.mTasks.addAll(tasks);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    void addTask(ReqTaskItem task) {
        if (mTasks.contains(task)) return;
        mTasks.add(task);
        notifyItemInserted(mTasks.size() - 1);
    }

    int removeTask(ReqTaskItem task) {
        int position = mTasks.indexOf(task);
        mTasks.remove(task);
        notifyItemRemoved(position);
        return position;
    }

    void clearTasks() {
        int size = mTasks.size();
        mTasks.clear();
        notifyItemRangeRemoved(0, size);
    }

    ReqTasksMasterAdapter() {
        mTasks = new ArrayList<>();
    }

    ReqTaskItem getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mTasks.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.req_task_master_row, parent, false);
            vh = new ItemViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.req_task_footer, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position >= mTasks.size()) return;
        ReqTaskItem task = mTasks.get(position);
        if (task == null) return;

        if (holder instanceof ItemViewHolder) {
            ItemViewHolder vh = (ItemViewHolder) holder;
            vh.requisitionId.setText(task.RequisitionId + " (" + task.TaskId + ")");
            vh.instructions.setText(task.ReqInstructions);

            String[] dateParts = (task.TaskEndTime).split(" ");
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            if (dateParts[0].equals(currentDate)) {
                vh.dateCompleted.setText(dateParts[1].substring(0, 5));
            } else {
                vh.dateCompleted.setText(dateParts[0].substring(5, 10).replace('-', '/'));
            }

            vh.completedBy.setText(task.Technician);
            vh.numVials.setText("vials: " + task.VialCount);
        }
        else if (holder instanceof ProgressViewHolder) {
            ProgressViewHolder vh = (ProgressViewHolder)holder;
            vh.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView requisitionId;
        TextView instructions;
        TextView dateCompleted;
        TextView completedBy;
        TextView numVials;

        ItemViewHolder(View view) {
            super(view);
            requisitionId = (TextView) view.findViewById(R.id.requisition_id);
            instructions = (TextView) view.findViewById(R.id.instructions);
            dateCompleted = (TextView) view.findViewById(R.id.date_completed);
            completedBy = (TextView) view.findViewById(R.id.completed_by);
            numVials = (TextView) view.findViewById(R.id.num_vials);
        }
    }
}
