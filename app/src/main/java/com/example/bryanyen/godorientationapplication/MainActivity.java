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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main";

    private ImageView mCompassImageView;
    private ImageView mMoneyGodImageView;
    private TextView mOrientationTextView;
    private RelativeLayout mWordConstraintLayout;

    private SensorManager mSensorManager;

    private Sensor mAccelerometerSensor;
    private Sensor mMagneticFieldSensor;
    private Sensor mOrientationSensor;

    //    private float[] accelerometerValues = new float[3];
    //    private float[] magneticFieldValues = new float[3];
    private float[] orientationValues = new float[3];

    private float currentDegree = 0f;

    private String godOrientation;
    private String compassOrientation = "北";

    private Animation mAlphaInAnimation;
    private Animation mAlphaOutAnimation;

    //    private boolean isOrientationSensor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCompassImageView = (ImageView) findViewById(R.id.imageView);
        mMoneyGodImageView = (ImageView) findViewById(R.id.imageView3);
        mOrientationTextView = (TextView) findViewById(R.id.textView);
        TextView textView = (TextView) findViewById(R.id.textView2);
        TextView whereGoldTextView = (TextView) findViewById(R.id.textView4);
        mWordConstraintLayout = (RelativeLayout) findViewById(R.id.wordConstraintLayout);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //        mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        LunarCalendar lunarCalendar = new LunarCalendar(new Date());
        godOrientation = DataBaseHelper.getMoneyGodData(getApplication(), lunarCalendar.getLunarMonthOfDay());

        textView.setText(lunarCalendar.getLunarDate());
        whereGoldTextView.setText("財神方位: " + godOrientation.replace("正", ""));
        mCompassImageView.setDrawingCacheEnabled(true);

        mAlphaOutAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        final Button changeSensorButton = (Button) findViewById(R.id.changeSensorButton);
        //        changeSensorButton.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                mSensorManager.unregisterListener(sensorEventListener);
        //                if (!isOrientationSensor) {
        //                    mSensorManager.registerListener(sensorEventListener, mOrientationSensor, SensorManager
        //                            .SENSOR_DELAY_NORMAL);
        //
        //                    changeSensorButton.setText("電子羅盤");
        //                    isOrientationSensor = true;
        //                } else {
        //                    mSensorManager.registerListener(sensorEventListener, mAccelerometerSensor, SensorManager
        //                            .SENSOR_DELAY_NORMAL);
        //                    mSensorManager.registerListener(sensorEventListener, mMagneticFieldSensor, SensorManager
        //                            .SENSOR_DELAY_NORMAL);
        //
        //                    changeSensorButton.setText("磁場偏移");
        //                    isOrientationSensor = false;
        //                }
        //
        //                getCompassOrientation();
        //            }
        //        });
        changeSensorButton.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        if (isOrientationSensor) {
        mSensorManager.registerListener(sensorEventListener, mOrientationSensor, SensorManager
                .SENSOR_DELAY_NORMAL);
        //        } else {
        //            mSensorManager.registerListener(sensorEventListener, mAccelerometerSensor, SensorManager
        //                    .SENSOR_DELAY_NORMAL);
        //            mSensorManager.registerListener(sensorEventListener, mMagneticFieldSensor, SensorManager
        //                    .SENSOR_DELAY_NORMAL);
        //        }

        getCompassOrientation();
    }

    public void onPause() {
        mSensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {

            //            if (isOrientationSensor) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                orientationValues = sensorEvent.values;
            }
            //            } else {
            //                if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //                    magneticFieldValues = sensorEvent.values;
            //                }
            //                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //                    accelerometerValues = sensorEvent.values;
            //                }
            //            }

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

    private double get16CompassRange(float values) {
        int angle;
        //        if (isOrientationSensor) {
        angle = 360;    // TYPE_ORIENTATION : 0 ~ 360
        //        } else {
        //            angle = 0;      // TYPE_ACCELEROMETER : -180 ~ 180
        //        }

        double maxValue = 0;
        // 判斷座標落在的範圍
        if ((values <= angle && values > (angle - 11.25)) || (values >= 0 && values < 11.25)) {
            compassOrientation = "北";
            maxValue = 0;
        } else if (values >= 11.25 && values < 33.75) {
            compassOrientation = "東北偏北";
            maxValue = 22.5;
        } else if (values >= 33.75 && values < 56.25) {
            compassOrientation = "東北";
            maxValue = 45;
        } else if (values >= 56.25 && values < 78.75) {
            compassOrientation = "東北偏東";
            maxValue = 67.5;
        } else if (values >= 78.75 && values < 101.25) {
            compassOrientation = "東";
            maxValue = 90;
        } else if (values >= 101.25 && values < 123.75) {
            compassOrientation = "東南偏東";
            maxValue = 112.5;
        } else if (values >= 123.75 && values < 146.25) {
            compassOrientation = "東南";
            maxValue = 135;
        } else if (values >= 146.25 && values < 168.75) {
            compassOrientation = "東南偏南";
            maxValue = 157.5;
        } else if (values >= 168.75 && values < (angle - 168.75)) {
            compassOrientation = "南";
            maxValue = 180;
        } else if (values >= (angle - 168.75) && values < (angle - 146.25)) {
            compassOrientation = "西南偏南";
            maxValue = angle - 157.5;
        } else if (values >= (angle - 146.25) && values < (angle - 123.75)) {
            compassOrientation = "西南";
            maxValue = angle - 135;
        } else if (values >= (angle - 123.75) && values < (angle - 102.25)) {
            compassOrientation = "西南偏西";
            maxValue = angle - 112.5;
        } else if (values >= (angle - 102.25) && values < (angle - 78.75)) {
            compassOrientation = "西";
            maxValue = angle - 90;
        } else if (values >= (angle - 78.75) && values < (angle - 56.25)) {
            compassOrientation = "西北偏西";
            maxValue = angle - 67.5;
        } else if (values >= (angle - 56.25) && values < (angle - 33.75)) {
            compassOrientation = "西北";
            maxValue = angle - 45;
        } else if (values >= (angle - 33.75) && values < (angle - 11.25)) {
            compassOrientation = "西北偏北";
            maxValue = angle - 22.5;
        }

        return maxValue;
    }

    private double get8CompassRange(float values) {
        int angle = 360;
        double maxValue = 0;
        // 判斷座標落在的範圍
        if ((values <= angle && values > (angle - 22.5)) || (values >= 0 && values < 22.5)) {
            compassOrientation = "北";
            maxValue = 0;
        } else if (values >= 22.5 && values < 67.5) {
            compassOrientation = "東北";
            maxValue = 45;
        } else if (values >= 67.5 && values < 112.5) {
            compassOrientation = "東";
            maxValue = 90;
        } else if (values >= 112.5 && values < 157.5) {
            compassOrientation = "東南";
            maxValue = 135;
        } else if (values >= 157.5 && values < (angle - 157.5)) {
            compassOrientation = "南";
            maxValue = 180;
        } else if (values >= (angle - 157.5) && values < (angle - 112.5)) {
            compassOrientation = "西南";
            maxValue = angle - 135;
        } else if (values >= (angle - 112.5) && values < (angle - 67.5)) {
            compassOrientation = "西";
            maxValue = angle - 90;
        } else if (values >= (angle - 67.5) && values < (angle - 22.5)) {
            compassOrientation = "西北";
            maxValue = angle - 45;
        }

        return maxValue;
    }

    private void getCompassOrientation() {
        float values;

        //        if (isOrientationSensor) {
        values = orientationValues[0];
        //        } else {
        //            float[] mValues = new float[3];
        //            float[] rotation = new float[9];
        //            SensorManager.getRotationMatrix(rotation, null, accelerometerValues, magneticFieldValues);
        //            SensorManager.getOrientation(rotation, mValues);
        //
        //            // 極座標轉度
        //            mValues[0] = (float) Math.toDegrees(mValues[0]);
        //            //        Log.i(TAG, values[0] + "");
        //            //values[1] = (float) Math.toDegrees(values[1]);
        //            //values[2] = (float) Math.toDegrees(values[2]);
        //
        //            values = mValues[0];
        //        }

        double maxValue = get8CompassRange(values);

        mMoneyGodImageView.setVisibility(View.INVISIBLE);
        //        Log.i(TAG, compassOrientation);
        mOrientationTextView.setText(compassOrientation);

        compassImageAnimation(values);
        godImageShow(compassOrientation, values, maxValue);
    }

    private void godImageShow(String compress, float value, double maxValue) {
        if (compress.equals(godOrientation)) {
            mMoneyGodImageView.setVisibility(View.VISIBLE);

            if (value < maxValue + 22.5 && value >= maxValue - 22.5) {
                mMoneyGodImageView.startAnimation(mAlphaOutAnimation);
            }
        }
    }
}