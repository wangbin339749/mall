package com.macro.mall.dto;

import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.SmsCouponBrandRelation;
import com.macro.mall.model.SmsCouponMemberRelation;

import java.util.List;

/**
 * 优惠券信息封装，包括绑定商品和绑定分类
 * Created by macro on 2018/8/28.
 */
public class SmsCouponParam extends SmsCoupon {
    //优惠券绑定的商品
    private List<SmsCouponMemberRelation> productRelationList;
    //优惠券绑定的商品分类
    private List<SmsCouponBrandRelation> productCategoryRelationList;

    public List<SmsCouponMemberRelation> getProductRelationList() {
        return productRelationList;
    }

    public void setProductRelationList(List<SmsCouponMemberRelation> productRelationList) {
        this.productRelationList = productRelationList;
    }

    public List<SmsCouponBrandRelation> getProductCategoryRelationList() {
        return productCategoryRelationList;
    }

    public void setProductCategoryRelationList(List<SmsCouponBrandRelation> productCategoryRelationList) {
        this.productCategoryRelationList = productCategoryRelationList;
    }
}
