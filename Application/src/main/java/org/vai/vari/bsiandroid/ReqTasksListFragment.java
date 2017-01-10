package org.vai.vari.bsiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;


public class ReqTasksListFragment extends Fragment {

    String mTaskType;

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
    private ReportAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mTaskType = getArguments().getString("taskType");
        mAdapter = new ReqTaskReportAdapter();
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

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

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the adapter between the ListView and its backing data.
        mListView.setAdapter(mAdapter);
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
        // Start the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(true);

        Calendar date_cutoff = GregorianCalendar.getInstance();
        date_cutoff.add(Calendar.DAY_OF_MONTH, -7);
        new ReqTasksAsync(){
            @Override
            protected void onPostExecute(Map<String, Object> result) {

                // Stop the refreshing indicator
                mSwipeRefreshLayout.setRefreshing(false);

                String error = (String)result.get("error");
                if (error != null) {
                    if (error.contains("Invalid session ID") ||
                            error.contains("Your session has expired")) {
                        Intent intent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
                        startActivityForResult(intent, MainActivity.LOGIN_REQUEST);
                    } else {
                        Toast.makeText(getActivity().getBaseContext(), error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    mAdapter.mDataSet = result;
                    mAdapter.notifyDataSetChanged();
                }
            }
        }.execute(DateFormat.getDateInstance(DateFormat.SHORT).format(date_cutoff.getTime()), mTaskType);
    }

}
