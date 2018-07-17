package edgar.wk;

import com.ubtrobot.mini.RobotApplication;

public class WKApp extends RobotApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 当前应用与其他正在运行的应用冲突时调用
     *
     * @param ubtSkillInfo 正在运行的应用信息
     */
    @Override
    protected void onStartFailed(RobotApplication.UbtSkillInfo ubtSkillInfo) {

    }

    /**
     * 应用被其他优先级更高应用打断时调用
     */
    @Override
    protected void onInterrupted() {

    }
}
