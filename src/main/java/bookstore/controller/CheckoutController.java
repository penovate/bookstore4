package bookstore.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bookstore.dto.CheckoutRequest;
import bookstore.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/orders")
public class CheckoutController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    @ResponseBody
    public Map<String, Object> checkout(@RequestBody CheckoutRequest checkoutRequest, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 從 Request Attribute 取得 userId (攔截器設定)
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return response;
        }

        try {
            orderService.createOrderFromCart(userId, checkoutRequest);
            response.put("success", true);
            response.put("message", "訂單建立成功");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "結帳失敗: " + e.getMessage());
        }

        return response;
    }
}
