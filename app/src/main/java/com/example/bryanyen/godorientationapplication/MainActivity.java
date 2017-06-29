package com.example.bryanyen.godorientationapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main";

    private ImageView mCompassImageView;
    private ImageView mMoneyGodImageView;
    private TextView mOrientationTextView;

    private SensorManager mSensorManager;

    private Sensor mAccelerometerSensor;
    private Sensor mMagneticFieldSensor;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    private float currentDegree = 0f;

    private String godOrientation;
    private String compassOrientation;

    private Animation mAlphaInAnimation;
    private Animation mAlphaOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCompassImageView = (ImageView) findViewById(R.id.imageView);
        mMoneyGodImageView = (ImageView) findViewById(R.id.imageView3);
        mOrientationTextView = (TextView) findViewById(R.id.textView);
        TextView textView = (TextView) findViewById(R.id.textView2);
        TextView whereGoldTextView = (TextView) findViewById(R.id.textView4);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        LunarCalendar lunarCalendar = new LunarCalendar(new Date());
        godOrientation = DataBaseHelper.getMoneyGodData(getApplication(), lunarCalendar.getLunarMonthOfDay());

        textView.setText(lunarCalendar.getLunarDate());
        whereGoldTextView.setText("財神方位: " + godOrientation);
        mCompassImageView.setDrawingCacheEnabled(true);

        mAlphaInAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        mAlphaOutAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        getCompassOrientation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometerSensor, SensorManager
                .SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(sensorEventListener, mMagneticFieldSensor, SensorManager
                .SENSOR_DELAY_NORMAL);

        getCompassOrientation();
    }

    public void onPause() {
        mSensorManager.unregisterListener(sensorEventListener);
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

            getCompassOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void compassImageAnimation(float degree) {

        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);        // 動畫旋轉持續時間ms
        ra.setFillAfter(true);      // 設置動畫結束後的保留狀態

        LinearInterpolator linearInterpolator = new LinearInterpolator();   // 建立線性旋轉
        ra.setInterpolator(linearInterpolator);

        mCompassImageView.startAnimation(ra);
        currentDegree = -degree;

    }

    private void getCompassOrientation() {
        float[] values = new float[3];
        float[] rotation = new float[9];
        SensorManager.getRotationMatrix(rotation, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(rotation, values);

        // 極座標轉度
        values[0] = (float) Math.toDegrees(values[0]);
        //        Log.i(TAG, values[0] + "");
        //values[1] = (float) Math.toDegrees(values[1]);
        //values[2] = (float) Math.toDegrees(values[2]);

        double maxValue = 0;
        // 判斷座標落在的範圍
        if (values[0] >= -11.25 && values[0] < 11.25) {
            compassOrientation = "北";
            maxValue = 0;
        } else if (values[0] >= 11.25 && values[0] < 33.75) {
            compassOrientation = "東北偏北";
            maxValue = 22.5;
        } else if (values[0] >= 33.75 && values[0] < 56.25) {
            compassOrientation = "東北";
            maxValue = 45;
        } else if (values[0] >= 56.25 && values[0] < 78.75) {
            compassOrientation = "東北偏東";
            maxValue = 67.5;
        } else if (values[0] >= 78.75 && values[0] < 102.25) {
            compassOrientation = "東";
            maxValue = 90;
        } else if (values[0] >= 101.25 && values[0] < 123.75) {
            compassOrientation = "東南偏東";
            maxValue = 112.5;
        } else if (values[0] >= 123.75 && values[0] < 146.25) {
            compassOrientation = "東南";
            maxValue = 135;
        } else if (values[0] >= 146.25 && values[0] < 168.75) {
            compassOrientation = "東南偏南";
            maxValue = 157.5;
        } else if (values[0] >= 168.75 && values[0] < -168.75) {
            compassOrientation = "南";
            maxValue = 180;
        } else if (values[0] >= -168.75 && values[0] < -146.25) {
            compassOrientation = "西南偏南";
            maxValue = -157.5;
        } else if (values[0] >= -146.25 && values[0] < -123.75) {
            compassOrientation = "西南";
            maxValue = -135;
        } else if (values[0] >= -123.75 && values[0] < -102.25) {
            compassOrientation = "西南偏西";
            maxValue = -112.5;
        } else if (values[0] >= -102.25 && values[0] < -78.75) {
            compassOrientation = "西";
            maxValue = -90;
        } else if (values[0] >= -78.75 && values[0] < -56.25) {
            compassOrientation = "西北偏西";
            maxValue = -67.5;
        } else if (values[0] >= -56.25 && values[0] < -33.75) {
            compassOrientation = "西北";
            maxValue = -45;
        } else if (values[0] >= -33.75 && values[0] < -11.25) {
            compassOrientation = "西北偏北";
            maxValue = -22.5;
        }

        mMoneyGodImageView.setVisibility(View.INVISIBLE);
        mMoneyGodImageView.clearAnimation();
        //        Log.i(TAG, compassOrientation);
        mOrientationTextView.setText(compassOrientation);

        compassImageAnimation(values[0]);
        godImageShow(compassOrientation, values[0], maxValue);
    }

    private void godImageShow(String compress, float value, double maxValue) {
        if (compress.length() == 1) {
            compress = "正" + compress;
        }

        if (compress.equals(godOrientation)) {
            mMoneyGodImageView.setVisibility(View.VISIBLE);

            if (value < maxValue - 10) {
                mMoneyGodImageView.startAnimation(mAlphaInAnimation);
            }

            if (value > maxValue + 10) {
                mMoneyGodImageView.startAnimation(mAlphaOutAnimation);
            }
        }
    }
}