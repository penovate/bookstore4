package bookstore.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import bookstore.bean.BooksBean;
import bookstore.bean.OrderItem;
import bookstore.bean.Orders;
import bookstore.dao.impl.OrderService;
import bookstore.util.HibernateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/InsertOrder")
public class InsertOrder extends HttpServlet {
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

            // 接收表單資料
            String userIdStr = request.getParameter("userId");
            String recipient = request.getParameter("recipientName");
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");
            String paymentMethod = request.getParameter("paymentMethod");

            // 建立訂單物件
            Orders order = new Orders();

            if (userIdStr != null && !userIdStr.isEmpty()) {
                order.getUserBean().setUserId(Integer.parseInt(userIdStr));
            } else {
                throw new ServletException("User ID is required");
            }

            order.setRecipientAt(recipient);
            order.setAddress(address);
            order.setPhone(phone);
            order.setPaymentMethod(paymentMethod);
            // 判斷是否付款
            if (paymentMethod == "貨到付款" || paymentMethod.equals("貨到付款")) {
                order.setPaymentStatus("未付款");
            } else {
                order.setPaymentStatus("已付款");
            }
            ;
            order.setOrderStatus("待出貨");
            order.setTotalAmount(new BigDecimal(0)); // 會由 Service 計算

            // 建立訂單明細
            List<OrderItem> items = new ArrayList<>();

            // 支援多筆商品
            String[] bookIds = request.getParameterValues("bookId");
            String[] quantities = request.getParameterValues("quantity");
            String[] prices = request.getParameterValues("price");

            if (bookIds != null) {
                for (int i = 0; i < bookIds.length; i++) {
                    
                    if (bookIds[i] != null && !bookIds[i].isEmpty()) {
                        OrderItem item = new OrderItem();

                        BooksBean book = new BooksBean();
                        book.setBookId(Integer.parseInt(bookIds[i]));
                    
                        item.setBooksBean(book); 

                        item.setQuantity(Integer.parseInt(quantities[i]));
                        item.setPrice(new BigDecimal(prices[i]));
                       
                        item.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                        
                        items.add(item);
                    }
                }
            }

            orderService.createOrder(order, items);

            request.setAttribute("order", order);
            request.setAttribute("items", items);
            request.getRequestDispatcher("/order/InsertOrderDisplay.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "建立訂單失敗: " + e.getMessage());
            request.getRequestDispatcher("/order/InsertOrderDisplay.jsp").forward(request, response);
        }
    }
}
