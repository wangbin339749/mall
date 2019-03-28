package com.macro.mall.portal.domain;

import com.macro.mall.model.SmsBrandCoupon;
import com.macro.mall.model.SmsCouponHistory;
/**
 * 优惠券领取历史详情封装
 * Created by wangbin on 2018/8/29.
 */
public class SmsCouponHistoryDetail extends SmsCouponHistory {
    //相关优惠券信息
    private SmsBrandCoupon brandCoupon;

    public SmsBrandCoupon getBrandCoupon() {
        return brandCoupon;
    }

    public void setBrandCoupon(SmsBrandCoupon brandCoupon) {
        this.brandCoupon = brandCoupon;
    }
}
