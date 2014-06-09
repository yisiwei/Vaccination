package cn.mointe.vaccination.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.mail.MailSenderInfo;
import cn.mointe.vaccination.mail.SimpleMailSender;
import cn.mointe.vaccination.tools.NetworkUtil;
import cn.mointe.vaccination.tools.PackageUtil;
import cn.mointe.vaccination.tools.StringUtils;

/**
 * 用户反馈
 * 
 */
public class FeedBackActivity extends ActionBarActivity implements
		OnClickListener {

	private LinearLayout mParentView;

	private ActionBar mBar;

	private EditText mContentEdit;// 内容
	private EditText mContactEdit;// 联系人
	private Button mSubmitBtn;

	private FeedBackTask mTask;
	private ProgressDialog mDialog;

	private String mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		mParentView = (LinearLayout) this.findViewById(R.id.feedback_llay);
		mContentEdit = (EditText) this.findViewById(R.id.feedback_content_edit);
		mContactEdit = (EditText) this.findViewById(R.id.feedback_contact_edit);
		mSubmitBtn = (Button) this.findViewById(R.id.submit_button);

		mSubmitBtn.setOnClickListener(this);
		mParentView.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.submit_button) {
			mContent = mContentEdit.getText().toString().trim();
			if (StringUtils.isNullOrEmpty(mContent)) {
				Toast.makeText(getApplicationContext(),
						R.string.feedback_content_is_not_null,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (NetworkUtil.getAPNType(this) == -1) {
				Toast.makeText(getApplicationContext(), R.string.network_fail,
						Toast.LENGTH_SHORT).show();
				return;
			}
			mTask = new FeedBackTask();
			mTask.execute();
		} else if (v.getId() == R.id.feedback_llay) {
			InputMethodManager imm = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	private class FeedBackTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = ProgressDialog.show(FeedBackActivity.this, getResources()
					.getString(R.string.hint),
					getResources().getString(R.string.loading_wait));
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean flag = false;
			String contact = mContactEdit.getText().toString().trim();

			MailSenderInfo mailInfo = new MailSenderInfo();
			mailInfo.setMailServerHost("smtp.exmail.qq.com");
			mailInfo.setMailServerPort("25");
			mailInfo.setValidate(true);
			mailInfo.setUserName("3rd@mointe.cn");
			mailInfo.setPassword("mointe6688");// 您的邮箱密码
			mailInfo.setFromAddress("3rd@mointe.cn");
			mailInfo.setToAddress("yisiwei@mointe.cn");
			mailInfo.setSubject("用户反馈意见");
			mailInfo.setContent("反馈内容：\n" + "	" + mContent + "\n\n版本："
					+ PackageUtil.getVersionName(getApplicationContext())
					+ "\n联系方式：" + contact);

			try {
				flag = SimpleMailSender.sendTextMail(mailInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return flag;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mDialog.dismiss();
			if (result) {
				Toast.makeText(getApplicationContext(),
						R.string.feedback_success, Toast.LENGTH_SHORT).show();
				FeedBackActivity.this.finish();
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.feedback_fail, Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
