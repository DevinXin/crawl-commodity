package com.devinxin.crawl.utils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final  class HttpClientUtil {


	private static final String CHARSET_UTF8 = "UTF-8";

	private static final int TIME = 30*1000;

	private static final int MAXCONNTOTAL = 500;
	private static final int MAXCONNPERROUTE = 500;

	private static final RequestConfig REQUESTCONFIG;

	private static final CloseableHttpClient HTTPCLIENT;

	static {
		REQUESTCONFIG = RequestConfig.custom().setSocketTimeout(TIME).setConnectTimeout(TIME)
				.setConnectionRequestTimeout(TIME).build();
		HTTPCLIENT = HttpClients.custom().setDefaultRequestConfig(REQUESTCONFIG).setMaxConnPerRoute(MAXCONNPERROUTE).setMaxConnTotal(MAXCONNTOTAL).build();
	}

 	private static String doPost(String url, String requestBody, Map<String, String> requestParams,String contentType) {
		CloseableHttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		String result = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			if(StringUtils.isNotBlank(contentType)){
				httpPost.setHeader("Content-Type", contentType);
			}
			if (StringUtils.isNotBlank(requestBody)) {
				httpEntity = new StringEntity(requestBody, CHARSET_UTF8);
				httpPost.setEntity(httpEntity);
			}

			if (null != requestParams && requestParams.size() > 0) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : requestParams.entrySet()) {
					nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, CHARSET_UTF8));
			}

			httpResponse = HTTPCLIENT.execute(httpPost);
			if (null != httpResponse) {
				httpEntity = httpResponse.getEntity();
				if(null != httpEntity){
					result = EntityUtils.toString(httpEntity, CHARSET_UTF8);
				}
			} else {
			}

		} catch (Exception e) {
			throw new RuntimeException("HttpClientUtil doPost occur Exception !" ,e);
		} finally {
			try {
				EntityUtils.consume(httpEntity);
			} catch (IOException e) {
				e.printStackTrace();
			}
			close(httpResponse);
		}
		return result;
	}

	public static String doPost(String url, String requestBody,String contentType) {
		return doPost(url, requestBody, null,contentType);
	}

	public static String doPost(String url, Map<String, String> requestParams,String contentType) {
		return doPost(url, null, requestParams,contentType);
	}

	public static String doGet(String url, Map<String, ? extends Object> requestParams,String contentType,String cookie) {
		CloseableHttpResponse httpResponse = null;
		String result = null;
		HttpGet httpGet = null;
		HttpEntity responseEntity = null;
		try {

			if (null != requestParams && requestParams.size() > 0) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				for (Map.Entry<String, ? extends Object> entry : requestParams.entrySet()) {
					Object entryValue = entry.getValue();
					String value = entryValue instanceof String?(String) entryValue: JSON.toJSONString(entryValue);
					BasicNameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), value);
					nameValuePairs.add(nameValuePair);
				}
				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs,CHARSET_UTF8);
				String params = EntityUtils.toString(urlEncodedFormEntity, CHARSET_UTF8);
				httpGet = new HttpGet(url + "?" + params);
			} else {
				httpGet = new HttpGet(url);
			}
			if(StringUtils.isNotBlank(contentType)){
				httpGet.setHeader("Content-Type", contentType);
			}

			if(StringUtils.isNotBlank(cookie)){
				httpGet.setHeader("cookie",cookie);
			}

			httpResponse = HTTPCLIENT.execute(httpGet);

			responseEntity = httpResponse.getEntity();
			if (null != responseEntity) {
				result = EntityUtils.toString(responseEntity, CHARSET_UTF8);
			}

		} catch (Exception e) {
		} finally {
			try {
				EntityUtils.consume(responseEntity);
			} catch (IOException e) {
				e.printStackTrace();
			}
			close(httpResponse);
		}
		return result;
	}

	public static String doGet(String url,Map<String, String> requestParams) {
		return doGet(url,requestParams,null,null);
	}

	public static String doGet(String url,String contentType) {
		return doGet(url,null,contentType,null);
	}
	public static String doGet(String url) {
		return doGet(url,null,null,null);
	}

	public static String doGetWithCookie(String url,String cookie) {
		return doGet(url,null,null,cookie);
	}

	private static void close(CloseableHttpResponse httpResponse) {
		try {
			if (null != httpResponse) {
				httpResponse.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
