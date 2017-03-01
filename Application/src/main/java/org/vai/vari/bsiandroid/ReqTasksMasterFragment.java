package org.vai.vari.bsiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


public class ReqTasksMasterFragment extends Fragment {

    String mTaskType;
    private RecyclerView mRecyclerView;
    private ReqTasksMasterAdapter mAdapter;
    private Calendar mQueryStartDate;
    private Calendar mQueryEndDate;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private boolean loading;
    private RecyclerView.OnScrollListener mOnScrollListener;

    static ReqTasksMasterFragment newInstance(String taskType) {
        ReqTasksMasterFragment f = new ReqTasksMasterFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("taskType", taskType);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("taskType", mTaskType);
        outState.putSerializable("startDate", mQueryStartDate);
        outState.putSerializable("endDate", mQueryEndDate);
        List<ReqTaskItem> tasks = mAdapter.getTasks();
        outState.putSerializable("tasks", new ArrayList<>(tasks));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_req_tasks_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mAdapter = new ReqTasksMasterAdapter();
        mRecyclerView.setAdapter(mAdapter);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // Retrieve item based on position
                ReqTaskItem task = mAdapter.getItem(position);
                mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount()-1);
                onItemSelected(task);
            }
        });

        final LinearLayoutManager lm = new LinearLayoutManager(container.getContext());
        mRecyclerView.setLayoutManager(lm);

        if (savedInstanceState != null) {
            mTaskType = savedInstanceState.getString("taskType");
            mQueryStartDate = (Calendar) savedInstanceState.getSerializable("startDate");
            mQueryEndDate = (Calendar) savedInstanceState.getSerializable("endDate");
            @SuppressWarnings("unchecked")
            Collection<ReqTaskItem> tasks = (Collection<ReqTaskItem>) savedInstanceState.getSerializable("tasks");
            if (tasks != null) {
                mAdapter.addTasks(tasks);
                if (tasks.contains(null)) {
                    fetchRequisitionTasks(mQueryStartDate.getTime(), mQueryEndDate.getTime());
                }
            }
        } else {
            mTaskType = getArguments().getString("taskType");
        }

        mOnScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!loading && !recyclerView.canScrollVertically(1)) {
                    loadMoreRequisitionTasks();
                    loading = true;
                }
            }
        };
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mOnScrollListener != null) {
            mRecyclerView.removeOnScrollListener(mOnScrollListener);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            refreshRequisitionTasks();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshRequisitionTasks();
                return false;
        }
        return false;
    }


    private void refreshRequisitionTasks() {
        // clear all tasks from list
        mAdapter.clearTasks();
        mRecyclerView.removeAllViews();

        mQueryStartDate = GregorianCalendar.getInstance();
        mQueryEndDate = GregorianCalendar.getInstance();
        mQueryStartDate.add(Calendar.DAY_OF_MONTH, -7);

        fetchRequisitionTasks(mQueryStartDate.getTime(), mQueryEndDate.getTime());
    }

    private void loadMoreRequisitionTasks() {
        mQueryStartDate.add(Calendar.DAY_OF_MONTH, -7);
        mQueryEndDate.add(Calendar.DAY_OF_MONTH, -7);

        fetchRequisitionTasks(mQueryStartDate.getTime(), mQueryEndDate.getTime());
    }

    private void fetchRequisitionTasks(final Date startDate, final Date endDate) {
        // add a null entry so the adapter will check view_type and show progress bar at bottom
        mAdapter.addTask(null);
        loading = true;

        new ReqTasksQueryAsync(){
            @Override
            protected void onPostExecute(List<ReqTaskItem> tasks) {
                mAdapter.removeTask(null);

                if (ex != null) {
                    if (ex.getMessage().contains("Invalid session ID") ||
                            ex.getMessage().contains("Your session has expired")) {
                        Intent intent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
                        startActivityForResult(intent, MainActivity.LOGIN_REQUEST);
                    } else {
                        Toast.makeText(getActivity().getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Collections.sort(tasks, new Comparator<ReqTaskItem>() {
                        @Override
                        public int compare(ReqTaskItem task1, ReqTaskItem task2) {
                            return task2.TaskEndTime.compareTo(task1.TaskEndTime);
                        }
                    });
                    mAdapter.addTasks(tasks);
                    Toast.makeText(getActivity().getBaseContext(), "" + tasks.size() + " tasks fetched", Toast.LENGTH_LONG).show();
                }

                // Stop the progress indicator
                loading = false;

            }
        }.execute(new HashMap<String, Object>() {{
            put("value", mTaskType);
            put("operator", "equals");
            put("field", ReqTasksQueryAsync.TASK_TYPE);
        }}, new HashMap<String, Object>() {{
            put("value", DateFormat.getDateInstance(DateFormat.SHORT).format(startDate));
            put("operator", "greater");
            put("field", ReqTasksQueryAsync.END_TIME);
        }}, new HashMap<String, Object>() {{
            put("value", DateFormat.getDateInstance(DateFormat.SHORT).format(endDate));
            put("operator", "less or equals");
            put("field", ReqTasksQueryAsync.END_TIME);
        }}, new HashMap<String, Object>() {{
            put("value", "@@Missing");
            put("operator", "not equals");
            put("field", ReqTasksQueryAsync.BSI_ID);
        }});
    }

    private void onItemSelected(ReqTaskItem task) {
        if (task != null) {
            Intent i = new Intent(getContext(), ReqTaskDetailActivity.class);
            i.putExtra("task", task);
            startActivity(i);
        }
    }
}
