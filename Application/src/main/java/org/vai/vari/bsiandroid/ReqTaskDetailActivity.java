package org.vai.vari.bsiandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.List;


public class ReqTaskDetailActivity extends AppCompatActivity {
    private ReqTaskDetailAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        // Fetch the item to display from bundle
        final ReqTaskItem task = (ReqTaskItem) getIntent().getSerializableExtra("task");
        mAdapter = new ReqTaskDetailAdapter(task);
        RecyclerView boxListView = (RecyclerView)findViewById(R.id.boxList);
        boxListView.setAdapter(mAdapter);
        boxListView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.addBox(null);
        new VialsQueryAsync(){
            @Override
            protected void onPostExecute(List<Box> boxes) {
                mAdapter.removeBox(0);
                mAdapter.addBoxes(boxes);
            }
        }.execute(task);
    }
}
