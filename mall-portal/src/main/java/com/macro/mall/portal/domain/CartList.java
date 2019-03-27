package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsCartItem;

import java.util.List;

public class CartList {
    private String brand;
    private List<OmsCartItem> list;
    private boolean checked;

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
