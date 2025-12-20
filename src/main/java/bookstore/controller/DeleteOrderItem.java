package bookstore.controller;

import java.io.IOException;

import bookstore.dao.impl.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/DeleteOrderItem")
public class DeleteOrderItem extends HttpServlet {
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

        String orderId = request.getParameter("orderId");
        try {
            Integer orderItemId = Integer.parseInt(request.getParameter("orderItemId"));
            orderService.deleteOrderItem(orderItemId);

            // 刪除後直接回明細頁 (GetOrder 會重新查詢)
            response.sendRedirect("GetOrder?id=" + orderId);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("GetOrder?id=" + orderId);
        }
    }
}
