package bookstore.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bookstore.bean.OrderItem;
import bookstore.bean.Orders;
import bookstore.dao.impl.OrderService;

@WebServlet("/GetOrder")
public class GetOrder extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        super.init();
        orderService = new OrderService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String orderIdStr = null; 

        try {
            // 1. 【修正點】嘗試從 URL 參數 ('id') 獲取 (用於 GetOrder?id=X 或 Redirect)
            orderIdStr = request.getParameter("id"); 

            // 2. 【修正點】如果 URL 參數為空，嘗試從 Request 屬性獲取 (用於 InsertOrderItems 的 FORWARD)
            if (orderIdStr == null) {
                Integer orderIdAttr = (Integer) request.getAttribute("orderId");
                if (orderIdAttr != null) {
                    orderIdStr = orderIdAttr.toString();
                }
            }
            
            // 3. 如果還是沒有 ID，則導向所有訂單列表
            if (orderIdStr == null || orderIdStr.isEmpty()) {
                response.sendRedirect("GetAllOrders");
                return;
            }

            // 4. 執行查詢
            Integer orderId = Integer.parseInt(orderIdStr);
            Orders order = orderService.getOrderById(orderId);
            
            if (order == null) {
                 // 找不到訂單，導向列表
                 response.sendRedirect("GetAllOrders?error=OrderNotFound");
                 return;
            }
            
            List<OrderItem> items = orderService.getOrderItems(orderId);

            request.setAttribute("order", order);
            request.setAttribute("items", items);
            request.getRequestDispatcher("/order/GetOrder.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            // ID 格式錯誤
            response.sendRedirect("GetAllOrders?error=InvalidIdFormat");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("GetAllOrders?error=ServerError");
        }
    }

}
