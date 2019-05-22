package com.umbrella.bureaux.myweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.search.Search;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class SearchActivity extends AppCompatActivity implements SearchClickAdapter.OnSearchClickListener{

    private SearchView mSearchView;
    private String TAG = "HEtest";
    private List<CitySearchItemClass> mList = new ArrayList<>();
    private LinearLayout linearLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;//获取编辑器
    private Set<String> stored_city = new HashSet<String>();
    private LinearLayout licAtPre;
    private TextView licAtPreText;
    private String NowCity;
    private ScrollView scrollView;
    private Button Beijing;
    private Button Shanghai;
    private Button Guangzhou;
    private Button Hongkong;
    private Button Shenzheng;
    private Button Teipei;
    private Button Hangzhou;
    private Button Suzhou;
    private Button Macao;
    private Button NewYork;
    private Button London;
    private Button Paris;
    private Button Sydney;
    private Button Tokyo;
    private Button Dubai;
    private Button Cario;
    private Button Rio;
    private Button Bangkok;
    private View view;
    private TextView t1;
    private TextView t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("搜索");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mSearchView =  findViewById(R.id.searchView);
        linearLayout=findViewById(R.id.father);
        licAtPre=findViewById(R.id.licAtPre);
        licAtPreText=findViewById(R.id.licAtPreText);



        try {        //--拿到字节码
            Class<?> argClass = mSearchView.getClass();
            //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
            Field ownField = argClass.getDeclaredField("mSearchPlate");
            //--暴力反射,只有暴力反射才能拿到私有属性
            ownField.setAccessible(true);
            View mView = (View) ownField.get(mSearchView);
            //--设置背景
            mView.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }

        initButtons();

        //设置暗色主题
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour;    // 0-23
        Log.i(TAG, "time: " + hour);
        if (!(hour >= 6 && hour <= 18)) {
            NightView();
        }

        HeWeather.getSearch(SearchActivity.this, new HeWeather.OnResultSearchBeansListener() {

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "onError1: "+throwable);
                licAtPreText.setText("当前位置: 定位中...");

            }

            @Override
            public void onSuccess(Search search) {
                String strByJson = new Gson().toJson(search.getBasic().get(0).getLocation());
                Log.i(TAG, "getCities" + strByJson);
                NowCity=strByJson.substring(1,strByJson.length()-1);
                licAtPreText.setText("当前位置: "+ NowCity);
                final String CID=search.getBasic().get(0).getCid();
                licAtPre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchActivity.this, WeatherActivity.class);

                        //用Bundle携带数据
                        Bundle bundle = new Bundle();
                        //传递name参数为tinyphp
                        bundle.putString("city", CID);
                        intent.putExtras(bundle);
                        Store(CID);
                        SearchActivity.this.finish();
                        startActivity(intent);
                    }
                });
            }
        });



        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {

                if (!TextUtils.isEmpty(newText)){
                    linearLayout.setVisibility(View.VISIBLE);
                    licAtPre.setVisibility(View.GONE);
                    HeWeather.getSearch(SearchActivity.this, newText,"world",20 , Lang.CHINESE_SIMPLIFIED, new HeWeather.OnResultSearchBeansListener() {

                        @Override
                        public void onError(Throwable throwable) {
                            Log.i(TAG, "getWeatherCity onError: ", throwable);
                            mList.clear();
                            Toast.makeText(SearchActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                            linearLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess(Search search) {

                            String strByJson = new Gson().toJson(search);
                            Log.i(TAG, "getCities" + strByJson);
                            JsonObject jsonObject = new JsonParser().parse(strByJson).getAsJsonObject();
                            JsonArray jsonArray = jsonObject.getAsJsonArray("basic");
                            if(jsonArray==null) {
                                mList.clear();
                                linearLayout.setVisibility(View.GONE);
                            return ;
                            }
                            Gson gson = new Gson();
                            mList.clear();
                            for (JsonElement user : jsonArray) {
                                //使用GSON，直接转成Bean对象
                                CitySearchItemClass citySearchItemClass = gson.fromJson(user, CitySearchItemClass.class);
                                mList.add(citySearchItemClass);
                            }

                            initView();

                        }
                    });
                }else{
                    mList.clear();
                    linearLayout.setVisibility(View.GONE);
                    licAtPre.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

    }

    private void initView() {
        SearchClickAdapter adapter = new SearchClickAdapter(this,this);

        RecyclerView rcvClick = findViewById(R.id.RVCITY);

        rcvClick.setLayoutManager(new LinearLayoutManager(this));
        rcvClick.setHasFixedSize(true);
        rcvClick.setAdapter(adapter);

        adapter.setRcvClickDataList(mList);
    }

    @Override
    public void onItemClick1(CitySearchItemClass content) {
        Intent intent = new Intent(SearchActivity.this, WeatherActivity.class);

        //用Bundle携带数据
        Bundle bundle = new Bundle();
        //传递name参数为tinyphp
        bundle.putString("city", content.getCid());
        intent.putExtras(bundle);
        Log.i(TAG, "CID-record: "+content.getCid());

        //储存
        Store(content.getCid());
        this.finish();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void Store (String s) {
        sharedPreferences = getSharedPreferences("stored_city", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Set res = new HashSet();
        stored_city = sharedPreferences.getStringSet("stored_city", res);

        stored_city.add(s);
        editor.clear();
        editor.putStringSet("stored_city", stored_city);
        editor.commit();
    }

    public void initButtons() {
        t2=findViewById(R.id.t2);
        t1=findViewById(R.id.t1);
        view=findViewById(R.id.fgx);
        scrollView=findViewById(R.id.bcg);
        Beijing=findViewById(R.id.Beijing);
        Shanghai=findViewById(R.id.Shanghai);
        Guangzhou=findViewById(R.id.Guangzhou);
        Hongkong=findViewById(R.id.Hongkong);
        Shenzheng=findViewById(R.id.Shenzheng);
        Teipei=findViewById(R.id.Taipei);
        Hangzhou=findViewById(R.id.Hangzhou);
        Suzhou=findViewById(R.id.Suzhou);
        Macao=findViewById(R.id.Macao);
        NewYork=findViewById(R.id.NewYork);
        London=findViewById(R.id.London);
        Paris=findViewById(R.id.Paris);
        Sydney=findViewById(R.id.Sydney);
        Tokyo=findViewById(R.id.Tokyo);
        Dubai=findViewById(R.id.Dubai);
        Cario=findViewById(R.id.Cario);
        Rio=findViewById(R.id.Rio);
        Bangkok=findViewById(R.id.Bankok);

        Beijing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("CN101010100");
                Store("CN101010100");
            }
        });

        Shanghai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("CN101020100");
                Store("CN101020100");
            }
        });

        Guangzhou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("CN101280101");
                Store("CN101280101");
            }
        });

        Hongkong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("CN101320101");
                Store("CN101320101");
            }
        });

        Shenzheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("CN101280601");
                Store("CN101280601");
            }
        });

        Teipei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("CN101340101");
                Store("CN101340101");
            }
        });

        Hangzhou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("CN101210101");
                Store("CN101210101");
            }
        });

        Suzhou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("CN101190401");
                Store("CN101190401");
            }
        });

        Macao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("CN101330101");
                Store("CN101330101");
            }
        });

        NewYork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("US3290117");
                Store("US3290117");
            }
        });

        London.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("GB2643741");
                Store("GB2643741");
            }
        });

        Paris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("FR2988507");
                Store("FR2988507");
            }
        });

        Sydney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("AU2147714");
                Store("AU2147714");
            }
        });

        Tokyo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("JP1850147");
                Store("JP1850147");
            }
        });

        Dubai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("AE292223");
                Store("AE292223");
            }
        });

        Cario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("EG360630");
                Store("EG360630");
            }
        });

        Rio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("BR3451190");
                Store("BR3451190");
            }
        });

        Bangkok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Jump("TH1609350");
                Store("TH1609350");
            }
        });
        Bangkok.setBackgroundResource(R.drawable.corners_bg);

    }

    protected void Jump(String cid) {
        Intent intent = new Intent(SearchActivity.this, WeatherActivity.class);

        //用Bundle携带数据
        Bundle bundle = new Bundle();
        //传递name参数为tinyphp
        bundle.putString("city", cid);
        intent.putExtras(bundle);
        this.finish();
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: "+"1");
        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "GPS权限获取失败, 定位无法使用", Toast.LENGTH_LONG).show();
        } else {
            Log.i(TAG, "lll"+"sucess");
        }
        HeWeather.getSearch(SearchActivity.this, new HeWeather.OnResultSearchBeansListener() {

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "onError1: "+throwable);
                licAtPreText.setText("当前位置: 定位失败");

            }

            @Override
            public void onSuccess(Search search) {
                String strByJson = new Gson().toJson(search.getBasic().get(0).getLocation());
                Log.i(TAG, "getCities" + strByJson);
                NowCity=strByJson.substring(1,strByJson.length()-1);
                licAtPreText.setText("当前位置: "+ NowCity);
                final String CID=search.getBasic().get(0).getCid();
                licAtPre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchActivity.this, WeatherActivity.class);

                        //用Bundle携带数据
                        Bundle bundle = new Bundle();
                        //传递name参数为tinyphp
                        bundle.putString("city", CID);
                        intent.putExtras(bundle);
                        Store(CID);
                        SearchActivity.this.finish();
                        startActivity(intent);
                    }
                });
            }
        });
    }

    public void NightView () {
        scrollView.setBackgroundColor(Color.rgb(28, 33, 42));
        licAtPreText.setTextColor(Color.rgb(255, 255, 255));
        view.setBackgroundResource(R.color.Gallery);
        t1.setTextColor(Color.rgb(255, 255, 255));
        t2.setTextColor(Color.rgb(255, 255, 255));
        Beijing.setBackgroundResource(R.drawable.bg_dark);
        Beijing.setTextColor(Color.rgb(255, 255, 255));
        Shanghai.setBackgroundResource(R.drawable.bg_dark);
        Shanghai.setTextColor(Color.rgb(255, 255, 255));
        Guangzhou.setBackgroundResource(R.drawable.bg_dark);
        Guangzhou.setTextColor(Color.rgb(255, 255, 255));
        Hongkong.setBackgroundResource(R.drawable.bg_dark);
        Hongkong.setTextColor(Color.rgb(255, 255, 255));
        Shenzheng.setBackgroundResource(R.drawable.bg_dark);
        Shenzheng.setTextColor(Color.rgb(255, 255, 255));
        Teipei.setBackgroundResource(R.drawable.bg_dark);
        Teipei.setTextColor(Color.rgb(255, 255, 255));
        Hangzhou.setBackgroundResource(R.drawable.bg_dark);
        Hangzhou.setTextColor(Color.rgb(255, 255, 255));
        Suzhou.setBackgroundResource(R.drawable.bg_dark);
        Suzhou.setTextColor(Color.rgb(255, 255, 255));
        Macao.setBackgroundResource(R.drawable.bg_dark);
        Macao.setTextColor(Color.rgb(255, 255, 255));
        NewYork.setBackgroundResource(R.drawable.bg_dark);
        NewYork.setTextColor(Color.rgb(255, 255, 255));
        London.setBackgroundResource(R.drawable.bg_dark);
        London.setTextColor(Color.rgb(255, 255, 255));
        Paris.setBackgroundResource(R.drawable.bg_dark);
        Paris.setTextColor(Color.rgb(255, 255, 255));
        Sydney.setBackgroundResource(R.drawable.bg_dark);
        Sydney.setTextColor(Color.rgb(255, 255, 255));
        Tokyo.setBackgroundResource(R.drawable.bg_dark);
        Tokyo.setTextColor(Color.rgb(255, 255, 255));
        Dubai.setBackgroundResource(R.drawable.bg_dark);
        Dubai.setTextColor(Color.rgb(255, 255, 255));
        Cario.setBackgroundResource(R.drawable.bg_dark);
        Cario.setTextColor(Color.rgb(255, 255, 255));
        Rio.setBackgroundResource(R.drawable.bg_dark);
        Rio.setTextColor(Color.rgb(255, 255, 255));
        Bangkok.setBackgroundResource(R.drawable.bg_dark);
        Bangkok.setTextColor(Color.rgb(255, 255, 255));
    }
}
