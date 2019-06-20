package com.coder.monitor.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coder.monitor.model.AdminUser;

import java.util.List;
import java.util.Map;

/**
 * Created by tomoya.
 * Copyright (c) 2018, All Rights Reserved.
 * https://yiiu.co
 */
public interface AdminUserMapper extends BaseMapper<AdminUser> {

  List<Map<String, Object>> selectAll();
}