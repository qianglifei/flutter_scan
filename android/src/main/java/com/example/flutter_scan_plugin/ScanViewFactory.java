package com.example.flutter_scan_plugin;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

/**
 * @author :qlf
 * Android 端创建PlatformViewFactory 用于生成PlatformView
 */
class ScanViewFactory extends PlatformViewFactory {
    private final BinaryMessenger mMessenger;
    private final Context mContext;
    private final Activity mActivity;
    private ActivityPluginBinding mActivityPluginBinding;

    ScanPlatformView(BinaryMessenger messenger,Context context,Activity activity,ActivityPluginBinding activityPluginBinding){
        super(StandardMessageCodec.INSTANCE);
        this.mMessenger = messenger;
        this.mContext = context;
        this.mActivity = activity;
        this.mActivityPluginBinding = activityPluginBinding;
    }

    /**
     * @param createArgsCodec the codec used to decode the args parameter of {@link #create}.
     */
    public ScanViewFactory(MessageCodec<Object> createArgsCodec) {
        super(createArgsCodec);
    }


    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        final Map<String,Object> creationParams = args;
        return new ScanPlatformView(mMessenger,mContext,mActivity,mActivityPluginBinding,viewId,creationParams);
    }
}
