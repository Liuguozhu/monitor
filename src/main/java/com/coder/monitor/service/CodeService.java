package com.coder.monitor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.coder.monitor.config.service.EmailService;
import com.coder.monitor.config.service.SmsService;
import com.coder.monitor.mapper.CodeMapper;
import com.coder.monitor.model.Code;
import com.coder.monitor.util.DateUtil;
import com.coder.monitor.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class CodeService {

  @Autowired
  private CodeMapper codeMapper;
  @Autowired
  private EmailService emailService;
  @Autowired
  private SmsService smsService;

  // 递归生成code，防止code重复
  private String generateToken() {
    String _code = StringUtil.randomNumber(6);
    Code code = this.selectByCode(_code);
    if (code != null) {
      return this.generateToken();
    }
    return _code;
  }

  public Code selectByCode(String _code) {
    QueryWrapper<Code> wrapper = new QueryWrapper<>();
    wrapper.lambda()
        .eq(Code::getCode, _code);
    return codeMapper.selectOne(wrapper);
  }

  // 查询没有用过的code
  public Code selectNotUsedCode(Integer userId, String email, String mobile) {
    QueryWrapper<Code> wrapper = new QueryWrapper<>();
    LambdaQueryWrapper<Code> lambda = wrapper.lambda();
    if (email != null) {
      lambda.eq(Code::getEmail, email);
      lambda.eq(Code::getUserId, userId);
    } else if (mobile != null) {
      lambda.eq(Code::getMobile, mobile);
    }
    lambda.eq(Code::getUsed, false);
    lambda.gt(Code::getExpireTime, new Date());
    return codeMapper.selectOne(wrapper);
  }

  // 创建一条验证码记录
  public Code createCode(Integer userId, String email, String mobile) {
    Code code = this.selectNotUsedCode(userId, email, mobile);
    if (code == null) {
      code = new Code();
      String _code = generateToken();
      code.setUserId(userId);
      code.setCode(_code);
      code.setEmail(email);
      code.setMobile(mobile);
      code.setInTime(new Date());
      code.setExpireTime(DateUtil.getMinuteAfter(new Date(), 30));
      codeMapper.insert(code);
    }
    return code;
  }

  // 验证邮箱验证码
  public Code validateCode(Integer userId, String email, String mobile, String _code) {
    QueryWrapper<Code> wrapper = new QueryWrapper<>();
    LambdaQueryWrapper<Code> lambda = wrapper.lambda();
    if (email != null) {
      lambda.eq(Code::getEmail, email);
      lambda.eq(Code::getUserId, userId);
    } else if (mobile != null) {
      lambda.eq(Code::getMobile, mobile);
    }
    lambda.eq(Code::getCode, _code);
    lambda.eq(Code::getUsed, false);
    lambda.gt(Code::getExpireTime, new Date());
    return codeMapper.selectOne(wrapper);
  }

  // 发送邮件
  public boolean sendEmail(Integer userId, String email, String title, String content) {
    Code code = this.createCode(userId, email, null);
    return emailService.sendEmail(email, title, content.replace("${code}", code.getCode()));
  }

  // 发送短信
  public boolean sendSms(String mobile) {
    // 生成code
    Code code = this.createCode(null, null, mobile);
    return smsService.sendSms(mobile, code.getCode());
  }

  public void update(Code code) {
    codeMapper.updateById(code);
  }
}
