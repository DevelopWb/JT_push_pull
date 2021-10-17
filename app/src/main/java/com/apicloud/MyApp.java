package com.apicloud;

import android.app.Application;

import com.orhanobut.hawk.Hawk;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/6/23 11:20
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/6/23 11:20
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).build();
    }
}
