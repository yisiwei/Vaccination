package cn.mointe.vaccination.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.adapter.InboxAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.InboxDao;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Inbox;
import cn.mointe.vaccination.provider.InboxProvider;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.PublicMethod;

public class InboxActivity extends FragmentActivity {

	private TextView mTitleText;
//	private ImageButton mTitleLeftImgbtn;// title左边图标
	private LinearLayout mTitleLeft;
	private ImageView mTitleRightImgbtn;// title右边图

	private ListView mListView;
	private InboxAdapter mAdapter;
	private List<Inbox> mInboxs;

	private LoaderManager mManager;
	private BabyDao mBabyDao;
	private InboxDao mInboxDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);

		mBabyDao = new BabyDao(this);
		mInboxDao = new InboxDao(this);
		mManager = getSupportLoaderManager();

		mTitleText = (TextView) this.findViewById(R.id.title_text);
//		mTitleLeftImgbtn = (ImageButton) this
//				.findViewById(R.id.title_left_imgbtn);
		mTitleLeft = (LinearLayout) this.findViewById(R.id.title_left);
		mTitleRightImgbtn = (ImageView) this
				.findViewById(R.id.title_right_imgbtn);
		mListView = (ListView) this.findViewById(R.id.inbox_list);

		mTitleText.setText(R.string.inbox);
		mTitleLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InboxActivity.this.finish();
			}
		});
		mTitleRightImgbtn.setVisibility(View.GONE);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(InboxActivity.this,
						InboxDetailActivity.class);
				Inbox inbox = mInboxs.get(position);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("inbox", inbox);
				mBundle.putInt("position", position);
				mBundle.putSerializable("inboxs", (Serializable) mInboxs);
				intent.putExtras(mBundle);
				startActivity(intent);
			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Inbox inbox = mInboxs.get(position);
				showDeleteDialog(inbox);
				return true;
			}

		});

		mManager.initLoader(1000, null, mCallBacks);
	}

	/**
	 * 删除消息Dialog
	 * 
	 * @param inbox
	 */
	private void showDeleteDialog(final Inbox inbox) {
		Log.i("MainActivity", "id:" + inbox.getId());
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app_icon);
		builder.setTitle(R.string.hint);
		builder.setMessage("确定删除此消息？");
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean result = mInboxDao.deleteInbox(inbox);
						if (result) {
							PublicMethod.showToast(InboxActivity.this,
									R.string.delete_success);
						} else {
							PublicMethod.showToast(InboxActivity.this,
									R.string.delete_fail);
						}
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		builder.create();
		builder.show();

	}

	private LoaderCallbacks<Cursor> mCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(InboxActivity.this);
			loader.setUri(InboxProvider.CONTENT_URI);
			loader.setSelection(DBHelper.INBOX_COLUMN_USERNAME + "=?");
			loader.setSelectionArgs(new String[] { mBabyDao.getDefaultBaby()
					.getName() });
			loader.setSortOrder(DBHelper.INBOX_COLUMN_DATE + " desc");
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mInboxs = new ArrayList<Inbox>();
			while (data.moveToNext()) {
				Inbox inbox = cursorToInbox(data);
				mInboxs.add(inbox);
			}
			mAdapter = new InboxAdapter(InboxActivity.this, mInboxs);
			mListView.setAdapter(mAdapter);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}

	};

	private Inbox cursorToInbox(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex(DBHelper.INBOX_COLUMN_ID));

		String username = cursor.getString(cursor
				.getColumnIndex(DBHelper.INBOX_COLUMN_USERNAME));
		String type = cursor.getString(cursor
				.getColumnIndex(DBHelper.INBOX_COLUMN_TYPE));
		String date = cursor.getString(cursor
				.getColumnIndex(DBHelper.INBOX_COLUMN_DATE));

		String title = cursor.getString(cursor
				.getColumnIndex(DBHelper.INBOX_COLUMN_TITLE));
		String detail = cursor.getString(cursor
				.getColumnIndex(DBHelper.INBOX_COLUMN_CONTENT));
		String isRead = cursor.getString(cursor
				.getColumnIndex(DBHelper.INBOX_COLUMN_IS_READ));

		Inbox inbox = new Inbox(id, username, type, date, title, detail, isRead);

		return inbox;
	}

}
