package com.shootr.mobile.util;

import com.shootr.mobile.domain.model.poll.Poll;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.user.User;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class MergeUtils {

  @SuppressWarnings("unchecked")
  public static <T> T mergeObjects(T from, T to) throws IllegalAccessException, InstantiationException {
    Class<?> clazz = from.getClass();
    Field[] fields = clazz.getDeclaredFields();
    Object returnValue = clazz.newInstance();
    for (Field field : fields) {
      field.setAccessible(true);
      Object value1 = field.get(from);
      Object value2 = field.get(to);
      Object value = (value1 != null) ? value1 : value2;
      field.set(returnValue, value);
    }
    return (T) returnValue;
  }

  private static final String GETTER_EXPRESSION = "(get)([A-Z]\\w+)";
  private static final String SETTER_EXPRESSION = "(set)([A-Z]\\w+)";
  private static final String SETTER_REPLACE_EXPRESSION = "set$2";
  private static final Pattern GETTER_PATTERN = Pattern.compile(GETTER_EXPRESSION);
  private static final Pattern SETTER_PATTERN = Pattern.compile(SETTER_EXPRESSION);


  public static Object mergePrintableItem(Object from, Object to) {
    Set<String> fieldsToModify = new HashSet<>();
    if (from instanceof Shot) {
      fieldsToModify.add("getDeletedData");
      fieldsToModify.add("getNiceCount");
      fieldsToModify.add("getReplyCount");
      fieldsToModify.add("getOrder");
      fieldsToModify.add("getReshootCount");
      fieldsToModify.add("getNiced");
      fieldsToModify.add("getNicedTime");
      fieldsToModify.add("getReshooted");
      fieldsToModify.add("getReshootedTime");
      fieldsToModify.add("getSeen");
    } else if (from instanceof Poll) {
      fieldsToModify.add("getDeletedData");
      fieldsToModify.add("getSeen");
    } else if (from instanceof User) {
      fieldsToModify.add("getSeen");
      fieldsToModify.add("getDeletedData");
      fieldsToModify.add("getUsername");
      fieldsToModify.add("getPhoto");
      fieldsToModify.add("getOrder");
    }

    return copy(from, to, fieldsToModify);
  }

  private static Object copy(Object from, Object to, Set<String> whitelist) {
    for (Method method : from.getClass().getDeclaredMethods()) {
      String name = method.getName();
      if (whitelist != null && whitelist.contains(name)) {
        if (Modifier.isPublic(method.getModifiers()) && isGetter(method)) {
          Method setter = getSetterForGetter(to, method);
          if (setter != null) {
            try {
              Object product = method.invoke(from);
              if (product != null) {
                setter.invoke(to, product);
              }
            } catch (IllegalAccessException | InvocationTargetException e) {
              //
            }
          }
        }
      }
    }

    return to;
  }

  public static void copy(Object from, Object to) {
    copy(from, to, null);
  }

  public static boolean isGetter(Method method) {
    return isGetter(method.getName());
  }

  private static boolean isGetter(String methodName) {
    return GETTER_PATTERN.matcher(methodName).matches();
  }

  public static boolean isSetter(Method method) {
    return isSetter(method.getName());
  }

  private static boolean isSetter(String methodName) {
    return SETTER_PATTERN.matcher(methodName).matches();
  }

  private static String getSetterNameFromGetterName(String methodName) {
    return methodName.replaceFirst(GETTER_EXPRESSION, SETTER_REPLACE_EXPRESSION);
  }

  private static Method getSetterForGetter(Object instance, Method method) {
    String setterName = getSetterNameFromGetterName(method.getName());
    try {
      return instance.getClass().getMethod(setterName, method.getReturnType());
    } catch (NoSuchMethodException e) {
      return null;
    }
  }
}
