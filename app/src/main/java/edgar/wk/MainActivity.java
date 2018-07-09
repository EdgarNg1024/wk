package edgar.wk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

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
    String image_url = "http://bj-mc-prod-asset.oss-cn-beijing.aliyuncs.com/wiki-pic/Gesture7.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn})
    void OnClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                Log.d(TAG, "btn");
                OkGo.<FaceDto>post(url)
                        .params("api_key", api_key)
                        .params("api_secret", api_secret)
                        .params("image_url", image_url)
                        .execute(new JsonCallBack<FaceDto>(FaceDto.class) {

                            @Override
                            public void onSuccess(Response<FaceDto> response) {
                                FaceDto result = response.body();
                                if (result != null && result.getHands() != null && result.getHands().size() > 0) {
                                    //多只手
                                    List<Hand> hands = result.getHands();
                                    if (hands.get(0).getGesture().getHand_open() > 50) {
                                        Log.d(TAG,"你打了我一巴掌呢");
                                    }
                                }
                            }
                        });
                break;
        }

    }
}
