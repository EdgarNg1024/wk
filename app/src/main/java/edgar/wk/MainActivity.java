package edgar.wk;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ubtrobot.mini.action.ActionApi;
import com.ubtrobot.mini.action.ActionInfo;
import com.ubtrobot.mini.action.PlayActionListener;
import com.ubtrobot.mini.led.MouthLedApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edgar.wk.face.dto.FaceDto;
import edgar.wk.face.dto.Hand;
import edgar.wk.net.data.callback.JsonCallBack;
import edgar.wk.photo.CameraActivity;
import edgar.wk.utils.ToastManager;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    LongSparseArray<FaceDto> resultFace = new LongSparseArray<FaceDto>();
    File file;
    public static int REQUEST_CAMERA = 222;
    private ActionApi actionApi;

    @BindView(R.id.btnFace)
    Button btn;
    @BindView(R.id.imageView)
    ImageView imgView;

    @BindView(R.id.actionET)
    EditText actionET;

    String url = "https://api-cn.faceplusplus.com/humanbodypp/beta/gesture";
    String api_key = "C3REx0MTx_6Fd5IzSCYkJ2CPG46fTsiU";
    String api_secret = "kPEyLMPQSyNe_FPiW9gfSzxDk1Rz6r6y";
    String image_url1 = "https://wx2.sinaimg.cn/mw690/71504783gy1ft56temgfwj20u0140q5y.jpg";
    String image_url2 = "https://wx1.sinaimg.cn/mw690/71504783gy1ft56tf2nq6j20u0140adk.jpg";

    List<String> image_urls = new ArrayList<String>();
    List<Integer> handSize = new ArrayList<Integer>(100);
    int icount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        image_urls.add(image_url1);
        image_urls.add(image_url2);

        initRobot();
    }

    @OnClick({R.id.btnFace, R.id.btnTakePhoto, R.id.btnAction})
    void OnClick(View v) {
        switch (v.getId()) {
            case R.id.btnFace:
                for (int i = 0; i < image_urls.size(); i++) {
                    handSize.add(0);
                    String imgurl = image_urls.get(i);
                    Log.d(TAG, "数据请求=================");
                    final int finalI = i;
                    OkGo.<FaceDto>post(url)
                            .params("api_key", api_key)
                            .params("api_secret", api_secret)
                            .params("image_url", imgurl)
                            .execute(new JsonCallBack<FaceDto>(FaceDto.class) {

                                @Override
                                public void onSuccess(Response<FaceDto> response) {
                                    Log.d(TAG, "数据返回");
                                    FaceDto result = response.body();
                                    if (result != null && result.getHands() != null && result.getHands().size() > 0) {
                                        //多只手
                                        List<Hand> hands = result.getHands();
                                        handSize.add(finalI, hands.get(0).getHand_rectangle().getWidth() * hands.get(0).getHand_rectangle().getHeight());

                                        icount++;
                                        Log.d(TAG, icount + "");
                                        if (icount == 2) {
                                            if (handSize.get(0) - handSize.get(1) < 0) {
                                                //推
                                                Log.d(TAG, "你推了我一下");
                                            } else {
                                                //吸引
                                                Log.d(TAG, "你把我吸过来了");
                                            }
                                        }

                                    }
                                }
                            });
                }
                break;
            case R.id.btnTakePhoto:

      /*          new RxPermissions(MainActivity.this)
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean accept) throws Exception {
                                if (accept) {
                                    //调用相机
                                    useCamera();
                                } else {
                                    ToastManager.getInstance(MainActivity.this).showText("权限被拒绝");
                                }
                            }
                        });*/

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
//                        Intent intent = new Intent();
//                        intent.setClass(MainActivity.this, CameraActivity.class);
//                        startActivityForResult(intent, REQUEST_CAMERA);
                        MouthLedApi mouthLedApi = MouthLedApi.get();
                        mouthLedApi.startBreathModel(Color.argb(0, 255, 255, 0), 1000 * 3);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, CameraActivity.class);
                                startActivityForResult(intent, REQUEST_CAMERA);
//                                MouthLedApi mouthLedApi = MouthLedApi.get();
//                                mouthLedApi.startBreathModel(Color.argb(0, 255, 255, 0), 10000);

                            }
                        }, 1000 * 3);
                    }
                }, 1000 * 10);

                break;
            case R.id.btnAction:
//                getActionList(v);

                actionApi.playAction(actionET.getText().toString(), new PlayActionListener() {
                    @Override
                    public void onStart() {
                        Log.i(TAG, "playAction开始执行动作!");
                    }

                    @Override
                    public void onFinished() {
                        Log.i(TAG, "playAction动作执行结束!");
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.i(TAG, "playAction执行表情错误,errorCode:" + errorCode + ",errorMsg:" + errorMsg);
                    }
                });
                Log.i(TAG, "playAction 接口调用成功!");

