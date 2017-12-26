package com.tikeyc.ttinkerpatchdemo.classes.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.AppUtils;

/**
 * Created by tikeyc on 2017/12/26.
 * GitHub：https://github.com/tikeyc
 */

public class RestartAppUtils {

    public static void restartAppWithAlarmManager(Application application, Activity context, long delayed) {
        //RestartAppUtils.restartAPP(this, 500);
        PendingIntent restartIntent;
        AlarmManager alarmManager;
        if (application != null) {
            Intent intent = application.getPackageManager().getLaunchIntentForPackage(application.getPackageName());
            restartIntent = PendingIntent.getActivity(application, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
        } else {
            Intent intent = context.getBaseContext().getPackageManager().getLaunchIntentForPackage(context.getBaseContext().getPackageName());
            restartIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + delayed, restartIntent); // delayed秒钟后重启应用
        //System.exit(0); //不能退出所有activity
        AppUtils.exitApp();//能退出所有activity
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    /**
     * 重启整个APP
     * @param context
     * @param delayed 延迟多少毫秒
     */
    public static void restartAPP(Context context,long delayed){

        /**开启一个新的服务，用来重启本APP*/
        Intent intent1 = new Intent(context, KillAppService.class);
        intent1.putExtra("PackageName", context.getPackageName());
        intent1.putExtra("Delayed", delayed);
        context.startService(intent1);

        /**杀死整个进程**/
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    /***重启整个APP*/
    public static void restartAPP(Context context){
        restartAPP(context,2000);
    }

}
