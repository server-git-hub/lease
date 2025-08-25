package com.atguigu.lease.web.app.controller.login;



import com.atguigu.lease.common.login.JwtUtil;
import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "登录管理")
@RequestMapping("/app/")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("login/getCode")
    @Operation(summary = "获取短信验证码")
    public Result getCode(@RequestParam String phone) throws Exception {
        loginService.SMSaliyun(phone);
        return Result.ok();
    }

    @PostMapping("login")
    @Operation(summary = "登录")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        String token=loginService.Login(loginVo);
        return Result.ok(token);
    }

    @GetMapping("info")
    @Operation(summary = "获取登录用户信息")
    public Result<UserInfoVo> info(@RequestParam("access_token") String token){
        Claims claims = JwtUtil.parseToken(token);
        Long id = Long.parseLong(claims.get("userId")+"");
        UserInfo userInfo = userInfoService.getById(id);
        UserInfoVo userInfoVo=new UserInfoVo(userInfo.getNickname(),userInfo.getAvatarUrl());
        return Result.ok(userInfoVo);
    }
}
