package cn.mointe.vaccination.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.tools.PackageUtil;
import cn.mointe.vaccination.tools.StringUtils;

import com.umeng.analytics.MobclickAgent;

/**
 * 欢迎界面
 * 
 * @author yi_siwei
 * 
 */
public class WelcomeActivity extends Activity {

	// sharedPreferences 文件名称
	public static final String SHAREDPREFERENCES = "sharedPreferences";

	// 版本号常量
	private static int VERSION_CODE = 0;

	private boolean isExistBaby = false;

	//private Tencent mTencent;
	//private static final String APP_ID = "101080056";

	// private String mOpenid;
	// private String mAccessToken;
	private String mExpiresIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置无标题
		Window window = getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setContentView(R.layout.activity_welcome);

		MobclickAgent.openActivityDurationTrack(false);
		//mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				SharedPreferences preferences = getSharedPreferences(
						SHAREDPREFERENCES, MODE_PRIVATE);
				// 从 sharedPreferences 中获取之前版本，第一次安装没有存储，默认值设置为0，
				// 所以第一次安装必定会显示引导页
				VERSION_CODE = preferences.getInt("VersionCode", 0);
				isExistBaby = preferences.getBoolean("IsExistBaby", false);

				// mOpenid = preferences.getString("openid", null);
				// mAccessToken = preferences.getString("access_token", null);
				mExpiresIn = preferences.getString("expires_in", null);

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
					if (StringUtils.isNullOrEmpty(mExpiresIn) || (Long.parseLong(mExpiresIn) - System
							.currentTimeMillis()) / 1000 <= 0) {
						Intent intent = new Intent(WelcomeActivity.this,
								LoginActivity.class);
						startActivity(intent);
						
//						if (isExistBaby) {
//							// Intent intent = new Intent(WelcomeActivity.this,
//							// MainActivity.class);
//							// mTencent.setOpenId(mOpenid);
//							// mTencent.setAccessToken(mAccessToken, mExpiresIn);
//							if (StringUtils.isNullOrEmpty(mExpiresIn) || (Long.parseLong(mExpiresIn) - System
//									.currentTimeMillis()) / 1000 <= 0) {
//								Intent intent = new Intent(WelcomeActivity.this,
//										LoginActivity.class);
//								startActivity(intent);
//							} else {
//								Intent intent = new Intent(WelcomeActivity.this,
//										MainActivity.class);
//								startActivity(intent);
//							}
//							WelcomeActivity.this.finish();
//						} else {
//							Intent intent = new Intent(WelcomeActivity.this,
//									RegisterBabyActivity.class);
//							startActivity(intent);
//						}
						WelcomeActivity.this.finish();
					}else{
						if (isExistBaby) {
							Intent intent = new Intent(WelcomeActivity.this,
									MainActivity.class);
							startActivity(intent);
						}else{
							Intent intent = new Intent(WelcomeActivity.this,
									RegisterBabyActivity.class);
							startActivity(intent);
						}
						WelcomeActivity.this.finish();
					}
				}
			}
		}, 1500);// 设置2秒延迟

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("WelcomeActivity"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("WelcomeActivity");
		MobclickAgent.onPause(this);
	}
}
