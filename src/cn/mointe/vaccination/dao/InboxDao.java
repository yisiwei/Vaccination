package cn.mointe.vaccination.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Inbox;
import cn.mointe.vaccination.domain.VaccineRemind;
import cn.mointe.vaccination.other.VaccinePullParseXml;
import cn.mointe.vaccination.provider.InboxProvider;

public class InboxDao {
	private ContentResolver mResolver;
	private Context mContext;

	public InboxDao(Context context) {
		this.mContext = context;
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

	/**
	 * 修改为已读
	 * 
	 * @param inbox
	 * @return
	 */
	public boolean updateInbox(Inbox inbox) {
		boolean flag = false;
		ContentValues values = new ContentValues();
		values.put(DBHelper.INBOX_COLUMN_IS_READ, "已读");
		int count = mResolver.update(InboxProvider.CONTENT_URI, values,
				DBHelper.INBOX_COLUMN_ID + "=?",
				new String[] { String.valueOf(inbox.getId()) });
		if (count > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 根据疫苗名称查询提醒内容
	 * 
	 * @param vaccineName
	 * @return
	 */
	public VaccineRemind getVaccineRemind(String vaccineName) {

		InputStream vaccineXml = null;
		try {
			vaccineXml = mContext.getResources().getAssets()
					.open("vaccine_remind.xml");
			List<VaccineRemind> vaccineReminds = VaccinePullParseXml
					.getVaccineReminds(vaccineXml);
			for (VaccineRemind vaccineRemind : vaccineReminds) {
				if (vaccineRemind.getVaccineName().equals(vaccineName)) {
					return vaccineRemind;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return null;
	}
}
