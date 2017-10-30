package org.zywx.coopman.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectValues {

	/**
	 * 
	 * @describe 给对象中的属性赋值：将object中非空的属性值赋给objectCopy（object和objectCopy是同类型的对象,
	 *           否则直接返回object） <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年9月28日 下午12:01:54 <br>
	 * @param object
	 * @param objectCopy
	 * @return
	 * @throws Exception
	 *             <br>
	 * @returnType T
	 *
	 */
	public static <T> T SetValueFromTo(T object, T objectCopy) {
		Class<?> classType = object.getClass();
		Class<?> classType1 = objectCopy.getClass();
		List<Field> fields = new ReflectUtil().getField(object);
		List<Method> methods = new ReflectUtil().getMethod(object);
		if (classType.equals(classType1)) {
			for (Field field : fields) {
				for (Method method : methods) {
					String fieldName = field.getName();
					Object value = null;
					// 获得属性的首字母并转换为大写，与setXXX和getXXX对应
					String firstLetter = fieldName.substring(0, 1).toUpperCase();
					String getMethodName = "get" + firstLetter + fieldName.substring(1);
					Method getMethod = null;
					try {
						if(!method.getName().toLowerCase().contains(field.getName().toLowerCase())){
							continue;
						}
						getMethod = classType.getMethod(getMethodName);
						value = getMethod.invoke(object);// 调用对象的getXXX方法
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
						if (null == getMethod) {
							continue;
						}
					}
					//为空的字段就不给赋值
					if (null == value) {
						continue;
					}
					String setMethodName = "set" + firstLetter + fieldName.substring(1);
					Method setMethod;
					try {
						setMethod = classType.getMethod(setMethodName, new Class[] { field.getType() });
						setMethod.invoke(objectCopy, new Object[] { value });// 调用对象的setXXX方法
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		} else
			return object;
		return objectCopy;
	}

}
