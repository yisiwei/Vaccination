package cn.mointe.vaccination.test;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.provider.DiaryProvider;
import cn.mointe.vaccination.tools.Log;

public class DiaryProviderTest extends AndroidTestCase {

	public void testQuery() {
		ContentResolver resolver = getContext().getContentResolver();
		Cursor cursor = resolver.query(DiaryProvider.CONTENT_URI, null, null,
				null, null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor
					.getColumnIndex(DBHelper.DIARY_COLUMN_BABY_NICKNAME));
			String date = cursor.getString(cursor
					.getColumnIndex(DBHelper.DIARY_COLUMN_DATE));
			String content = cursor.getString(cursor
					.getColumnIndex(DBHelper.DIARY_COLUMN_DIARY_CONTENT));
			Log.i("MainActivity", name + "--" + date + "-content=" + content);
		}
	}

	//0509 0530 0517 0520
	public void testAdd() {
		ContentResolver resolver = getContext().getContentResolver();

		ContentValues values = new ContentValues();
		values.put(DBHelper.DIARY_COLUMN_BABY_NICKNAME, "addtest");
		values.put(DBHelper.DIARY_COLUMN_DATE, "2014-05-12");
		values.put(DBHelper.DIARY_COLUMN_DIARY_CONTENT, "test test test");
		Uri uri = resolver.insert(DiaryProvider.CONTENT_URI, values);
		Log.i("MainActivity", "uri:" + uri);
	}
}
