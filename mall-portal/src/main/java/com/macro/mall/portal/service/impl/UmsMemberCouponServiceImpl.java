package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.SmsBrandCouponMapper;
import com.macro.mall.mapper.SmsCouponHistoryMapper;
//import com.macro.mall.mapper.SmsCouponMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.SmsCouponHistoryDao;
import com.macro.mall.portal.domain.CartList;
import com.macro.mall.portal.domain.CommonResult;
import com.macro.mall.portal.domain.SmsCouponHistoryDetail;
import com.macro.mall.portal.service.OmsCartItemService;
import com.macro.mall.portal.service.UmsMemberCouponService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 会员优惠券管理Service实现类
 * Created by macro on 2018/8/29.
 */
@Service
public class UmsMemberCouponServiceImpl implements UmsMemberCouponService {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private SmsBrandCouponMapper couponMapper;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    @Autowired
    private SmsCouponHistoryDao couponHistoryDao;

    @Override
    public CommonResult add(Long couponId, Long memberID) {
        UmsMember currentMember = memberService.getCurrentMember(memberID);
        //获取优惠券信息，判断数量
        SmsBrandCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
        if (coupon == null) {
            return new CommonResult().failed("优惠券不存在");
        }
        if (coupon.getCount() <= 0) {
            return new CommonResult().failed("优惠券已经领完了");
        }
        Date now = new Date();
        if (now.before(coupon.getEnableTime())) {
            return new CommonResult().failed("优惠券还没到领取时间");
        }
        //判断用户领取的优惠券数量是否超过限制
        SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
        couponHistoryExample.createCriteria().andCouponIdEqualTo(couponId).andMemberIdEqualTo(currentMember.getId());
        int count = couponHistoryMapper.countByExample(couponHistoryExample);
        if (count >= coupon.getPerLimit()) {
            return new CommonResult().failed("您已经领取过该优惠券");
        }
        //生成领取优惠券历史
        SmsCouponHistory couponHistory = new SmsCouponHistory();
        couponHistory.setCouponId(couponId);
        couponHistory.setCouponCode(generateCouponCode(currentMember.getId()));
        couponHistory.setCreateTime(now);
        couponHistory.setMemberId(currentMember.getId());
        couponHistory.setMemberNickname(currentMember.getNickname());
        //主动领取
        couponHistory.setGetType(1);
        //未使用
        couponHistory.setUseStatus(0);
        couponHistoryMapper.insert(couponHistory);
        //修改优惠券表的数量、领取数量
        coupon.setCount(coupon.getCount() - 1);
        coupon.setReceiveCount(coupon.getReceiveCount() == null ? 1 : coupon.getReceiveCount() + 1);
        couponMapper.updateByPrimaryKey(coupon);
        return new CommonResult().success("领取成功", null);
    }

    /**
     * 16位优惠码生成：时间戳后8位+4位随机数+用户id后4位
     */
    private String generateCouponCode(Long memberId) {
        StringBuilder sb = new StringBuilder();
        Long currentTimeMillis = System.currentTimeMillis();
        String timeMillisStr = currentTimeMillis.toString();
        sb.append(timeMillisStr.substring(timeMillisStr.length() - 8));
        for (int i = 0; i < 4; i++) {
            sb.append(new Random().nextInt(10));
        }
        String memberIdStr = memberId.toString();
        if (memberIdStr.length() <= 4) {
            sb.append(String.format("%04d", memberId));
        } else {
            sb.append(memberIdStr.substring(memberIdStr.length() - 4));
        }
        return sb.toString();
    }

    @Override
    public List<SmsCouponHistory> list(Integer useStatus, Long memberID) {
        UmsMember currentMember = memberService.getCurrentMember(memberID);
        SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
        SmsCouponHistoryExample.Criteria criteria = couponHistoryExample.createCriteria();
        criteria.andMemberIdEqualTo(currentMember.getId());
        if (useStatus != null) {
            criteria.andUseStatusEqualTo(useStatus);
        }
        return couponHistoryMapper.selectByExample(couponHistoryExample);
    }

    @Override
    public List<SmsCouponHistoryDetail> listCart(Long memberID, List<CartList> cartItemList, Integer type) {
        Date now = new Date();
        //获取该用户所有优惠券
        List<SmsCouponHistoryDetail> allList = couponHistoryDao.getDetailList(memberID);
        //根据优惠券使用类型来判断优惠券是否可用
        List<SmsCouponHistoryDetail> enableList = new ArrayList<>();
        List<SmsCouponHistoryDetail> disableList = new ArrayList<>();

        for (int i =0; i< allList.size(); i++) {
            BigDecimal minPoint = allList.get(i).getBrandCoupon().getMinPoint();
            Date endTime = allList.get(i).getBrandCoupon().getEndTime();
            //判断是否满足优惠起点
            //计算商家优惠券是否可用
            BigDecimal brandAmount = calcBrandTotalAmount(allList.get(i).getBrandCoupon().getBrandId(), cartItemList);
            if (now.before(endTime) && brandAmount.subtract(minPoint).intValue() >= 0) {
                enableList.add(allList.get(i));
            } else {
                disableList.add(allList.get(i));
            }
        }
        if (type.equals(1)) {
            return enableList;
        } else {
            return disableList;
        }
    }

    private BigDecimal calcBrandTotalAmount(Long brandID, List<CartList> cartLists) {
        BigDecimal total = new BigDecimal("0");
        List<OmsCartItem> cartItems = new ArrayList<>();
        for (CartList cartList : cartLists) {
            for (OmsCartItem omsCartItem : cartList.getList()) {
                if (omsCartItem.getBrandId() == brandID) {
                    cartItems.add(omsCartItem);
                }
            }
        }
        for (OmsCartItem item : cartItems) {
            BigDecimal realPrice = item.getPrice().subtract(item.getPrice());
            total = total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
        }
        return total;
    }

    private BigDecimal calcTotalAmountByproductCategoryId(List<OmsCartItem> cartItemList, List<Long> productCategoryIds) {
        BigDecimal total = new BigDecimal("0");
        for (OmsCartItem item : cartItemList) {
            if (productCategoryIds.contains(item.getProductCategoryId())) {
//                BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
//                total=total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return total;
    }

//    private BigDecimal calcTotalAmountByProductId(List<CartItem> cartItemList, List<Long> productIds) {
//        BigDecimal total = new BigDecimal("0");
//        for (CartItem item : cartItemList) {
//            if(productIds.contains(item.getProductId())){
//                BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
//                total=total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
//            }
//        }
//        return total;
//    }

}
