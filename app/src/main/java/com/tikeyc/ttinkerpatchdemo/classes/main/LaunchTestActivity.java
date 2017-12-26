package com.tikeyc.ttinkerpatchdemo.classes.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tikeyc.ttinkerpatchdemo.R;
import com.tikeyc.ttinkerpatchdemo.classes.utils.RestartAppUtils;

import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

public class LaunchTestActivity extends AppCompatActivity {


    @ViewInject(R.id.message_TextView)
    private TextView message_TextView;
    @ViewInject(R.id.progressBar)
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_test);

        initView();


        //downLoadTinkerPatchFile();

        testDownLoadTinkerPatchFile();


    }

    private void initView() {
        x.view().inject(this);

        progressBar.setMax(100);
        progressBar.setSecondaryProgress(0);
    }


    //////////////////
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int progress = progressBar.getSecondaryProgress() + 20;
                if (progress > 100) progress = 100;
                progressBar.setSecondaryProgress(progress);
                if (progress == 100) {
                    //开始执行加载过程
                    message_TextView.setText("加载补丁中...");
                    progressBar.setSecondaryProgress(0);
                }

            } else if (msg.what == 2){
                int progress = progressBar.getSecondaryProgress() + 30;
                if (progress > 100) progress = 100;
                progressBar.setSecondaryProgress(progress);
                if (progress == 100) {
                    handler.removeCallbacksAndMessages(null);
                    task.cancel();
                    timer.cancel();
                    //
                    RestartAppUtils.restartAppWithAlarmManager(null ,LaunchTestActivity.this, 0);
                    //RestartAppUtils.restartAPP(LaunchTestActivity.this, 0);
                }
            }
            super.handleMessage(msg);
        };
    };
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            if (message_TextView.getText().toString().equals("下载补丁中...")) {
                message.what = 1;
            } else if (message_TextView.getText().toString().equals("加载补丁中...")){
                message.what = 2;
            }

            handler.sendMessage(message);
        }
    };

    private void testDownLoadTinkerPatchFile() {

        timer.schedule(task, 1000, 1000);

    }



    ////////////////////以下是正式环境下的执行代码////////////////////

    private final static int MAX_DOWNLOAD_THREAD = 2; // 有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
    private final Executor executor = new PriorityExecutor(MAX_DOWNLOAD_THREAD, true);

    private String savaFolderPath;

    public String getSavaFolderPath() {
        savaFolderPath = getExternalFilesDir(null).getAbsolutePath() + "/tinkerPatchFile";
        File file = new File(savaFolderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return savaFolderPath;
    }

    /**
     * 根据不同版本的App下来不同版本的补丁包(用户手机上的App版本不确定，所以需要按版本下载不同的补丁包)
     * 1.0.0  patch_signed_7zip.apk
     * 1.0.1  patch_signed_7zip.apk
     * 1.0.2  patch_signed_7zip.apk
     * 1.0.3  patch_signed_7zip.apk
     * 1.0.4  patch_signed_7zip.apk
     */
    private void downLoadTinkerPatchFile() {

        //app版本
        String versionName = null;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //保存路径
        String title = "patch_signed_7zip";
        String fileType = "apk";
        String savePath = getSavaFolderPath() + "/" + title + "." + fileType;
        //下载补丁包
        String url = "http://localHost/downLoadTinkerPatchFile?" + "versionName=" + versionName;
        RequestParams requestParams = new RequestParams(url);
        requestParams.setAutoResume(true);
        requestParams.setAutoRename(false);
        requestParams.setSaveFilePath(savePath);
        requestParams.setExecutor(executor);
        requestParams.setCancelFast(true);
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                LogUtil.d("补丁下载成功，加载补丁中");
                //在SampleResultService类中回调打补丁状态信息
                TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), result.getAbsolutePath());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(LaunchTestActivity.this, "补丁下载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                float progress = (float)current / (float)total;
                LogUtil.e("progress:" + progress);
                progress = progress * 100;
                java.text.DecimalFormat df = new java.text.DecimalFormat("0.0");
                final String showProgress = df.format(progress);
                LogUtil.e("showProgress:" + showProgress);

            }
        });

    }

}
