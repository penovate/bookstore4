package bookstore.controller;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bookstore.bean.OrderItem;
import bookstore.bean.Orders;
import bookstore.dao.impl.OrderService;
import bookstore.util.HibernateUtil;

@WebServlet("/GetOrder")
public class GetOrder extends HttpServlet {
    private static final long serialVersionUID = 1L;
   

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.getCurrentSession();
        OrderService orderService = new OrderService(session);
    
        
        
        String orderIdStr = null; 

        try {
            // 1. 從URL參數('id')獲取 (用於 GetOrder?id=X 或 Redirect)
            orderIdStr = request.getParameter("id"); 

            // 2. 如果URL參數為空，嘗試從 Request 屬性獲取 (用於 InsertOrderItems 的 FORWARD)
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
