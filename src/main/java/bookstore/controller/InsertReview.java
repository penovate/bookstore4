package bookstore.controller;

import java.io.IOException;

import bookstore.bean.BooksBean;
import bookstore.bean.ReviewBean;
import bookstore.dao.impl.ReviewsDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/InsertReview")
public class InsertReview extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String message = "";

        // 從表單取得資料（完全對應 JSP 的 input name）
        String userIdStr = request.getParameter("user_id");
        String bookIdStr = request.getParameter("book_id");
        String ratingStr = request.getParameter("rating");
        String comment = request.getParameter("comment");

        // --- 基本欄位檢查 ---
        if (userIdStr == null || userIdStr.isEmpty() ||
            bookIdStr == null || bookIdStr.isEmpty() ||
            ratingStr == null || ratingStr.isEmpty() ||
            comment == null || comment.isEmpty()) {

            message = "新增失敗！會員編號、書籍編號、評分與評論內容都不能為空白。";
            request.setAttribute("message", message);
            request.getRequestDispatcher("/reviews/ReviewInsert.jsp").forward(request, response);
            return;
        }
        
        // String → Integer（Servlet 的責任）
        Integer userId;
        Integer bookId;
        Integer rating;

        try {
            userId = Integer.valueOf(userIdStr);
            bookId = Integer.valueOf(bookIdStr);
            rating = Integer.valueOf(ratingStr);

            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            message = "新增失敗！評分必須是 1~5 的數字。";
            request.setAttribute("message", message);
            request.getRequestDispatcher("/reviews/ReviewInsert.jsp")
                   .forward(request, response);
            return;
        }

        // --- 建立 Bean ---
        ReviewBean review = new ReviewBean();
        BooksBean book = new BooksBean();
        book.setBookId(bookId);
        review.setBook(book);
        review.setBookId(bookId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment);

        // 很重要：建立時間
        review.setCreatedAt(java.time.LocalDateTime.now());
        
        // --- 呼叫 DAO ---
        ReviewsDAOImpl dao = new ReviewsDAOImpl();
        int result;

        try {
            result = dao.insertReview(review);
        } catch (Exception e) {
            e.printStackTrace();
            message = "新增失敗！資料庫寫入錯誤，請檢查資料是否正確！";
            request.setAttribute("message", message);
            request.getRequestDispatcher("/reviews/ReviewInsert.jsp")
                   .forward(request, response);
            return;
        }

        // 結果處理
        // --- 新增成功 ---
        if (result > 0) {
            message = "新增書籍評論成功！";
            request.setAttribute("review", review);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/reviews/ReviewInsertFinish.jsp").forward(request, response);
            return;
        } else {
            message = "新增失敗！請稍後再試。";
            request.setAttribute("message", message);
            request.getRequestDispatcher("/reviews/ReviewInsert.jsp")
                   .forward(request, response);
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/reviews/ReviewInsert.jsp");
    }

}

