package org.vai.vari.bsiandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ReqTaskDetailFragment extends Fragment {
    private ReqTaskItem mTask;
    private ReqTaskDetailAdapter mAdapter;

    public static ReqTaskDetailFragment newInstance(ReqTaskItem task) {
        ReqTaskDetailFragment fragment = new ReqTaskDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("task", task);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTask = (ReqTaskItem) getArguments().getSerializable("task");
        if (mTask == null) return;
        mAdapter = new ReqTaskDetailAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mTask == null) return null;

        final View view = inflater.inflate(R.layout.fragment_task_detail,
                container, false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(mAdapter);
        TextView reqNotes = (TextView)view.findViewById(R.id.reqNotes);
        reqNotes.setText(mTask.Notes);
        TextView instructions = (TextView)view.findViewById(R.id.taskInstructions);
        instructions.setText(mTask.TaskInstructions);
        final ProgressBar loading = (ProgressBar)view.findViewById(R.id.progressBar2);
        loading.setVisibility(View.VISIBLE);
        new VialsQueryAsync(){
            @Override
            protected void onPostExecute(Box[] boxes) {
                mAdapter.setData(boxes);
                loading.setVisibility(View.INVISIBLE);
            }
        }.execute(mTask);

        return view;
    }
}
