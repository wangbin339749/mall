package com.macro.mall.portal.service;

import com.macro.mall.model.PmsBrand;
import java.util.List;

/**
 * 首页内容管理Service
 * Created by macro on 2019/1/28.
 */
public interface BMSService {

    /**
     * 商家列表
     */
    List<PmsBrand> shopList(Integer pageSize, Integer pageNum);

}
