package cn.mointe.vaccination.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.tools.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends Activity {

	private String SCOPE = "get_simple_userinfo,add_topic"; // 全部为 all
	private static final String APP_ID = "101080056";

	private Tencent mTencent;
	//private QQAuth mQQAuth;
	//private UserInfo mInfo;

	private TextView mTitleText;
	private ImageButton mTitleLeftImgbtn;// title左边图标
	private ImageButton mTitleRightImgbtn;// title右边图标

	private ImageButton mQQLogin;

	private VaccinationPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		//mQQAuth = QQAuth.createInstance(APP_ID, this.getApplicationContext());
		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		mPreferences = new VaccinationPreferences(this);

		mTitleText = (TextView) this.findViewById(R.id.title_text);
		mTitleLeftImgbtn = (ImageButton) this
				.findViewById(R.id.title_left_imgbtn);
		mTitleRightImgbtn = (ImageButton) this
				.findViewById(R.id.title_right_imgbtn);

		mQQLogin = (ImageButton) this.findViewById(R.id.login_qq);

		mTitleText.setText(R.string.login);
		mTitleLeftImgbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginActivity.this.finish();
			}
		});
		mTitleRightImgbtn.setVisibility(View.GONE);

		mQQLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				qqLogin();
			}

		});
	}

	private void qqLogin() {
		if (!mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				/** 授权失败的回调 */
				@Override
				public void onError(UiError arg0) {
					Toast.makeText(LoginActivity.this, "授权失败",
							Toast.LENGTH_SHORT).show();
				}

				/** 授权成功的回调 */
				@Override
				public void onComplete(Object response) {
					Toast.makeText(LoginActivity.this, "授权成功",
							Toast.LENGTH_SHORT).show();
					// loginInfo.setText(arg0.toString());
					Log.i("MainActivity", "登录信息:" + response.toString());
					JSONObject json = (JSONObject) response;
					try {
						mPreferences.setOpenid(json.getString("openid"));
						mPreferences.setAccessToken(json
								.getString("access_token"));
						mPreferences.setExpiresIn(String.valueOf(System
								.currentTimeMillis()
								+ Long.parseLong(json.getString("expires_in"))
								* 1000));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					boolean isExistBaby = mPreferences.getIsExistBaby();
					if (isExistBaby) {
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(LoginActivity.this,
								RegisterBabyActivity.class);
						startActivity(intent);
					}
					LoginActivity.this.finish();
				}

				/** 取消授权的回调 */
				@Override
				public void onCancel() {
					Toast.makeText(LoginActivity.this, "取消授权",
							Toast.LENGTH_SHORT).show();
				}

			};
			mTencent.login(this, SCOPE, listener);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("LoginActivity"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("LoginActivity");
		MobclickAgent.onPause(this);
	}

	// 应用调用Andriod_SDK接口时，使能成功接收到回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mTencent.onActivityResult(requestCode, resultCode, data);
	}

}
