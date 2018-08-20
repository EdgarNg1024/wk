package edgar.wk.help.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edgar.wk.R;

public class HelpActivity extends AppCompatActivity {

    @BindView(R.id.etPerson)
    protected EditText EtPhoneMembers;

    //紧急联系人们
    List<String> phoneMembers;

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

                break;

        }

    }
}
