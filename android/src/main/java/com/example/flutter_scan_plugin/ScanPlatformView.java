package com.example.flutter_scan_plugin;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Map;

import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

/**
 * @author :qlf
 * 添加原生组件实现第一步
 * ① Android 端实现原生组件 PlatformView 提供原生View
 */
class ScanPlatformView  implements PlatformView, MethodChannel.MethodCallHandler ,ScanView.CaptureListener{

    private MethodChannel methodChannel;
    private Context mContext;
    private Activity mActivity;
    private ActivityPluginBinding activityPluginBinding;
    private ParentView mParentView;
    private Boolean bFlashLight;
    private ScanView mScanView;
    private ScanDrawView mScanDrawView;

    ScanPlatformView(BinaryMessenger binaryMessenger,Context context,Activity activity,ActivityPluginBinding activityPluginBinding, int ViewId, Map<String,Object> args){
        methodChannel = new MethodChannel(binaryMessenger,"" + ViewId);
        methodChannel.setMethodCallHandler(this);
        this.mContext = context;
        this.mActivity = activity;
        this.activityPluginBinding = activityPluginBinding;
        initForBinding(args);
    }

    private initForBinding(Map<String,Object> args){
        this.mScanView = new ScanView(context, activity, activityPluginBinding,  args);
        this.mScanView.setCaptureListener(this);

        this.mScanDrawView = new ScanDrawView(context, activity, args);

        this.mParentView = new ParentView(context);
        this.parentView.addView(this.mScanView);
        this.parentView.addView(this.mScanDrawView);
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if (call.method.equals("resume")) {
            resume();
        } else if (call.method.equals("pause")) {
            pause();
        } else if (call.method.equals("toggleTorchMode")) {
            toggleTorchMode();
        }
    }

    @Override
    public View getView() {
        return this.mParentView;
    }

    @Override
    public void dispose() {
        this.mScanView.dispose();
    }

    private void resume() {
        this.mScanView.resume();
        this.mScanDrawView.resume();
    }

    private void pause() {
        this.mScanView.pause();
        this.mScanDrawView.pause();
    }

    private void toggleTorchMode() {
        this.mScanView.toggleTorchMode(!flashlight);
        flashlight = !flashlight;
    }

    @Override
    public void onCapture(String text) {
        channel.invokeMethod("onCaptured", text);
        pause();
    }
}
