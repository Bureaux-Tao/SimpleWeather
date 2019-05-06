package com.umbrella.bureaux.myweather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

/**
 * Item 点击对应的 Adapter
 */

public class RcvClickAdapter extends RecyclerView.Adapter<RcvClickAdapter.RcvClickViewHolder> {

    private static final String TAG = RcvClickAdapter.class.getSimpleName();

    private Context mContext;

    private List<CityList> mList = new ArrayList<>();

    private OnItemClickListener mListener;

    public RcvClickAdapter(Context context, OnItemClickListener listener) {
        mContext = context;

        mListener = listener;
    }

    public void setRcvClickDataList(List<CityList> list) {
        Log.d(TAG, "setRcvClickDataList: " + list.size());

        mList = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RcvClickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_city, parent, false);
        return new RcvClickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RcvClickViewHolder holder, int position) {
        final CityList content = mList.get(position);

        switch (position % 7) {
            case 0:
                holder.linearLayout.setBackgroundResource(R.drawable.circle_0);
                break;
            case 1:
                holder.linearLayout.setBackgroundResource(R.drawable.circle_1);
                break;
            case 2:
                holder.linearLayout.setBackgroundResource(R.drawable.circle_2);
                break;
            case 3:
                holder.linearLayout.setBackgroundResource(R.drawable.circle_3);
                break;
            case 4:
                holder.linearLayout.setBackgroundResource(R.drawable.circle_4);
                break;
            case 5:
                holder.linearLayout.setBackgroundResource(R.drawable.circle_5);
                break;
            case 6:
                holder.linearLayout.setBackgroundResource(R.drawable.circle_6);
                break;
        }

        HeWeather.getWeatherNow(mContext, content.getCity(), Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "onError: " + throwable);
                Toast.makeText(mContext, "无网络连接", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Now now) {
                String a = now.getNow().getCond_txt();
                int b = Integer.parseInt(now.getNow().getTmp());
                if (a.equals("多云")) {
                    holder.imageView.setImageResource(R.drawable.icons8_partly_cloudy_day);
                } else if (a.equals("晴")) {
                    holder.imageView.setImageResource(R.drawable.icons8_sun);
                } else if (a.equals("阴")) {
                    holder.imageView.setImageResource(R.drawable.icons8_cloud);
                } else if (a.contains("雨")) {
                    holder.imageView.setImageResource(R.drawable.icons8_rainy_weather);
                } else if (a.contains("雾")) {
                    holder.imageView.setImageResource(R.drawable.icons8_fog_day);
                } else if (a.contains("雪")) {
                    holder.imageView.setImageResource(R.drawable.icons8_snow_storm);
                } else if (a.contains("雷")) {
                    holder.imageView.setImageResource(R.drawable.icons8_storm);
                } else if (a.contains("冰雹")) {
                    holder.imageView.setImageResource(R.drawable.icons8_hail);
                } else {
                    holder.imageView.setImageResource(R.drawable.icons8_question_shield);
                }

                holder.over_view_tmp.setText(now.getNow().getTmp() + "°");
            }
        });
        holder.CityName.setText(content.getCity());

        // 第一种写法：直接在 Adapter 里写
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "你点击的是：" + content.getCity(), Toast.LENGTH_SHORT).show();
                //新建一个显式意图，第一个参数为当前Activity类对象，第二个参数为你要打开的Activity类
                Intent intent = new Intent(mContext, WeatherActivity.class);

                //用Bundle携带数据
                Bundle bundle = new Bundle();
                //传递name参数为tinyphp
                bundle.putString("city", content.getCity());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        // 第二种写法：将点击事件传到 Activity 里去写
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("huahua", "单击");
//                mListener.onItemClick(content);
//            }
//        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("huahua", "长按");
                mListener.onItemClick(content);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class RcvClickViewHolder extends RecyclerView.ViewHolder {

        TextView CityName, over_view_tmp;
        ImageView imageView;
        View linearLayout;

        public RcvClickViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.icon);
            CityName = itemView.findViewById(R.id.CityName);
            over_view_tmp = itemView.findViewById(R.id.over_view_tmp);
            linearLayout = itemView.findViewById(R.id.CityItem);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CityList content);
    }
}