package cn.mointe.vaccination.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.mointe.vaccination.R;

public class AboutUsActivity extends Activity {

//	private ActionBar mBar;
	private WebView mAboutUs;
	
	private TextView mTitleText;
	private ImageButton mTitleLeftImgbtn;// title左边图标
	private ImageButton mTitleRightImgbtn;// title右边图标

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);

//		mBar = getSupportActionBar();
//		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
//		mBar.setHomeButtonEnabled(true);
		
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

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == android.R.id.home) {
//			this.finish();
//		}
//		return super.onOptionsItemSelected(item);
//	}

}
