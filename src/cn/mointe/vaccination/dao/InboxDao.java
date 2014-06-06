package cn.mointe.vaccination.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Inbox;
import cn.mointe.vaccination.provider.InboxProvider;

public class InboxDao {
	private ContentResolver mResolver;

	public InboxDao(Context context) {
		mResolver = context.getContentResolver();
	}

	/**
	 * 添加消息
	 * 
	 * @param inbox
	 * @return
	 */
	public boolean saveInbox(Inbox inbox) {
		boolean flag = false;
		ContentValues values = new ContentValues();

		values.put(DBHelper.INBOX_COLUMN_USERNAME, inbox.getUsername());
		values.put(DBHelper.INBOX_COLUMN_TYPE, inbox.getType());
		values.put(DBHelper.INBOX_COLUMN_DATE, inbox.getDate());

		values.put(DBHelper.INBOX_COLUMN_TITLE, inbox.getTitle());
		values.put(DBHelper.INBOX_COLUMN_CONTENT, inbox.getContent());
		values.put(DBHelper.INBOX_COLUMN_IS_READ, inbox.getIsRead());

		try {
			mResolver.insert(InboxProvider.CONTENT_URI, values);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 删除消息
	 * 
	 * @param inbox
	 * @return
	 */
	public boolean deleteInbox(Inbox inbox) {
		boolean flag = false;

		int count = mResolver.delete(InboxProvider.CONTENT_URI,
				DBHelper.INBOX_COLUMN_ID + "=?",
				new String[] { String.valueOf(inbox.getId()) });
		if (count > 0) {
			flag = true;
		}
		return flag;
	}
}
