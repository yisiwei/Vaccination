package cn.mointe.vaccination.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Vaccine;
import cn.mointe.vaccination.provider.VaccineProvider;

public class VaccineDao {

	private ContentResolver mResolver;

	public VaccineDao(Context context) {
		mResolver = context.getContentResolver();
	}

	/**
	 * 加载疫苗库
	 * 
	 * @return
	 */
	public List<String> loadVaccines() {
		List<String> vaccines = new ArrayList<String>();
		Cursor cursor = mResolver.query(VaccineProvider.CONTENT_URI, null,
				null, null, null);
		while (cursor.moveToNext()) {
			String vaccineName = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINE_COLUMN_VACCINE_NAME));
			vaccines.add(vaccineName);
		}
		cursor.close();
		return vaccines;

	}

	/**
	 * 根据疫苗名称查询疫苗信息
	 * 
	 * @param name
	 * @return
	 */
	public Vaccine queryVaccineByName(String name) {
		Vaccine vaccine = null;
		Cursor cursor = mResolver.query(VaccineProvider.CONTENT_URI, null,
				DBHelper.VACCINE_COLUMN_VACCINE_NAME + "=?",
				new String[] { name }, null);
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(DBHelper.VACCINE_COLUMN_ID));
			String vaccineName = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINE_COLUMN_VACCINE_NAME));
			String vaccineCode = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINE_COLUMN_VACCINE_CODE));
			String vaccineType = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINE_COLUMN_VACCINE_TYPE));
			String vaccineIntro = cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINE_COLUMN_VACCINE_INTRO));
			vaccine = new Vaccine(id, vaccineName, vaccineCode, vaccineType,
					vaccineIntro);
		}
		return vaccine;
	}

	/**
	 * 初始化疫苗库
	 */
	public void savaVaccines() {

		saveVaccine(new Vaccine(
				"乙肝疫苗",
				"V01",
				"目前我国有三种重组乙型肝炎疫苗、分别为重组乙型肝炎疫苗（酿酒酵母）、重组乙型肝炎疫苗（汉逊酵母）和重组乙型肝炎疫苗（CHO、中华仓鼠卵巢细胞）三类",
				"乙型病毒性肝炎（乙肝）"));
		saveVaccine(new Vaccine("卡介苗", "V02", "用卡介苗菌经培养后手机菌体，加入稳定剂冻干制成。",
				"儿童结核病，特别是结核性脑膜炎和栗粒性肺结核"));
		saveVaccine(new Vaccine("脊灰疫苗", "V03",
				"OPV为国家免疫规划疫苗，有糖丸和液体2种剂型，糖丸有单价（Ⅰ 型）和三价（Ⅰ +Ⅱ+Ⅲ型）疫苗。", "小儿麻痹"));

		saveVaccine(new Vaccine(
				"Hib结合疫苗",
				"V04",
				"我国使用的是Hib结合疫苗。由于Hib疫苗在免疫程序和目标人群与百日咳、白喉、破伤风等类似。包含Hib的联合疫苗也较常见。",
				"Hib疫苗又称b型流感嗜血杆菌疫苗，可预防b型流感嗜血杆菌引起的多种侵袭性疾病。如脑膜炎、肺炎等。5岁以下人群是Hib感染的主要人群。"));
		saveVaccine(new Vaccine("轮状病毒活疫苗", "V05", "", "因A群轮状病毒引起的腹泻"));
		saveVaccine(new Vaccine(
				"脊灰灭活疫苗",
				"V06",
				"本品脊髓灰质炎灭活疫苗（IPV）系采用脊髓灰质炎病毒Ⅰ 型（Mahoney株）、Ⅱ型（MEF-1株）、Ⅲ型（Saukett株）分别接种与Vero细胞培养并收获病毒，经浓缩、纯化后用甲醛灭活，按比例混合后制成的3价液体疫苗。",
				"有脊髓灰质炎1型、2型和3型病毒导致的脊髓灰质炎。"));

		saveVaccine(new Vaccine("五联疫苗", "V07", "",
				"百日咳、白喉、破伤风、脊髓灰质炎和b型流感嗜血杆菌感染等五种疾病。"));
		saveVaccine(new Vaccine("百白破疫苗", "V08", "", "百日咳、白喉、破伤风。"));
		saveVaccine(new Vaccine("7价肺炎结合疫苗", "V09", "7价肺炎球菌结合疫苗", "因肺炎球菌引起的肺炎。"));

		saveVaccine(new Vaccine(
				"A群流脑疫苗",
				"V10",
				"我国上市的脑膜炎球菌疫苗包括A群脑膜炎球菌多糖疫苗、A群C群脑膜炎球菌多糖疫苗、A群C群脑膜炎球菌结合疫苗和ACCYW135群脑膜炎球菌多糖疫苗。接种含相应菌群乘风的脑膜炎球菌只能预防相应菌群的脑膜炎球菌引起的流脑，如接种A群流脑疫苗只能预防A群流脑。",
				"A群脑膜炎球菌引起的流行性脑脊髓膜炎，简称流脑。"));
		saveVaccine(new Vaccine(
				"流感疫苗",
				"V11",
				"流感疫苗可以预防流行性感冒病毒因为的流感，不能防止普通感冒。我国批准上市的流感疫苗均为灭活疫苗，包括裂解疫苗和亚单位疫苗，国外有公司生产流感减毒活疫苗。",
				"流行性感冒病毒引起的流感"));
		saveVaccine(new Vaccine("乙脑减毒灭疫苗", "V12",
				"流行性乙型脑炎疫苗主要分乙脑减毒活疫苗和乙脑灭活疫苗（Vero细胞）2种。", "流行性乙型脑炎（乙脑）。"));

		saveVaccine(new Vaccine(
				"麻腮风疫苗",
				"V13",
				"在使用麻疹减毒活疫苗的种类较多，既有单价疫苗，也有含麻疹成分的联合疫苗，另含风疹、流行性腮腺炎等成分。在使用麻疹-风疹联合疫苗（麻风疫苗）、麻疹-流行性腮腺炎联合疫苗（腮腺疫苗）、麻疹-流行性腮腺炎-风疹联合疫苗（麻腮风疫苗）时，麻疹成分的免疫效果都一样很好。",
				"麻疹、流行性腮腺炎、风疹"));
		saveVaccine(new Vaccine("甲肝灭活疫苗", "V14", "甲肝疫苗目前有两种：甲肝减毒活疫苗和甲肝灭活疫苗。",
				"甲型病毒性肝炎"));
		saveVaccine(new Vaccine(
				"水痘疫苗",
				"V15",
				"水痘减毒活疫苗使用全球通用的Oka减毒株，经人二倍体细胞培养制成。除单价的水痘疫苗外，国外有公司生产有麻疹-风疹-流行性腮腺炎-水痘联合疫苗。",
				"水痘及因水痘带状疱疹而引起的并发症"));

		saveVaccine(new Vaccine("23价肺炎疫苗", "V16", "23价肺炎球菌多糖疫苗。", "因肺炎球菌引起的肺炎。"));
		saveVaccine(new Vaccine("白破疫苗", "V17", "吸附白喉破伤风联合疫苗。", "白喉、破伤风。"));
		saveVaccine(new Vaccine(
				"A+C群流脑疫苗",
				"V18",
				"我国上市的脑膜炎球菌疫苗：包括A群脑膜炎球菌多糖疫苗、A群C群脑膜炎球菌多糖疫苗、A群C群脑膜炎球菌结合疫苗和ACYW135群脑膜炎球菌多糖疫苗。接种含相应球菌成分的脑膜炎球菌疫苗只能预防相应菌群的脑膜炎球菌引起的流脑，如接种A群流脑疫苗只能预防A群流脑。",
				"A群C群脑膜炎球菌引起的流行性脑脊髓膜炎，简称流脑"));

		saveVaccine(new Vaccine("出血热疫苗", "V19",
				"双价肾综合正出血热灭活疫苗有Vero细胞、地鼠肾细胞、沙鼠肾细胞三种。", "肾综合症出血热"));
		saveVaccine(new Vaccine("霍乱疫苗", "V20", "", "霍乱和产毒性大肠杆菌引起的腹泻"));
		saveVaccine(new Vaccine("甲肝灭活疫苗", "V21", "甲肝疫苗目前有两种：甲肝减毒疫苗和甲肝灭活疫苗。",
				"甲型病毒性肝炎。"));

		saveVaccine(new Vaccine("甲乙肝联合疫苗", "V22", "", "甲型病毒性肝炎、乙型病毒性肝炎。"));
		saveVaccine(new Vaccine("狂犬病疫苗", "V23", "", "狂犬病"));
		saveVaccine(new Vaccine("麻疹风疹", "V24", "正在使用的麻疹减毒活疫苗的种类较多。",
				"麻疹、风疹等相应传染病。"));

		saveVaccine(new Vaccine("伤寒疫苗", "V25", "用伤寒沙门菌培养液纯化的Vi多糖疫苗", "预防伤寒"));
		saveVaccine(new Vaccine("乙脑灭活疫苗", "V26",
				"流行性乙型脑炎疫苗分为乙脑减毒活疫苗和乙脑灭活疫苗（Vero细胞）两种。", "流行性乙型脑炎（乙脑）。"));
		saveVaccine(new Vaccine(
				"麻疹疫苗",
				"V27",
				"正在使用的麻疹减毒活疫苗的种类较多，既有单价疫苗，也有含麻疹成分的联合疫苗，另含风疹、流行性腮腺炎等成分。在使用麻疹-风疹联合疫苗（麻风疫苗）、麻疹-流行性腮腺炎联合疫苗（麻腮疫苗）、反震-流行性腮腺炎风疹联合疫苗（麻腮风疫苗）时，麻疹成分的免疫效果都一样很好。",
				"预防麻疹等相应的传染病。"));
	}

	/**
	 * 保存Vaccine
	 * 
	 * @param vaccine
	 */
	public void saveVaccine(Vaccine vaccine) {

		ContentValues values = new ContentValues();

		values.put(DBHelper.VACCINE_COLUMN_VACCINE_NAME,
				vaccine.getVaccine_name());
		values.put(DBHelper.VACCINE_COLUMN_VACCINE_CODE,
				vaccine.getVaccine_code());
		values.put(DBHelper.VACCINE_COLUMN_VACCINE_TYPE,
				vaccine.getVaccine_type());
		values.put(DBHelper.VACCINE_COLUMN_VACCINE_INTRO,
				vaccine.getVaccine_intro());

		mResolver.insert(VaccineProvider.CONTENT_URI, values);
	}

}
