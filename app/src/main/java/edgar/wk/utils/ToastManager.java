package edgar.wk.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class ToastManager {

	/**toast*/
	private Toast mToast;
	/**ToastManager*/
	private static ToastManager mToastManager;

	private Context mContext;

	private ToastManager(Context c){
		mContext = c;
		mToast = Toast.makeText(c, "", Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.CENTER, 0, 0);	
	}
	/**
	 * 
	 * @comments  单例模式 获取引用
	 * @param c
	 * @return
	 * @version 1.0
	 */
	public static synchronized ToastManager getInstance(Context c){
		if(mToastManager == null){
			mToastManager = new ToastManager(c);
		}
		return mToastManager;
	}
	/**
	 * 
	 * @comments 纯文字显示 
	 * @param text
	 * @version 1.0
	 */
	public void showText(String text){
		mToast.setText(text);
		mToast.show();		
	}
	/**
	 * 
	 * @comments 纯文字显示 
	 * @param text
	 * @version 1.0
	 */
	public void showText(int text){
		mToast.setText(text);
		mToast.show();		
	}

	/**
	 *
	 * @comments 自定义VIew
	 * @param layoutId
	 * @version 1.0
	 */
	public void showTextView(int layoutId){
		View view = LayoutInflater.from(mContext).inflate(layoutId, null);
		mToast.setView(view);
		mToast.show();
	}

	/**
	 *
	 * @comments 自定义VIew
	 * @param view
	 * @version 1.0
	 */
	public void showTextView(View view){
		mToast.setView(view);
		mToast.show();
	}

	public void cancelToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}
}

