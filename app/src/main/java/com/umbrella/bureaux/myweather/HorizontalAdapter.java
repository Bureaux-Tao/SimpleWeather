package com.umbrella.bureaux.myweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 * 横向布局 的 Adapter
 */

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.HorizontalViewHolder> {

    private static final String TAG = HorizontalAdapter.class.getSimpleName();

    private Context mContext;

    private List<HourlyBean> mList = new ArrayList<>();

    public HorizontalAdapter(Context context) {
        mContext = context;
    }

    public void setHorizontalDataList(List<HourlyBean> list) {
        Log.d(TAG, "setHorizontalDataList: " + list.size());

        mList = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_item_home, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        holder.time.setText(mList.get(position).getTime().substring(11,16));
        holder.weather.setText(mList.get(position).getCondTxt());
        holder.poss.setText("降水:"+mList.get(position).getPop()+'%');
        holder.temperature.setText(mList.get(position).getTmp()+"°C");

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {

        TextView time,weather,temperature,poss;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            weather = itemView.findViewById(R.id.weather);
            temperature = itemView.findViewById(R.id.tpt);
            poss = itemView.findViewById(R.id.poss);
        }
    }
}