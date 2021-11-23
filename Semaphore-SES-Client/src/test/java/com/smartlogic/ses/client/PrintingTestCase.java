package com.smartlogic.ses.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class PrintingTestCase extends SESServerMockTestCase {
  @SuppressWarnings({ "rawtypes" })
  public void print(Object object) {

    if (object == null) {
      System.out.println("Printing class: Null class");
      return;
    }

    System.out.println("Printing class: " + object.getClass().getCanonicalName());

    if ("java.lang.String".equals(object.getClass().getCanonicalName())) {
      System.out.println("String");
      System.out.println(object);
      return;
    }
    if ("java.util.TreeSet".equals(object.getClass().getCanonicalName())) {
      System.out.println("TreeSet");
      for (Object key : ((Collection) object)) {
        System.out.println("VALUE:");
        print(key);

      }
      return;
    }
    if (("java.util.HashMap".equals(object.getClass().getCanonicalName())) ||
        ("java.util.TreeMap".equals(object.getClass().getCanonicalName()))) {
      System.out.println("TreeMap");
      for (Object key : ((Map) object).keySet()) {
        System.out.println("KEY:");
        print(key);
        System.out.println("VALUE:");
        print(((Map) object).get(key));

      }
      return;
    }

    for (Method method : object.getClass().getMethods()) {
      if (method.getName().startsWith("get")) {
        if (method.getName().equals("getModifiers"))
          continue;
        if (method.getName().equals("getSuperclass"))
          continue;
        if (method.getName().equals("getClass"))
          continue;

        if (method.getReturnType().getCanonicalName().equals("java.lang.String") ||
            method.getReturnType().getCanonicalName().equals("int") ||
            method.getReturnType().getCanonicalName().equals("float")) {
          try {
            System.out.println(object.getClass().getName() +
                ":\t\t" +
                method.getName() +
                ":\t\t" +
                method.invoke(object, new Object[] {}));
          } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " +
                object.getClass().getName() +
                ":\t\t" +
                method.getName() +
                ":\t\t" +
                e.getMessage());
          } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException: " +
                object.getClass().getName() +
                ":\t\t" +
                method.getName() +
                ":\t\t" +
                e.getMessage());
          } catch (InvocationTargetException e) {
            System.out.println("InvocationTargetException: " +
                object.getClass().getName() +
                "\t\t:" +
                method.getName() +
                ":\t\t" +
                e.getMessage());
          }
        } else if (method.getReturnType().getCanonicalName().equals("java.util.List")) {
          try {
            List arrayList = (List) method.invoke(object, new Object[] {});
            for (Object arrayObject : arrayList) {
              if (arrayObject.getClass().getCanonicalName().equals("java.lang.String")) {
                System.out.println(object.getClass().getName() +
                    ":\t\t" +
                    method.getName() +
                    ":\t\t" +
                    arrayObject);
              } else {
                print(arrayObject);
              }
            }
          } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " +
                object.getClass().getName() +
                ":\t\t" +
                method.getName() +
                ":\t\t" +
                e.getMessage());
          } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException: " +
                object.getClass().getName() +
                ":\t\t" +
                method.getName() +
                ":\t\t" +
                e.getMessage());
          } catch (InvocationTargetException e) {
            System.out.println("InvocationTargetException: " +
                object.getClass().getName() +
                ":\t\t" +
                method.getName() +
                ":\t\t" +
                e.getMessage());
          }
        } else {
          try {
            print(method.invoke(object, new Object[] {}));
          } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " +
                object.getClass().getName() +
                ":\t\t" +
                method.getName() +
                ":\t\t" +
                e.getMessage());
          } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException: " +
                object.getClass().getName() +
                ":\t\t" +
                method.getName() +
                ":\t\t" +
                e.getMessage());
          } catch (InvocationTargetException e) {
            System.out.println("InvocationTargetException: " +
                object.getClass().getName() +
                ":\t\t" +
                method.getName() +
                ":\t\t" +
                e.getMessage());
          }
        }
      }
    }
  }
}
