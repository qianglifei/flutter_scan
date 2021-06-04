package com.example.flutter_scan_plugin;

import android.app.Activity;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import static android.content.Context.VIBRATOR_SERVICE;

/** FlutterScanPlugin */
public class FlutterScanPlugin implements FlutterPlugin, MethodCallHandler , ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity activity;
  private ActivityPluginBinding activityPluginBinding;
  private Result result;
  private QrCodeAsyncTask task;
  private FlutterPluginBinding flutterPluginBinding;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.flutterPluginBinding = flutterPluginBinding;
  }
  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
      configChannel(binding);
  }

  private void configChannel(ActivityPluginBinding binding) {
      activity = binding.getActivity();
      channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(),"flutter_scan_plugin");
      channel.setMethodCallHandler(this);
      flutterPluginBinding.getPlatformViewRegistry()
              .registerViewFactory("mobile/scan_view",new ScanViewFactory(
                   flutterPluginBinding.getBinaryMessenger(),
                   flutterPluginBinding.getApplicationContext(),
                   activity,
                   binding
              ));
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

    this.flutterPluginBinding = null;
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
            configChannel(binding);
  }

  @Override
  public void onDetachedFromActivity() {
      activity == null;
      channel.setMethodCallHandler(null);
  }

  /**
   * AsyncTask 静态内部类，防止内存泄漏
   */
  static class QrCodeAsyncTask extends AsyncTask<String, Integer, String> {
    private final WeakReference<FlutterScanPlugin> mWeakReference;
    private final String path;

    public QrCodeAsyncTask(ScanPlugin plugin, String path) {
      mWeakReference = new WeakReference<>(plugin);
      this.path = path;
    }

    @Override
    protected String doInBackground(String... strings) {
      // 解析二维码/条码
      return QRCodeDecoder.decodeQRCode(mWeakReference.get().flutterPluginBinding.getApplicationContext(), path);
    }

    @Override
    protected void onPostExecute(String s) {
      super.onPostExecute(s);
      //识别出图片二维码/条码，内容为s
      ScanPlugin plugin = (ScanPlugin) mWeakReference.get();
      plugin._result.success(s);
      plugin.task.cancel(true);
      plugin.task = null;
      if (s!=null) {
        Vibrator myVib = (Vibrator) plugin.flutterPluginBinding.getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        if (myVib != null) {
          if (Build.VERSION.SDK_INT >= 26) {
            myVib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
          } else {
            myVib.vibrate(50);
          }
        }
      }
    }
  }
}
