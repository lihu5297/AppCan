package org.zywx.cooldev.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReflectUtil {

	private static Log log = LogFactory.getLog(ReflectUtil.class.getName());
	/**
	 * 获取object的属性method
	 * @param object
	 * @return
	 */
	public static List<java.lang.reflect.Method> getMethod(Object object) {
		List<java.lang.reflect.Method> lt = new ArrayList<java.lang.reflect.Method>();
		java.lang.reflect.Method[] methods = object.getClass().getDeclaredMethods();
		for (java.lang.reflect.Method m : methods) {
			//log.info(m.getName());
			lt.add(m);
		}
		return lt;
	}

	/**
	 * 获取Object的属性Field
	 * @param object
	 * @return
	 */
	public static List<java.lang.reflect.Field> getField(Object object) {
		List<java.lang.reflect.Field> lt = new ArrayList<java.lang.reflect.Field>();
		java.lang.reflect.Field[] fields = object.getClass().getDeclaredFields();
		for (java.lang.reflect.Field f : fields) {
			//log.info(f.getName());
			lt.add(f);
		}
		return lt;
	}
	
	/**
	 * 通过反射,设置某个对象的值
	 * @param object 对象
	 * @param fieldName  属性
	 * @param value    值
	 * 
	 * User u = new User();
		ReflectUtil.invokeSetMethod(u, "userlevel","ADVANCE");
		System.out.println(u.getUserlevel());
	 */
	public static void invokeSetMethod(Object object,String fieldName,Object value) {
		Class fieldType = null;
		List<Method> listMethod = ReflectUtil.getMethod(object);
		List<Field> listField = ReflectUtil.getField(object);
		for(Field f:listField){
			if(f.getName().equals(fieldName)){
				fieldType = f.getType();
			}
		}
		//log.info(fieldType.getName());
		String setMethodName = "set"+Character.toTitleCase(fieldName.charAt(0))+fieldName.substring(1);
		for(Method md:listMethod){
			if(md.getName().equals(setMethodName)){
				try {
					if(fieldType.getName().equals(String.class.getName())){
						md.invoke(object, value);
					}else if(fieldType.getName().equals(Long.class.getName())||fieldType.getName().equals("long")){
						md.invoke(object, Long.parseLong(value.toString()));
						
					}else if(fieldType.getName().equals(Integer.class.getName())||fieldType.getName().equals("int")){
						md.invoke(object, Integer.parseInt(value.toString()));
					}else if(fieldType.getName().startsWith("org.zywx.cooldev.commons.Enums$")){
						String enumType = fieldType.getName().substring(fieldType.getName().indexOf("$")+1);
						Class clazz=Class.forName(fieldType.getName());
						Field field=object.getClass().getDeclaredField(fieldName);
						field.setAccessible(true);
						field.set(object, Enum.valueOf(clazz, value.toString()));
					}else if(fieldType.getName().equals(Date.class.getName())){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							md.invoke(object,sdf.parse(value.toString()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
//					md.invoke(object, value);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
