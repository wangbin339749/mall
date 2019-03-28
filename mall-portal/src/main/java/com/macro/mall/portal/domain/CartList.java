package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsCartItem;
import com.macro.mall.model.SmsBrandCoupon;

import java.util.List;

public class CartList {
    private Long brandId;
    private String brand;
    private List<SmsBrandCoupon> smsBrandCoupons;
    private List<OmsCartItem> list;
    private boolean checked;

    public List<SmsBrandCoupon> getSmsBrandCoupons() {
        return smsBrandCoupons;
    }

    public void setSmsBrandCoupons(List<SmsBrandCoupon> smsBrandCoupons) {
        this.smsBrandCoupons = smsBrandCoupons;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<OmsCartItem> getList() {
        return list;
    }

    public void setList(List<OmsCartItem> list) {
        this.list = list;
    }
}
