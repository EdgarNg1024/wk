package edgar.wk.help.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;

import com.ubtechinc.sauron.api.FaceInfo;
import com.ubtrobot.commons.ResponseListener;
import com.ubtrobot.mini.sauron.FaceApi;
import com.ubtrobot.mini.sauron.FaceFindListener;
import com.ubtrobot.mini.sauron.TakePicApi;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edgar.wk.R;
import edgar.wk.utils.LogUtils;
import edgar.wk.utils.permissions.Permission;
import edgar.wk.utils.permissions.RxPermissions;

public class HelpActivity extends AppCompatActivity {

    @BindView(R.id.etPerson)
    protected EditText EtPhoneMembers;

    @BindView(R.id.etMsgContent)
    protected EditText EtMsgContent;

    //紧急联系人们
    List<String> phoneMembers;
    //消息内容
    String msgContent;
    //是否需要打电话
    boolean isNeedCall = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.btnHelp})
    void OnClick(View v) {
        switch (v.getId()) {
            case R.id.btnHelp:
                phoneMembers = Arrays.asList(EtPhoneMembers.getText().toString().split(","));
                msgContent = EtMsgContent.getText().toString();


                new RxPermissions(this).requestEach(Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS)
                        .subscribe((Permission permission) -> {
                                    if (permission.granted) {
                                        // 用户已经同意该权限

                                        //找人脸
                                        long timeout = 10000;
                                        FaceApi.get().findFace(timeout, new FaceFindListener() {
                                            @Override
                                            public void onPause() {

                                            }

                                            @Override
                                            public void onStart() {

                                            }

                                            @Override
                                            public void onFaceChange(List<FaceInfo> list) {
                                                //找到就发彩信
                                                // TODO: 2018/8/21 0021 发彩信
                                                TakePicApi.get().takePicImmediately(new ResponseListener<Void>() {
                                                    @Override
                                                    public void onResponseSuccess(Void aVoid) {
                                                        //发彩信
                                                    }

                                                    @Override
                                                    public void onFailure(int i, @NonNull String s) {
                                                        //失败就发短信
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onStop() {

                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                //找不到就发短信
                                                // 获取短信管理器
                                                android.telephony.SmsManager smsManager = android.telephony.SmsManager
                                                        .getDefault();

                                                for (String phoneNum : phoneMembers) {
                                                    // 拆分短信内容（手机短信长度限制）
                                                    List<String> divideContents = smsManager.divideMessage(msgContent);
                                                    for (String text : divideContents) {
                                                        smsManager.sendTextMessage(phoneNum, null, text, null, null);
                                                    }
                                                }
                                            }
                                        });


                                        //同时按顺序拨打电话
                                        // TODO: 2018/8/21 0021 不通的话,接着打电话给别人,全都不通的话,就打电话给120;如果已经通了的话,就不继续拨打电话了
                                        //监听处理
                                        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                        PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
                                            @Override
                                            public void onCallStateChanged(int state, String incomingNumber) {
                                                LogUtils.d(state);
                                                switch (state) {
                                                    case TelephonyManager.CALL_STATE_IDLE: //空闲
                                                        break;
                                                    case TelephonyManager.CALL_STATE_RINGING: //响铃来电
                                                        break;
                                                    case TelephonyManager.CALL_STATE_OFFHOOK: //摘机（正在通话中）
                                                        //打通了,就不需要再拨打了
                                                        isNeedCall = false;
                                                        //todo 播放预设好的语音

                                                        MediaPlayer mplayer=new MediaPlayer();
                                                        mplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                        String path="";
                                                        try {
                                                            mplayer.setDataSource(path);
                                                            mplayer.prepare();
                                                            mplayer.start();
                                                        } catch (Exception e){
                                                            e.printStackTrace();
                                                        }

                                                        break;
                                                    default:
                                                        //看看还有什么其他状态
                                                        LogUtils.d(state);
                                                }
                                            }
                                        };
                                        if (mTelephonyManager != null) {
                                            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                                        }
                                        for (String phoneNum : phoneMembers) {
                                            if (!isNeedCall)
                                                continue;
                                            callPhone(phoneNum);
                                            // TODO: 2018/8/21 0021  全都不通的话,就打电话给120
                                        }
                                    } else if (permission.shouldShowRequestPermissionRationale) {
                                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                    } else {
                                        // 用户拒绝了该权限，并且选中『不再询问』，提醒用户手动打开权限
                                    }
                                }
                        );
                break;

        }

    }


    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */
    @SuppressLint("MissingPermission")
    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }


}
