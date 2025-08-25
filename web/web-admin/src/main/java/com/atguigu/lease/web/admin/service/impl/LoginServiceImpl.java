package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.Exception.notDeleteException;
import com.atguigu.lease.common.constants.RedisConstant;
import com.atguigu.lease.common.login.JwtUtil;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.service.SystemUserService;
import com.atguigu.lease.web.admin.service.UserInfoService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.netty.util.internal.ObjectUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private SystemUserService systemUserService;

    @Override
    public CaptchaVo getCaptcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48,4);
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        String key=RedisConstant.ADMIN_LOGIN_PREFIX+ UUID.randomUUID();
        String value=specCaptcha.text().toLowerCase();
        redisTemplate.opsForValue().set(key,value, RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);
        String image=specCaptcha.toBase64();
        CaptchaVo captchaVo=new CaptchaVo(image,key);
        return captchaVo;
    }

    @Override
    public String login(LoginVo loginVo) {
        String captchaCode = loginVo.getCaptchaCode();
        //判断验证码是否为空
        if(ObjectUtils.isEmpty(captchaCode)){
            throw new notDeleteException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
        }
        //判断验证码是否过期
        String value=(String)redisTemplate.opsForValue().get(loginVo.getCaptchaKey());
        if(value==null){
            throw new notDeleteException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);
        }
        //验证验证码是否正确
        if(!captchaCode.equalsIgnoreCase(value)){
            throw new notDeleteException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);
        }
        //验证用户是否存在
        LambdaQueryWrapper<SystemUser> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemUser::getUsername,loginVo.getUsername());
        SystemUser systemUser = systemUserService.getOne(lambdaQueryWrapper);
        if(systemUser==null){
            throw new notDeleteException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }
        //验证密码是否正确
        String password = DigestUtils.md5DigestAsHex(loginVo.getPassword().getBytes());
        if(!password.equals(systemUser.getPassword())){
            throw new notDeleteException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }
        //验证用户是否禁用
        if(systemUser.getStatus()== BaseStatus.DISABLE){
            throw new notDeleteException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
        }
        //返回token
        String token = JwtUtil.createToken(systemUser.getId(), systemUser.getUsername());
        return token;
    }
}
