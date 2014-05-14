package cn.mointe.vaccination.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.mointe.vaccination.R;

public class NoticeActivity extends ActionBarActivity {

	private Button mNextBtn;
	private TextView mNoticeContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);
		
		mNoticeContent = (TextView)findViewById(R.id.notices_tv); 
		mNoticeContent.setMovementMethod(ScrollingMovementMethod.getInstance());

		mNextBtn = (Button) this.findViewById(R.id.notice_btn_next);
		mNextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(NoticeActivity.this,
						RegisterBabyActivity.class));
				NoticeActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
