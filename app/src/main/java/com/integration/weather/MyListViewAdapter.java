package com.integration.weather;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.integration.weather.db.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wongerfeng on 2019/7/12.
 */
public class MyListViewAdapter extends BaseAdapter {

    private List<Province> mDataList = new ArrayList<>();
    private Activity mActivity;

    private MyListViewAdapter(List<Province> dataList, Activity activity) {
        mDataList = dataList;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_item, null);
            viewHolder.mTextView = convertView.findViewById(R.id.chooseItem);


        }else {

        }

        Province item = (Province) getItem(position);

        if (item != null) {
            viewHolder.mTextView.setText(item.getProvinceName());
        }

        return convertView;
    }


    class ViewHolder{
        TextView mTextView;

        private ViewHolder() {
        }

        private ViewHolder(View view) {
            mTextView = view.findViewById(R.id.chooseItem);
        }
    }
}
