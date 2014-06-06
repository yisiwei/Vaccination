package cn.mointe.vaccination.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.tools.Log;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginTestActivity extends Activity {

	private Tencent mTencent;
	private QQAuth mQQAuth;
	private UserInfo mInfo;

	private Button login;
	private TextView loginInfo;
	private TextView nickname;
	private ImageView imageView;

	String url_qqlogin;
	private String SCOPE = "get_simple_userinfo,add_topic"; // 全部为 all
	private static final String APP_ID = "101080056";

	private Button button2;

	private VaccinationPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_test);

		mPreferences = new VaccinationPreferences(this);

		login = (Button) this.findViewById(R.id.button1);
		loginInfo = (TextView) this.findViewById(R.id.textView1);
		nickname = (TextView) this.findViewById(R.id.textView2);
		imageView = (ImageView) this.findViewById(R.id.imageView1);

		button2 = (Button) this.findViewById(R.id.button2);

		mQQAuth = QQAuth.createInstance(APP_ID, this.getApplicationContext());
		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickLogin();
			}
		});

		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showUserInfo();
			}
		});
	}

	private void showUserInfo() {
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				@Override
				public void onCancel() {

				}

				@Override
				public void onComplete(Object arg0) {
					Log.i("MainActivity", "UserInfo:" + arg0);
					final JSONObject json = (JSONObject) arg0;
					Message msg = new Message();
					msg.obj = json;
					msg.what = 0;
					mHandler.sendMessage(msg);
					new Thread() {
						@Override
						public void run() {
							Bitmap bitmap = null;
							try {
								bitmap = getbitmap(json
										.getString("figureurl_qq_2"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
							Message msg = new Message();
							msg.obj = bitmap;
							msg.what = 1;
							mHandler.sendMessage(msg);
						}
					}.start();
				}

				@Override
				public void onError(UiError arg0) {

				}

			};

			mInfo = new UserInfo(this, mQQAuth.getQQToken());
			mInfo.getUserInfo(listener);
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						nickname.setText(response.getString("nickname"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else if (msg.what == 1) {
				Bitmap bitmap = (Bitmap) msg.obj;
				imageView.setImageBitmap(bitmap);
			}
		}
	};

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmap(String imageUri) {
		Log.v("MainActivity", "getbitmap:" + imageUri);
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();

			Log.v("MainActivity", "image download finished." + imageUri);
		} catch (IOException e) {
			e.printStackTrace();
			Log.v("MainActivity", "getbitmap bmp fail---");
			return null;
		}
		return bitmap;
	}

	// 应用调用Andriod_SDK接口时，使能成功接收到回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mTencent.onActivityResult(requestCode, resultCode, data);
	}

	private void onClickLogin() {

		if (!mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				/** 授权失败的回调 */
				@Override
				public void onError(UiError arg0) {
					Toast.makeText(LoginTestActivity.this, "授权失败",
							Toast.LENGTH_SHORT).show();
				}

				/** 授权成功的回调 */
				@Override
				public void onComplete(Object arg0) {
					Toast.makeText(LoginTestActivity.this, "授权成功",
							Toast.LENGTH_SHORT).show();
					loginInfo.setText(arg0.toString());
					Log.i("MainActivity", "登录信息:" + arg0.toString());
					JSONObject json = (JSONObject) arg0;
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

				}

				/** 取消授权的回调 */
				@Override
				public void onCancel() {
					Toast.makeText(LoginTestActivity.this, "取消授权",
							Toast.LENGTH_SHORT).show();
				}

			};
			mTencent.login(this, SCOPE, listener);
		} else {
			mTencent.logout(this);
		}
	}

}
