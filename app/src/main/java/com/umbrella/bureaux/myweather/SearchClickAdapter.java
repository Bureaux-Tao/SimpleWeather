package com.umbrella.bureaux.myweather;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchClickAdapter extends RecyclerView.Adapter<SearchClickAdapter.RcvClickViewHolder>{
    private static final String TAG = RcvClickAdapter.class.getSimpleName();

    private Context mContext;
    private OnSearchClickListener mListener;

    public SearchClickAdapter(Context context, OnSearchClickListener listener) {
        mContext = context;

        mListener = listener;
    }


    private List<CitySearchItemClass> mList = new ArrayList<>();

    public SearchClickAdapter(Context context) {
        mContext = context;

    }

    public void setRcvClickDataList(List<CitySearchItemClass> list) {
        Log.d(TAG, "setRcvClickDataList: " + list.size());

        mList = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RcvClickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_city_search_item, parent, false);
        return new RcvClickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RcvClickViewHolder holder, int position) {
        final CitySearchItemClass content = mList.get(position);
//        Log.i(TAG, "onBindViewHolder: "+content.getLocation().equals(content.getParentCity()));
        if(content.getLocation().equals(content.getParentCity())) {
            holder.tvContent.setText(content.getLocation()+"  "+content.getAdminArea()+"  "+content.getCnty());
        } else if(content.getAdminArea().equals(content.getParentCity())) {
            holder.tvContent.setText(content.getLocation()+"  "+content.getAdminArea()+"  "+content.getCnty());
        } else {
            holder.tvContent.setText(content.getLocation()+"  "+content.getParentCity()+"  "+content.getAdminArea()+"  "+content.getCnty());
        }

        //设置暗色主题
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour;    // 0-23
        Log.i(TAG, "time: " + hour);
        if (!(hour >= 6 && hour <= 18)) {
            holder.tvContent.setTextColor(Color.rgb(255, 255, 255));
            holder.view.setBackgroundResource(R.color.Gallery);
        }

        final String pass=content.getCid();
//        Log.i(TAG, "CID-record: "+pass);

        // 第二种写法：将点击事件传到 Activity 里去写
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick1(content);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class RcvClickViewHolder extends RecyclerView.ViewHolder {

        TextView  tvContent;
        View view;

        public RcvClickViewHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            view=itemView.findViewById(R.id.fgx2);
        }
    }

    public interface OnSearchClickListener {
        void onItemClick1(CitySearchItemClass content);
    }

}
