package bookstore.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bookstore.bean.Orders;
import bookstore.dao.impl.OrderService;

@WebServlet("/UpdateOrder")
public class UpdateOrder extends HttpServlet {
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

        try {
            // 接收表單資料
            Integer orderId = Integer.parseInt(request.getParameter("orderId"));
            String recipient = request.getParameter("recipient");
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");
            String paymentMethod = request.getParameter("paymentMethod");
            String paymentStatus = request.getParameter("paymentStatus");
            String orderStatus = request.getParameter("orderStatus");
            // totalAmount需要由訂單明細計算，不能單獨修改

            Orders order = orderService.getOrderById(orderId);
            if (order != null) {
                order.setRecipientAt(recipient);
                order.setAddress(address);
                order.setPhone(phone);
                order.setPaymentMethod(paymentMethod);
                order.setPaymentStatus(paymentStatus);
                order.setOrderStatus(orderStatus);

                orderService.updateOrder(order);

                request.setAttribute("order", order);
                request.getRequestDispatcher("/order/UpdateOrderDisplay.jsp").forward(request, response);
            } else {
                throw new RuntimeException("找不到訂單 ID: " + orderId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("GetAllOrders");
        }
    }
}
