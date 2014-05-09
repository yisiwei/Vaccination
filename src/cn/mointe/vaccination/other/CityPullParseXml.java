package cn.mointe.vaccination.other;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;
import cn.mointe.vaccination.domain.City;
import cn.mointe.vaccination.domain.Province;

public class CityPullParseXml {

	public CityPullParseXml() {

	}

	/**
	 * 获取所有的省
	 * 
	 * @param xml
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static List<Province> getProvinces(InputStream xml)
			throws XmlPullParserException, IOException {
		List<Province> provinces = null;
		Province province = null;
		XmlPullParser pullParser = Xml.newPullParser();
		// 为Pull解析器设置要解析的XML数据
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				provinces = new ArrayList<Province>();
				break;
			case XmlPullParser.START_TAG:
				if ("province".equals(pullParser.getName())) {
					int id = Integer.valueOf(pullParser.getAttributeValue(0));
					province = new Province();
					province.setId(id);

				}
				if ("name".equals(pullParser.getName())) {
					String proviceName = pullParser.nextText();
					province.setProviceName(proviceName);
				}
				break;
			case XmlPullParser.END_TAG:
				if ("province".equals(pullParser.getName())) {
					provinces.add(province);
					province = null;
				}
				break;

			}
			event = pullParser.next();
		}
		return provinces;
	}

	/**
	 * 查询城市和区县
	 * 
	 * @param xml
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static List<City> getCitys(InputStream xml)
			throws XmlPullParserException, IOException {
		List<City> citys = null;
		// List<String> countys = null;
		Map<String, String> countys = null;
		City city = null;
		XmlPullParser pullParser = Xml.newPullParser();
		// 为Pull解析器设置要解析的XML数据
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				citys = new ArrayList<City>();
				break;
			case XmlPullParser.START_TAG:
				if ("city".equals(pullParser.getName())) {
					String cityName = pullParser.getAttributeValue(0);
					city = new City();
					city.setCityName(cityName);
					// countys = new ArrayList<String>();
					countys = new HashMap<String, String>();
				}
				if ("county".equals(pullParser.getName())) {
					String code = pullParser.getAttributeValue(0);
					String county = pullParser.nextText();
					// countys.add(county);
					countys.put(code, county);
				}
				break;
			case XmlPullParser.END_TAG:
				if ("city".equals(pullParser.getName())) {
					city.setCountys(countys);
					citys.add(city);
					city = null;
					countys = null;
				}
				break;
			}
			event = pullParser.next();
		}

		return citys;
	}

	/**
	 * 根据省份查询对应的城市和区县
	 * 
	 * @param province
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static List<City> getCitysByProvince(Context context, String province)
			throws IOException, XmlPullParserException {

		InputStream cityXml = null;

		if (province.equals("北京")) {
			cityXml = context.getResources().getAssets()
					.open("city_beijing.xml");
		} else if (province.equals("上海")) {
			cityXml = context.getResources().getAssets()
					.open("city_shanghai.xml");
		} else if (province.equals("天津")) {
			cityXml = context.getResources().getAssets()
					.open("city_tianjin.xml");
		} else if (province.equals("重庆")) {
			cityXml = context.getResources().getAssets()
					.open("city_chongqing.xml");
		} else if (province.equals("黑龙江")) {
			cityXml = context.getResources().getAssets()
					.open("city_heilongjiang.xml");
		} else if (province.equals("吉林")) {
			cityXml = context.getResources().getAssets().open("city_jilin.xml");
		} else if (province.equals("辽宁")) {
			cityXml = context.getResources().getAssets()
					.open("city_liaoning.xml");
		} else if (province.equals("内蒙古")) {
			cityXml = context.getResources().getAssets()
					.open("city_neimenggu.xml");
		} else if (province.equals("河北")) {
			cityXml = context.getResources().getAssets().open("city_hebei.xml");
		} else if (province.equals("山西")) {
			cityXml = context.getResources().getAssets()
					.open("city_shanxi.xml");
		} else if (province.equals("陕西")) {
			cityXml = context.getResources().getAssets()
					.open("city_shanxi2.xml");
		} else if (province.equals("山东")) {
			cityXml = context.getResources().getAssets()
					.open("city_shandong.xml");
		} else if (province.equals("新疆")) {
			cityXml = context.getResources().getAssets()
					.open("city_xinjiang.xml");
		} else if (province.equals("西藏")) {
			cityXml = context.getResources().getAssets()
					.open("city_xizang.xml");
		} else if (province.equals("青海")) {
			cityXml = context.getResources().getAssets()
					.open("city_qinghai.xml");
		} else if (province.equals("甘肃")) {
			cityXml = context.getResources().getAssets().open("city_gansu.xml");
		} else if (province.equals("宁夏")) {
			cityXml = context.getResources().getAssets()
					.open("city_ningxia.xml");
		} else if (province.equals("河南")) {
			cityXml = context.getResources().getAssets().open("city_henan.xml");
		} else if (province.equals("江苏")) {
			cityXml = context.getResources().getAssets()
					.open("city_jiangsu.xml");
		} else if (province.equals("湖北")) {
			cityXml = context.getResources().getAssets().open("city_hubei.xml");
		} else if (province.equals("浙江")) {
			cityXml = context.getResources().getAssets()
					.open("city_zhejiang.xml");
		} else if (province.equals("安徽")) {
			cityXml = context.getResources().getAssets().open("city_anhui.xml");
		} else if (province.equals("福建")) {
			cityXml = context.getResources().getAssets()
					.open("city_fujian.xml");
		} else if (province.equals("江西")) {
			cityXml = context.getResources().getAssets()
					.open("city_jiangxi.xml");
		} else if (province.equals("湖南")) {
			cityXml = context.getResources().getAssets().open("city_hunan.xml");
		} else if (province.equals("贵州")) {
			cityXml = context.getResources().getAssets()
					.open("city_guizhou.xml");
		} else if (province.equals("四川")) {
			cityXml = context.getResources().getAssets()
					.open("city_sichuan.xml");
		} else if (province.equals("广东")) {
			cityXml = context.getResources().getAssets()
					.open("city_guangdong.xml");
		} else if (province.equals("云南")) {
			cityXml = context.getResources().getAssets()
					.open("city_yunnan.xml");
		} else if (province.equals("广西")) {
			cityXml = context.getResources().getAssets()
					.open("city_guangxi.xml");
		} else if (province.equals("海南")) {
			cityXml = context.getResources().getAssets()
					.open("city_hainan.xml");
		} else if (province.equals("香港")) {
			cityXml = context.getResources().getAssets()
					.open("city_xianggang.xml");
		} else if (province.equals("澳门")) {
			cityXml = context.getResources().getAssets().open("city_aomen.xml");
		} else if (province.equals("台湾")) {
			cityXml = context.getResources().getAssets()
					.open("city_taiwan.xml");
		}

		List<City> citys = getCitys(cityXml);

		return citys;
	}

}
