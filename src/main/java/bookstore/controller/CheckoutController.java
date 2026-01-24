package bookstore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import bookstore.dto.CheckoutRequest;
import bookstore.service.ECPayService;
import bookstore.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import bookstore.bean.OrderItem;
import bookstore.bean.Orders;

@Controller
@RequestMapping("/orders")
public class CheckoutController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ECPayService ecPayService;

    // 建立訂單並根據付款方式產生綠界參數
    @PostMapping("/checkout")
    @ResponseBody
    public Map<String, Object> checkout(@RequestBody CheckoutRequest checkoutRequest, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return response;
        }

        try {
            Orders order = orderService.createOrderFromCart(userId, checkoutRequest);

            response.put("success", true);
            response.put("orderId", order.getOrderId());

            response.put("orderId", order.getOrderId());

            System.out.println("DEBUG: Payment Method in Order: " + order.getPaymentMethod());

            // 建立要傳入綠界的商品明細字串
            StringBuilder itemNameBuilder = new StringBuilder();
            List<OrderItem> items = orderService.getOrderItemsByOrderId(order.getOrderId());
            if (items != null && !items.isEmpty()) {
                for (OrderItem item : items) {
                    if (itemNameBuilder.length() > 0) {
                        itemNameBuilder.append("#");
                    }
                    String bookName = "未知書籍";
                    if (item.getBooksBean() != null) {
                        bookName = item.getBooksBean().getBookName();
                    }
                    itemNameBuilder.append(bookName).append(" x ").append(item.getQuantity());
                }
            }
            System.out.println("DEBUG: Generated ECPay ItemName: " + itemNameBuilder.toString());

            // 處理信用卡付款流程
            if ("信用卡".equals(order.getPaymentMethod())) {
                System.out.println("DEBUG: Entering ECPay block");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String tradeDate = sdf.format(order.getCreatedAt());

                // 產生 MerchantTradeNo (長度需在 20 字元內)，使用結帳當前的時間戳，確保不會重複造成無法結帳
                String merchantTradeNo = "Test" + System.currentTimeMillis();

                // 設定回傳網址 (本地測試需透過 ngrok 改為公開網址)
                String returnURL = "https://unpreferable-unmeteorologic-ruthann.ngrok-free.dev/orders/ecpay/return";
                String clientBackURL = "http://localhost:5173/dev/user/orders";

                Map<String, String> ecpayParams = ecPayService.genAioCheckOutALL(
                        merchantTradeNo,
                        tradeDate,
                        order.getFinalAmount().setScale(0, RoundingMode.HALF_UP).toString(),
                        "Bookstore Order #" + order.getOrderId(),
                        itemNameBuilder.length() > 0 ? itemNameBuilder.toString() : "網路書店書籍一批",
                        returnURL,
                        clientBackURL);

                response.put("ecpayParams", ecpayParams);
                response.put("paymentUrl", ECPayService.ECPAY_PAYMENT_URL);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "結帳失敗: " + e.getMessage());
        }

        return response;
    }

    // 接收綠界付款結果回傳 (Server Side Callback)
    @PostMapping("/ecpay/return")
    @ResponseBody
    public String ecpayReturn(@RequestParam Map<String, String> params) {
        if (!ecPayService.verifyCheckMacValue(params)) {
            return "0|CheckMacValue Error";
        }

        if ("1".equals(params.get("RtnCode"))) {
            String merchantTradeNo = params.get("MerchantTradeNo");
            try {
                // 解析 OrderId
                int tIndex = merchantTradeNo.indexOf('T');
                Integer orderId = Integer.parseInt(merchantTradeNo.substring(3, tIndex));
                orderService.updatePaymentStatus(orderId, "已付款");
                return "1|OK";
            } catch (Exception e) {
                return "0|Order Parsing Error";
            }
        }
        return "1|OK";
    }

    // 接收綠界地圖回傳(CVS Map)，指定 produces 確保中文不會變成亂碼
    // ECPay Map回傳接收
    @CrossOrigin(origins = { "https://logistics-stage.ecpay.com.tw",
            "https://payment-stage.ecpay.com.tw" }, allowCredentials = "true")
    @PostMapping(value = "/ecpay/mapReturn")
    @ResponseBody
    public String ecpayMapReturn(@RequestParam Map<String, String> params) {

        String storeId = params.get("CVSStoreID");
        String storeName = params.get("CVSStoreName");
        String address = params.get("CVSAddress");

        // 2. 回傳 JS 代碼通知 Vue 視窗
        return "<html><body><script>" +
                "window.opener.postMessage({ type: 'STORE_SELECTED', storeId: '" + storeId +
                "', storeName: '" + storeName + "', address: '" + address + "' }, '*');" +
                "window.close();" +
                "</script></body></html>";
    }

    // 產生並導向綠界物流地圖
    @PostMapping(value = "/ecpay/map", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String ecpayMap(@RequestParam(required = false, defaultValue = "UNIMART") String logisticsSubType) {

        // 確保使用正確的 ServerReplyURL (必須是綠界伺服器能連到的網址)
        String serverReplyURL = "https://unpreferable-unmeteorologic-ruthann.ngrok-free.dev/orders/ecpay/mapReturn";

        // 使用 Service 生成參數
        Map<String, String> params = ecPayService.genCvsMapParams(serverReplyURL);

        // 如果前端有傳送特定超商類型 (FAMI/UNIMART)，則覆蓋
        if (logisticsSubType != null) {
            params.put("LogisticsSubType", logisticsSubType);
        }

        // 建立自動送出的 Hidden Form HTML
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<form id='mapForm' action='").append(ECPayService.ECPAY_MAP_URL).append("' method='POST'>");
        params.forEach((k, v) -> {
            html.append("<input type='hidden' name='").append(k).append("' value='").append(v).append("' />");
        });
        html.append("</form>");
        html.append("<script>document.getElementById('mapForm').submit();</script>");
        html.append("</body></html>");

        return html.toString();
    }
}