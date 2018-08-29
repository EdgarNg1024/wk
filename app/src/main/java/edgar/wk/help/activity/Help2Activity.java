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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ubtechinc.sauron.api.FaceInfo;
import com.ubtrobot.mini.sauron.FaceApi;
import com.ubtrobot.mini.sauron.FaceFindListener;
import com.ubtrobot.mini.voice.VoicePool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edgar.wk.R;
import edgar.wk.fall.FallAlertActivity;
import edgar.wk.fall.dto.FallAlertResultDto;
import edgar.wk.fall.dto.Skeleton;
import edgar.wk.net.data.callback.JsonCallBack;
import edgar.wk.utils.LogUtils;
import edgar.wk.utils.ToastManager;
import edgar.wk.utils.permissions.Permission;
import edgar.wk.utils.permissions.RxPermissions;

public class Help2Activity extends AppCompatActivity {

    @BindView(R.id.btnHelp)
    protected Button btnHelp;
    @BindView(R.id.etPerson)
    protected EditText EtPhoneMembers;
    @BindView(R.id.etMsgContent)
    protected EditText EtMsgContent;

    String fallAlertUrl = "https://api-cn.faceplusplus.com/humanbodypp/v1/skeleton";
    String api_key = "C3REx0MTx_6Fd5IzSCYkJ2CPG46fTsiU";
    String api_secret = "kPEyLMPQSyNe_FPiW9gfSzxDk1Rz6r6y";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        btnHelp.setText("点击开启跌倒监控");
    }

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

    private final String TAG = "Help2Activity";


    @OnClick({R.id.btnHelp})
    void OnClick(View v) {
        switch (v.getId()) {
            case R.id.btnHelp:
                //找到人脸就上传到face++
                //之后就判断是否跌倒状态
                //跌倒之后,过2分钟之后再找人脸
                //找到还是跌倒状态的话就启动报警流程

 /*               long timeout = 15;
                FaceApi.get().findFace(timeout, new FaceFindListener() {
                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFaceChange(List<FaceInfo> list) {
                        LogUtils.d(list);

                    }

                    @Override
                    public void onStop() {

                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });*/

                // TODO: 2018/8/30 0030 找到人脸之后,摄像头要停止
                Intent i = new Intent();
                i.setClass(Help2Activity.this, FallAlertActivity.class);
                startActivityForResult(i, REQUEST_FALLALERT);
                break;


            case R.id.btnGo:
                // TODO: 2018/8/30 0030 这些先不处理
                phoneMembers = new ArrayList<String>();
                phoneMembers.addAll(Arrays.asList(EtPhoneMembers.getText().toString().split(",")));
                msgContent = EtMsgContent.getText().toString();


                new RxPermissions(this).requestEach(Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS)
                        .subscribe((Permission permission) -> {
                                    if (permission.granted) {
                                        // 用户已经同意该权限

                                        //找人脸
                                        long timeout1 = 15;//单位是秒
                                        FaceApi.get().findFace(timeout1, new FaceFindListener() {
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
                                                        smsManager.sendTextMessage(phoneNum, null, text, null, null);
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
                                            phoneMembers.add("120");

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FALLALERT && resultCode == RESULT_OK) {
            //倒地报警
            String picUrl = data.getStringExtra("picPathValue");
            File picFile = new File(picUrl);
            OkGo.<FallAlertResultDto>post(fallAlertUrl)
                    .headers("enctype", "multipart/form-data")
                    .params("api_key", api_key)
                    .params("api_secret", api_secret)
                    .params("image_file", picFile)
                    .execute(new JsonCallBack<FallAlertResultDto>(FallAlertResultDto.class) {

                        @Override
                        public void onSuccess(Response<FallAlertResultDto> response) {
                            Log.d(TAG, "数据返回");
                            FallAlertResultDto result = response.body();
                            if (result != null && result.getSkeletons() != null && result.getSkeletons().size() > 0) {
                                //只有一个人的时候,倒下才需要报警
                                boolean isNeedAlert = judgePose(result.getSkeletons().get(0));
                                if (isNeedAlert) {
                                    //需要报警
                                    VoicePool.get().playTTs("这里有人跌倒啦~快来帮忙啊!!!!", null);
                                } else {
                                    VoicePool.get().playTTs("哇,这里有人窝...", null);
                                }
                            }
                        }

                        @Override
                        public void onError(Response<FallAlertResultDto> response) {
                            super.onError(response);
                            ToastManager.getInstance(Help2Activity.this).showText("衰佐啦");
                        }
                    });
        }
    }


    /**
     * 根据姿态判断是否跌倒
     *
     * @param skeleton
     */
    private boolean judgePose(Skeleton skeleton) {
        boolean result = false;
        // TODO: 2018/8/15 0015 需要完善如何判断是跌倒了
//if(skeleton.getLandmark().getLeft_shoulder())
        return result;
    }
}
