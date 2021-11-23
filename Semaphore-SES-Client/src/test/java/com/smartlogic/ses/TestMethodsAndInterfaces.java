package com.smartlogic.ses;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import junit.framework.TestCase;

/**
 * To ensure JAXB compliance all objects that are persisted should be serializable and have the
 * annotation XmlRootElement.
 * 
 * All get methods should have equivalent set methods and there should be a zero argument
 * constructor for each.
 * 
 * @author keitha
 *
 */

public class TestMethodsAndInterfaces extends TestCase {

  public final static Set<String> unserializableClasses = new HashSet<String>(
      Arrays.asList(new String[] { "SESClient", "SESClientRest", "SESFilter", "TermComparator" }));

  public final static Set<String> unXMLableClasses = new HashSet<String>(
      Arrays.asList(new String[] { "SESClient", "SESClientRest", "SESFilter", "TermComparator" }));

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void testClassBehavior() throws IOException, ClassNotFoundException {
    String[] javaNames =
        getClassNamesInDirectory(new File("src/main/java"), "com.smartlogic.ses.client");
    if (javaNames == null)
      return;

    for (String javaName : javaNames) {
      String className = javaName.substring(0, javaName.length() - 5);
      Class clazz = Class.forName("com.smartlogic.ses.client." + className);
      if (!Serializable.class.isAssignableFrom(clazz)) {
        if (!unserializableClasses.contains(className)) {
          fail(className + " is not serializable");
        }
      }

      XmlRootElement xmlRootElement = (XmlRootElement) clazz.getAnnotation(XmlRootElement.class);
      if (xmlRootElement == null) {
        if (!Modifier.isAbstract(clazz.getModifiers())) {
          if (!unXMLableClasses.contains(className)) {
            fail(className + " is not XMLable");
          } else {
            continue;
          }
        }
      }

      try {
        clazz.getConstructor(new Class[0]);
      } catch (NoSuchMethodException e) {
        fail(className + " has no zero argument constructor");
      }

      for (Method method : clazz.getMethods()) {
        if ((method.getName().startsWith("get")) && (method.getParameterTypes().length == 0)) {
          if (method.getName().equals("getClass"))
            continue;

          String setMethodName = "set" + method.getName().substring(3);
          Class getMethodReturnValue = method.getReturnType();
          try {
            clazz.getMethod(setMethodName, getMethodReturnValue);
          } catch (NoSuchMethodException e) {
            if (!method.isAnnotationPresent(XmlTransient.class)) {
              fail(className +
                  " method " +
                  method.getName() +
                  "has no equivalent set method: " +
                  method.getName());
            }
          }
        }
      }
    }
  }

  public static String[] getClassNamesInDirectory(File rootDirectory, String packageName)
      throws IOException {
    File folder = new File(rootDirectory, packageName.replace(".", "/"));
    return folder.list(new ClassFilenameFilter());
  }

  public static class ClassFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String pathname) {
      return pathname.endsWith("java");
    }
  }
}
