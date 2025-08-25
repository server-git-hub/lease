package com.atguigu.lease.web.app.service.impl;


import com.aliyun.dysmsapi20170525.Client;
import com.atguigu.lease.common.Exception.notDeleteException;
import com.atguigu.lease.common.constants.RedisConstant;
import com.atguigu.lease.common.login.JwtUtil;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private Client client;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void SMSaliyun(String phone) throws Exception {

        //1.检查是否已有验证码
        Object code = redisTemplate.opsForValue().get(ResultCodeEnum.APP_LOGIN_AUTH + phone);
        if(!ObjectUtils.isEmpty(code)){
            throw new notDeleteException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
        }
        //2.生成一个短信验证码
        int rondom=(int)(Math.random()*900000+100000);
        //3.存储到redis
        redisTemplate.opsForValue().set(ResultCodeEnum.APP_LOGIN_AUTH+phone,rondom, RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC, TimeUnit.SECONDS);
        //4.发送短信client
        //由于aliyun短信官网有变，假装发了个验证码
//        SendSmsRequest smsRequest = new SendSmsRequest();
//        smsRequest.setPhoneNumbers(phone);
//        smsRequest.setSignName("阿里云短信测试");
//        smsRequest.setTemplateCode("SMS_154950909");
//        smsRequest.setTemplateParam("{\"code\":\"" + rondom + "\"}\n");
//        client.sendSms(smsRequest);
    }

    @Override
    public String Login(LoginVo loginVo) {
        //通过key获取验证码
        Object key = ResultCodeEnum.APP_LOGIN_AUTH + loginVo.getPhone();
        Object code = redisTemplate.opsForValue().get(key);
        //校验验证码是否存在
        if(ObjectUtils.isEmpty(code)){
            throw new notDeleteException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED);
        }
        //校验验证码是否正确
        String str = code.toString();
        if(!loginVo.getCode().equals(str)){
            throw new notDeleteException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
        }
        //查看手机号是否存在
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(UserInfo::getPhone, loginVo.getPhone());
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        if(userInfo==null){
            userInfo = new UserInfo();
            userInfo.setNickname("佚名"+loginVo.getPhone());
            userInfo.setStatus(BaseStatus.ENABLE);
            userInfo.setPhone(loginVo.getPhone());
            userInfoService.save(userInfo);
        }
        //校验用户状态是否可用
        if(userInfo.getStatus()==BaseStatus.DISABLE){
            throw new notDeleteException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
        }
        //返回token
        String token = JwtUtil.createToken(userInfo.getId(),userInfo.getNickname());
        return token;
    }

    @Override
    public UserInfoVo getLogin(String phone) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(UserInfo::getPhone, phone);
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        UserInfoVo userInfoVo=new UserInfoVo(userInfo.getNickname(),userInfo.getAvatarUrl());
        return userInfoVo;
    }
}
