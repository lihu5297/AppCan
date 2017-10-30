/**  
 * @author jingjian.wu
 * @date 2015年9月16日 下午6:42:06
 */

package org.zywx.coopman.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * @author jingjian.wu
 * @date 2015年9月16日 下午6:42:06
 */

public class HttpTools {
	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是name1=value1&name2=value2的形式。
	 * @return URL所代表远程资源的响应
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlName = url;
			if(null!=param && !"".equals(param)){
				urlName += "?" + param;
			}
			URL realUrl = new URL(urlName);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 建立实际的连接
			conn.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = conn.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += "/n" + line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定URL发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是name1=value1&name2=value2的形式。
	 * @return URL所代表远程资源的响应
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result +=  line;
			}
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	

	/**
	 * 
	 * @describe 登录发送get请求	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午9:58:12	<br>
	 * @param url
	 * @return  <br>
	 * @returnType String
	 *
	 */
	public static String get(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			// 创建httpget.
			HttpGet httpget = new HttpGet(url);
			System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				String res = null;
				System.out.println("--------------------------------------");
				// 打印响应状态
				System.out.println(response.getStatusLine());
				if (entity != null) {
					// 打印响应内容长度
					System.out.println("Response content length: " + entity.getContentLength());
					res = EntityUtils.toString(entity);
					// 打印响应内容
					System.out.println("Response content: " + res);

				}
				System.out.println("------------------------------------");
				return res;
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @describe 解析XML数据	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午9:58:38	<br>
	 * @param xmlDoc
	 * @return  <br>
	 * @returnType List<org.jdom2.Element>
	 *
	 */
	public static List<org.jdom2.Element> xmlElements(String xmlDoc) {
        //创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        //创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        List<org.jdom2.Element>  el =  new ArrayList<>();
        try {
            //通过输入源构造一个Document
        	org.jdom2.Document doc = sb.build(source);
            //取的根元素
            org.jdom2.Element root = doc.getRootElement();
            System.out.println(root.getName());//输出根元素的名称（测试）
            //得到根元素所有子元素的集合
            List<org.jdom2.Element> level1 = root.getChildren();
            //获得XML中的命名空间（XML中未定义可不写）
            Namespace ns = root.getNamespace();
            getAllElement(el,level1);
        } catch (JDOMException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
        return el;
    }
	
	/**
	 * 
	 * @describe 获取所有XML数据节点	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午9:59:03	<br>
	 * @param els
	 * @param level1  <br>
	 * @returnType void
	 *
	 */
	private static void getAllElement(List<org.jdom2.Element> els, List<org.jdom2.Element> level1) {
		for(int i=0;i<level1.size();i++){
			org.jdom2.Element et = (org.jdom2.Element) level1.get(i);//循环依次得到子元素
			if(et.getChildren().size()<1 ||et.getChildren().get(0).getChildren()==null){
				els.add(et);
				System.out.println(et.getName()+":"+et.getText());
				continue;
			}
            List<org.jdom2.Element> level2 = et.getChildren();
            getAllElement(els,level2);
        }
		
	}

	/**
	 * 
	 * @describe 获取单个XML数据节点	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午9:59:25	<br>
	 * @param strResult
	 * @return
	 * @throws DocumentException  <br>
	 * @returnType HashMap<String,String>
	 *
	 */
	public static HashMap<String,String> getElement(String strResult) throws DocumentException{
		HashMap<String,String> resMap = new HashMap<>();
		StringReader read = new StringReader(strResult);
		SAXReader reader = new SAXReader();
		Document doc = reader.read(read);
		Element root = doc.getRootElement();
		List<HashMap<String,Object>> list =  new ArrayList<>();
		readNode(list,root, "");
		for(Map<?,?> map :list){
			String str="";
			Set<?> set = map.keySet();
			Iterator<?> it = set.iterator();
			while(it.hasNext()){
				str = it.next().toString();
			}
			String res = (String) map.get(str);
			resMap.put(str, res);
		}
		return resMap;
	}
	
	/**
	 * 
	 * @describe 遍历读取节点	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午10:00:15	<br>
	 * @param list
	 * @param root
	 * @param prefix  <br>
	 * @returnType void
	 *
	 */
	private static void readNode(List<HashMap<String, Object>> list,Element root, String prefix) {
		if (root == null)
			return;
		// 获取属性
		@SuppressWarnings("unchecked")
		List<Attribute> attrs = root.attributes();
		if (attrs != null && attrs.size() > 0) {
			System.err.print(prefix);
			for (Attribute attr : attrs) {
				System.err.print(attr.getValue() + " ");
				HashMap<String,Object> map = new HashMap<>();
				map.put(attr.getName(), attr.getValue());
				list.add(map);
			}
			System.err.println();
		}
		// 获取他的子节点
		@SuppressWarnings("unchecked")
		List<Element> childNodes = root.elements();
		prefix += "\t";
		for (Element e : childNodes) {
			readNode(list,e, prefix);
		}

	}
	
	
	
	public static String doPost(String url,Map<String,String> map,String charset){
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try{
			httpClient = new SSLClient();
			httpPost = new HttpPost(url);
			//设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator iterator = map.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String,String> elem = (Entry<String, String>) iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
			}
			if(list.size() > 0){
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
				httpPost.setEntity(entity);
			}
			HttpResponse response = httpClient.execute(httpPost);
			if(response != null){
				HttpEntity resEntity = response.getEntity();
				if(resEntity != null){
					result = EntityUtils.toString(resEntity,charset);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
	}

}
