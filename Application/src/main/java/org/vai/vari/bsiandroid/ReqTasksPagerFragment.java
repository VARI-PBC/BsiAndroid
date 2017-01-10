package org.vai.vari.bsiandroid;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class ReqTasksPagerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_req_tasks_pager, container, false);
        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(new ReqTaskPagerAdapter(getChildFragmentManager()));
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
