package com.tikeyc.ttinkerpatchdemo.classes.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import org.xutils.common.util.LogUtil;

/**
 * Created by tikeyc on 2017/12/26.
 * GitHub：https://github.com/tikeyc
 */

public class KillAppService extends Service {

    /**关闭应用后多久重新启动*/
    private static  long stopDelayed = 2000;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(PackageName);
            startActivity(LaunchIntent);
            KillAppService.this.stopSelf();
        }
    };

    private String PackageName;

    public KillAppService() {
        LogUtil.d("KillAppService init");
        handler = new Handler();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        stopDelayed = intent.getLongExtra("Delayed",2000);
        PackageName = intent.getStringExtra("PackageName");
//        handler.postDelayed(（）->{
//            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(PackageName);
//            startActivity(LaunchIntent);
//            KillAppService.this.stopSelf();
//        },stopDelayed);
        handler.sendEmptyMessageDelayed(0, stopDelayed);
        LogUtil.d("KillAppService onStartCommand");
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d("KillAppService onBind");
        return null;
    }

}
