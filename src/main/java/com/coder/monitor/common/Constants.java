package com.coder.monitor.common;


import java.util.concurrent.ConcurrentHashMap;

public class Constants {

  private Constants() {
  }

  public static final String REDIS_SYSTEM_CONFIG_KEY = "monitor_system_config";

  public static final String REDIS_RED_ENVELOPE_KEY = "monitor_red_envelope_"; // 后面还要拼上红包的id

  // 如果没有开启redis服务，但开启了websocket，那么连接的用户信息会被存在这个对象里
//  public static final ConcurrentHashMap<String, UserWithSocketIOClient> websocketUserMap = new ConcurrentHashMap<>();
  public static final ConcurrentHashMap<Integer, String> usernameSocketIdMap = new ConcurrentHashMap<>();

}
