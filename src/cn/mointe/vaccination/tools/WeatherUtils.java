package cn.mointe.vaccination.tools;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class WeatherUtils {

	public static String getWeatherByCityId(String cityId)
			throws ClientProtocolException, IOException {
		// 穿衣指数
		// http://mobile.weather.com.cn/data/zsM/101010100.html
		String weatherResult = "";
		String url = "http://mobile.weather.com.cn/data/sk/" + cityId + ".html";
		HttpGet request = new HttpGet(url);
		request.setHeader("Referer", "http://mobile.weather.com.cn/");
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(request);
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			weatherResult = EntityUtils.toString(httpResponse.getEntity());
		}
		return weatherResult;
	}
}
