package com.example.flutter_scan_plugin;

import android.util.Log;

import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.Size;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static java.lang.Math.min;

/**
 * @author :qlf
 */
class ScanView extends BarcodeView implements PluginRegistry.RequestPermissionsResultListener{
    public interface CaptureListener {
        void onCapture(String text);
    }
    private CaptureListener captureListener;

    private String LOG_TAG = "scan";
    private int CAMERA_REQUEST_CODE = 6537;
    private Context context;
    private Activity activity;
    private ActivityPluginBinding activityPluginBinding;
    private Application.ActivityLifecycleCallbacks lifecycleCallback;

    private double vw;
    private double vh;
    private double scale = .7;

    public ScanViewNew(Context context, Activity activity, @NonNull ActivityPluginBinding activityPluginBinding, @Nullable Map<String, Object> args) {
        super(context, null);

        this.context = context;
        this.activity = activity;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.activityPluginBinding = activityPluginBinding;
        activityPluginBinding.addRequestPermissionsResultListener(this);
        this.scale = (double) args.get("scale");

        checkPermission();
    }

    private void start() {
        addListenLifecycle();
        this.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                captureListener.onCapture(result.getText());
                Vibrator myVib = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
                if (myVib != null) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        myVib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        myVib.vibrate(50);
                    }
                }
            }
        });
        _resume();
    }

    private void checkPermission() {
        if (hasPermission()) {
            start();
        } else {
            String[] permissions = new String[1];
            permissions[0] = Manifest.permission.CAMERA;
            ActivityCompat.requestPermissions(activity, permissions, CAMERA_REQUEST_CODE);
        }
    }

    private boolean hasPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                activity.checkSelfPermission(Manifest.permission.CAMERA) == PERMISSION_GRANTED;
    }

    private void addListenLifecycle() {
//        activity.getApplication().registerActivityLifecycleCallbacks(lifecycleCallback);
    }

    public void _resume() {
        this.resume();
    }
    public void _pause() {
        this.pause();
    }
    public void toggleTorchMode(boolean mode) {
        this.setTorch(mode);
    }
    public void setCaptureListener(CaptureListener captureListener) {
        this.captureListener = captureListener;
    }
    public void dispose() {
        this.stopDecoding();
        _pause();
//        activity.getApplication().unregisterActivityLifecycleCallbacks(lifecycleCallback);
//        lifecycleCallback = null;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        vw = getWidth();
        vh = getHeight();
        if (scale < 1.0) {
            int areaWidth = (int) (min(vw, vh) * scale);
            this.setFramingRectSize(new Size(areaWidth, areaWidth));
        }
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PERMISSION_GRANTED) {
            start();
            Log.i(LOG_TAG, "onRequestPermissionsResult: true");
            return true;
        }
        Log.i(LOG_TAG, "onRequestPermissionsResult: false");
        return false;
    }
}
