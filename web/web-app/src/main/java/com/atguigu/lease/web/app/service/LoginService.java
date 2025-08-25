package com.atguigu.lease.web.app.service;

import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;

public interface LoginService {


    void SMSaliyun(String phone) throws Exception;

    String Login(LoginVo loginVo);

    UserInfoVo getLogin(String phone);
}
