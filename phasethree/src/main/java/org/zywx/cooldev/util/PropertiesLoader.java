	/**  
     * @author jingjian.wu
     * @date 2015年10月9日 下午4:01:31
     */
    
package org.zywx.cooldev.util;

import java.util.Properties;


    /**
 * @author jingjian.wu
 * @date 2015年10月9日 下午4:01:31
 */

public class PropertiesLoader {

	private static final String PROPERTY_LOCATION = "cooldev.properties";
	
	public static String getText(String key){
		 String keyStr=null;
		  try {
		   Properties props = new Properties(); 
		   props.load(PropertiesLoader.class.getClassLoader().getResourceAsStream(PROPERTY_LOCATION));
		   /*if(props.getProperty("envFlag").equals("test")){
			   props = new Properties(); 
			   props.load(PropertiesLoader.class.getClassLoader().getResourceAsStream("test_cooldev.properties"));
		   }*/
		   keyStr=props.getProperty(key);
		  } catch (Exception e) {
		   e.printStackTrace();
		  }	  
		  return keyStr; 
	 }
	
	public static String getText(String key,String fileName){
		 String keyStr=null;
		  try {
		   Properties props = new Properties(); 
		   props.load(PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName));
		   keyStr=props.getProperty(key);
		  } catch (Exception e) {
		   e.printStackTrace();
		  }	  
		  return keyStr; 
	 }
}
