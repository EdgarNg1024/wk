package edgar.wk.help.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
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
    public static int REQUEST_FALLALERT = 333;
    //打电话的时间戳
    private long tsCall = 0;
    //phone index
    private int callIndex = 0;

    private final String TAG = "HelpActivity";
    private final String alertNum2 = "120";

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
                phoneMembers = new ArrayList<String>();
                phoneMembers.addAll(Arrays.asList(EtPhoneMembers.getText().toString().split(",")));
                msgContent = EtMsgContent.getText().toString();

                new RxPermissions(this).requestEach(Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS)
                        .subscribe((Permission permission) -> {
                                    if (permission.granted) {
                                        // 用户已经同意该权限

                                        // 获取短信管理器
                                        android.telephony.SmsManager smsManager = android.telephony.SmsManager
                                                .getDefault();

                                        /*//找人脸
                                        long timeout = 15;//单位是秒
                                        FaceApi.get().findFace(timeout, new FaceFindListener() {
                                            @Override
                                            public void onPause() {

                                            }

                                            @Override
                                            public void onStart() {

                                            }

                                            @Override
                                            public void onFaceChange(List<FaceInfo> list) {
                                                //找到人就拍照之后发彩信
                                                //找不到就发短信
                                                // 获取短信管理器
                                                android.telephony.SmsManager smsManager = android.telephony.SmsManager
                                                        .getDefault();

                                                for (String phoneNum : phoneMembers) {
                                                    // 拆分短信内容（手机短信长度限制）
                                                    List<String> divideContents = smsManager.divideMessage(msgContent);
                                                    for (String text : divideContents) {
                                                        LogUtils.d(phoneNum + text);
                                                        if (!phoneNum.equals(alertNum2)) {
                                                            smsManager.sendTextMessage(phoneNum, null, text, null, null);
                                                        }
                                                    }
                                                }
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
*/

                                        //监听处理
                                        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                        MediaPlayer mplayer = new MediaPlayer();
                                        PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
                                            @Override
                                            public void onCallStateChanged(int state, String incomingNumber) {
                                                LogUtils.w(state);
                                                switch (state) {
                                                    case TelephonyManager.CALL_STATE_IDLE: //空闲
                                                        LogUtils.d("空闲");
                                                        if (!isNeedCall) {
                                                            mplayer.stop();
                                                        }
                                                        isNeedCall = true;

                                                        if (tsCall != 0) {
                                                            //没有打过电话
                                                            long des = System.currentTimeMillis() - tsCall;
                                                            LogUtils.d(des);
                                                            if (65 * 1000 < des || des < 80 * 1000) {
                                                                //无人听电话
                                                                //可以继续拨打下一个电话
                                                                callIndex++;
                                                                if (callIndex < phoneMembers.size()) {
                                                                    callPhone(phoneMembers.get(callIndex));
                                                                }
                                                            } else {
                                                                tsCall = 0;
                                                                //如果已经有人接听了,那就不需要再打电话了
                                                            }
                                                        }

                                                        break;
                                                    case TelephonyManager.CALL_STATE_RINGING: //响铃来电
                                                        LogUtils.d("响铃来电");
                                                        break;
                                                    case TelephonyManager.CALL_STATE_OFFHOOK: //摘机（正在通话中）
                                                        LogUtils.d("正在通话中");
                                                        //打通了,就不需要再拨打了
                                                        if (isNeedCall) {
                                                            tsCall = System.currentTimeMillis();
                                                        }

                                                        isNeedCall = false;
                                                        //todo 播放预设好的语音

                                                      /*  try {
                                                            AssetFileDescriptor fd = HelpActivity.this.getAssets().openFd("gckl.mp3");
                                                            mplayer.setDataSource(fd);
                                                            mplayer.prepare();
                                                            mplayer.start();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }*/

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
                                        if (phoneMembers != null && phoneMembers.size() > 0) {
                                            callPhone(phoneMembers.get(0));
                                            if (!phoneMembers.get(phoneMembers.size() - 1).equals(alertNum2)) {
                                                phoneMembers.add(alertNum2);
                                            }
                                            for (String phoneNum : phoneMembers) {
                                                // 拆分短信内容（手机短信长度限制）
                                                List<String> divideContents = smsManager.divideMessage(msgContent);
                                                for (String text : divideContents) {
                                                    LogUtils.d(phoneNum + text);
                                                    if (!phoneNum.equals(alertNum2)) {
                                                        smsManager.sendTextMessage(phoneNum, null, text, null, null);
                                                    }
                                                }
                                            }
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
