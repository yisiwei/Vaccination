package cn.mointe.vaccination.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.tools.PackageUtil;

/**
 * 欢迎界面
 * 
 * @author yi_siwei
 * 
 */
public class WelcomeActivity extends ActionBarActivity {

	// sharedPreferences 文件名称
	public static final String SHAREDPREFERENCES = "sharedPreferences";

	// 版本号常量
	private static int VERSION_CODE = 0;
	
	private boolean isExistBaby = false;
	
	//private ActionBar mBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置无标题
		Window window = getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
//		mBar = getSupportActionBar();
//		mBar.hide();
		setContentView(R.layout.activity_welcome);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				SharedPreferences preferences = getSharedPreferences(
						SHAREDPREFERENCES, MODE_PRIVATE);
				// 从 sharedPreferences 中获取之前版本，第一次安装没有存储，默认值设置为0，
				// 所以第一次安装必定会显示引导页
				VERSION_CODE = preferences.getInt("VersionCode", 0);
				isExistBaby = preferences.getBoolean("IsExistBaby", false);
				// 获取版本号
				int versionCode = PackageUtil
						.getVersionCode(WelcomeActivity.this);

				// 判断是否有版本更新
				if (versionCode > VERSION_CODE) {
					// 有版本更新，跳转到引导页界面
					Intent intent = new Intent(WelcomeActivity.this,
							GuideActivity.class);
					startActivity(intent);
					WelcomeActivity.this.finish();
				} else {
					// 无版本更新，跳转到主界面
					if (isExistBaby) {
						Intent intent = new Intent(WelcomeActivity.this,
								MainActivity.class);
						startActivity(intent);
						WelcomeActivity.this.finish();
					}else{
						Intent intent = new Intent(WelcomeActivity.this,
								RegisterBabyActivity.class);
						startActivity(intent);
						WelcomeActivity.this.finish();
					}
				}
			}
		}, 1500);// 设置2秒延迟

	}

}
