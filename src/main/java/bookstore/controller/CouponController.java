package bookstore.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bookstore.bean.CouponBean;
import bookstore.service.CouponService;
import bookstore.util.JwtUtil;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private JwtUtil jwtUtil;

    // 領取優惠券
    @PostMapping("/claim")
    public ResponseEntity<?> claimCoupon(@RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> payload) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Integer userId = Integer.parseInt(jwtUtil.getMemberId(token));
            String code = payload.get("code");

            CouponBean coupon = couponService.claimCoupon(userId, code);
            return ResponseEntity.ok(Map.of("success", true, "coupon", coupon));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 取得使用者的優惠券
    @GetMapping("/my")
    public ResponseEntity<?> getCouponsByUserId(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Integer userId = Integer.parseInt(jwtUtil.getMemberId(token));

            List<CouponBean> coupons = couponService.getCouponsByUserId(userId);
            return ResponseEntity.ok(Map.of("success", true, "coupons", coupons));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 管理員：取得所有優惠券
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllCoupons() {
        try {
            // 在實際應用中，此處應檢查管理員權限
            List<CouponBean> coupons = couponService.getAllCoupons();
            return ResponseEntity.ok(Map.of("success", true, "coupons", coupons));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
