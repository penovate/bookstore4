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

@WebServlet("/RestoreOrder")
public class RestoreOrder extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        OrderService orderService = new OrderService(session);

        try {
            orderService.processRestoreOrder(id);
            // 還原成功後，因為訂單狀態變了，它會從取消清單消失，所以導回「活動訂單」主頁面
            response.sendRedirect("GetAllOrders"); 
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("GetAllCancelOrders?error=RestoreFailed");
        }
    }
}
