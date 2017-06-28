package com.example.bryanyen.goldcompassapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bryan.yen on 2017/6/26.
 * <p>
 * 農曆計算工具
 */

public class GoldDate {

    private static final String TAG = "godData";

    /**
     * 轉換的結果集.year .month .day .isLeap .yearCyl .dayCyl .monCyl
     */
    private int result[];
    private Calendar calendar;
    private static int[] lunarInfo = {0x04bd8, 0x04ae0, 0x0a570, 0x054d5,
            0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, 0x04ae0,
            0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2,
            0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40,
            0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566, 0x0d4a0,
            0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7,
            0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0,
            0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550, 0x15355,
            0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263,
            0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0,
            0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6, 0x095b0,
            0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46,
            0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50,
            0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954,
            0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0,
            0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0,
            0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50,
            0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
            0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6,
            0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0,
            0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0};

    //    private static int[] solarMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static String[] Gan = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static String[] Zhi = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private static String[] Animals = {"鼠", "牛", "虎", "兔", "龍", "蛇", "馬", "羊", "猴", "雞", "狗", "豬"};
    //    private static int[] sTermInfo = {0, 21208, 42467, 63836, 85337, 107014, 128867, 150921, 173149, 195551,
    // 218072,
    //            240693, 263343, 285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795, 462224, 483532,
    // 504758};
    private static String[] nStr1 = {"日", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
    private static String[] nStr2 = {"初", "十", "廿", "卅", "　"};
    private static String[] monthNong = {"正", "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    private static String[] yearName = {"零", "壹", "貳", "叁", "肆", "伍", "陸", "柒", "捌", "玖"};

    public GoldDate() {
        this.calendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        convert();// 轉換日期
    }

    public GoldDate(int year, int month, int day) {
        calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        convert();// 轉換日期
    }

    /**
     * @param year      西元年
     * @param month     西元月
     * @param day       西元日
     * @param hourOfDay 24小時制(0-23)
     * @param minute    分鐘
     */
    public GoldDate(int year, int month, int day, int hourOfDay, int minute) {
        calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hourOfDay, minute);
        convert();// 轉換日期
    }

    public GoldDate(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        convert();// 轉換日期
    }

    public GoldDate(Calendar calendar) {
        this.calendar = calendar;
        convert();// 轉換日期
    }

    /**
     * 取得農曆年份的總天數
     *
     * @param year 農曆年
     * @return 天數
     */
    private static int totalDaysOfYear(int year) {
        int sum = 348; // 29*12
        for (int i = 0x8000; i > 0x8; i >>= 1) {
            sum += (lunarInfo[year - 1900] & i) == 0 ? 0 : 1; // 大月+1天
        }
        return (sum + leapDays(year)); // +閏月的天數
    }

    /**
     * 取得農曆年閏月的天數
     *
     * @param year 農曆年
     * @return 閏月天數
     */
    private static int leapDays(int year) {
        int result = 0;
        if (leapMonth(year) != 0) {
            result = (lunarInfo[year - 1900] & 0x10000) == 0 ? 29 : 30;
        }
        return result;
    }

    /**
     * 檢查農曆年閏哪個月 1-12 , 沒閏月傳回 0
     *
     * @param year 農曆年
     * @return 0 ~ 12, 0 = false.
     */
    private static int leapMonth(int year) {
        return (lunarInfo[year - 1900] & 0xf);
    }

    /**
     * 取得農曆年該月總天數
     *
     * @param y 農曆年
     * @param m 農曆月
     * @return 天數
     */
    private static int monthDays(int y, int m) {
        return ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0 ? 29 : 30);
    }

