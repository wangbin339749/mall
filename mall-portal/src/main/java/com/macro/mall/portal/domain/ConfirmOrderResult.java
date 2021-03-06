package com.macro.mall.portal.domain;

import com.macro.mall.model.UmsMemberReceiveAddress;
import java.math.BigDecimal;
import java.util.List;

/**
 * 确认单信息封装
 * Created by macro on 2018/8/30.
 */
public class ConfirmOrderResult {
    //包含优惠信息的购物车信息
    private List<CartList> cartLists;
    //用户收货地址列表
    private List<UmsMemberReceiveAddress> memberReceiveAddressList;
    //用户可用优惠券列表
    private List<SmsCouponHistoryDetail> couponHistoryDetailList;
    //计算的金额
    private CalcAmount calcAmount;

    public List<CartList> getCartLists() {
        return cartLists;
    }

    public void setCartLists(List<CartList> cartLists) {
        this.cartLists = cartLists;
    }

    public List<UmsMemberReceiveAddress> getMemberReceiveAddressList() {
        return memberReceiveAddressList;
    }

    public void setMemberReceiveAddressList(List<UmsMemberReceiveAddress> memberReceiveAddressList) {
        this.memberReceiveAddressList = memberReceiveAddressList;
    }

    public List<SmsCouponHistoryDetail> getCouponHistoryDetailList() {
        return couponHistoryDetailList;
    }

    public void setCouponHistoryDetailList(List<SmsCouponHistoryDetail> couponHistoryDetailList) {
        this.couponHistoryDetailList = couponHistoryDetailList;
    }

    public CalcAmount getCalcAmount() {
        return calcAmount;
    }

    public void setCalcAmount(CalcAmount calcAmount) {
        this.calcAmount = calcAmount;
    }

    public static class CalcAmount {
        //订单商品总金额
        private BigDecimal totalAmount;
        //运费
        private BigDecimal freightAmount;
        //应付金额
        private BigDecimal payAmount;

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getFreightAmount() {
            return freightAmount;
        }

        public void setFreightAmount(BigDecimal freightAmount) {
            this.freightAmount = freightAmount;
        }

        public BigDecimal getPayAmount() {
            return payAmount;
        }

        public void setPayAmount(BigDecimal payAmount) {
            this.payAmount = payAmount;
        }
    }
}
