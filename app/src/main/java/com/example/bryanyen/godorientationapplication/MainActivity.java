package com.example.bryanyen.godorientationapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main";

    private static final int NEAR_GOD = 1;
    private static final int LEAVE_GOD = -1;
    private static final int LEFT_SIDE = 1;
    private static final int RIGHT_SIDE = -1;

    private ImageView mMoneyGodImageView;
    private TextView mOrientationTextView;
    private TextView mETextView;
    private TextView mSTextView;
    private TextView mWTextView;
    private TextView mNTextView;
    private RelativeLayout mWordConstraintLayout;

    private SensorManager mSensorManager;

    private Sensor mAccelerometerSensor;
    private Sensor mMagneticFieldSensor;
    private Sensor mOrientationSensor;

    //    private float[] accelerometerValues = new float[3];
    //    private float[] magneticFieldValues = new float[3];
    private float[] orientationValues = new float[3];
    private float currentDegree = 0f;
    private double temp = 0;

    private String godOrientation;
    private String compassOrientation = "";

    private Animation mAlphaInAnimation;
    private Animation mAlphaOutAnimation;
    private Animation mTranslateLeftTopInAnimation;
    private Animation mTranslateLeftTopOutAnimation;
    private Animation mTranslateRightTopInAnimation;
    private Animation mTranslateRightTopOutAnimation;

    //    private boolean isOrientationSensor = false;
    private boolean isAnimationInRunning = false;
    private boolean isAnimationOutRunning = false;
    private boolean isInGod = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoneyGodImageView = (ImageView) findViewById(R.id.imageView3);
        mOrientationTextView = (TextView) findViewById(R.id.textView);
        TextView textView = (TextView) findViewById(R.id.textView2);
        TextView whereGoldTextView = (TextView) findViewById(R.id.textView4);
        mWordConstraintLayout = (RelativeLayout) findViewById(R.id.wordConstraintLayout);
        mETextView = (TextView) findViewById(R.id.eTextView);
        mWTextView = (TextView) findViewById(R.id.wTextView);
        mSTextView = (TextView) findViewById(R.id.sTextView);
        mNTextView = (TextView) findViewById(R.id.nTextView);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //        mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        LunarCalendar lunarCalendar = new LunarCalendar(new Date());
        godOrientation = DataBaseHelper.getMoneyGodData(getApplication(), lunarCalendar.getLunarMonthOfDay()).replace
                ("正", "");

        textView.setText(lunarCalendar.getLunarDate());
        whereGoldTextView.setText("財神方位: " + godOrientation);
        //        mCompassImageView.setDrawingCacheEnabled(true);
        mWordConstraintLayout.setDrawingCacheEnabled(true);

        animationInit();

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
        RotateAnimation raWord;
        RotateAnimation ra;

        // 處理圖片在0~360交換時翻轉問題
        if (Math.abs(degree + currentDegree) > 180) {
            //            Log.d("TEST", "range degree :" + degree);
            //            Log.d("TEST", "range current :" + currentDegree);

            if (degree >= 0) {
                currentDegree = (float) -(degree + 0.1);
            } else {
                degree = (float) (Math.abs(currentDegree) + 0.1);
            }
        }

        // 羅盤動畫
        ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);        // 動畫旋轉持續時間ms
        ra.setFillAfter(true);      // 設置動畫結束後的保留狀態

        // 方向文字動畫
        raWord = new RotateAnimation(-currentDegree, degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        raWord.setDuration(200);        // 動畫旋轉持續時間ms
        raWord.setFillAfter(true);      // 設置動畫結束後的保留狀態

        LinearInterpolator linearInterpolator = new LinearInterpolator();   // 建立線性旋轉
        ra.setInterpolator(linearInterpolator);

        LinearInterpolator linearInterpolatorWord = new LinearInterpolator();   // 建立線性旋轉
        raWord.setInterpolator(linearInterpolatorWord);

        currentDegree = -degree;

        mWordConstraintLayout.startAnimation(ra);

        mNTextView.setAnimation(raWord);
        mWTextView.setAnimation(raWord);
        mSTextView.setAnimation(raWord);
        mETextView.setAnimation(raWord);
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

    private List<Double> getGodCompassRange(String godOrientation) {

        List<Double> list = new ArrayList<>();
        // 判斷座標落在的範圍
        switch (godOrientation) {
            case "北":
                list.add(337.5);
                list.add(22.5);
                break;
            case "東北":
                list.add(22.5);
                list.add(67.5);
                break;
            case "東":
                list.add(67.5);
                list.add(112.5);
                break;
            case "東南":
                list.add(112.5);
                list.add(157.5);
                break;
            case "南":
                list.add(157.5);
                list.add(202.5);
                break;
            case "西南":
                list.add(202.5);
                list.add(247.5);
                break;
            case "西":
                list.add(247.5);
                list.add(292.5);
                break;
            case "西北":
                list.add(292.5);
                list.add(337.5);
                break;
            default:
                break;
        }

        Log.d(TAG, " god compress min :" + list.get(0));
        Log.d(TAG, " god compress max :" + list.get(1));

        return list;
    }

    private void getCompassOrientation() {
        float values = orientationValues[0];
        Log.d(TAG, String.valueOf(values));

        double maxValue = get8CompassRange(values);

        compassImageAnimation(values);
        godImageShow(values, getGodCompassRange(godOrientation));

        mOrientationTextView.setText(compassOrientation);
    }

    private void godImageShow(float values, List<Double> list) {
        double compressMin = list.get(0);
        double compressMax = list.get(1);

        if (values < compressMax + 5 && values > compressMax) {
            int te = checkPoint(values, LEFT_SIDE);
            if (te == NEAR_GOD) {
                if (!isAnimationInRunning) {
                    mMoneyGodImageView.startAnimation(mTranslateLeftTopInAnimation);
                }
            }
            if (te == LEAVE_GOD) {
                if (!isAnimationOutRunning) {
                    mMoneyGodImageView.startAnimation(mTranslateRightTopOutAnimation);
                }
            }
        }
        if (values < compressMin && values > compressMin - 5) {
            int te = checkPoint(values, RIGHT_SIDE);
            if (te == NEAR_GOD) {
                if (!isAnimationInRunning) {
                    mMoneyGodImageView.startAnimation(mTranslateRightTopInAnimation);
                }
            }
            if (te == LEAVE_GOD) {
                if (!isAnimationOutRunning) {
                    mMoneyGodImageView.startAnimation(mTranslateLeftTopOutAnimation);
                }
            }
        }
        if (values >= compressMin && values <= compressMax) {
            if (!isAnimationInRunning) {
                mMoneyGodImageView.startAnimation(mTranslateLeftTopInAnimation);
            } else {
                mMoneyGodImageView.setVisibility(View.VISIBLE);
            }
            isInGod = true;
        }
        if (values >= compressMax + 5 || values <= compressMin - 5) {
            if (isInGod && !isAnimationOutRunning) {
                mMoneyGodImageView.startAnimation(mTranslateLeftTopOutAnimation);
            }
            mMoneyGodImageView.setVisibility(View.INVISIBLE);
        }
    }

    private int checkPoint(final double range, int eventSide) {
        int go;
        if (temp > range) {
            if (eventSide == LEFT_SIDE) {
                go = NEAR_GOD;
            } else {
                go = LEAVE_GOD;
            }
        } else {
            if (eventSide == LEFT_SIDE) {
                go = LEAVE_GOD;
            } else {
                go = NEAR_GOD;
            }
        }

        temp = range;

        return go;
    }

    private void animationInit() {
        mTranslateLeftTopInAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_left_top_in);
        mTranslateLeftTopInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMoneyGodImageView.setVisibility(View.INVISIBLE);
                isAnimationInRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMoneyGodImageView.setVisibility(View.VISIBLE);
                isAnimationOutRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mTranslateLeftTopOutAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_left_top_out);
        mTranslateLeftTopOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMoneyGodImageView.setVisibility(View.VISIBLE);
                isAnimationOutRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMoneyGodImageView.setVisibility(View.INVISIBLE);
                isAnimationInRunning = false;
                isInGod = false;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mTranslateRightTopInAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_right_top_in);
        mTranslateRightTopInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMoneyGodImageView.setVisibility(View.INVISIBLE);
                isAnimationInRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMoneyGodImageView.setVisibility(View.VISIBLE);
                isAnimationOutRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mTranslateRightTopOutAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_right_top_out);
        mTranslateRightTopOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMoneyGodImageView.setVisibility(View.VISIBLE);
                isAnimationOutRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMoneyGodImageView.setVisibility(View.INVISIBLE);
                isAnimationInRunning = false;
                isInGod = false;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}