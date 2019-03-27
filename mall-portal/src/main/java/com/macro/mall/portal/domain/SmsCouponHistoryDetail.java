package com.macro.mall.portal.domain;

import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.SmsCouponBrandRelation;
import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.model.SmsCouponMemberRelation;

import java.util.List;

/**
 * 优惠券领取历史详情封装
 * Created by macro on 2018/8/29.
 */
public class SmsCouponHistoryDetail extends SmsCouponHistory {
    //相关优惠券信息
    private SmsCoupon coupon;
    //优惠券关联商户
    private List<SmsCouponBrandRelation> smsCouponBrandRelationList;
    //优惠券关联用户
    private List<SmsCouponMemberRelation> smsCouponMemberRelationList;

    public SmsCoupon getCoupon() {
        return coupon;
    }

    public void setCoupon(SmsCoupon coupon) {
        this.coupon = coupon;
    }

    public List<SmsCouponBrandRelation> getSmsCouponBrandRelationList() {
        return smsCouponBrandRelationList;
    }

    public void setSmsCouponBrandRelationList(List<SmsCouponBrandRelation> smsCouponBrandRelationList) {
        this.smsCouponBrandRelationList = smsCouponBrandRelationList;
    }

    public List<SmsCouponMemberRelation> getSmsCouponMemberRelationList() {
        return smsCouponMemberRelationList;
    }

    public void setSmsCouponMemberRelationList(List<SmsCouponMemberRelation> smsCouponMemberRelationList) {
        this.smsCouponMemberRelationList = smsCouponMemberRelationList;
    }
}
