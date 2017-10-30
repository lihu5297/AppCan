package org.zywx.cooldev.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class NewGitHttpUtil {


	public static String httpPostWithJSON(String url, String json,Map<String,String> headers) throws Exception {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		if(null!=headers){
			Iterator it= headers.keySet().iterator();
			while(it.hasNext()){
				String key = (String) it.next();
				httpPost.addHeader(key, headers.get(key));
			}
		}

		StringEntity se = new StringEntity(json,"UTF-8");
		httpPost.setEntity(se);
		CloseableHttpResponse response = httpClient.execute(httpPost);
		try {
		    HttpEntity resEntity = response.getEntity();
		    return EntityUtils.toString(resEntity);

		} finally {
		    response.close();
		}
	}

	
	public static String delete(String urlParams,Map<String,String> headers){
		URL url = null;
		try {
		    url = new URL(urlParams);
		} catch (MalformedURLException exception) {
		    exception.printStackTrace();
		}
		HttpURLConnection httpURLConnection = null;
		try {
		    httpURLConnection = (HttpURLConnection) url.openConnection();
		    httpURLConnection.setRequestProperty("Content-Type",
		                "application/x-www-form-urlencoded");
		    httpURLConnection.setRequestMethod("DELETE");
		    httpURLConnection.setDoInput(true);
		    if(null!=headers){
				Iterator it= headers.keySet().iterator();
				while(it.hasNext()){
					String key = (String) it.next();
					httpURLConnection.setRequestProperty(key, headers.get(key));
				}
			}
		    
		    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		    return sb.toString();
		} catch (IOException exception) {
		    exception.printStackTrace();
		    return null;
		} finally {         
		    if (httpURLConnection != null) {
		        httpURLConnection.disconnect();
		    }
		} 
	}
	
	public static String get(String urlParams,Map<String,String> headers){
		URL url = null;
		try {
		    url = new URL(urlParams);
		} catch (MalformedURLException exception) {
		    exception.printStackTrace();
		}
		HttpURLConnection httpURLConnection = null;
		try {
		    httpURLConnection = (HttpURLConnection) url.openConnection();
		    httpURLConnection.setRequestProperty("Content-Type",
		                "application/x-www-form-urlencoded");
		    httpURLConnection.setRequestMethod("GET");
		    httpURLConnection.setDoInput(true);
		    if(null!=headers){
				Iterator it= headers.keySet().iterator();
				while(it.hasNext()){
					String key = (String) it.next();
					httpURLConnection.setRequestProperty(key, headers.get(key));
				}
			}
		    
		    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		    return sb.toString();
		} catch (IOException exception) {
		    exception.printStackTrace();
		    return null;
		} finally {         
		    if (httpURLConnection != null) {
		        httpURLConnection.disconnect();
		    }
		} 
	}
	
	public static String put(String urlParams,Map<String,String> headers){
		URL url = null;
		try {
		   url = new URL(urlParams);
		} catch (MalformedURLException exception) {
		    exception.printStackTrace();
		}
		HttpURLConnection httpURLConnection = null;
		DataOutputStream dataOutputStream = null;
		try {
		    httpURLConnection = (HttpURLConnection) url.openConnection();
		    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		    httpURLConnection.setRequestMethod("PUT");
		    httpURLConnection.setDoInput(true);
//		    httpURLConnection.setDoOutput(true);
//		    dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
//		    dataOutputStream.writeUTF("hello");
		    if(null!=headers){
				Iterator it= headers.keySet().iterator();
				while(it.hasNext()){
					String key = (String) it.next();
					httpURLConnection.setRequestProperty(key, headers.get(key));
				}
			}
		    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			System.out.println("put-result:"+sb);
			return sb.toString();
		} catch (IOException exception) {
		    exception.printStackTrace();
		    return null;
		}  finally {
		    if (dataOutputStream != null) {
		        try {
		            dataOutputStream.flush();
		            dataOutputStream.close();
		        } catch (IOException exception) {
		            exception.printStackTrace();
		        }
		    }
		    if (httpURLConnection != null) {
		    	httpURLConnection.disconnect();
		    }
		}
	}
	// 接收HTTPPost中的JSON:
	public static String receivePost(HttpServletRequest request) throws IOException, UnsupportedEncodingException {

		// 读取请求内容
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		// 将资料解码
		String reqBody = sb.toString();
		return URLDecoder.decode(reqBody, HTTP.UTF_8);
	}
	
	
	
	
}
