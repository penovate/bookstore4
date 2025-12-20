package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import bookstore.bean.ReviewBean;
import bookstore.dao.impl.ReviewsDAOImpl;

@WebServlet("/InsertReview")
public class InsertReview extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String message = "";

        // 從表單取得資料（完全對應 JSP 的 input name）
        String userId = request.getParameter("user_id");
        String bookId = request.getParameter("book_id");
        String rating = request.getParameter("rating");
        String comment = request.getParameter("comment");

        // --- 基本欄位檢查 ---
        if (userId == null || userId.isEmpty() ||
            bookId == null || bookId.isEmpty() ||
            rating == null || rating.isEmpty() ||
            comment == null || comment.isEmpty()) {

            message = "新增失敗！會員編號、書籍編號、評分與評論內容都不能為空白。";
            request.setAttribute("message", message);
            request.getRequestDispatcher("/reviews/ReviewInsert.jsp").forward(request, response);
            return;
        }

        // --- 建立 Bean ---
        ReviewBean review = new ReviewBean();
        review.setUserId(userId);
        review.setBookId(bookId);
        review.setRating(rating);
        review.setComment(comment);

        // --- 呼叫 DAO ---
        ReviewsDAOImpl dao = new ReviewsDAOImpl();
        int result = 0;

        try {
            result = dao.insertReview(review);
        } catch (Exception e) {
            e.printStackTrace();
            message = "新增失敗！資料庫寫入錯誤，請檢查資料是否正確！";
        }

        // --- 新增成功 ---
        if (result > 0) {
            message = "新增書籍評論成功！";
            request.setAttribute("review", review);
            request.setAttribute("message", message);
            request.getRequestDispatcher("/reviews/ReviewInsertFinish.jsp").forward(request, response);
            return;
        }

        // --- 新增失敗 ---
        request.setAttribute("message", message);
        request.getRequestDispatcher("/reviews/ReviewInsert.jsp").forward(request, response);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/reviews/ReviewInsert.jsp");
    }

}

