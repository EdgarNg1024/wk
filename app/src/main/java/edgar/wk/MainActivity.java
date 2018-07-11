package edgar.wk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edgar.wk.face.dto.FaceDto;
import edgar.wk.face.dto.Hand;
import edgar.wk.net.data.callback.JsonCallBack;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    File file;
    int REQUEST_CAMERA = 222;

    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.imageView)
    ImageView imgView;

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
    }

    @OnClick({R.id.btn, R.id.btnTakePhoto})
    void OnClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
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

//                                        if (hands.get(0).getGesture().getHand_open() > 50) {
//                                            Log.d(TAG, "你打了我一巴掌呢");
//                                        }
                                    }
                                }
                            });
                }
                break;
            case R.id.btnTakePhoto:
                applyWritePermission();
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
                + "/testc/" + System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();

        //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
        Uri uri = FileProvider.getUriForFile(this, "edgar.wk.fileprovider", file);
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void applyWritePermission() {

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= 23) {
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check == PackageManager.PERMISSION_GRANTED) {
                //调用相机
                useCamera();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            useCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            useCamera();
        } else {
            // 没有获取 到权限，从新请求，或者关闭app
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
//            imgView.setImageURI(Uri.fromFile(file));
            imgView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));

            //在手机相册中显示刚拍摄的图片
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        }
    }
}
