package com.macro.mall.portal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.portal.config.WxMappingJackson2HttpMessageConverter;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.RedisService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.PasswordEncoder;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 会员管理Service实现类
 * Created by macro on 2018/8/3.
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisService redisService;
    @Value("${redis.key.prefix.authCode}")
    private String REDIS_KEY_PREFIX_AUTH_CODE;
    @Value("${authCode.expire.seconds}")
    private Long AUTH_CODE_EXPIRE_SECONDS;
    @Value("${weixin.appid}")
    private String WINXIN_APPID;
    @Value("${weixin.secret}")
    private String WEIXIN_SECRET;

    @Override
    public UmsMember getByUsername(String username) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(memberList)) {
            return memberList.get(0);
        }
        return null;
    }

    @Override
    public UmsMember getById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public CommonResult register(String code, String userInfo) {
        //查询是否已有该用户
        UmsMemberExample example = new UmsMemberExample();
        WeiXinInfo info = getWeiXinUserInfo(code);
        if(info == null || userInfo == null){
            return new CommonResult().failed("接口异常请重试");
        }
        example.createCriteria().andWeixinOpenidEqualTo(info.getOpenid());
        List<UmsMember> umsMembers = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(umsMembers)) {
            return new CommonResult().failed("该用户已经存在");
        }
        //没有该用户进行添加操作
        ObjectMapper mapper = new ObjectMapper();
        WeiXinUserInfo user = null;
        try {

            user = mapper.readValue(userInfo, WeiXinUserInfo.class);
            System.out.println(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        UmsMember umsMember = new UmsMember();
        umsMember.setWeixinOpenid(info.getOpenid());
        umsMember.setWeixinSessionKey(info.getSession_key());
        umsMember.setSourceType(1);//mini program
        umsMember.setCreateTime(new Date());
        umsMember.setStatus(1);
        umsMember.setCity(user.getCity());
        umsMember.setGender(user.getGender());
        umsMember.setIcon(user.getAvatarUrl());
        umsMember.setNickname(user.getNickName());

        //获取默认会员等级并设置
        UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
        levelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
        if (!CollectionUtils.isEmpty(memberLevelList)) {
            umsMember.setMemberLevelId(memberLevelList.get(0).getId());
        }
        memberMapper.insert(umsMember);
        umsMember.setPassword(null);

        List<UmsMember> umsMembers2 = memberMapper.selectByExample(example);
        return new CommonResult().success("注册成功", umsMembers2.get(0));
    }

    /**
     * weixin 根据code获取用户信息openid session_key
     *
     * @param code
     * @return
     */
    public WeiXinInfo getWeiXinUserInfo(String code) {
        String url = getUrl(code);
        System.out.print(url);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
        ResponseEntity<WeiXinInfo> info = restTemplate.getForEntity(url, WeiXinInfo.class);
        if (info.getStatusCode() == HttpStatus.OK){
            if (info.getBody().getErrcode() == 0){
                return info.getBody();
            }
        }
        return null;
    }

    private String getUrl(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WINXIN_APPID + "&secret=" + WEIXIN_SECRET + "&js_code=" + code + "&grant_type=authorization_code";
        return url;
    }

    public WeiXinTokenInfo getToken(String token) {
        String url = getTokenUrl(token);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
        ResponseEntity<WeiXinTokenInfo> info = restTemplate.getForEntity(url, WeiXinTokenInfo.class);
        System.out.print(info.getBody().getAccess_token());
        if (info.getStatusCode() == HttpStatus.OK){
            if (info.getBody().getErrcode() == 0){
                return info.getBody();
            }
        }
        return null;
    }

    private String getTokenUrl(String token) {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + WINXIN_APPID + "&secret=" + WEIXIN_SECRET;
        return url;
    }

    @Override
    public CommonResult generateAuthCode(String telephone) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        //验证码绑定手机号并存储到redis
        redisService.set(REDIS_KEY_PREFIX_AUTH_CODE + telephone, sb.toString());
        redisService.expire(REDIS_KEY_PREFIX_AUTH_CODE + telephone, AUTH_CODE_EXPIRE_SECONDS);
        return new CommonResult().success("获取验证码成功", sb.toString());
    }

    @Override
    public CommonResult updatePassword(String telephone, String password, String authCode) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(telephone);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(memberList)) {
            return new CommonResult().failed("该账号不存在");
        }
        //验证验证码
        if (!verifyAuthCode(authCode, telephone)) {
            return new CommonResult().failed("验证码错误");
        }
        UmsMember umsMember = memberList.get(0);
        umsMember.setPassword(passwordEncoder.encodePassword(password, null));
        memberMapper.updateByPrimaryKeySelective(umsMember);
        return new CommonResult().success("密码修改成功", null);
    }

    @Override
    public UmsMember getCurrentMember() {
        return null;
//        SecurityContext ctx = SecurityContextHolder.getContext();
//        Authentication auth = ctx.getAuthentication();
//        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
//        return memberDetails.getUmsMember();
    }

    @Override
    public void updateIntegration(Long id, Integer integration) {
        UmsMember record = new UmsMember();
        record.setId(id);
        record.setIntegration(integration);
        memberMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public CommonResult tokenCheck(String token) {
        WeiXinTokenInfo urlToken = getToken(token);
        return new CommonResult().success("token获取成功", urlToken);
    }

    @Override
    public CommonResult login(String code) {
        //查询是否已有该用户
        UmsMemberExample example = new UmsMemberExample();
        WeiXinInfo info = getWeiXinUserInfo(code);
        if(info == null){
            return new CommonResult().failed("接口异常请重试");
        }
        example.createCriteria().andWeixinOpenidEqualTo(info.getOpenid());
        List<UmsMember> umsMembers = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(umsMembers)) {
            return new CommonResult().success("login success", umsMembers.get(0));
        }else {
            return new CommonResult().failed(CommonResult.FAILED_UNREGISTER, "用户未注册");
        }
    }

    //对输入的验证码进行校验
    private boolean verifyAuthCode(String authCode, String telephone) {
        if (StringUtils.isEmpty(authCode)) {
            return false;
        }
        String realAuthCode = redisService.get(REDIS_KEY_PREFIX_AUTH_CODE + telephone);
        return authCode.equals(realAuthCode);
    }

}
