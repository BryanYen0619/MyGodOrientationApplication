package com.example.bryanyen.godcompassapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main";

    private ImageView imageView;
    private ImageView startImageView;
    private TextView textView;

    private SensorManager sensorManager;

    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;
    //    private Sensor orientationSensor;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    //    private float[] orientationValues = new float[3];

    private float currentDegree = 0f;

    private String whereGold;
    private String compressText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setDrawingCacheEnabled(true);

        startImageView = (ImageView) findViewById(R.id.imageView3);

        textView = (TextView) findViewById(R.id.textView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);


        GoldDate goldDate = new GoldDate(new Date());
        //                Log.d(TAG, "date :" + goldDate.getLunarDate());
        //        Log.d(TAG, "date :" + goldDate.getLunarDay());
        whereGold = goldDate.getMoneyGodData(getApplication());
        //        Log.d(TAG, "gold :" + whereGold);

        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText(goldDate.getLunarDate());

        TextView whereGoldTextView = (TextView) findViewById(R.id.textView4);
        whereGoldTextView.setText("財神方位: " + whereGold);

        calculateOrientation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager
                .SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, magneticFieldSensor, SensorManager
                .SENSOR_DELAY_NORMAL);
        //        sensorManager.registerListener(sensorEventListener, orientationSensor, SensorManager
        // .SENSOR_DELAY_NORMAL);

        calculateOrientation();
    }

    public void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = sensorEvent.values;
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values;
            }

            calculateOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void compassImageAnimation(float degree) {
        if (currentDegree != -degree) {
            RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setRepeatCount(RotateAnimation.INFINITE);
            ra.setDuration(1200);        // 動畫旋轉持續時間ms
            ra.setFillAfter(true);      // 設置動畫結束後的保留狀態
            imageView.startAnimation(ra);
            currentDegree = -degree;
        }
    }

    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);

        // 極座標轉度
        values[0] = (float) Math.toDegrees(values[0]);
        //        Log.i(TAG, values[0] + "");
        //values[1] = (float) Math.toDegrees(values[1]);
        //values[2] = (float) Math.toDegrees(values[2]);

        // 判斷座標落在的範圍
        if (values[0] >= -11.25 && values[0] < 11.25) {
            compressText = "北";
        } else if (values[0] >= 11.25 && values[0] < 33.75) {
            compressText = "東北偏北";
        } else if (values[0] >= 33.75 && values[0] < 56.25) {
            compressText = "東北";
        } else if (values[0] >= 56.25 && values[0] < 78.75) {
            compressText = "東北偏東";
        } else if (values[0] >= 78.75 && values[0] < 102.25) {
            compressText = "東";
        } else if (values[0] >= 101.25 && values[0] < 123.75) {
            compressText = "東南偏東";
        } else if (values[0] >= 123.75 && values[0] < 146.25) {
            compressText = "東南";
        } else if (values[0] >= 146.25 && values[0] < 168.75) {
            compressText = "東南偏南";
        } else if (values[0] >= 168.75 && values[0] < -168.75) {
            compressText = "南";
        } else if (values[0] >= -168.75 && values[0] < -146.25) {
            compressText = "西南偏南";
        } else if (values[0] >= -146.25 && values[0] < -123.75) {
            compressText = "西南";
        } else if (values[0] >= -123.75 && values[0] < -102.25) {
            compressText = "西南偏西";
        } else if (values[0] >= -102.25 && values[0] < -78.75) {
            compressText = "西";
        } else if (values[0] >= -78.75 && values[0] < -56.25) {
            compressText = "西北偏西";
        } else if (values[0] >= -56.25 && values[0] < -33.75) {
            compressText = "西北";
        } else if (values[0] >= -33.75 && values[0] < -11.25) {
            compressText = "西北偏北";
        }

        startImageView.setVisibility(View.INVISIBLE);
        //        Log.i(TAG, compressText);
        textView.setText(compressText);

        compassImageAnimation(values[0]);
        goldImageShow(compressText, values[0]);
    }

    private void goldImageShow(String compress, float value) {
        if (compress.length() == 1) {
            compress = "正" + compress;
        }
        if (compress.equals(whereGold)) {

            startImageView.setVisibility(View.VISIBLE);
        }
    }
}