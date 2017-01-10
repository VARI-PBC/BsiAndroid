package org.vai.vari.bsiandroid;

import java.util.Map;


public abstract class ReportAdapter extends android.widget.BaseAdapter {
    public Map<String, Object> mDataSet;

    @Override
    public int getCount() {
        if (mDataSet == null) return 0;

        Object[] rows = (Object[])mDataSet.get("rows");
        return rows.length;
    }

    @Override
    public Object getItem(int position) {
        if (mDataSet == null) return null;

        Object[] rows = (Object[])mDataSet.get("rows");
        return rows[position];
    }

    @Override
    public long getItemId(int position) {
        if (mDataSet == null) return 0;

        return position;
    }
}
