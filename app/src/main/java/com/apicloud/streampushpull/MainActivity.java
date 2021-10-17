package com.apicloud.streampushpull;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.apicloud.BaseAppActivity;
import com.apicloud.playerdemo.PlayLiveActivity;
import com.apicloud.push.LiveActivity;
import com.orhanobut.hawk.Hawk;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

public class MainActivity extends BaseAppActivity implements View.OnClickListener {

    /**
     * 请输入URL
     */
    private EditText mUrlEt;
    /**
     * 推流
     */
    private TextView mPushStreanTv;
    /**
     * 拉流
     */
    private TextView mPullStreamTv;
    public static final String URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mUrlEt = (EditText) findViewById(R.id.url_et);
        mPushStreanTv = (TextView) findViewById(R.id.push_strean_tv);
        mPushStreanTv.setOnClickListener(this);
        mPullStreamTv = (TextView) findViewById(R.id.pull_stream_tv);
        mPullStreamTv.setOnClickListener(this);
//        Hawk.put(URL,"rtmp://www.juntaikeji.com:19601/live/2");
//        mUrlEt.setText(Hawk.get(URL, "rtmp://www.juntaikeji.com:19601/live/1"));

//        mUrlEt.setText("rtmp://live-push.bilivideo.com/live-bvc/?streamname=live_396731842_81355915&key=2a1cf08b6ec73a01a16c9fa9d8feed10");
    }

    @Override
    public void onClick(View v) {
        String value = mUrlEt.getText().toString().trim();

        if (TextUtils.isEmpty(value)) {
            Toast.makeText(this, "请输入地址", Toast.LENGTH_SHORT).show();
            return;
        }
        Hawk.put(URL, value);
        switch (v.getId()) {
            default:
                break;
            case R.id.push_strean_tv:
                startActivity(new Intent(this, LiveActivity.class));
                break;
            case R.id.pull_stream_tv:
                startActivity(new Intent(this, PlayLiveActivity.class).putExtra("videoPath", value));
                break;
        }
    }
}
