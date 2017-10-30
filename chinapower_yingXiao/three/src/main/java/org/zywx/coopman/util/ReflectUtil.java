package org.zywx.coopman.util;

import java.util.ArrayList;
import java.util.List;

public class ReflectUtil {

	/**
	 * 获取object的属性method
	 * @param object
	 * @return
	 */
	public List<java.lang.reflect.Method> getMethod(Object object) {
		List<java.lang.reflect.Method> lt = new ArrayList<java.lang.reflect.Method>();
		java.lang.reflect.Method[] methods = object.getClass().getDeclaredMethods();
		for (java.lang.reflect.Method m : methods) {
			System.out.println(m.getName());
			lt.add(m);
		}
		if(null!=object.getClass().getSuperclass()){
			java.lang.reflect.Method[] methods1 = object.getClass().getSuperclass().getDeclaredMethods();
			for (java.lang.reflect.Method f : methods1) {
				System.out.println(f.getName());
				lt.add(f);
			}
		}
		return lt;
	}

	/**
	 * 获取Object的属性Field
	 * @param object
	 * @return
	 */
	public List<java.lang.reflect.Field> getField(Object object) {
		List<java.lang.reflect.Field> lt = new ArrayList<java.lang.reflect.Field>();
		java.lang.reflect.Field[] fields = object.getClass().getDeclaredFields();
		for (java.lang.reflect.Field f : fields) {
			lt.add(f);
		}
		
		if(null!=object.getClass().getSuperclass()){
			java.lang.reflect.Field[] fields1 = object.getClass().getSuperclass().getDeclaredFields();
			for (java.lang.reflect.Field f : fields1) {
				System.out.println(f.getName());
				lt.add(f);
			}
		}
		return lt;
	}
}
