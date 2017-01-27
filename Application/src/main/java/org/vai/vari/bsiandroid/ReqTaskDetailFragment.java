package org.vai.vari.bsiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


public class ReqTaskDetailFragment extends Fragment {
    private ReqTaskItem task;

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
        task = (ReqTaskItem) getArguments().getSerializable("task");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (task == null) return null;

        final View view = inflater.inflate(R.layout.fragment_task_detail,
                container, false);

        // Retrieve the ListView
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        // Set the adapter between the ListView and its backing data.
        ReqTaskDetailAdapter adapter = new ReqTaskDetailAdapter(task);
        listView.setAdapter(adapter);

        return view;
    }
}
