package bookstore.controller;

import java.io.IOException;
import java.util.List;

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

@WebServlet("/GetAllOrderItems")
public class GetAllOrderItems extends HttpServlet {
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

        // 查詢所有訂單明細
        List<OrderItem> items = orderService.getAllOrderItems();
        request.setAttribute("items", items);
        request.getRequestDispatcher("/order/GetAllOrderItems.jsp").forward(request, response);
    }
}
