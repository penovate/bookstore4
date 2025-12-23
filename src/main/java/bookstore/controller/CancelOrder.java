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

@WebServlet("/CancelOrder")
public class CancelOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.getCurrentSession();
        OrderService orderService = new OrderService(session);

        orderService.processCancelOrder(id); // 執行軟刪除邏輯
        response.sendRedirect("GetAllOrders"); // 刪除後導回主畫面，該訂單就會消失
    }
}
