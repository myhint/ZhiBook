package com.srtianxia.zhibook.app;

import android.app.Application;
import android.content.Context;



import cn.bmob.v3.Bmob;

/**
 * Created by srtianxia on 2016/1/20.
 */
public class APP extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Bmob.initialize(this, "cfdeeb9c25ea74674dee63513743090a");
//        ZhiHuModel.getInstance();
    }

    public static Context getContext(){
        return context;
    }
}
