package org.vai.vari.bsiandroid;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ReqTaskDetailActivity extends AppCompatActivity {
    ReqTaskDetailFragment fragmentItemDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        // Fetch the item to display from bundle
        ReqTaskItem task = (ReqTaskItem) getIntent().getSerializableExtra("task");
        if (savedInstanceState == null) {
            // Insert detail fragment based on the item passed
            fragmentItemDetail = ReqTaskDetailFragment.newInstance(task);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flDetailContainer, fragmentItemDetail);
            ft.commit();
        }

        TextView requisitionId = (TextView) findViewById(R.id.requisition_id);
        requisitionId.setText(task.RequisitionId + " (" + task.TaskId + ")");

        TextView instructions = (TextView) findViewById(R.id.instructions);
        instructions.setText(task.TaskInstructions);

        TextView dateCompleted = (TextView) findViewById(R.id.date_completed);
        String[] dateParts = (task.TaskEndTime).split(" ");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        if (dateParts[0].equals(currentDate)) {
            dateCompleted.setText(dateParts[1].substring(0, 5));
        } else {
            dateCompleted.setText(dateParts[0].substring(5, 10).replace('-', '/'));
        }

        TextView completedBy = (TextView) findViewById(R.id.completed_by);
        completedBy.setText(task.Technician);

        TextView numVials = (TextView) findViewById(R.id.num_vials);
        numVials.setText("vials: " + task.VialCount);
    }
}
