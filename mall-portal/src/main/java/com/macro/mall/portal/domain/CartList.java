package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsCartItem;

import java.util.List;

public class CartList {
    private int brand_id;
    private String brand;
//    private List<>
    private List<OmsCartItem> list;
    private boolean checked;

    public int getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
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
