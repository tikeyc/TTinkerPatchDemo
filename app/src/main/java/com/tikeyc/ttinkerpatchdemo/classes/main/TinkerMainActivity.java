package com.tikeyc.ttinkerpatchdemo.classes.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tikeyc.ttinkerpatchdemo.R;
import com.tikeyc.ttinkerpatchdemo.classes.user.LoginActivity;
import com.tikeyc.ttinkerpatchdemo.classes.utils.RestartAppUtils;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class TinkerMainActivity extends AppCompatActivity {


    @ViewInject(R.id.progressBar_LinearLayout)
    private LinearLayout progressBar_LinearLayout;
    @ViewInject(R.id.message_TextView)
    private TextView message_TextView;
    @ViewInject(R.id.progressBar)
    private ProgressBar progressBar;


    @Event(value = {R.id.onReceiveUpgradePatch_button, R.id.cleanPatch_button,
            R.id.killAllOtherProcess_button, R.id.installNavitveLibraryABI_button,
            R.id.loadLibraryFromTinker_button}, type = View.OnClickListener.class)
    private void buttonClickAction(View view) {
        if (view.getId() == R.id.onReceiveUpgradePatch_button) {
//            Tinker tinker = Tinker.with(getApplicationContext());
//            if (tinker.isTinkerLoaded()) {
//                Toast.makeText(this, "patch is loaded", Toast.LENGTH_SHORT).show();
//                return;
//            }
            //请求打补丁 （发现打一次补丁以后就不用再打了）
            String patchPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk";//补丁包的本地路径,测试时将补丁包手动存放在SD卡中
            File file = new File(patchPath);
            if (!file.exists()) {
                Toast.makeText(this, "你还没有制作补丁包并保存到SD卡中", Toast.LENGTH_SHORT).show();
                return;
            }
            //loadTinkerPatch();
            progressBar_LinearLayout.setVisibility(View.VISIBLE);
            testDownLoadTinkerPatchFile();

        } else if (view.getId() == R.id.cleanPatch_button) {
            //卸载补丁 （看源码其实是删除patch_signed_7zip.apk补丁包文件）
            Tinker.with(getApplicationContext()).cleanPatch();// 卸载所有的补丁
            //Tinker.with(getApplicationContext()).cleanPatchByVersion("版本号");// 卸载指定版本的补丁

        } else if (view.getId() == R.id.killAllOtherProcess_button) {
//            //杀死应用的其他进程
//            ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
//            android.os.Process.killProcess(android.os.Process.myPid());

            //RestartAppUtils.restartAPP(this, 500);

            RestartAppUtils.restartAppWithAlarmManager(null ,this, 0);

        } else if (view.getId() == R.id.installNavitveLibraryABI_button) {
            if (true) {
                Toast.makeText(this, "暂未实现", Toast.LENGTH_SHORT).show();
                return;
            }
            //Hack方式修复so
            TinkerLoadLibrary.installNavitveLibraryABI(this, "abi");//abi：cpu架构类型 比如：armeabi

        } else if (view.getId() == R.id.loadLibraryFromTinker_button) {
            if (true) {
                Toast.makeText(this, "暂未实现", Toast.LENGTH_SHORT).show();
                return;
            }
            //非Hack方式修复so;
            TinkerLoadLibrary.loadLibraryFromTinker(getApplicationContext(), "lib/" + "abi", "so库的模块名"); // 加载任意abi库;abi：cpu架构类型 比如：armeabi
            TinkerLoadLibrary.loadArmLibrary(getApplicationContext(), "so库的模块名"); // 只适用于加载armeabi库
            TinkerLoadLibrary.loadArmV7Library(getApplicationContext(), "so库的模块名"); // 只适用于加载armeabi-v7a库

        }
    }


    /**
     * 用于测试
     * 基础包loginEnable的值为 false
     * 修复包loginEnable的值为 true
     */
    private boolean loginEnable = true;

    @Event(value = R.id.login_button, type = View.OnClickListener.class)
    private void loginButtonClick(View view) {

        if (loginEnable) {
            Tinker tinker = Tinker.with(getApplicationContext());
            if (tinker.isTinkerLoaded()) {
                Toast.makeText(this, "patch is loaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "patch is not loaded", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "暂未实现登录功能，请修改代码loginEnable = true;制作补丁包，加载，再试", Toast.LENGTH_SHORT).show();
        }
    }

    /**启动APP时的模拟测试
     * @param view
     */
    @Event(value = R.id.launch_button, type = View.OnClickListener.class)
    private void launchButtonClick(View view) {
        Intent intent  = new Intent(this, LaunchTestActivity.class);
        startActivity(intent);
        //finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinker_main);

        initView();
    }

    private void initView() {
        x.view().inject(this);


        progressBar.setMax(100);
        progressBar.setSecondaryProgress(0);
    }


    /**
     * 请求打补丁
     */
    private void loadTinkerPatch() {
        //请求打补丁 （发现打一次补丁以后就不用再打了）
        String patchPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk";//补丁包的本地路径,测试时将补丁包手动存放在SD卡中
        File file = new File(patchPath);
        if (!file.exists()) {
            Toast.makeText(this, "你还没有制作补丁包并保存到SD卡中", Toast.LENGTH_SHORT).show();
            return;
        }
        //在SampleResultService类中回调打补丁状态信息
        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), patchPath);
    }

    //////////////////模拟测试 下载补丁 加载补丁过程//////////////////


    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacksAndMessages(null);
        task.cancel();
        timer.cancel();
        handler = null;
        task = null;
        timer = null;
    }

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
                    //下载补丁完成后去打补丁，打补丁貌似没找到打补丁的进度回调，只能模拟一下了
                    loadTinkerPatch();
                }

            } else if (msg.what == 2){
                int progress = progressBar.getSecondaryProgress() + 1;
                if (progress > 100) progress = 100;
                progressBar.setSecondaryProgress(progress);
                if (progress == 100) {
                    handler.removeCallbacksAndMessages(null);
                    task.cancel();
                    timer.cancel();

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

}
