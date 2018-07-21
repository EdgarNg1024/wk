package edgar.wk;

import com.ubtrobot.mini.RobotApplication;
import com.ubtrobot.mini.RobotSkillInfo;

public class WKApp extends RobotApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }



    @Override
    protected void onStartFailed(RobotSkillInfo robotSkillInfo) {

    }

    /**
     * 应用被其他优先级更高应用打断时调用
     */
    @Override
    protected void onInterrupted() {

    }
}
