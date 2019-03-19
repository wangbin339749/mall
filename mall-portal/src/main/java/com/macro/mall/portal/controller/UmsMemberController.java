package com.macro.mall.portal.controller;

import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 会员登录注册管理Controller
 * Created by macro on 2018/8/3.
 */
@Controller
@Api(tags = "UmsMemberController", description = "会员登录注册管理")
@RequestMapping("/sso")
public class UmsMemberController {
    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("微信账号注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Object register(@RequestParam String code, @RequestParam String userInfo) {
        return memberService.register(code, userInfo);
    }

    @ApiOperation("login登录")
    @RequestMapping(value = "/login_user", method = RequestMethod.POST)
    @ResponseBody
    public Object login(@RequestParam String code) {
        return memberService.login(code);
    }

    @ApiOperation("token校验")
    @RequestMapping(value = "/token_check", method = RequestMethod.POST)
    @ResponseBody
    public Object tokenCheck(@RequestParam String token) {
        return memberService.tokenCheck(token);
    }

}
