package bookstore.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bookstore.bean.Orders;
import bookstore.dao.impl.OrderService;

@WebServlet("/GetAllOrders")
public class GetAllOrders extends HttpServlet {
    private static final long serialVersionUID = 1L;
    OrderService orderService = new OrderService();
   
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 查詢所有訂單
        List<Orders> orders = orderService.getAllOrders();
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/order/GetAllOrders.jsp").forward(request, response);
    }
}
