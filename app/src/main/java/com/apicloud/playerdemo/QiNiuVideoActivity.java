package com.apicloud.playerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.apicloud.playerdemo.utils.GetPathFromUri;
import com.apicloud.playerdemo.utils.PermissionChecker;
import com.apicloud.streampushpull.R;
import com.pili.pldroid.player.AVOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class QiNiuVideoActivity extends AppCompatActivity {

    private static final String TAG = "QiNiuVideoActivity";
    private static final String DEFAULT_TEST_URL = "rtmp://www.juntaikeji.com:19601/live/1";

    private Spinner mActivitySpinner;
    private EditText mEditText;
    private RadioGroup mStreamingTypeRadioGroup;
    private RadioGroup mDecodeTypeRadioGroup;
    private CheckBox mVideoCacheCheckBox;
    private CheckBox mLoopCheckBox;
    private CheckBox mVideoDataCallback;
    private CheckBox mAudioDataCallback;
    private CheckBox mDisableCheckBox;
    private RadioButton mLivingCheckBox;
    private RadioButton mPlayCheckBox;
    private LinearLayout mStartSetting;
    private EditText mStartPosEditText;

    public static final String[] TEST_ACTIVITY_ARRAY = {
            "PLMediaPlayerActivity",
            "PLAudioPlayerActivity",
            "PLVideoViewActivity",
            "PLVideoTextureActivity",
            "MultiInstanceActivity"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qiniu_main);
        mEditText = findViewById(R.id.VideoPathEdit);
        mEditText.setText(DEFAULT_TEST_URL);

        mStreamingTypeRadioGroup = findViewById(R.id.StreamingTypeRadioGroup);
        mDecodeTypeRadioGroup = findViewById(R.id.DecodeTypeRadioGroup);

        mActivitySpinner = findViewById(R.id.TestSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, TEST_ACTIVITY_ARRAY);
        mActivitySpinner.setAdapter(adapter);
        mActivitySpinner.setSelection(2);

        mVideoCacheCheckBox = findViewById(R.id.CacheCheckBox);
        mLoopCheckBox = findViewById(R.id.LoopCheckBox);
        mVideoDataCallback = findViewById(R.id.VideoCallback);
        mAudioDataCallback = findViewById(R.id.AudioCallback);
        mDisableCheckBox = findViewById(R.id.DisableLog);

        mVideoCacheCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!isPermissionOK()) {
                    mVideoCacheCheckBox.setChecked(false);
                }
            }
        });

        mLivingCheckBox = findViewById(R.id.RadioLiveStreaming);
        mPlayCheckBox = findViewById(R.id.RadioPlayback);
        mStartSetting = findViewById(R.id.StartSetting);

        mLivingCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartSetting.setVisibility(View.INVISIBLE);
            }
        });
        mPlayCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartSetting.setVisibility(View.VISIBLE);
            }
        });

        mStartPosEditText = findViewById(R.id.TextStartPos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean isPermissionOK() {
        PermissionChecker checker = new PermissionChecker(this);
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checker.checkPermission();
        if (!isPermissionOK) {
            Toast.makeText(this, "Some permissions is not approved !!!", Toast.LENGTH_SHORT).show();
        }
        return isPermissionOK;
    }

    public void onClickLocalFile(View v) {
        if(!isPermissionOK()){
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
        }
        startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), 0);
    }

    public void onClickPlay(View v) {
        String videopath = mEditText.getText().toString();
        if (!"".equals(videopath)) {
            jumpToPlayerActivity(videopath, false);
        }
    }

    public void onClickList(View v) {
        String videopath = mEditText.getText().toString();
        if (!"".equals(videopath)) {
            jumpToPlayerActivity(videopath, true);
        }
    }

    public void jumpToPlayerActivity(String videoPath, boolean isList) {
//        if (isList) {
//            Intent intent = new Intent(this, PLVideoListActivity.class);
//            intent.putExtra("videoPath", videoPath);
//            startActivity(intent);
//            return;
//        }
        Class<?> cls;
        cls = PlayLiveActivity.class;
        Intent intent = new Intent(this, cls);
        intent.putExtra("videoPath", videoPath);
        if (mDecodeTypeRadioGroup.getCheckedRadioButtonId() == R.id.RadioHWDecode) {
            intent.putExtra("mediaCodec", AVOptions.MEDIA_CODEC_HW_DECODE);
        } else if (mDecodeTypeRadioGroup.getCheckedRadioButtonId() == R.id.RadioSWDecode) {
            intent.putExtra("mediaCodec", AVOptions.MEDIA_CODEC_SW_DECODE);
        } else {
            intent.putExtra("mediaCodec", AVOptions.MEDIA_CODEC_AUTO);
        }
        if (mStreamingTypeRadioGroup.getCheckedRadioButtonId() == R.id.RadioLiveStreaming) {
            intent.putExtra("liveStreaming", 1);
        } else {
            intent.putExtra("liveStreaming", 0);
        }
        intent.putExtra("cache", mVideoCacheCheckBox.isChecked());
        intent.putExtra("loop", mLoopCheckBox.isChecked());
        intent.putExtra("video-data-callback", mVideoDataCallback.isChecked());
        intent.putExtra("audio-data-callback", mAudioDataCallback.isChecked());
        intent.putExtra("disable-log", mDisableCheckBox.isChecked());
        if (!"".equals(mStartPosEditText.getText().toString())) {
            intent.putExtra("start-pos", Integer.valueOf(mStartPosEditText.getText().toString()));
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != Activity.RESULT_OK) {
//            return;
//        }
//        if (requestCode == 0) {
//            String selectedFilepath = GetPathFromUri.getPath(this, data.getData());
//            Log.i(TAG, "Select file: " + selectedFilepath);
//            if (selectedFilepath != null && !"".equals(selectedFilepath)) {
//                mEditText.setText(selectedFilepath, TextView.BufferType.EDITABLE);
//            }
//        } else if (requestCode == IntentIntegrator.REQUEST_CODE) {
//            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//            if (result != null) {
//                if (result.getContents() == null) {
//                    Toast.makeText(this, "扫码取消！", Toast.LENGTH_SHORT).show();
//                } else {
//                    mEditText.setText(result.getContents());
//                }
//            }
//        }
    }
//
//    protected String getBuildTimeDescription() {
//        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(BuildConfig.BUILD_TIMESTAMP);
//    }
}
