package com.coder.monitor.util;

import com.google.gson.Gson;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;

public class JsonUtil {

  public final static Gson gson = new Gson();

  // 对象转json
  public static String objectToJson(Object object) {
    if (object == null) return null;
    return gson.toJson(object);
  }

  // json转对象
  public static <T> T jsonToObject(String json, Class<T> object) {
    if (StringUtils.isEmpty(json)) return null;
    return gson.fromJson(json, object);
  }

  // 带泛型的json转对象
  public static <T> T jsonToObject(String json, Type type) {
    if (StringUtils.isEmpty(json)) return null;
    return gson.fromJson(json, type);
  }
}
