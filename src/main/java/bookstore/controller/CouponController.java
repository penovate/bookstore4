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
    @PostMapping("/claim")//可從F12的網路->找到"claim"請求，查看標頭Authorization
    public ResponseEntity<?> claimCoupon(@RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> payload) {
        try {
        	//token有前綴詞Bearer_空白一格，把前綴詞拿掉取得token字串
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Integer userId = Integer.parseInt(jwtUtil.getMemberId(token));
            String code = payload.get("code");

            CouponBean coupon = couponService.claimCoupon(userId, code);
            
            // 建立一個狀態碼200的成功回應，並將優惠券結果物件以json格式傳回前端
            return ResponseEntity.ok(Map.of("success", true, "coupon", coupon));
        } catch (Exception e) {
        	
        	//有異常，建立狀態碼200的成功回應(回應success是false)，並將失敗原因訊息以json格式傳回前端
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 取得使用者的優惠券(查詢使用者已有優惠券)
    @GetMapping("/my")//可從F12的網路->找到"my"請求，查看標頭Authorization
    public ResponseEntity<?> getCouponsByUserId(@RequestHeader("Authorization") String token) {
        try {
        	// 將token前綴詞Bearer_拿掉，取得完整token字串
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Integer userId = Integer.parseInt(jwtUtil.getMemberId(token));

            List<CouponBean> coupons = couponService.getCouponsByUserId(userId);
            
            // 建立一個狀態碼200的成功回應，並將優惠券結果物件以json格式傳回前端
            return ResponseEntity.ok(Map.of("success", true, "coupons", coupons));
        } catch (Exception e) {
        	
        	//有異常，建立狀態碼200的成功回應(回應success是false)，並將失敗原因訊息以json格式傳回前端
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 管理員：取得所有優惠券
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllCoupons() {
        try {
            List<CouponBean> coupons = couponService.getAllCoupons();
            
            // 建立一個狀態碼200的成功回應，並將優惠券結果物件以json格式傳回前端
            return ResponseEntity.ok(Map.of("success", true, "coupons", coupons));
        } catch (Exception e) {
        	
        	//有異常，建立狀態碼200的成功回應(回應success是false)，並將失敗原因訊息以json格式傳回前端
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
