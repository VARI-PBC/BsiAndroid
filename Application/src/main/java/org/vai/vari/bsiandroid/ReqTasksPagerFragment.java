package org.vai.vari.bsiandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ReqTasksPagerFragment extends Fragment {

    ViewPager pager;
    ReqTaskPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_req_tasks_pager, container, false);
        pager = (ViewPager)view.findViewById(R.id.pager);
        adapter = new ReqTaskPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        return view;
    }

    private static CharSequence[] TaskTitles = new String[] { "Pull", "Return" };
    private static String[] TaskTypes = new String[] { "U", "F" };

    public static class ReqTaskPagerAdapter extends FragmentPagerAdapter {
        ReqTaskPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            return ReqTasksListFragment.newInstance(TaskTypes[position]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TaskTitles[position];
        }
    }

}
