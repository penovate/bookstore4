package bookstore.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bookstore.bean.CouponBean;

import bookstore.exceptionCenter.BusinessException;
import bookstore.repository.CouponRepository;
import bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    // 領取優惠券
    public CouponBean claimCoupon(Integer userId, String code) {
        // 驗證使用者
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "找不到使用者"));

        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException(400, "優惠券代碼不能為空");
        }

        String validCode = code.trim();
        BigDecimal discount;
        BigDecimal minSpend;

        // 根據代碼不同產生不同面額優惠券: "read50" 和 "read100"，equalsIgnoreCase容許不分大小寫
        if ("read50".equalsIgnoreCase(validCode)) {
            discount = new BigDecimal("50");
            minSpend = new BigDecimal("499");
        } else if ("read100".equalsIgnoreCase(validCode)) {
            discount = new BigDecimal("100");
            minSpend = new BigDecimal("899");
        } else {
            throw new BusinessException(400, "無效的優惠券代碼");
        }

        // 檢查使用者是否已經擁有此優惠券
        List<CouponBean> existing = couponRepository.findByUserIdAndCouponCode(userId, validCode);
        if (!existing.isEmpty()) {
            throw new BusinessException(400, "您已經領取過此優惠券");
        }

        CouponBean coupon = new CouponBean();
        coupon.setUserId(userId);
        coupon.setCouponCode(validCode);
        coupon.setDiscountAmount(discount);
        coupon.setMinSpend(minSpend);
        coupon.setStatus(0); // 未使用
        coupon.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return couponRepository.save(coupon);
    }

    @SuppressWarnings("null") //忽略空值警告
    public List<CouponBean> getCouponsByUserId(Integer userId) {
        return couponRepository.findByUserId(userId);
    }

    public List<CouponBean> getAllCoupons() {
        return couponRepository.findAll();
    }

    //使用優惠券
    public void useCoupon(Integer couponId) {
        CouponBean coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(404, "找不到優惠券"));

        if (coupon.getStatus() == 1) {
            throw new BusinessException(400, "優惠券已使用");
        }

        coupon.setStatus(1);
        coupon.setUsedAt(new Timestamp(System.currentTimeMillis()));
        couponRepository.save(coupon);
    }
}