//                int moveTime = 100;
//                ArrayList<MotionParam> angleList = new ArrayList<MotionParam>();
//
//                MotionParam motor1 = new MotionParam(1, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor2 = new MotionParam(2, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor3 = new MotionParam(3, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor4 = new MotionParam(4, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor5 = new MotionParam(5, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor6 = new MotionParam(6, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor7 = new MotionParam(7, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor8 = new MotionParam(8, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor9 = new MotionParam(9, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor10 = new MotionParam(10, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor11 = new MotionParam(11, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor12 = new MotionParam(12, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor13 = new MotionParam(13, 120, RunMode.LINEAR, moveTime);
//                MotionParam motor14 = new MotionParam(14, 120, RunMode.LINEAR, moveTime);
//
//
//                angleList.add(motor1);
//                angleList.add(motor2);
//                angleList.add(motor3);
//                angleList.add(motor4);
//                angleList.add(motor5);
//                angleList.add(motor6);
//                angleList.add(motor7);
//                angleList.add(motor8);
//                angleList.add(motor9);
//                angleList.add(motor10);
//                angleList.add(motor11);
//                angleList.add(motor12);
//                angleList.add(motor13);
//                angleList.add(motor14);
//
//                MotorApi.get().moveToAngle(angleList, null);
                break;
        }

    }

    /**
     * 使用相机
     */
    private void useCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        external-path
//        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/test/" + System.currentTimeMillis() + ".jpg");
//        files-path
//        file = new File(this.getFilesDir()
//                + "/test1/" + System.currentTimeMillis() + ".jpg");
//        cache-path
        file = new File(this.getCacheDir()
                + "/test/" + System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();

        //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
        Uri uri = FileProvider.getUriForFile(this, "edgar.wk.fileprovider", file);

        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
//            imgView.setImageURI(Uri.fromFile(file));
//            imgView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            ArrayList<String> picData = data.getStringArrayListExtra("picPathValue");
            imgView.setImageBitmap(BitmapFactory.decodeFile(picData.get(0)));
     /*       final String picUrl = data.getStringArrayListExtra("picPathValue").get(1);

            imgView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "换照片啦");
                    imgView.setImageBitmap(BitmapFactory.decodeFile(picUrl));
                }
            }, 2000);*/

            //在手机相册中显示刚拍摄的图片
//            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri contentUri = Uri.fromFile(file);
//            mediaScanIntent.setData(contentUri);
//            sendBroadcast(mediaScanIntent);

            int i = 0;
            resultFace.clear();

            for (final String picUrl : picData) {
//            String picUrl = picData.get(0);
                imgView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        File picFile = new File(picUrl);
                        OkGo.<FaceDto>post(url)
                                .headers("enctype", "multipart/form-data")
                                .params("api_key", api_key)
                                .params("api_secret", api_secret)
                                .params("image_file", picFile)
                                .params("image_file", picUrl)
                                .execute(new JsonCallBack<FaceDto>(FaceDto.class) {

                                    @Override
                                    public void onSuccess(Response<FaceDto> response) {
                                        Log.d(TAG, "数据返回");
                                        FaceDto result = response.body();
                                        if (result != null && result.getHands() != null && result.getHands().size() > 0) {
                                            ToastManager.getInstance(MainActivity.this).showText("数据返回来啦");
                                            Long key = Long.parseLong(result.getRequest_id().split(",")[0]);
                                            resultFace.put(key, result);
                                            if (resultFace.size() >= 2) {
                                                ToastManager.getInstance(MainActivity.this).showText("处理完成~");
                                                actionApi.playAction("023", new PlayActionListener() {
                                                    @Override
                                                    public void onStart() {
                                                        Log.i(TAG, "playAction开始执行动作!");
                                                    }

                                                    @Override
                                                    public void onFinished() {
                                                        Log.i(TAG, "playAction动作执行结束!");
                                                    }

                                                    @Override
                                                    public void onFailure(int errorCode, String errorMsg) {
                                                        Log.i(TAG, "playAction执行表情错误,errorCode:" + errorCode + ",errorMsg:" + errorMsg);
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Response<FaceDto> response) {
                                        super.onError(response);
                                        ToastManager.getInstance(MainActivity.this).showText("衰佐啦");
                                    }
                                });
                    }
                }, 2000 * i++);

            }
        }
    }

    /**
     * 初始化接口类实例
     */
    private void initRobot() {
        actionApi = ActionApi.get();
    }

    /**
     * 获取动作列表
     *
     * @param view
     */
    public void getActionList(View view) {
        List<ActionInfo> actionList = actionApi.getActionList();
        for (ActionInfo actionInfo : actionList) {
            Log.i(TAG, "============================================");
            Log.i(TAG, "Action name======" + actionInfo.getName());
            Log.i(TAG, "Action Id======" + actionInfo.getId());
            Log.i(TAG, "Action Description======" + actionInfo.getDescription());
            Log.i(TAG, "Action Duration======" + actionInfo.getDuration());
        }
        Log.i(TAG, "getActionList接口调用成功!");
    }

}
