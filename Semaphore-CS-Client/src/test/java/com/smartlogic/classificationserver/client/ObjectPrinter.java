package com.smartlogic.classificationserver.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class ObjectPrinter {

	@SuppressWarnings("unchecked")
	public static String toString(Object inputObject) {
		System.out.println(inputObject);
		StringBuilder stringBuilder = new StringBuilder(inputObject.getClass().getCanonicalName() + "\n");
		for (Method method: inputObject.getClass().getMethods()) {
			System.out.println(method.getName());
			if (!(method.getName().startsWith("get"))) continue;
			if (!(method.getParameterTypes().length == 0)) continue;
			if ("getClass".equals(method.getName())) continue;
			
			try {
				Object object = method.invoke(inputObject, new Object[0]);
				if (object == null) {
					stringBuilder.append(method.getName().substring(3) + ":<NULL>\n");					
				} else if (object instanceof String) {
					stringBuilder.append(method.getName().substring(3) + ":" + object + "\n");
				} else if (object instanceof Integer) {
					stringBuilder.append(method.getName().substring(3) + ":" + object + "\n");
				} else if (object instanceof Long) {
					stringBuilder.append(method.getName().substring(3) + ":" + object + "\n");
				} else if (object instanceof Float) {
					stringBuilder.append(method.getName().substring(3) + ":" + object + "\n");
				} else if (object instanceof Double) {
					stringBuilder.append(method.getName().substring(3) + ":" + object + "\n");
				} else if (object instanceof Collection) {
					stringBuilder.append(method.getName().substring(3) + "\n");
					for (Object subObject: (Collection<? extends Object>)object) {
						stringBuilder.append("    " + toString(subObject) + "\n");
					}
				} else {
					stringBuilder.append(method.getName().substring(3) + ":" + toString(object) + "\n");
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			
		}
		return stringBuilder.toString();
	}

}
