package com.apicloud;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/8/4 14:50
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/4 14:50
 */
public abstract class BaseAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerTool.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManagerTool.getInstance().removeActivity(this);
    }
}