    /**
     * 將西元日期轉農曆日期
     */
    private void convert() {
        // 基準時間 1900-01-31是農曆1900年正月初一
        Calendar baseCalendar = Calendar.getInstance();
        baseCalendar.set(1900, 0, 31, 0, 0, 0); // 1900-01-31是農曆1900年正月初一
        Date baseDate = baseCalendar.getTime();
        // 偏移量（天）
        int offset = (int) ((calendar.getTimeInMillis() - baseDate.getTime()) / 86400000); // 天數(86400000=24*60*60*1000)
        // 基準時間在天干地支纪年法中的位置
        int monCyl = 14; // 1898-10-01是農曆甲子月
        int dayCyl = offset + 40; // 1899-12-21是農曆1899年臘月甲子日

        // 得到年數
        int i;
        int temp = 0;
        for (i = 1900; i < 2050 && offset > 0; i++) {
            temp = totalDaysOfYear(i); // 農曆每年天數
            offset -= temp;
            monCyl += 12;
        }
        if (offset < 0) {
            offset += temp;
            i--;
            monCyl -= 12;
        }

        int year = i; // 農曆年份
        int yearCyl = i - 1864; // 1864年是甲子年

        int leap = leapMonth(i); // 閏哪个月
        boolean isLeap = false;
        int j;
        for (j = 1; j < 13 && offset > 0; j++) {
            // 閏月
            if (leap > 0 && j == (leap + 1) && !isLeap) {
                --j;
                isLeap = true;
                temp = leapDays(year);
            } else {
                temp = monthDays(year, j);
            }
            // 解除閏月
            if (isLeap && j == (leap + 1)) {
                isLeap = false;
            }

            offset -= temp;

            if (!isLeap) {
                monCyl++;
            }
        }

        if (offset == 0 && leap > 0 && j == leap + 1) {
            if (isLeap) {
                isLeap = false;
            } else {
                isLeap = true;
                --j;
                --monCyl;
            }
        }

        if (offset < 0) {
            offset += temp;
            --j;
            --monCyl;
        }

        // 閏年誤差計算，3年差1個月
        if (yearCyl % 3 == 0) {
            --monCyl;
        }

        int month = j; // 農曆月份
        int day = offset + 1; // 農曆天

        result = new int[]{year, month, day, isLeap ? 1 : 0, yearCyl, monCyl, dayCyl};
    }

    /**
     * 獲取偏移量對應的干支, 0=甲子
     *
     * @param num 數字
     * @return 偏移量（年or月or日）
     */
    private static String cyclical(int num) {
        return (Gan[num % 10] + Zhi[num % 12]);
    }

    /**
     * 取得中文日期
     *
     * @param day 農曆日
     * @return 日期
     */
    private static String chineseDay(int day) {
        String result;
        switch (day) {
            case 10:
                result = "初十";
                break;
            case 20:
                result = "二十";
                break;
            case 30:
                result = "三十";
                break;
            default:
                result = nStr2[(day / 10)];// 取商
                result += nStr1[day % 10];// 取余
        }
        return (result);
    }

    /**
     * 取得正楷大寫年份
     *
     * @param y 年份
     * @return 年份字串
     */
    private static String chineseYear(int y) {
        String s = " ";
        int d;
        while (y > 0) {
            d = y % 10;
            y = (y - d) / 10;
            s = yearName[d] + s;
        }
        return (s);
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.M.d EEEEE", Locale.TAIWAN);

    /**
     * 輸出格式：2015.07.04 周六 乙未[羊]年 壬午月 辛巳日
     *
     * @return 日期字串
     */
    public String getLunarDate() {
        String s = sdf.format(calendar.getTime()) + " ";
        s += cyclical(result[4]) + "[" + Animals[(result[0] - 4) % 12] + "]年 ";
        s += cyclical(result[5]) + "月 ";
        s += cyclical(result[6]) + "日";
        return s;
    }

    /**
     * 輸出格式：五月十九
     *
     * @return 日期
     */
    public String getLunarDay() {
        return (result[3] == 1 ? "閏" : "") + monthNong[result[1]] + "月" + chineseDay(result[2]);
    }

    /**
     * 獲取時辰，輸出格式：戊子時
     *
     * @return 戊子時
     */
    public String getLunarTime() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int timeOffset = (result[6] % 10) * 24 + hour;
        return Gan[((timeOffset + 1) / 2) % 10] + Zhi[((hour + 1) / 2) % 12] + "時";
    }

    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * 後一天
     */
    public void nextDay() {
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        convert();
    }

    /**
     * 前一天
     */
    public void preDay() {
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        convert();
    }


    /**
     * 從DB取得今日財神方位
     * <p>
     * DB資料來源：http://www.fushantang.com/1013/m1006.html
     */
    public String getMoneyGodData(Context mContext) {
        final String TABLE_NAME = "lushData";
        final String DATE_TABLE_NAME = "lushDay";
        final String MONEY_DOD_TABLE_NAME = "moneyGodOrientation";
        try {
            DataBaseHelper mDbHelper = new DataBaseHelper(mContext);
            mDbHelper.createDataBase();
            mDbHelper.openDataBase();
            SQLiteDatabase mDb = mDbHelper.getReadableDatabase();

            String sql = "SELECT " + DATE_TABLE_NAME + "," + MONEY_DOD_TABLE_NAME +
                    " FROM " + TABLE_NAME +
                    " WHERE " + DATE_TABLE_NAME + " IN('" + cyclical(result[6]) + "')";

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToNext();
            }

            String moneyGodPosition = "";
            if (mCur != null) {
                moneyGodPosition = mCur.getString(mCur.getColumnIndex(MONEY_DOD_TABLE_NAME));
                mCur.close();
            }

            mDbHelper.close();

            return moneyGodPosition;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString() + "  UnableToCreateDatabase");
        }

        return "";
    }
}
