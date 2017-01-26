package org.vai.vari.bsiandroid;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


class ReqTaskReportAdapter extends ArrayAdapter<ReqTaskItem> {

    ReqTaskReportAdapter(Context context, List<ReqTaskItem> tasks) {
        super(context, R.layout.req_task_row_item, tasks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.req_task_row_item, parent, false);
        }
        else {
            view = convertView;
        }

        ReqTaskItem task = getItem(position);
        if (task == null) return view;

        TextView requisitionId = (TextView) view.findViewById(R.id.requisition_id);

        requisitionId.setText(task.RequisitionId + " (" + task.TaskName + ")");

        TextView taskTemplate = (TextView) view.findViewById(R.id.task_template);
        taskTemplate.setText(task.TemplateLabel);

        TextView dateCompleted = (TextView) view.findViewById(R.id.date_completed);
        String[] dateParts = (task.TaskEndTime).split(" ");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        if (dateParts[0].equals(currentDate)) {
            dateCompleted.setText(dateParts[1].substring(0, 5));
        } else {
            dateCompleted.setText(dateParts[0].substring(5, 10).replace('-', '/'));
        }

        TextView completedBy = (TextView) view.findViewById(R.id.completed_by);
        completedBy.setText(task.Technician);
        return view;
    }
}
