package edgar.wk.help.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edgar.wk.R;

public class HelpActivity extends AppCompatActivity {

    @BindView(R.id.etPerson)
    protected EditText EtPhoneMembers;

    @BindView(R.id.etMsgContent)
    protected EditText EtMsgContent;

    //紧急联系人们
    List<String> phoneMembers;
    //消息内容
    String msgContent;


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

                //同时按顺序拨打电话

                break;

        }

    }
}
