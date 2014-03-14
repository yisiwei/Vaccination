package cn.mointe.vaccination.test;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.provider.VaccinationRuleProvider;

public class VaccinationRuleProviderTest extends AndroidTestCase {

	public final static String TAG = "MainActivity";

	public void testSave() {
		ContentResolver resolver = this.getContext().getContentResolver();
		ContentValues values = new ContentValues();

		values.put(DBHelper.RULE_COLUMN_VACCINE_CODE, "V01");
		values.put(DBHelper.RULE_COLUMN_MOON_AGE, 0);
		values.put(DBHelper.RULE_COLUMN_IS_CHARGE, 1);
		values.put(DBHelper.RULE_COLUMN_VACCINATION_NUMBER, "第1/3剂");

		Uri uri = resolver.insert(VaccinationRuleProvider.CONTENT_URI, values);
		Log.i(TAG, "insert--uri=" + uri);
	}

	public void testDelete() {
		ContentResolver resolver = this.getContext().getContentResolver();
		int count = resolver.delete(VaccinationRuleProvider.CONTENT_URI, null,
				null);
		Log.i(TAG, "delete--count=" + count);
	}

	public void testUpdate() {
		ContentResolver resolver = this.getContext().getContentResolver();
		ContentValues values = new ContentValues();
		values.put("vaccinationDate", "2014-02-01");
		int count = resolver.update(VaccinationRuleProvider.CONTENT_URI, values,
				"_id=?", new String[] { "1" });
		Log.i(TAG, "update--count=" + count);
	}

	public void testQuery() {
		ContentResolver resolver = this.getContext().getContentResolver();
		Cursor cursor = resolver.query(VaccinationRuleProvider.CONTENT_URI, null,
				null, null, null);
		while (cursor.moveToNext()) {
			String name = cursor
					.getString(cursor.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_NAME));
			Log.i(TAG, "query--name=" + name);
		}
		cursor.close();
	}
	

}
