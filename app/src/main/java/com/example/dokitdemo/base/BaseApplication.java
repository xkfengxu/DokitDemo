package com.example.dokitdemo.base;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.alibaba.android.arouter.launcher.ARouter;
import com.didichuxing.doraemonkit.DoraemonKit;

/**
 * @author fengxu
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DoraemonKit.install(this);
        //false:不显示入口icon 默认为true
//        DoraemonKit.setAwaysShowMianIcon(false);

        if (getApplicationInfo() != null &&
                (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }
}
