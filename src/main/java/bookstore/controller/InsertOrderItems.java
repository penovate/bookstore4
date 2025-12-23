package bookstore.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import bookstore.bean.BooksBean;
import bookstore.bean.OrderItem;
import bookstore.dao.impl.OrderService;
import bookstore.util.HibernateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/InsertOrderItems")
public class InsertOrderItems extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 取得訂單ID
        String orderIdStr = request.getParameter("orderId");
        Integer orderId = null;

        try {
            SessionFactory factory = HibernateUtil.getSessionFactory();
            Session session = factory.getCurrentSession();
            OrderService orderService = new OrderService(session);

            // 確認有沒有取得訂單Id
            if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("未提供有效的訂單編號 (orderId)。");
            }
            // 將orderId轉換為Integer
            orderId = Integer.parseInt(orderIdStr);

            // 建立訂單明細
            List<OrderItem> items = new ArrayList<>();

            // 支援多筆商品
            String[] bookIds = request.getParameterValues("bookId");
            String[] prices = request.getParameterValues("price");
            String[] quantities = request.getParameterValues("quantity");

            if (bookIds != null) {
                for (int i = 0; i < bookIds.length; i++) {
                    if (bookIds[i] != null && !bookIds[i].isEmpty()) {
                        OrderItem item = new OrderItem();
                        BooksBean book = new BooksBean();
                        book.setBookId(Integer.parseInt(bookIds[i]));
                        item.getBooksBean().setBookId(Integer.parseInt(bookIds[i]));
                        item.setQuantity(Integer.parseInt(quantities[i]));
                        item.setPrice(new BigDecimal(prices[i]));
                        item.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
                        items.add(item);
                    }
                }
            }

            orderService.addItemsToOrder(orderId, items);

            // 等等跳轉到查詢單筆訂單的servlet需要訂單ID
            request.setAttribute("orderId", orderId);

            request.getRequestDispatcher("/GetOrder").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "建立訂單明細失敗: " + e.getMessage());
            request.getRequestDispatcher("/GetOrder").forward(request, response);
        }
    }
}
