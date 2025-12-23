package bookstore.controller;

import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import bookstore.dao.impl.OrderService;
import bookstore.util.HibernateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DeleteOrder")
public class DeleteOrder extends HttpServlet {
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

            Integer orderId = Integer.parseInt(request.getParameter("id"));
            orderService.deleteOrder(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 刪除後回列表
        response.sendRedirect("GetAllOrders");
    }
}
