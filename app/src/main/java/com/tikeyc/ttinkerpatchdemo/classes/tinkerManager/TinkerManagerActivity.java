package com.tikeyc.ttinkerpatchdemo.classes.tinkerManager;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
import com.tikeyc.ttinkerpatchdemo.R;
import com.tikeyc.ttinkerpatchdemo.classes.LoginActivity;

import org.xutils.view.annotation.Event;
import org.xutils.x;

import java.io.File;


public class TinkerManagerActivity extends AppCompatActivity {


    @Event(value = {R.id.onReceiveUpgradePatch_button, R.id.cleanPatch_button,
            R.id.killAllOtherProcess_button, R.id.installNavitveLibraryABI_button,
            R.id.loadLibraryFromTinker_button}, type = View.OnClickListener.class)
    private void buttonClickAction(View view) {
        if (view.getId() == R.id.onReceiveUpgradePatch_button) {
            Tinker tinker = Tinker.with(getApplicationContext());
            if (tinker.isTinkerLoaded()) {
                Toast.makeText(this, "patch is loaded", Toast.LENGTH_SHORT).show();
                return;
            }
            //请求打补丁 （发现打一次补丁以后就不用再打了）
            String patchPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk";//补丁包的本地路径,测试时将补丁包手动存放在SD卡中
            File file = new File(patchPath);
            if (!file.exists()) {
                Toast.makeText(this, "你还没有制作补丁包并保存到SD卡中", Toast.LENGTH_SHORT).show();
                return;
            }
            //在SampleResultService类中回调打补丁状态信息
            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), patchPath);

        } else if (view.getId() == R.id.cleanPatch_button) {
            //卸载补丁 （看源码其实是删除patch_signed_7zip.apk补丁包文件）
            Tinker.with(getApplicationContext()).cleanPatch();// 卸载所有的补丁
            //Tinker.with(getApplicationContext()).cleanPatchByVersion("版本号");// 卸载指定版本的补丁

        } else if (view.getId() == R.id.killAllOtherProcess_button) {
            //杀死应用的其他进程
            ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
            android.os.Process.killProcess(android.os.Process.myPid());

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
     * 基础包loginEnable的值为false
     * 修复包loginEnable的值为true
     */
    private boolean loginEnable = false;

    @Event(value = R.id.login_button, type = View.OnClickListener.class)
    private void loginButtonClick(View view) {
        Tinker tinker = Tinker.with(getApplicationContext());
        if (tinker.isTinkerLoaded()) {
            Toast.makeText(this, "patch is loaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "patch is not loaded", Toast.LENGTH_SHORT).show();
            return;
        }
        if (loginEnable) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "暂未实现登录功能，请修改代码loginEnable = true;制作补丁包，加载，再试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinker_manager);

        initView();
    }

    private void initView() {
        x.view().inject(this);
    }


}
