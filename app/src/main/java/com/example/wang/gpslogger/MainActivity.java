package com.example.wang.gpslogger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.wang.gpslogger.accelerometer.Location_Activity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AMapLocationClientOption locationOption = null;
    private AMapLocationClient locationClient = null;
    private TextView tvLat;
    private TextView tvLng;
    private TextView tvStatus;
    private TextView tvHeight;
    private TextView tvTime;
    private TextView tvDistance;
    private Context mContext = this;


    boolean isStart = false;
    private LinearLayout llLatLng;

    int pointCount = 1;

    static String xmlHeader = "<?xml version='1.0' encoding='Utf-8' standalone='yes' ?>";
    static String gpxTrackHeader = "<gpx xmlns=\"http://www.topografix.com/GPX/1/0\" version=\"1.0\" creator=\"org.yriarte.tracklogger\">\n<trk>\n<trkseg>\n";
    static String gpxTrackFooter = "\n</trkseg>\n</trk>\n</gpx>\n";


    FileWriter gpxLogWriter;
    private TextView tvSetting;
    private TextView tvClose;
    private TextView tvStart;
    private TextView tvSave;
    private ImageView ivSettings;

    String filePath = "/GPS data";
    private String fileName;

    LocationType locationType = LocationType.Hight_Accuracy;

    public enum LocationType {
        Device_Sensors,//GPS 设备模式

        Hight_Accuracy,

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvSetting = findViewById(R.id.tv_setting);
        tvClose = findViewById(R.id.tv_close);
        tvStart = findViewById(R.id.tv_start);
        tvSave = findViewById(R.id.tv_save);
        ivSettings = findViewById(R.id.iv_settings);
        tvStatus = findViewById(R.id.tv_status);
        tvHeight = findViewById(R.id.tv_height);
        tvTime = findViewById(R.id.tv_time);
        tvDistance = findViewById(R.id.tv_distance);
        tvLat = findViewById(R.id.tv_lat);
        tvLng = findViewById(R.id.tv_lng);
        llLatLng = findViewById(R.id.ll_latlng);


        switch (locationType) {
            case Device_Sensors:

                break;
            case Hight_Accuracy:

                if (!NetWorkUtils.isNetWorkConnected(mContext)) {
                    toastShow("请检查网络连接");
                }
                break;
        }

        //初始化定位
        initLocation();

        FileUtil.getRandomFilePath(mContext, "", true);

        initListener();
    }

    private void initListener() {
        llLatLng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(tvLng.getText().toString()) && !tvLng.getText().toString().equals("00")) {
                    //把状态改成正在预约中状态
                    CommonBuilderDialog.Builder builder = new CommonBuilderDialog.Builder(mContext);

                    String formatPointCount = new DecimalFormat("#000").format(pointCount);

                    builder.setMessage("是否添加" + formatPointCount + "为兴趣点");

                    builder.setCancelable(false);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (gpxLogWriter == null)
                                return;
                            try {
                                pointCount++;
                                gpxLogWriter.append(gpxTrackPoint(locationTemp.getLatitude(), locationTemp.getLongitude(), locationTemp.getAltitude(), locationTemp.getTime()));

                            } catch (Exception e) {
                                gpxLogWriter = null;
                            }
                            dialogInterface.dismiss();


                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    CommonBuilderDialog commonBuilderDialog = builder.create();
                    if (commonBuilderDialog != null && !commonBuilderDialog.isShowing()) {
                        commonBuilderDialog.show();
                    }
                }
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gpxLogWriter == null) {
                    toastShow("请先开启里程");
                } else if (pointCount == 1) {
                    toastShow("请先添加兴趣点");
                } else {
                    try {
                        gpxLogWriter.flush();
                        gpxLogWriter.close();
                        toastShow("保存成功 文件名称" + fileName + ".gpx");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Location_Activity.class));
            }
        });

        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //把状态改成正在预约中状态
                CommonBuilderDialog.Builder builder = new CommonBuilderDialog.Builder(mContext);
                builder.setMessage("结束数据记录");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        stopLocation();
                        tvHeight.setText("0");
                        tvTime.setText("00:00");

                        distance = 0;
                        setTvDistance();
                        tvLat.setText("0");
                        tvLng.setText("0");
                        AppUtil.killApp(0);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                CommonBuilderDialog commonBuilderDialog = builder.create();
                if (commonBuilderDialog != null && !commonBuilderDialog.isShowing()) {
                    commonBuilderDialog.show();
                }
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.MAIN");
//                intent.addCategory("android.intent.category.HOME");
//                startActivity(intent);
            }
        });

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStart) {
                    tvStart.setText("停止");
                    //根据控件的选择，重新设置定位参数
                    resetOption();
                    // 设置定位参数
                    locationClient.setLocationOption(locationOption);
                    // 启动定位
                    locationClient.startLocation();
                    try {

                        fileName = getFileName();

                        System.out.println("--------------------filename" + fileName);

                        gpxLogWriter = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filePath + "/"
                                + fileName
                                + ".gpx"
                        );
                        gpxLogWriter.write(xmlHeader + gpxTrackHeader);
                    } catch (Exception e) {
                        gpxLogWriter = null;
                    }
                    isStart = true;
                } else {

                    pointCount = 1;

                    isStart = false;

                    tvStart.setText("开始");

                    // 停止定位
                    locationClient.stopLocation();

                }
            }


        });
        tvDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (distance > 0) {
                    distance = 0;
                    tvDistance.setText(distance + "");
                    toastShow("里程置0成功");
                }
            }
        });
    }

    private String getFileName() {
        JSONArray gpx = FileUtil.getAllFiles(Environment.getExternalStorageDirectory().getAbsolutePath() + filePath, "gpx");

        List<Integer> fileNameList = new ArrayList<>();

        for (int i = 0; i < gpx.length(); i++) {
            try {

                if (StringUtil.isNumeric(gpx.getJSONObject(i).getString("name"))) {
                    fileNameList.add(Integer.valueOf(gpx.getJSONObject(i).getString("name")));
                }

                System.out.println("-----------" + gpx.getJSONObject(i).getString("name") + "path=" + gpx.getJSONObject(i).getString("path"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Integer max = null;
        try {
            max = Collections.max(fileNameList);
        } catch (Exception e) {
            System.out.println("---------" + "getException");

        }

        if (max == null) {

            return new DecimalFormat("#00").format(1);
        }

        return new DecimalFormat("#00").format(max + 1);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }


    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
        if (gpxLogWriter == null)
            return;
        try {
            gpxLogWriter.append(gpxTrackFooter);
            gpxLogWriter.close();
        } catch (Exception e) {
        }
        gpxLogWriter = null;

    }

    // 根据控件的选择，重新设置定位参数
    private void resetOption() {
        // 设置是否需要显示地址信息
//        locationOption.setNeedAddress(cbAddress.isChecked());
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
//        locationOption.setGpsFirst(cbGpsFirst.isChecked());
        // 设置是否开启缓存
//        locationOption.setLocationCacheEnable(cbCacheAble.isChecked());
        // 设置是否单次定位
//        locationOption.setOnceLocation(cbOnceLocation.isChecked());
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
//        locationOption.setOnceLocationLatest(cbOnceLastest.isChecked());
        //设置是否使用传感器
        locationOption.setSensorEnable(true);
        //设置是否开启wifi扫描，如果设置为false时同时会停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
        locationOption.setInterval(Long.valueOf(1000));

    }

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {

        tvStatus.setText("loading");
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();


        switch (locationType) {
            case Device_Sensors:
                mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);//GPS 设备模式
                break;
            case Hight_Accuracy:
                mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
                break;
        }


        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
//        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    float distance = 0;//当前里程数

    AMapLocation locationTemp;
    int index = 0;
    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                locationTemp = location;
                tvStatus.setVisibility(View.GONE);

                if (index == 0) {
                    toastShow("定位成功");
                    index++;
                }

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");

//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    System.out.println("-------" + location.getTime());

                    SimpleDateFormat df = new SimpleDateFormat("mm:ss");
                    Date date = new Date(location.getTime());
                    String format = df.format(date);//定位时间

//                    long time = location.getTime();

                    tvTime.setText(format);

                    sb.append("海拔: " + location.getAltitude() + "\n");

                    if (location.getAltitude() >= 0) {
                        String format1 = new DecimalFormat("#0.00").format(location.getAltitude());
                        tvHeight.setText(format1 + "m");
                    }

                    sb.append("定位类型: " + location.getLocationType() + "\n");

                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");

                    double longitude = location.getLongitude();

                    String formatLatLng = getFormatLatLng(longitude);

                    tvLat.setText(formatLatLng);

                    String formatLatLng1 = getFormatLatLng(location.getLatitude());

                    tvLng.setText(formatLatLng1);

                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");

                    distance = distance + location.getSpeed() * 1;


                    setTvDistance();

                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");


                    //定位完成的时间
//                    sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }
                sb.append("***定位质量报告***").append("\n");

//                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
//                sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
//                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
//                sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
//                sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
//                sb.append("****************").append("\n");
//                //定位之后的回调时间
//                sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                //解析定位结果，
                String result = sb.toString();
//                tvResult.setText(result);
            } else {
                tvStatus.setVisibility(View.GONE);
                toastShow("定位失败");

//                tvResult.setText("定位失败，loc is null");
            }
        }
    };

    private void setTvDistance() {
        Float distanceInt = new Float(distance) / 1000;

        if (distanceInt >= 9999) {
            distance = 0;
            distanceInt = 0f;
        }
        if (distance < 10) {
            String format1 = new DecimalFormat("#0.000").format(distanceInt);
            tvDistance.setText(format1 + "km");
        } else if (distance > 10 && distance < 100) {
            String format1 = new DecimalFormat("#0.00").format(distanceInt);
            tvDistance.setText(format1 + "km");
        } else if (distance >= 100 && distance < 1000) {
            String format1 = new DecimalFormat("#0.0").format(distanceInt);
            tvDistance.setText(format1 + "km");
        } else if (distance >= 1000) {
            String format1 = new DecimalFormat("#").format(distanceInt);
            tvDistance.setText(format1 + "km");
        }
    }


    public String gpxTrackPoint(double lat, double lon, double ele, long time) {
        String trkpt = "<trkpt";
        trkpt += " lon=\"" + Double.valueOf(lon).toString() + "\"";
        trkpt += " lat=\"" + Double.valueOf(lat).toString() + "\"";
        trkpt += ">\n  <ele>" + Double.valueOf(ele).toString() + "</ele>\n";
        byte timebytes[] = new Timestamp(time).toString().getBytes();
        timebytes[10] = 'T';
        timebytes[19] = 'Z';
        trkpt += "  <time>" + new String(timebytes).substring(0, 20) + "</time>\n";
        trkpt += "</trkpt>\n";
        return trkpt;
    }

    private String getFormatLatLng(double longitude) {
        String longitudeS = String.valueOf(longitude);

        String[] split = longitudeS.split("\\.");


        System.out.println("---------------" + "longitudeS=" + longitudeS);
        for (int i = 0; i < split.length; i++) {
            System.out.println("---------------" + "split=" + split[i]);

        }

        if (split.length > 0) {


            String s = "0." + split[1];

            float dotLongitude = Float.valueOf(s) * 60;

            BigDecimal b = new BigDecimal(dotLongitude);

            float f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();

            //b.setScale(2,BigDecimal.ROUND_HALF_UP)   表明四舍五入，保留两位小数
            System.out.println("---------------" + "more than 1=" + f1);

            return split[0] + "°" + f1 + "’";
        }

        System.out.println("---------------" + "more than 0");

        return String.valueOf(longitude);

    }

    private void toastShow(String text) {
        if (mContext != null)
            Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }

}
