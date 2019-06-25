package com.coder.monitor.config.service;

public interface BaseService<T> {

  // 外接服务初始化实例方法
  T instance();
}
