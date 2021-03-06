package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.OmsCartItemMapper;
import com.macro.mall.mapper.SmsBrandCouponMapper;
import com.macro.mall.model.OmsCartItem;
import com.macro.mall.model.OmsCartItemExample;
import com.macro.mall.model.SmsBrandCoupon;
import com.macro.mall.model.SmsBrandCouponExample;
import com.macro.mall.portal.domain.CartList;
import com.macro.mall.portal.service.OmsCartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 购物车管理Service实现类
 * Created by macro on 2018/8/2.
 */
@Service
public class OmsCartItemServiceImpl implements OmsCartItemService {
    @Autowired
    private OmsCartItemMapper cartItemMapper;
    @Autowired
    private SmsBrandCouponMapper brandCouponMapper;
//    @Autowired
//    private PortalProductDao productDao;

    @Override
    public int add(OmsCartItem cartItem) {
        int count;
        cartItem.setDeleteStatus(0);
        OmsCartItem existCartItem = getCartItem(cartItem);
        if (existCartItem == null) {
            cartItem.setCreateDate(new Date());
            count = cartItemMapper.insert(cartItem);
        } else {
            cartItem.setModifyDate(new Date());
            existCartItem.setQuantity(existCartItem.getQuantity() + cartItem.getQuantity());
            count = cartItemMapper.updateByPrimaryKey(existCartItem);
        }
        return count;
    }

    /**
     * 根据会员id,商品id和规格获取购物车中商品
     */
    private OmsCartItem getCartItem(OmsCartItem cartItem) {
        OmsCartItemExample example = new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = example.createCriteria().andMemberIdEqualTo(cartItem.getMemberId())
                .andProductIdEqualTo(cartItem.getProductId()).andDeleteStatusEqualTo(0);
        if (!StringUtils.isEmpty(cartItem.getSp1())) {
            criteria.andSp1EqualTo(cartItem.getSp1());
        }
        if (!StringUtils.isEmpty(cartItem.getSp2())) {
            criteria.andSp2EqualTo(cartItem.getSp2());
        }
        if (!StringUtils.isEmpty(cartItem.getSp3())) {
            criteria.andSp3EqualTo(cartItem.getSp3());
        }
        List<OmsCartItem> cartItemList = cartItemMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(cartItemList)) {
            return cartItemList.get(0);
        }
        return null;
    }

    @Override
    public List<CartList> list(Long memberId) {
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andMemberIdEqualTo(memberId);
        List<OmsCartItem> cartItemList =  cartItemMapper.selectByExample(example);
        List<CartList> cartLists = groupCartItemByBrand(cartItemList);

        List<CartList> cartListsWithCoupon = getCoupons(cartLists);//获取商家优惠券
        return cartListsWithCoupon;
    }

    /**
     * 获取商家优惠券
     * @param cartLists
     * @return
     */
    private List<CartList> getCoupons(List<CartList> cartLists) {
        for(CartList cartList : cartLists){
            SmsBrandCouponExample example = new SmsBrandCouponExample();
            example.createCriteria().andBrandIdEqualTo(cartList.getBrandId());
            List<SmsBrandCoupon> brandCoupons = brandCouponMapper.selectByExample(example);
            cartList.setSmsBrandCoupons(brandCoupons);
        }
        return cartLists;
    }


//    @Override
//    public int updateQuantity(Long id, Long memberId, Integer quantity) {
//        OmsCartItem cartItem = new OmsCartItem();
//        cartItem.setQuantity(quantity);
//        OmsCartItemExample example = new OmsCartItemExample();
//        example.createCriteria().andDeleteStatusEqualTo(0)
//                .andIdEqualTo(id).andMemberIdEqualTo(memberId);
//        return cartItemMapper.updateByExampleSelective(cartItem, example);
//    }

    @Override
    public int delete(Long memberId, List<Long> ids) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andIdIn(ids).andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record, example);
    }

//    @Override
//    public CartProduct getCartProduct(Long productId) {
//        return productDao.getCartProduct(productId);
//    }

    @Override
    public int updateAttr(OmsCartItem cartItem) {
        //删除原购物车信息
        OmsCartItem updateCart = new OmsCartItem();
        updateCart.setId(cartItem.getId());
        updateCart.setModifyDate(new Date());
        updateCart.setDeleteStatus(1);
        cartItemMapper.updateByPrimaryKeySelective(updateCart);
        cartItem.setId(null);
        add(cartItem);
        return 1;
    }

    @Override
    public int clear(Long memberId) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record,example);
    }

    /**
     * 以品牌为单位对购物车中商品进行分组
     */
    private List<CartList> groupCartItemByBrand(List<OmsCartItem> cartItemList) {
        Map<String, List<OmsCartItem>> productCartMap = new TreeMap<>();
        for (OmsCartItem cartItem : cartItemList) {
            List<OmsCartItem> productCartItemList = productCartMap.get(cartItem.getProductBrand());
            if (productCartItemList == null) {
                productCartItemList = new ArrayList<>();
                productCartItemList.add(cartItem);
                productCartMap.put(cartItem.getProductBrand(), productCartItemList);
            } else {
                productCartItemList.add(cartItem);
            }
        }

        List<CartList> cartLists = new ArrayList<>();
        for (String brand : productCartMap.keySet()){
            CartList cartList = new CartList();
            cartList.setBrand(brand);
            cartList.setBrandId(productCartMap.get(brand).get(0).getBrandId());
            cartList.setChecked(true);
            cartList.setList(productCartMap.get(brand));
            cartLists.add(cartList);
        }
        return cartLists;
    }
}
