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
 * 纵向布局 的 Adapter
 */

public class VerticalAdapter extends RecyclerView.Adapter<VerticalAdapter.VerticalViewHolder> {

    private static final String TAG = VerticalAdapter.class.getSimpleName();

    private Context mContext;

    private List<ForecastList> mList = new ArrayList<>();

    public VerticalAdapter(Context context) {
        mContext = context;
    }

    public void setVerticalDataList(List<ForecastList> list) {
        Log.d(TAG, "setVerticalDataList: " + list.size());

        mList = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_vertical_recycle_item, parent, false);
        return new VerticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalViewHolder holder, int position) {

        holder.date.setText(mList.get(position).getDate()+' ');
        holder.cond_txt.setText(mList.get(position).getCondTxtD()+' '+'转'+' '+mList.get(position).getCondTxtN());
        holder.tmp_7.setText(mList.get(position).getTmpMin()+"° ~ "+mList.get(position).getTmpMax()+'°');
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {

        TextView date,tmp_7,cond_txt;

        public VerticalViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            tmp_7 = itemView.findViewById(R.id.tmp_7);
            cond_txt = itemView.findViewById(R.id.cond_txt);
        }
    }
}