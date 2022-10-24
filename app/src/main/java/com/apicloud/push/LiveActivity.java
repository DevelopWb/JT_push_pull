package com.apicloud.push;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.apicloud.ActivityManagerTool;
import com.apicloud.BackgroundService;
import com.apicloud.streampushpull.MainActivity;
import com.apicloud.streampushpull.R;
import com.orhanobut.hawk.Hawk;

import java.security.PublicKey;
import java.util.LinkedList;

import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.filter.hardvideofilter.BaseHardVideoFilter;
import me.lake.librestreaming.filter.hardvideofilter.HardVideoGroupFilter;
import me.lake.librestreaming.ws.StreamAVOption;
import me.lake.librestreaming.ws.StreamLiveCameraView;
import me.lake.librestreaming.ws.filter.hardfilter.GPUImageBeautyFilter;
import me.lake.librestreaming.ws.filter.hardfilter.extra.GPUImageCompatibleFilter;


public class LiveActivity extends AppCompatActivity {
    private static final String TAG = LiveActivity.class.getSimpleName()+"push_background";
    private StreamLiveCameraView mLiveCameraView;
    private StreamAVOption streamAVOption;
    private LiveUI mLiveUI;
    private CharSequence[] resDisplay = new CharSequence[]{"640x480", "1280x720", "1920x1080", "2560x1440", "3840x2160"};
    private ServiceConnection conn = null;
    private BackgroundService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        initLiveConfig();
        mLiveUI = new LiveUI(this,mLiveCameraView,Hawk.get(MainActivity.URL,""));
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(new Intent(this, BackgroundService.class));
        } else {
            // Pre-O behavior.
            startService(new Intent(this, BackgroundService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mLiveCameraView.isStreaming()) {
            mLiveCameraView.startStreaming(Hawk.get(MainActivity.URL,""));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mService != null) {
//            mService.startPush();
//        }

    }

    //    /**
//     * 配置相机的分辨率
//     */
//    private void setCameraRes(CharSequence[] res_display, int index) {
//        new AlertDialog.Builder(this).setTitle("设置分辨率").setSingleChoiceItems(res_display, index, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int position) {
//                String title = res_display[position].toString();
//                if (isStreaming()) {
//                    Toast.makeText(StreamActivity.this, getPushStatusMsg() + ",无法切换分辨率", Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                    return;
//                }
//                String[] titles = title.split("x");
//                if (res_display.length > 3) {
//                    //原生相机配置分辨率
//                    if (!Util.getSupportResolution(StreamActivity.this).contains(title)) {
//                        Toast.makeText(StreamActivity.this, "您的相机不支持此分辨率", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                        return;
//                    }
//                    Hawk.put(HawkProperty.KEY_SCREEN_PUSHING_RES_INDEX, position);
//                    Hawk.put(HawkProperty.KEY_NATIVE_WIDTH, Integer.parseInt(titles[0]));
//                    Hawk.put(HawkProperty.KEY_NATIVE_HEIGHT, Integer.parseInt(titles[1]));
//                } else {
//                    Hawk.put(HawkProperty.KEY_SCREEN_PUSHING_UVC_RES_INDEX, position);
//                    Hawk.put(HawkProperty.KEY_UVC_WIDTH, Integer.parseInt(titles[0]));
//                    Hawk.put(HawkProperty.KEY_UVC_HEIGHT, Integer.parseInt(titles[1]));
//                }
//                mScreenResTv.setText("分辨率:" + title);
//
//                if (mMediaStream != null) {
//                    mMediaStream.updateResolution();
//                }
//                dialog.dismiss();
//            }
//
//
//        }).show();
//    }
    /**
     * 设置推流参数
     */
    public void initLiveConfig() {
        mLiveCameraView = (StreamLiveCameraView) findViewById(R.id.stream_previewView);

        //参数配置 start
        streamAVOption = new StreamAVOption();
        streamAVOption.streamUrl = Hawk.get(MainActivity.URL,"");
        //参数配置 end

        mLiveCameraView.init(this, streamAVOption);
        mLiveCameraView.addStreamStateListener(resConnectionListener);
    }

    RESConnectionListener resConnectionListener = new RESConnectionListener() {
        @Override
        public void onOpenConnectionResult(int result) {
            //result 0成功  1 失败
            Toast.makeText(LiveActivity.this,"推流成功",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWriteError(int errno) {
            Toast.makeText(LiveActivity.this,"推流出错,请尝试重连",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCloseConnectionResult(int result) {
            //result 0成功  1 失败
            Toast.makeText(LiveActivity.this,"关闭推流",Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLiveCameraView != null) {
            mLiveCameraView.stopStreaming();
        }
        mLiveCameraView.destroy();
        stopService(new Intent(this, BackgroundService.class));

    }





}
