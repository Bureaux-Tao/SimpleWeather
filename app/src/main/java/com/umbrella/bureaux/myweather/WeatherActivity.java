package com.umbrella.bureaux.myweather;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import interfaces.heweather.com.interfacesmodule.bean.Code;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.hourly.Hourly;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class WeatherActivity extends AppCompatActivity {

    private String TAG = "HEtest";
    private String city = "深圳";
    private List<HourlyBean> hourlyBeanList = new ArrayList<>();
    private List<ForecastList> ForecastBeanList = new ArrayList<>();
    private static final Pattern DIGITS_PATTERN = Pattern.compile("\\d+");
    private ScrollView scrollView;
    private ProgressDialog progressDialog;
    private boolean Z1;
    private boolean Z2;
    private boolean Z3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        String name = bundle.getString("city");
        Log.i("获取到的city值为",name);
        city=name;

        Z1 = false;
        Z2 = false;
        Z3 = false;
        buildProgressDialog();
        scrollView = (ScrollView) findViewById(R.id.bg);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(city);

        //设置暗色主题
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour;    // 0-23
        Log.i(TAG, "time: " + hour);
        if (hour >= 6 && hour <= 18) {
            scrollView.setBackgroundColor(Color.rgb(255, 255, 255));
        } else {
            scrollView.setBackgroundColor(Color.rgb(29, 33, 41));
        }

        //初始化控件
        final TextView Today = (TextView) findViewById(R.id.Today);
        final TextView Tmp = (TextView) findViewById(R.id.Tmp);
        final TextView Feel = (TextView) findViewById(R.id.Feel);
        final TextView weatherTxt = (TextView) findViewById(R.id.weatherTxt);
        final TextView visNum = (TextView) findViewById(R.id.visNum);
        final TextView HumNum = (TextView) findViewById(R.id.HumNum);
        final TextView UpdateTime = (TextView) findViewById(R.id.UpdateTime);
        final TextView windir = (TextView) findViewById(R.id.windir);
        final TextView windspd = (TextView) findViewById(R.id.windspd);
        final TextView windsc = (TextView) findViewById(R.id.windsc);
        final TextView high = (TextView) findViewById(R.id.high);
        final TextView low = (TextView) findViewById(R.id.low);
        final ImageView imageView = (ImageView) findViewById(R.id.weatherImg);
        final TextView Pcpn = (TextView) findViewById(R.id.Pcpn);
        final TextView Press = (TextView) findViewById(R.id.Press);
        final TextView getCloud = (TextView) findViewById(R.id.getCloud);
        final TextView sunrise = (TextView) findViewById(R.id.sunrise);
        final TextView sunsite = (TextView) findViewById(R.id.sunsite);


        /**
         * 实况天气
         * 实况天气即为当前时间点的天气状况以及温湿风压等气象指数，具体包含的数据：体感温度、
         * 实测温度、天气状况、风力、风速、风向、相对湿度、大气压强、降水量、能见度等。
         *
         * @param context  上下文
         * @param location 地址详解
         * @param lang     多语言，默认为简体中文
         * @param unit     单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问回调接口
         */
        HeWeather.getWeatherNow(WeatherActivity.this, city, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable e) {
                showErrorDialog(e);
                Log.i(TAG, "Weather Now onError: ", e);
            }

            @Override
            public void onSuccess(Now dataObject) {
                Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(dataObject));
                //下面开始放数据
                String a = dataObject.getNow().getCond_txt();
                int b = Integer.parseInt(dataObject.getNow().getTmp());
                if (a.equals("多云")) {
                    imageView.setImageResource(R.drawable.icons8_partly_cloudy_day);
                } else if (a.equals("晴")) {
                    imageView.setImageResource(R.drawable.icons8_sun);
                } else if (a.equals("阴")) {
                    imageView.setImageResource(R.drawable.icons8_cloud);
                } else if (a.contains("雨")) {
                    imageView.setImageResource(R.drawable.icons8_rainy_weather);
                } else if (a.contains("雾")) {
                    imageView.setImageResource(R.drawable.icons8_fog_day);
                } else if (a.contains("雪")) {
                    imageView.setImageResource(R.drawable.icons8_snow_storm);
                } else if (a.contains("雷")) {
                    imageView.setImageResource(R.drawable.icons8_storm);
                } else if (a.contains("冰雹")) {
                    imageView.setImageResource(R.drawable.icons8_hail);
                } else {
                    imageView.setImageResource(R.drawable.icons8_question_shield);
                }

                if (a.equals("多云") && (b >= 18 && b <= 30)) {
                    Feel.setText("较舒适");
                } else if (a.equals("阴")) {
                    Feel.setText("比较闷");
                } else if (a.contains("雨")) {
                    Feel.setText("较潮湿");
                } else if (a.contains("雾")) {
                    Feel.setText("较潮湿");
                } else if (a.contains("雪")) {
                    Feel.setText("较寒冷");
                } else if (a.contains("雷")) {
                    Feel.setText("较潮湿");
                } else if (a.contains("冰雹")) {
                    Feel.setText("较寒冷");
                } else if (b < 10) {
                    Feel.setText("较寒冷");
                } else if (b > 30) {
                    Feel.setText("较炎热");
                } else {
                    Feel.setText("较舒适");
                }

                Today.setText(dataObject.getUpdate().getLoc().substring(0, 10));
                Tmp.setText(dataObject.getNow().getTmp() + "°C");

                weatherTxt.setText(dataObject.getNow().getCond_txt());
                visNum.setText(dataObject.getNow().getVis() + "Km");
                HumNum.setText(dataObject.getNow().getHum());
                UpdateTime.setText(dataObject.getUpdate().getLoc());
                windir.setText(dataObject.getNow().getWind_dir());
                windspd.setText(dataObject.getNow().getWind_spd() + " Km/h");
                windsc.setText(dataObject.getNow().getWind_sc() + " 级");
                Pcpn.setText(dataObject.getNow().getPcpn() + " mm");
                Press.setText(dataObject.getNow().getPres() + " Pa");
                getCloud.setText(dataObject.getNow().getCloud());

                Z1 = true;

                if (Z1 && Z2 && Z3) {
                    cancelProgressDialog();
                }

                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(dataObject.getStatus())) {
                    //此时返回数据
                    NowBase now = dataObject.getNow();
                } else {
                    //在此查看返回数据失败的原因
                    String status = dataObject.getStatus();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });

        /**
         * @param context  上下文
         * @param location (如果不添加此参数,SDK会根据GPS联网定位,根据当前经纬度查询)所查询的地区，可通过该地区名称、ID、Adcode、IP和经纬度进行查询经纬度格式：纬度,经度
         *                 （英文,分隔，十进制格式，北纬东经为正，南纬西经为负)
         * @param lang     多语言，默认为简体中文，其他语言请参照多语言对照表
         * @param unit     单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问回调接口
         */
        HeWeather.getWeatherForecast(WeatherActivity.this, city, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherForecastBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                showErrorDialog(throwable);
                Log.i(TAG, "getWeatherForecast onError: ", throwable);
            }

            @Override
            public void onSuccess(Forecast forecast) {
                String strByJson = new Gson().toJson(forecast);
                Log.i(TAG, " getWeatherForecast: " + strByJson);
                //先转JsonObject
                JsonObject jsonObject = new JsonParser().parse(strByJson).getAsJsonObject();
                //再转JsonArray 加上数据头
                JsonArray jsonArray = jsonObject.getAsJsonArray("daily_forecast");
                Gson gson = new Gson();
                JsonObject jsonObject1 = (JsonObject) jsonArray.get(0);
                Log.i(TAG, "sun " + jsonObject1.get("sr").toString());
                Log.i(TAG, "sun " + jsonObject1.get("ss").toString());
                high.setText(getDigits(jsonObject1.get("tmp_max").toString()) + '°');
                low.setText(getDigits(jsonObject1.get("tmp_min").toString()) + '°');
                sunrise.setText(jsonObject1.get("sr").toString().substring(1, 6));
                sunsite.setText(jsonObject1.get("ss").toString().substring(1, 6));

                //循环遍历
                for (JsonElement user : jsonArray) {
                    //通过反射 得到UserBean.class
                    ForecastList forecastList = gson.fromJson(user, new TypeToken<ForecastList>() {
                    }.getType());
                    ForecastBeanList.add(forecastList);
                }
                System.out.println(ForecastBeanList);
                initView1();
                Z2 = true;
                if (Z1 && Z2 && Z3) {
                    cancelProgressDialog();
                }
            }
        });


        /**
         * @param context  上下文
         * @param location (如果不添加此参数,SDK会根据GPS联网定位,根据当前经纬度查询)所查询的地区，可通过该地区名称、ID、Adcode、IP和经纬度进行查询经纬度格式：纬度,经度
         *                 （英文,分隔，十进制格式，北纬东经为正，南纬西经为负)
         * @param lang     多语言，默认为简体中文，其他语言请参照多语言对照表
         * @param unit     单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问回调接口
         */
        HeWeather.getWeatherHourly(WeatherActivity.this, city, new HeWeather.OnResultWeatherHourlyBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                showErrorDialog(throwable);
                Log.i(TAG, "Weather time onError: ", throwable);
            }

            @Override
            public void onSuccess(Hourly hourly) {
                String strByJson = new Gson().toJson(hourly.getHourly());
                Log.i(TAG, " Weather time onSuccess: " + strByJson);
                //Json的解析类对象
                JsonParser parser = new JsonParser();
                //将JSON的String 转成一个JsonArray对象
                JsonArray jsonArray = parser.parse(strByJson).getAsJsonArray();
                Gson gson = new Gson();
                //加强for循环遍历JsonArray
                for (JsonElement user : jsonArray) {
                    //使用GSON，直接转成Bean对象
                    HourlyBean hourlyBean = gson.fromJson(user, HourlyBean.class);
                    hourlyBeanList.add(hourlyBean);
                }
                System.out.println(hourlyBeanList);
                initView();
                Z3 = true;

                if (Z1 && Z2 && Z3) {
                    cancelProgressDialog();
                }
            }
        });

    }

    private void initView() {
        HorizontalAdapter adapter = new HorizontalAdapter(this);

        RecyclerView rcvHorizontal = findViewById(R.id.RV);

        LinearLayoutManager managerHorizontal = new LinearLayoutManager(this);
        managerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);

        rcvHorizontal.setLayoutManager(managerHorizontal);
        rcvHorizontal.setHasFixedSize(true);
        rcvHorizontal.setAdapter(adapter);

        adapter.setHorizontalDataList(hourlyBeanList);
    }

    private void initView1() {
        VerticalAdapter adapter = new VerticalAdapter(this);

        RecyclerView rcvVertical = findViewById(R.id.RV1);

        LinearLayoutManager managerVertical = new LinearLayoutManager(this);
        managerVertical.setOrientation(LinearLayoutManager.VERTICAL);

        // 也可以直接写成：
//        rcvVertical.setLayoutManager(new LinearLayoutManager(this));

        rcvVertical.setLayoutManager(managerVertical);
        rcvVertical.setHasFixedSize(true);
        rcvVertical.setAdapter(adapter);
        adapter.setVerticalDataList(ForecastBeanList);
    }

    public static String getDigits(String str) {

        Matcher matcher = DIGITS_PATTERN.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    /**
     * 加载框
     */
    public void buildProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage("正在获取天气资源...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /**
     * @Description: TODO 取消加载框
     */
    public void cancelProgressDialog() {
        if (progressDialog != null)
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
    }

    private void showErrorDialog(Throwable e) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setIcon(R.drawable.icons8_error);
        normalDialog.setTitle("没有网络");
        normalDialog.setMessage("获取资源失败\n"+e);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        finish();
                    }
                });
        // 显示
        normalDialog.show();
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

}