package com.umbrella.bureaux.myweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zaaach.citypicker.CityPickerActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.search.Search;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RcvClickAdapter.OnItemClickListener {
    private View view;

    private String TAG = "HEtest";
    private static final int LOCATION_CODE = 1;
    private LocationManager lm;//【位置管理】
    private List<CityList> mList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;//获取编辑器
    private Set<String> stored_city = new HashSet<String>();
    RcvClickAdapter adapter;
    private String BUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        //After LOLLIPOP not translucent status bar
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //Then call setStatusBarColor.
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.Cornflower_Blue));
        //设置暗色主题
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour;    // 0-23
        view = findViewById(R.id.page);
        Log.i(TAG, "time: " + hour);
        if (hour >= 6 && hour <= 18) {
            view.setBackgroundColor(Color.rgb(239, 239, 239));
        } else {
            view.setBackgroundColor(Color.rgb(29, 33, 41));
        }


        sharedPreferences = getSharedPreferences("stored_city", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        HeConfig.init("HE1905022236021729", "196e15a0389445cabe23cb094344d456");
//        quanxian();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        /****************浮动按钮点击事件*****************/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mList.size()<=30) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setClass(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this,  "您至多可以添加30个城市", Toast.LENGTH_LONG).show();
                }

            }
        });
        /****************浮动按钮点击事件*****************/


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

//        initData();
//        initView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i(TAG, "onOptionsItemSelected: " + getLogcatInfo());
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:taosh.dev@gmail.com"));
            data.putExtra(Intent.EXTRA_SUBJECT, "BUG REPORT");
            data.putExtra(Intent.EXTRA_TEXT, getLogcatInfo());
            startActivity(data);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Uri uri = Uri.parse("https://www.heweather.com/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Uri uri = Uri.parse("http://www.newexploration.cn:20011/#/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Uri uri = Uri.parse("http://www.newexploration.cn/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Uri uri = Uri.parse("https://github.com/Bureaux-Tao");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initData() {
        mList.clear();
        Set res = new HashSet();
        stored_city = sharedPreferences.getStringSet("stored_city", res);
        Log.i(TAG, "ontip: " + stored_city);
        Log.i(TAG, "ontip: " + res);
        for (String str : stored_city) {
            System.out.println(str);
            CityList cityList = new CityList();
            cityList.setCity(str);
            mList.add(cityList);
        }
        if (mList.size() == 0) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setClass(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }
    }

    private void initView() {
        adapter = new RcvClickAdapter(this, this);

        RecyclerView rcvClick = findViewById(R.id.cityRV);

        rcvClick.setLayoutManager(new LinearLayoutManager(this));
        rcvClick.setHasFixedSize(true);
        rcvClick.setAdapter(adapter);

        adapter.setRcvClickDataList(mList);
    }

    @Override
    public void onItemClick(CityList content) {
        HeWeather.getSearch(MainActivity.this, content.getCity(),"world",20 , Lang.CHINESE_SIMPLIFIED, new HeWeather.OnResultSearchBeansListener(){

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(MainActivity.this, "发送未知错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Search search) {
                Toast.makeText(MainActivity.this, search.getBasic().get(0).getLocation() + "已被移除", Toast.LENGTH_SHORT).show();
            }
        });
        mList.remove(mList.indexOf(content));
        stored_city.remove(content.getCity());
        if (mList.size() == 0) {

            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setClass(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }
        initView();

    }

    @Override
    protected void onStop() {
        editor.clear();
        editor.putStringSet("stored_city", stored_city);
        editor.commit();
        Log.i(TAG, "ontip: " + stored_city);
        super.onStop();
    }

    private static String getLogcatInfo() {
        String strLogcatInfo = "";
        Process process;
        try {
            ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("logcat");
            commandLine.add("-d");

            commandLine.add("*:E"); // 过滤所有的错误信息

            ArrayList<String> clearLog = new ArrayList<String>();  //设置命令  logcat -c 清除日志
            clearLog.add("logcat");
            clearLog.add("-c");

            process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));
                strLogcatInfo = strLogcatInfo + line + "\n";
            }

            bufferedReader.close();
        } catch (Exception ex) {
            strLogcatInfo = strLogcatInfo + ex + "\n";
        }
        return strLogcatInfo;
    }

    @Override
    protected void onResume() {
        initData();
        initView();
        super.onResume();
    }
}
