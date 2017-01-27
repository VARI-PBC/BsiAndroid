package org.vai.vari.bsiandroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;


public class ReqTasksListFragment extends Fragment {

    String mTaskType;
    private boolean mIsLargeLayout = false;

    static ReqTasksListFragment newInstance(String taskType) {
        ReqTasksListFragment f = new ReqTasksListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("taskType", taskType);
        f.setArguments(args);

        return f;
    }

    /**
     * The {@link android.support.v4.widget.SwipeRefreshLayout} that detects swipe gestures and
     * triggers callbacks in the app.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * The {@link android.widget.ListView} that displays the content that should be refreshed.
     */
    private ListView mListView;

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

        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        // Retrieve the ListView
        mListView = (ListView) view.findViewById(android.R.id.list);
        /**
         * Implement {@link SwipeRefreshLayout.OnRefreshListener}. When users do the "swipe to
         * fetchRequisitionTasks" gesture, SwipeRefreshLayout invokes
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}. In
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}, call a method that
         * refreshes the content. Call the same method in response to the Refresh action from the
         * action bar.
         */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRequisitionTasks();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item,
                                    int position, long rowId) {
                // Retrieve item based on position
                ReqTaskItem task = (ReqTaskItem)adapterView.getAdapter().getItem(position);
                // Fire selected listener event with item
                onItemSelected(task);

                //item.setSelected(true);
            }
        });

        if (view.findViewById(R.id.flDetailContainer) != null) {
            mIsLargeLayout = true;
            setActivateOnItemClick(true);
        }

        return view;
    }

    private void onItemSelected(ReqTaskItem task) {
        if (mIsLargeLayout) { // single activity with list and detail
            // Replace framelayout with new detail fragment
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fetchRequisitionTasks();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                fetchRequisitionTasks();
                return false;
        }
        return false;
    }


    private void fetchRequisitionTasks() {
        if (mIsLargeLayout) { // single activity with list and detail
            ReqTaskDetailFragment fragmentItem = ReqTaskDetailFragment.newInstance(null);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.flDetailContainer, fragmentItem);
            ft.commit();
        }

        // Start the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(true);

        Calendar date_cutoff = GregorianCalendar.getInstance();
        date_cutoff.add(Calendar.DAY_OF_MONTH, -14);
        final Context context = this.getContext();
        String dateCutoff = DateFormat.getDateInstance(DateFormat.SHORT).format(date_cutoff.getTime());
        new ReqTasksAsync(){
            @Override
            protected void onPostExecute(Map<String, ReqTaskItem> tasks) {

                if (ex != null) {
                    if (ex.getMessage().contains("Invalid session ID") ||
                            ex.getMessage().contains("Your session has expired")) {
                        Intent intent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
                        startActivityForResult(intent, MainActivity.LOGIN_REQUEST);
                    } else {
                        Toast.makeText(getActivity().getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Set the adapter between the ListView and its backing data.
                    List<ReqTaskItem> taskList = new ArrayList<>(tasks.values());
                    Collections.sort(taskList, new Comparator<ReqTaskItem>() {
                        @Override
                        public int compare(ReqTaskItem task1, ReqTaskItem task2) {
                            return task2.TaskEndTime.compareTo(task1.TaskEndTime);
                        }
                    });
                    ReqTaskReportAdapter adapter = new ReqTaskReportAdapter(context, taskList);
                    mListView.setAdapter(adapter);
                }

                // Stop the refreshing indicator
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }.execute(dateCutoff, mTaskType);
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    private void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        mListView.setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
    }
}
