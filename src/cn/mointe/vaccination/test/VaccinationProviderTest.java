package cn.mointe.vaccination.test;

import java.text.ParseException;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.Log;

public class VaccinationProviderTest extends AndroidTestCase {

	public final static String TAG = "MainActivity";

	public void testSave() {
		ContentResolver resolver = this.getContext().getContentResolver();
		ContentValues values = new ContentValues();
		// values.put("vaccineName", "乙肝疫苗");
		// values.put("isHave", 0);
		// values.put("reserveDate", "2014-02-01");
		// values.put("age", "1月龄");

		// values.put("vaccineName", "脊灰减毒活疫苗");
		// values.put("isHave", 0);
		// values.put("reserveDate", "2014-03-01");
		// values.put("age", "2月龄");

		values.put("vaccine_name", "白破疫苗");
		values.put("reserve_time", "2014-03-01");
		values.put("moon_age", "2月龄");

		// values.put("vaccineName", "百白破疫苗");
		// values.put("isHave", 0);
		// values.put("reserveDate", "2014-04-01");
		// values.put("age", "3月龄");

		Uri uri = resolver.insert(VaccinationProvider.CONTENT_URI, values);
		Log.i(TAG, "insert--uri=" + uri);
	}

	public void testDelete() {
		ContentResolver resolver = this.getContext().getContentResolver();
		int count = resolver
				.delete(VaccinationProvider.CONTENT_URI, null, null);
		Log.i(TAG, "delete--count=" + count);
	}

	public void testUpdate() {
		ContentResolver resolver = this.getContext().getContentResolver();
		ContentValues values = new ContentValues();
		values.put("vaccinationDate", "2014-02-01");
		int count = resolver.update(VaccinationProvider.CONTENT_URI, values,
				"_id=?", new String[] { "1" });
		Log.i(TAG, "update--count=" + count);
	}

	public void testQuery() {
		ContentResolver resolver = this.getContext().getContentResolver();
		Cursor cursor = resolver.query(VaccinationProvider.CONTENT_URI, null,
				null, null, null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_NAME));
			Log.i(TAG, "query--name=" + name);
		}
		cursor.close();
	}

	public void testSaveVaccinations() {
		VaccinationDao dao = new VaccinationDao(this.getContext());
		dao.savaVaccinations("2014-01-05", "小花儿");
	}

	public void testFindNextVaccinationDate() throws ParseException {
		VaccinationDao dao = new VaccinationDao(this.getContext());
		String next = dao.findNextVaccinationDate("xiaobaobei", "2014-05-18");
		Log.i(TAG, next);
	}

	public void testGetVaccinationByBabyBirthday() {
		VaccinationDao dao = new VaccinationDao(this.getContext());

		List<Vaccination> list = dao.getVaccinationsByBabyNameAndAddBabyDate(
				"baby8", "2014-04-15");
		for (Vaccination vac : list) {
			Log.i(TAG, vac.getVaccine_name());
		}
	}

	public void testFindNextDate() throws ParseException {
		VaccinationDao dao = new VaccinationDao(this.getContext());
		Log.i(TAG, "date=" + dao.findNextDate("宝宝"));
	}
}
