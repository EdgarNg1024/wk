package edgar.wk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

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


    @BindView(R.id.btn)
    Button btn;

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

    @OnClick({R.id.btn})
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
        }

    }
}
