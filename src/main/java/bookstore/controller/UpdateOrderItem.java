package bookstore.controller;

import java.io.IOException;
import java.math.BigDecimal;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import bookstore.bean.OrderItem;
import bookstore.dao.impl.OrderService;
import bookstore.util.HibernateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/UpdateOrderItem")
public class UpdateOrderItem extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            SessionFactory factory = HibernateUtil.getSessionFactory();
            Session session = factory.getCurrentSession();
            OrderService orderService = new OrderService(session);

            Integer orderItemId = Integer.parseInt(request.getParameter("orderItemId"));
            Integer orderId = Integer.parseInt(request.getParameter("orderId"));
            Integer quantity = Integer.parseInt(request.getParameter("quantity"));
            BigDecimal price = new BigDecimal(request.getParameter("price"));

            OrderItem item = new OrderItem();
            item.setOrderItemId(orderItemId);
            item.getOrders().setOrderId(orderId);
            item.setQuantity(quantity);
            item.setPrice(price);
            item.setSubtotal(price.multiply(new BigDecimal(quantity)));

            orderService.updateOrderItem(item);

            // 成功後轉到成功頁面，並帶上 orderId 供返回使用
            request.setAttribute("orderId", orderId);
            request.getRequestDispatcher("/order/UpdateOrderItemDisplay.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // 失敗暫時回明細頁
            String orderId = request.getParameter("orderId");
            response.sendRedirect("GetOrder?id=" + orderId);
        }
    }
}
