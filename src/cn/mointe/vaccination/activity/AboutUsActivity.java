package cn.mointe.vaccination.activity;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.mointe.vaccination.R;

public class AboutUsActivity extends Activity {

	// private ActionBar mBar;
	private WebView mAboutUs;

	private TextView mTitleText;
	private ImageButton mTitleLeftImgbtn;// title左边图标
	private ImageButton mTitleRightImgbtn;// title右边图标

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);

		// mBar = getSupportActionBar();
		// mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		// mBar.setHomeButtonEnabled(true);

		mTitleText = (TextView) this.findViewById(R.id.title_text);
		mTitleLeftImgbtn = (ImageButton) this
				.findViewById(R.id.title_left_imgbtn);
		mTitleRightImgbtn = (ImageButton) this
				.findViewById(R.id.title_right_imgbtn);

		mTitleText.setText(R.string.action_about_us);
		mTitleLeftImgbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AboutUsActivity.this.finish();
			}
		});
		mTitleRightImgbtn.setVisibility(View.GONE);

		mAboutUs = (WebView) this.findViewById(R.id.about_webView);
		mAboutUs.loadUrl("file:///android_asset/aboutUs.html");

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("AboutUsActivity"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("AboutUsActivity"); // 保证 onPageEnd 在onPause之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

}
