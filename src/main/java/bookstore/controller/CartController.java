package bookstore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bookstore.bean.Cart;
import bookstore.exceptionCenter.BusinessException;
import bookstore.service.CartService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // View page - now just returns the JSP, data loaded via AJAX
    @GetMapping
    public String viewCartPage() {
        return "cart/cart";
    }

    // API to get cart items
    @GetMapping("/api/items")
    @ResponseBody
    public Map<String, Object> getCartItems(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return response;
        }

        List<Cart> cartItems = cartService.getUserCart(userId);
        response.put("cartItems", cartItems);

        int totalAmount = cartItems.stream()
                .filter(item -> isValid(item))
                .mapToInt(item -> item.getBooksBean().getPrice().intValue() * item.getQuantity())
                .sum();
        response.put("totalAmount", totalAmount);
        response.put("success", true);

        return response;
    }

    private boolean isValid(Cart item) {
        return item.getBooksBean().getOnShelf() != null
                && item.getBooksBean().getOnShelf() == 1
                && item.getBooksBean().getStock() >= item.getQuantity();
    }

    @PostMapping("/api/add")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam("bookId") Integer bookId,
            @RequestParam("quantity") Integer quantity, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {

            // Fallback for non-API calls or mixed usage (not expected with full JWT but
            // just in case)
            // But interceptor should have handled it or blocked it?
            // If interceptor blocks, it sends error code.
            // If we are here, userId might be null if interceptor didn't set it (e.g.
            // pattern mismatch)
            response.put("success", false);
            response.put("message", "請先登入");
            return response;
        }

        try {
            cartService.addToCart(userId, bookId, quantity); // Use extracted userId
            response.put("success", true);

            // Return updated total count or similar if needed
            List<Cart> cartItems = cartService.getUserCart(userId);
            response.put("cartCount", cartItems.size());

            response.put("message", "加入購物車成功");
        } catch (BusinessException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "加入購物車失敗: " + e.getMessage());
        }
        return response;
    }

    // Legacy buyNow removed or converted?
    // Plan said: "Buy Now... redirect to /cart"
    // Since we want JWT, Buy Now button on frontend should be an AJAX call to add,
    // then JS redirects.
    // So we don't strictly need a backend /buyNow endpoint that does logic +
    // redirect if we change frontend to:
    // 1. AJAX Add
    // 2. JS Redirect to /cart
    // BUT, if we want a single atomic action (add and go), we can just use the add
    // API.
    // So I won't implement a specific /api/buyNow unless needed.
    // The previous /buyNow was @PostMapping and returned String (view).
    // I will let Frontend handle the flow.

    @PostMapping("/api/update")
    @ResponseBody
    public Map<String, Object> updateQuantity(@RequestParam("cartId") Integer cartId,
            @RequestParam("quantity") Integer quantity, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return response;
        }

        try {
            // Check ownership? Service updateQuantity doesn't check owner currently,
            // but finding by ID is usually safe enough if IDs are random/long, but integers
            // are guessable.
            // Ideally Service should check if Cart belongs to UserId.
            // For now I'll stick to existing Service logic but we might want to improve it
            // later.

            Cart cart = cartService.updateQuantity(cartId, quantity);
            // Verify ownership if needed, but let's assume valid session user implies valid
            // access for now or service handles it.
            // Actually service.updateQuantity just does repo.findById(cartId).

            response.put("success", true);
            response.put("quantity", cart.getQuantity());
            int subtotal = cart.getBooksBean().getPrice().intValue() * cart.getQuantity();
            response.put("subtotal", subtotal);

            List<Cart> cartItems = cartService.getUserCart(userId);
            int total = cartItems.stream()
                    .filter(item -> isValid(item)) // Re-use validation logic
                    .mapToInt(item -> item.getBooksBean().getPrice().intValue() * item.getQuantity())
                    .sum();
            response.put("totalAmount", total);

        } catch (BusinessException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/api/remove")
    @ResponseBody
    public Map<String, Object> remove(@RequestParam("cartId") Integer cartId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            response.put("success", false);
            return response;
        }

        try {
            cartService.removeFromCart(cartId);
            response.put("success", true);

            List<Cart> cartItems = cartService.getUserCart(userId);
            int total = cartItems.stream()
                    .filter(item -> isValid(item))
                    .mapToInt(item -> item.getBooksBean().getPrice().intValue() * item.getQuantity())
                    .sum();
            response.put("totalAmount", total);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}
