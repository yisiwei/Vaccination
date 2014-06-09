package cn.mointe.vaccination.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "vaccination.db";// 数据库名称
	public static final int DB_VERSION = 2;// 数据库版本

	// baby表
	public static final String BABY_TABLE_NAME = "baby";// 表名称

	public static final String BABY_COLUMN_ID = "_id";// 主键
	public static final String BABY_COLUMN_NAME = "name";// 昵称
	public static final String BABY_COLUMN_IMAGE = "image_url";// 头像路径
	
	public static final String BABY_COLUMN_CREATE_DATE = "create_date";// 创建日期

	public static final String BABY_COLUMN_BIRTHDAY = "birthday";// 出生日期
	public static final String BABY_COLUMN_SEX = "sex";// 性别
	public static final String BABY_COLUMN_RESIDENCE = "residence_place";// 居住地

	public static final String BABY_COLUMN_VACCINATION_PLACE = "vaccination_place";// 接种地
	public static final String BABY_COLUMN_CITY_CODE = "city_code";
	public static final String BABY_COLUMN_VACCINATION_PHONE = "vaccination_phone";// 接种地电话
	public static final String BABY_COLUMN_IS_DEFAULT = "is_default";// 是否是默认宝宝

	// 创建baby表SQL语句
	public static final String CREATE_BABY_TABLE_SQL = "create table "
			+ BABY_TABLE_NAME + "(" + BABY_COLUMN_ID
			+ " integer primary key autoincrement, " + BABY_COLUMN_IMAGE
			+ " TEXT, " + BABY_COLUMN_CREATE_DATE + " TEXT," + BABY_COLUMN_NAME + " TEXT, " + BABY_COLUMN_BIRTHDAY
			+ " TEXT, " + BABY_COLUMN_SEX + " TEXT, " + BABY_COLUMN_RESIDENCE
			+ " TEXT, " + BABY_COLUMN_VACCINATION_PLACE + " TEXT,"
			+ BABY_COLUMN_CITY_CODE + " TEXT," + BABY_COLUMN_VACCINATION_PHONE
			+ " TEXT, " + BABY_COLUMN_IS_DEFAULT + " TEXT " + ")";

	// vaccinationInfo表
	public static final String VACCINATION_TABLE_NAME = "vaccinationInfo";// 表名

	public static final String VACCINATION_COLUMN_ID = "_id";
	public static final String VACCINATION_COLUMN_VACCINE_NAME = "vaccine_name";// 疫苗名称
	public static final String VACCINATION_COLUMN_RESERVE_TIME = "reserve_time";// 预约时间

	public static final String VACCINATION_COLUMN_FINISH_TIME = "finish_time";// 完成时间
	public static final String VACCINATION_COLUMN_MOON_AGE = "moon_age";// 月龄
	public static final String VACCINATION_COLUMN_VACCINE_TYPE = "vaccine_type";// 疫苗类型（一类/二类）

	public static final String VACCINATION_COLUMN_CHARGE_STANDARD = "charge_standard";// 收费情况
	public static final String VACCINATION_COLUMN_VACCINATION_NUMBER = "vaccination_number";// 第几次接种（第1/3剂）
	public static final String VACCINATION_COLUMN_BABY_NICKNAME = "baby_nickname";// 宝宝昵称

	// 创建vaccinationInfo表SQL语句
	public static final String CREATE_VACCINATIONINFO_TABLE_SQL = "create table "
			+ VACCINATION_TABLE_NAME
			+ "("
			+ VACCINATION_COLUMN_ID
			+ " integer primary key autoincrement, "
			+ VACCINATION_COLUMN_VACCINE_NAME
			+ " TEXT, "
			+ VACCINATION_COLUMN_RESERVE_TIME
			+ " TEXT, "
			+ VACCINATION_COLUMN_FINISH_TIME
			+ " TEXT, "
			+ VACCINATION_COLUMN_MOON_AGE
			+ " TEXT, "
			+ VACCINATION_COLUMN_VACCINE_TYPE
			+ " TEXT, "
			+ VACCINATION_COLUMN_CHARGE_STANDARD
			+ " TEXT, "
			+ VACCINATION_COLUMN_VACCINATION_NUMBER
			+ " TEXT,"
			+ VACCINATION_COLUMN_BABY_NICKNAME + " TEXT " + ")";

	// vaccine表（疫苗库）
	public static final String VACCINE_TABLE_NAME = "vaccine";// 表名

	public static final String VACCINE_COLUMN_ID = "_id";
	public static final String VACCINE_COLUMN_VACCINE_NAME = "vaccine_name";// 疫苗名称
	public static final String VACCINE_COLUMN_VACCINE_CODE = "vaccine_code";// 疫苗code
	public static final String VACCINE_COLUMN_VACCINE_TYPE = "vaccine_type";// 疫苗种类

	public static final String VACCINE_COLUMN_VACCINE_INTRO = "vaccine_intro";// 预防疾病

	// 创建vaccine表SQL语句
	public static final String CREATE_VACCINE_TABLE_SQL = "create table "
			+ VACCINE_TABLE_NAME + "(" + VACCINE_COLUMN_ID
			+ " integer primary key autoincrement, "
			+ VACCINE_COLUMN_VACCINE_NAME + " TEXT, "
			+ VACCINE_COLUMN_VACCINE_CODE + " TEXT, "
			+ VACCINE_COLUMN_VACCINE_TYPE + " TEXT, "
			+ VACCINE_COLUMN_VACCINE_INTRO + " TEXT " + ")";

	// VaccineSpecification表（疫苗说名书）
	public static final String VS_TABLE_NAME = "vaccineSpecification";// 表名

	public static final String VS_COLUMN_ID = "_id";
	public static final String VS_COLUMN_VACCINE_NAME = "vaccine_name";// 疫苗名称
	public static final String VS_COLUMN_PRODUCT_NAME = "product_name";// 商品名称

	public static final String VS_COLUMN_MANUFACTURERS = "manufacturers";// 生产厂商
	public static final String VS_COLUMN_PRICE = "price";// 价格
	public static final String VS_COLUMN_FUNCTIONANDUSE = "functionanduse";// 作用于用途

	public static final String VS_COLUMN_DESCRIPTION = "description";// 性状
	public static final String VS_COLUMN_INOCULATION_OBJECT = "inoculation_object";// 接种对象
	public static final String VS_COLUMN_VACCINATION_IMAGE_URL = "vaccination_image_url";// 疫苗产品图片

	public static final String VS_COLUMN_PRODUCT_SPECIFICATION = "product_specification";// 产品规格
	public static final String VS_COLUMN_IMMUNE_PROCEDURE = "immune_procedure";// 免疫程序
	public static final String VS_COLUMN_ADVERSE_REACTION = "adverse_reaction";// 不良反应

	public static final String VS_COLUMN_CONTRAINDICATION = "contraindication";// 禁忌
	public static final String VS_COLUMN_CAUTION = "caution";// 注意事项
	public static final String VS_COLUMN_LICENSE_NUMBER = "license_number";// 批准文号

	public static final String VS_COLUMN_VALIDITY_PERIOD = "validity_period";// 有效期

	// 创建vaccineSpecification表SQL语句
	public static final String CREATE_VACCINESPECIFICATION_TABLE_SQL = "create table "
			+ VS_TABLE_NAME
			+ "("
			+ VS_COLUMN_ID
			+ " integer primary key autoincrement, "
			+ VS_COLUMN_VACCINE_NAME
			+ " TEXT, "
			+ VS_COLUMN_PRODUCT_NAME
			+ " TEXT, "
			+ VS_COLUMN_MANUFACTURERS
			+ " TEXT, "
			+ VS_COLUMN_PRICE
			+ " TEXT, "
			+ VS_COLUMN_FUNCTIONANDUSE
			+ " TEXT, "
			+ VS_COLUMN_DESCRIPTION
			+ " TEXT, "
			+ VS_COLUMN_INOCULATION_OBJECT
			+ " TEXT,"
			+ VS_COLUMN_VACCINATION_IMAGE_URL
			+ " TEXT, "
			+ VS_COLUMN_PRODUCT_SPECIFICATION
			+ " TEXT, "
			+ VS_COLUMN_IMMUNE_PROCEDURE
			+ " TEXT, "
			+ VS_COLUMN_ADVERSE_REACTION
			+ " TEXT, "
			+ VS_COLUMN_CONTRAINDICATION
			+ " TEXT, "
			+ VS_COLUMN_CAUTION
			+ " TEXT, "
			+ VS_COLUMN_LICENSE_NUMBER
			+ " TEXT, "
			+ VS_COLUMN_VALIDITY_PERIOD + " TEXT " + ")";

	// VacinationDiary表（接种日记）
	public static final String VD_TABLE_NAME = "vacinationDiary";

	public static final String VD_COLUMN_ID = "_id";
	public static final String VD_COLUMN_BABY_NICKNAME = "baby_nickname";// 宝宝昵称
	public static final String VD_COLUMN_FINISH_TIME = "finish_time";// 完成时间

	public static final String VD_COLUMN_VACCINE_NAME = "vaccine_name";// 疫苗名称
	public static final String VD_COLUMN_VACCINATION_NUMBER = "vaccination_number";// 第几次接种（第1/3剂）
	public static final String VD_COLUMN_MOON_AGE = "moon_age";// 月龄

	public static final String VD_COLUMN_LICENSE_NUMBER = "license_number";// 批准文号
	public static final String VD_COLUMN_VACCINATION_SITE = "vaccination_site";// 接种部位
	public static final String VD_COLUMN_MANUFACTURERS = "manufacturers";// 生产厂商

	public static final String VD_COLUMN_VACCINATION_PLACE = "vaccination_place";// 接种地
	public static final String VD_COLUMN_REMARK = "remark";// 备注

	// 创建VacinationDiary表SQL语句
	public static final String CREATE_VACCINATIONDIARY_TABLE_SQL = "create table "
			+ VD_TABLE_NAME
			+ "("
			+ VD_COLUMN_ID
			+ " integer primary key autoincrement, "
			+ VD_COLUMN_BABY_NICKNAME
			+ " TEXT, "
			+ VD_COLUMN_FINISH_TIME
			+ " TEXT, "
			+ VD_COLUMN_VACCINE_NAME
			+ " TEXT, "
			+ VD_COLUMN_VACCINATION_NUMBER
			+ " TEXT, "
			+ VD_COLUMN_MOON_AGE
			+ " TEXT, "
			+ VD_COLUMN_LICENSE_NUMBER
			+ " TEXT, "
			+ VD_COLUMN_VACCINATION_SITE
			+ " TEXT, "
			+ VD_COLUMN_MANUFACTURERS
			+ " TEXT, "
			+ VD_COLUMN_VACCINATION_PLACE
			+ " TEXT, "
			+ VD_COLUMN_REMARK
			+ " TEXT " + ")";

	// 接种列表生成规则表
	public static final String RULE_TABLE_NAME = "vaccinationRule";

	public static final String RULE_COLUMN_ID = "_id";
	public static final String RULE_COLUMN_VACCINE_CODE = "vaccine_code";
	public static final String RULE_COLUMN_VACCINE_NAME = "vaccine_name";
	public static final String RULE_COLUMN_MOON_AGE = "moon_age";

	public static final String RULE_COLUMN_IS_CHARGE = "is_charge";
	public static final String RULE_COLUMN_VACCINATION_NUMBER = "vaccination_number";
	public static final String RULE_COLUMN_VACCINE_TYPE = "vaccine_type";

	// 创建规则表SQL语句
	public static final String CREATE_VACCINATION_RULE_TABLE_SQL = "create table "
			+ RULE_TABLE_NAME
			+ "("
			+ RULE_COLUMN_ID
			+ " integer primary key autoincrement, "
			+ RULE_COLUMN_VACCINE_NAME
			+ " TEXT, "
			+ RULE_COLUMN_VACCINE_CODE
			+ " TEXT, "
			+ RULE_COLUMN_MOON_AGE
			+ " integer, "
			+ RULE_COLUMN_IS_CHARGE
			+ " TEXT, "
			+ RULE_COLUMN_VACCINE_TYPE
			+ " TEXT, "
			+ RULE_COLUMN_VACCINATION_NUMBER + " TEXT " + ")";

	public DBHelper(Context context) {
		// 第二个参数：数据库名称
		// 第三个参数：游标工厂对象 null表示使用系统默认游标对象
		// 第四个参数：数据库版本号，不能为0，从1开始
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * 数据库第一次被创建时调用
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BABY_TABLE_SQL);
		db.execSQL(CREATE_VACCINATIONINFO_TABLE_SQL);
		db.execSQL(CREATE_VACCINESPECIFICATION_TABLE_SQL);
		db.execSQL(CREATE_VACCINE_TABLE_SQL);
		db.execSQL(CREATE_VACCINATIONDIARY_TABLE_SQL);
		db.execSQL(CREATE_VACCINATION_RULE_TABLE_SQL);
	}

	/**
	 * 数据库版本发生变化时调用
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + RULE_TABLE_NAME);
		db.execSQL(CREATE_VACCINATION_RULE_TABLE_SQL);
	}

}
