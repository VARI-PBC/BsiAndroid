package org.vai.vari.bsiandroid;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


class ReqTaskReportAdapter extends ReportAdapter {

    @Override
    public int getCount() {
        if (mDataSet == null) return 0;

        Object[] rows = (Object[])mDataSet.get("rows");
        return rows.length;
    }

    @Override
    public Object getItem(int position) {
        if (mDataSet == null) return null;

        Object[] rows = (Object[])mDataSet.get("rows");
        return rows[position];
    }

    @Override
    public long getItemId(int position) {
        if (mDataSet == null) return 0;

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mDataSet == null) return null;

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.req_task_row_item, parent, false);
        }
        else {
            view = convertView;
        }
        Object[] values = (Object[])getItem(position);
        TextView requisitionId = (TextView) view.findViewById(R.id.requisition_id);

        requisitionId.setText(values[0] + " (" + values[1] + ")");

        TextView taskTemplate = (TextView) view.findViewById(R.id.task_template);
        taskTemplate.setText((String)values[2]);

        TextView dateCompleted = (TextView) view.findViewById(R.id.date_completed);
        String[] dateParts = ((String)values[3]).split(" ");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (dateParts[0] == currentDate) {
            dateCompleted.setText(dateParts[1].substring(0, 5));
        } else {
            dateCompleted.setText(dateParts[0].substring(5, 10).replace('-', '/'));
        }

        TextView completedBy = (TextView) view.findViewById(R.id.completed_by);
        completedBy.setText((String)values[4]);
        return view;
    }
}
