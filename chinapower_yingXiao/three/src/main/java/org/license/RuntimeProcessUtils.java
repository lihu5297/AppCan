package  org.license;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class RuntimeProcessUtils {
	private static final String MAC_REGEX = "(\\w){2}-(\\w){2}-(\\w){2}-(\\w){2}-(\\w){2}-(\\w){2}";

	public static int processCmdLine(String cmdLine) {
		int success = -1;

		Runtime rt = Runtime.getRuntime();

		Process process = null;
		try {
			process = rt.exec(cmdLine);
			success = process.waitFor();
		} catch (IOException e) {
			throw new RuntimeException("执行Process出错, cmd = " + cmdLine, e);
		} catch (InterruptedException e) {
			throw new RuntimeException("执行Process出错, cmd = " + cmdLine, e);
		}

		return success;
	}
	
	public static String getOSName() {
		return System.getProperty("os.name").toLowerCase();
	}

	public static String trimCharacter(String str) {
		if (str == null)
			return ":";
		return str.trim().replace("-", ":");
	}

	
	public static String getMacAddress(){
		String mac = "";
		Set<String> macSet = getMACAddressSet();
		if(macSet!=null && macSet.toArray()!=null 
				&& macSet.toArray().length>0 && macSet.toArray()[0]!=null){
			mac = macSet.toArray()[0].toString();
		}
		return mac;
	}
	
	
	
	public static Set<String> getMACAddressSet() {
		Set<String> mac = new HashSet<String>();
		if (getOSName().startsWith("windows")) {
			mac = getWindowsMACAddress();
		} else {
			mac = getLinuxMACAddress();
			if (mac.size() == 0) {
				mac = getUnixMACAddress();
			}
		}
		return mac;
	}

	private static Set<String> getUnixMACAddress() {
		Set<String> mac = new HashSet<String>();
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("netstat -in");

			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.replaceAll("\\.", "-");
				if (line.split(MAC_REGEX).length > 1) {
					int index = line
							.split(MAC_REGEX)[0]
							.length();
					if (line.length() >= index + 17)
						;
					mac.add(trimCharacter(line.substring(index, index + 17))
							.toLowerCase());
				}
			}
		} catch (IOException e) {
			new RuntimeException("获取MAC出错", e);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e1) {
				new RuntimeException("获取MAC出错", e1);
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}
	
	//获取Unix系统网卡名称
	private static Set<String> getUnixMACNameSet() {
		Set<String> macNameSet = new HashSet<String>();
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("netstat -in");
			
			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			String macName = null;
			while ((line = bufferedReader.readLine()) != null) {
				String content = new String(line.getBytes());
				if(content.trim().equals(""))
					continue;
				if(!content.startsWith(" ")){
					macName = content.substring(0, content.indexOf(" "));
					if(macName.equals("Name"))
						continue;
					macNameSet.add(macName);
				}
			}
		} catch (IOException e) {
			new RuntimeException("获取MAC出错", e);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e1) {
				new RuntimeException("获取MAC出错", e1);
			}
			bufferedReader = null;
			process = null;
		}
		
		return macNameSet;
	}

	private static Set<String> getLinuxMACAddress() {
		Set<String> ipSet = new HashSet<String>();
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("ifconfig");
			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				index = line.toLowerCase().indexOf("hwaddr");
				if (index >= 0)
					ipSet.add(trimCharacter(
							line.substring(index + "hwaddr".length() + 1)
									.trim()).toLowerCase());
			}
		} catch (IOException e) {
			new RuntimeException("获取MAC出错", e);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e1) {
				new RuntimeException("获取MAC出错", e1);
			}
			bufferedReader = null;
			process = null;
		}

		return ipSet;
	}

	private static Set<String> getWindowsMACAddress() {
		Set<String> ipSet = new HashSet<String>();
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("cmd.exe /c ipconfig /all");
			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream(), "gbk"));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				if (null != line)
					index = line.toLowerCase().indexOf("physical address");
				if (index < 0) {
					index = line.toLowerCase().indexOf("物理地址");
				}
				if (index >= 0) {
					index = line.indexOf(":");
					if (index >= 0) {
						if (line.substring(index + 1).trim().length() > 17)
							continue;
						ipSet.add(trimCharacter(
								line.substring(index + 1).trim()).toLowerCase());
					}
				}
			}
		} catch (IOException e) {
			new RuntimeException("获取MAC出错", e);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e1) {
				new RuntimeException("获取MAC出错", e1);
			}
			bufferedReader = null;
			process = null;
		}

		return ipSet;
	}

	/**
	 * 获取ip地址，linux或unix系统，首先获取eth0的IP
	 * 如果ip绑定的的网卡不是eth0,则获取网卡集合进行遍历，并获得其对应ip
	 * @return
	 */
	public static Set<String> getIPAddress() {
		String ip = "";
		String mac = null;
		Set<String>ips=new HashSet<String>();
		Set<String> macNameSet = new HashSet<String>();
		if (getOSName().startsWith("windows")) {
			ip = getWidnowsLocalIP();
		} else {
			ip= getOtherLocalIP("eth0");
			ips.add( getOtherLocalIP("eth0").replaceAll(" ",""));
//			if(ip==""||ip==null){
				//linux或unix系统上，如果ip绑定的的网卡不是eth0,则获取网卡集合进行遍历，并获得其对应ip
				macNameSet = getXMACNameSet();
				Iterator<String> it = macNameSet.iterator();
				while(it.hasNext()){
		            mac = (String) it.next();
		            if(mac==null||mac.equals("")||mac.equals("eth0"))
		            	continue;
		            ip = getOtherLocalIP(mac);
		            ips.add( getOtherLocalIP(mac).replaceAll(" ",""));
		            
		            if(ip==null||ip==""||ip=="localhost"||ip=="127.0.0.1")
		            	continue;
				}
			}
//		}
		return ips;
	}
	
	private static Set<String> getXMACNameSet(){
		Set<String> macNameSet = new HashSet<String>();
		if(getOSName().startsWith("linux")){
			macNameSet = getLinuxMACNameSet();
		}
		if(getOSName().startsWith("unix")){
			macNameSet = getUnixMACNameSet();
		}
		return macNameSet;
	}

	private static String getWidnowsLocalIP() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (ia == null) {
			return "some error..";
		}
		return ia.getHostAddress();
	}

	public static Set<String> getIPSet() {
		return getOtherLocalIPSet();
	}

	private static String getOtherLocalIP(String netName) {
		String ip = "";
		try {
			Enumeration<NetworkInterface> e1 = NetworkInterface
					.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				if (netName.equals(ni.getName())) {
					Enumeration<InetAddress> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ia = (InetAddress) e2.nextElement();
						if (ia instanceof Inet4Address) {
							ip = ia.getHostAddress();
							break;
						}
					}
				}
			}
		} catch (SocketException e) {
			new RuntimeException("获取IP出错", e);
			System.exit(-1);
		}
		return ip;
	}

	private static Set<String> getOtherLocalIPSet() {
		Set<String> ipSet = new HashSet<String>();
		try {
			Enumeration<NetworkInterface> e1 = NetworkInterface
					.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				Enumeration<InetAddress> e2 = ni.getInetAddresses();
				while (e2.hasMoreElements()) {
					InetAddress ia = (InetAddress) e2.nextElement();
					if (ia instanceof Inet6Address)
						continue;
					ipSet.add(ia.getHostAddress());
				}
			}
		} catch (SocketException e) {
			new RuntimeException("获取IP出错", e);
			System.exit(-1);
		}
		return ipSet;
	}
    //linux系统获取网卡名称   add by haojie at 2014.02.10
	private static Set<String> getLinuxMACNameSet() {
		Set<String> macNameSet = new HashSet<String>();
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("ifconfig");
			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			String macName = null;
			while ((line = bufferedReader.readLine()) != null) {
				String content = new String(line.getBytes());
				if(content.trim().equals(""))
					continue;
				if(!content.startsWith(" ")){
					macName = content.substring(0, content.indexOf(" "));
					macNameSet.add(macName);
				}
			}
		} catch (IOException e) {
			new RuntimeException("获取MAC出错", e);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e1) {
				new RuntimeException("获取MAC出错", e1);
			}
			bufferedReader = null;
			process = null;
		}

		return macNameSet;
	}
	
	public static void main(String[] args) {
		System.out.println("linux macNameSet:"+RuntimeProcessUtils.getLinuxMACNameSet());
		System.out.println("unix macNameSet:"+RuntimeProcessUtils.getUnixMACNameSet());
		System.out.println("ip:" + RuntimeProcessUtils.getIPAddress());
		System.out.println("mac:" + RuntimeProcessUtils.getMacAddress());
	}

}
