package com.macro.mall.portal.service;

import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.portal.domain.CartList;
import com.macro.mall.portal.domain.CommonResult;
import com.macro.mall.portal.domain.SmsCouponHistoryDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户优惠券管理Service
 * Created by macro on 2018/8/29.
 */
public interface UmsMemberCouponService {
    /**
     * 会员添加优惠券
     */
    @Transactional
    CommonResult add(Long couponId, Long memberID);

    /**
     * 获取优惠券列表
     * @param useStatus 优惠券的使用状态
     */
    List<SmsCouponHistory> list(Integer useStatus, Long memberID);

    /**
     * 根据购物车信息获取可用优惠券
     */
    List<SmsCouponHistoryDetail> listCart(Long memberID, List<CartList> cartItemList, Integer type);
}
