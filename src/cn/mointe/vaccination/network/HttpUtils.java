package cn.mointe.vaccination.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import cn.mointe.vaccination.tools.Log;

public class HttpUtils {

	public static final int REQUEST_TIMEOUT = 3000;// 请求超时时间
	public static final int SO_TIMEOUT = 3000;// 等待数据超时时间

	public HttpUtils() {

	}

	public static String get(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		String result = null;
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils
						.toString(httpResponse.getEntity(), "UTF-8");
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e("MainActivity", e.getMessage().toString());
		} catch (IOException e) {
			Log.e("MainActivity", e.getMessage().toString());
			e.printStackTrace();
		}
		return result;
	}

	public static String post(String url) {
		String result = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("wd", "百白破疫苗"));
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(list, "UTF-8");
			httpPost.setEntity(httpEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(httpResponse.getEntity());
			} else {
				Log.i("MainActivity", "状态码："
						+ httpResponse.getStatusLine().getStatusCode());
				result = String.valueOf(httpResponse.getStatusLine()
						.getStatusCode());

			}
		} catch (UnsupportedEncodingException e) {
			Log.e("MainActivity", e.getMessage().toString());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e("MainActivity", e.getMessage().toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("MainActivity", e.getMessage().toString());
			e.printStackTrace();
		}
		return result;
	}
}
