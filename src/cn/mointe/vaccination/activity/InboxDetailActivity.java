package cn.mointe.vaccination.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.InboxDao;
import cn.mointe.vaccination.domain.Inbox;
import cn.mointe.vaccination.tools.Log;

public class InboxDetailActivity extends Activity implements OnGestureListener {

	private TextView mTitleText;
//	private ImageButton mTitleLeftImgbtn;// title左边图标
	private LinearLayout mTitleLeft;
	private ImageView mTitleRightImgbtn;// title右边图

	private ViewFlipper mViewFlipper;
	private GestureDetector mGestureDetector;
	private View mView;

	private TextView mTitle;
	private TextView mDate;
	private TextView mContent;

	private List<Inbox> mInboxs;
	private int mPosition;

	private InboxDao mInboxDao;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox_detail);

		mInboxDao = new InboxDao(this);
		mGestureDetector = new GestureDetector(this, this);

		mTitleText = (TextView) this.findViewById(R.id.title_text);
//		mTitleLeftImgbtn = (ImageButton) this
//				.findViewById(R.id.title_left_imgbtn);
		mTitleLeft = (LinearLayout) this.findViewById(R.id.title_left);
		mTitleRightImgbtn = (ImageView) this
				.findViewById(R.id.title_right_imgbtn);

		mTitleLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InboxDetailActivity.this.finish();
			}
		});
		mTitleRightImgbtn.setVisibility(View.GONE);

		Inbox inbox = (Inbox) getIntent().getSerializableExtra("inbox");
		mInboxs = (List<Inbox>) getIntent().getSerializableExtra("inboxs");
		mPosition = getIntent().getIntExtra("position", 0);
		Log.i("MainActivity", inbox.getTitle() + "-" + inbox.getContent()
				+ "--" + mInboxs.size());

		mTitleText.setText("收件箱" + "(" + (mPosition + 1) + "/" + mInboxs.size()
				+ ")");

		mViewFlipper = (ViewFlipper) this.findViewById(R.id.inbox_viewFlipper);
		mViewFlipper.removeAllViews();

		mView = LayoutInflater.from(this).inflate(R.layout.activity_inbox_item,
				null);
		mTitle = (TextView) mView.findViewById(R.id.inbox_detail_title);
		mDate = (TextView) mView.findViewById(R.id.inbox_detail_date);
		mContent = (TextView) mView.findViewById(R.id.inbox_detail_content);

		mTitle.setText(inbox.getTitle());
		mDate.setText(inbox.getDate());
		mContent.setText(inbox.getContent());
		if (inbox.getIsRead().equals("未读")) {
			boolean result = mInboxDao.updateInbox(inbox);
			Log.i("MainActivity", "修改为已读：" + result);
		}

		mViewFlipper.addView(mView, 0);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 50) {

			int index = mPosition + 1;
			if (index <= mInboxs.size() - 1) {
				mPosition++;
				Inbox inbox = mInboxs.get(index);

				mTitle.setText(inbox.getTitle());
				mDate.setText(inbox.getDate());
				mContent.setText(inbox.getContent());

				mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_left_in));
				mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_left_out));

				mViewFlipper.showNext();
				mTitleText.setText("收件箱" + "(" + (mPosition + 1) + "/"
						+ mInboxs.size() + ")");
				if (inbox.getIsRead().equals("未读")) {
					boolean result = mInboxDao.updateInbox(inbox);
					Log.i("MainActivity", "修改为已读：" + result);
				}
				return true;
			}
		} else if (e1.getX() - e2.getX() < -50) {
			int index = mPosition - 1;
			if (index >= 0) {
				mPosition--;
				Inbox inbox = mInboxs.get(index);

				mTitle.setText(inbox.getTitle());
				mDate.setText(inbox.getDate());
				mContent.setText(inbox.getContent());

				mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_right_in));
				mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_right_out));

				mViewFlipper.showPrevious();
				mTitleText.setText("收件箱" + "(" + (mPosition + 1) + "/"
						+ mInboxs.size() + ")");
				if (inbox.getIsRead().equals("未读")) {
					boolean result = mInboxDao.updateInbox(inbox);
					Log.i("MainActivity", "修改为已读：" + result);
				}
				return true;
			}

		}

		return false;
	}
}
