package com.macro.mall.portal.controller;

import com.macro.mall.model.PmsBrand;
import com.macro.mall.portal.domain.CommonResult;
import com.macro.mall.portal.service.BMSService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

/**
 * 商家管理Controller
 * Created by wang bin on 2019/3/19.
 */
@Controller
@Api(tags = "BmsController", description = "商家管理")
@RequestMapping("/sso")
public class BmsController {
    @Autowired
    private BMSService bmsService;

    @ApiOperation("商家列表")
    @RequestMapping(value = "/shops", method = RequestMethod.GET)
    @ResponseBody
    public Object shopList(@RequestParam(value = "pageSize", defaultValue = "4") Integer pageSize,
                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsBrand> brandList = bmsService.shopList(pageSize, pageNum);
        return new CommonResult().success(brandList);
    }
}