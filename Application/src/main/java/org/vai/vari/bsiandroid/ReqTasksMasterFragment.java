package org.vai.vari.bsiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ReqTasksMasterFragment extends Fragment {

    String mTaskType;
    private RecyclerView mRecyclerView;
    private ReqTasksMasterAdapter mAdapter;
    private boolean mIsLargeLayout = false;
    private Calendar mQueryStartDate;
    private Calendar mQueryEndDate;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private static final int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    static ReqTasksMasterFragment newInstance(String taskType) {
        ReqTasksMasterFragment f = new ReqTasksMasterFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("taskType", taskType);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mTaskType = getArguments().getString("taskType");
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_req_tasks_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mAdapter = new ReqTasksMasterAdapter();
        mRecyclerView.setAdapter(mAdapter);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // Retrieve item based on position
                ReqTaskItem task = mAdapter.getItem(position);
                // Fire selected listener event with item
                mAdapter.setSelectedPostition(position);
                mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount()-1);
                onItemSelected(task);
            }
        });

        final LinearLayoutManager lm = new LinearLayoutManager(container.getContext());
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = lm.getItemCount();
                lastVisibleItem = lm.findLastVisibleItemPosition();
                if (!loading
                        && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    // End has been reached
                    loadMoreRequisitionTasks();
                    loading = true;
                }
            }
        });

        if (view.findViewById(R.id.flDetailContainer) != null) {
            mIsLargeLayout = true;
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshRequisitionTasks();
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
        // clear detail view
        if (mIsLargeLayout) { // single activity with list and detail
            ReqTaskDetailFragment fragmentItem = ReqTaskDetailFragment.newInstance(null);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.flDetailContainer, fragmentItem);
            ft.commit();
        }

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

    private void fetchRequisitionTasks(Date startDate, Date endDate) {
        // add a null entry so the adapter will check view_type and show progress bar at bottom
        mAdapter.addTask(null);
        loading = true;

        new ReqTasksQueryAsync(){
            @Override
            protected void onPostExecute(List<ReqTaskItem> tasks) {
                mAdapter.removeTask(mAdapter.getItemCount()-1);

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
                }

                // Stop the progress indicator
                loading = false;

            }
        }.execute(mTaskType,
                DateFormat.getDateInstance(DateFormat.SHORT).format(startDate),
                DateFormat.getDateInstance(DateFormat.SHORT).format(endDate));
    }

    private void onItemSelected(ReqTaskItem task) {
        if (mIsLargeLayout) { // single activity with list and detail
            // Replace FrameLayout with new detail fragment
            ReqTaskDetailFragment fragmentItem = ReqTaskDetailFragment.newInstance(task);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.flDetailContainer, fragmentItem);
            ft.commit();
        } else if (task != null) { // go to separate activity
            Intent i = new Intent(getContext(), ReqTaskDetailActivity.class);
            i.putExtra("task", task);
            startActivity(i);
        }
    }
}