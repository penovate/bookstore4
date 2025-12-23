package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;

import bookstore.bean.ReviewBean;
import bookstore.dao.impl.ReviewsDAOImpl;

@WebServlet("/UpdateReview")
public class UpdateReview extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// 顯示更新頁面   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String reviewIdStr = request.getParameter("review_id");
		if (reviewIdStr == null || reviewIdStr.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/GetAllReviews?error=缺少評價ID");
			return;
		}

		Integer reviewId = Integer.valueOf(reviewIdStr);
		ReviewsDAOImpl dao = new ReviewsDAOImpl();
		ReviewBean review = dao.selectReviewById(reviewId);
		if (review != null) {
			request.setAttribute("review", review);
			request.getRequestDispatcher("/reviews/ReviewUpdate.jsp").forward(request, response);
		} else {
			response.sendRedirect(request.getContextPath() + "/GetAllReviews?error=找不到該評價資料！");
		}
		
	}

	 // 處理更新
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
        String message = "";
        
        String reviewIdStr = request.getParameter("review_id");
        String userIdStr = request.getParameter("user_id"); 
        String bookIdStr = request.getParameter("book_id");
        String ratingStr = request.getParameter("rating");
        String comment = request.getParameter("comment");
        
        if (reviewIdStr == null || reviewIdStr.isEmpty()) {
            message = "更新失敗！缺少評價ID！";
            response.sendRedirect(request.getContextPath() + "/GetAllReviews?msg=" + URLEncoder.encode(message, "UTF-8"));
            return;
        }

        if (comment == null || comment.trim().isEmpty()) {
            message = "更新失敗！評論內容不能為空白！";
            response.sendRedirect(request.getContextPath() 
                    + "/UpdateReview?review_id=" + reviewIdStr 
                    + "&error=" + URLEncoder.encode(message, "UTF-8"));
            return;
        }

     // === String → Integer（Servlet 的責任）===
        Integer reviewId;
        Integer userId;
        Integer bookId;
        Integer rating;
        
        try {
        		reviewId = Integer.valueOf(reviewIdStr);
            userId   = Integer.valueOf(userIdStr);
            bookId   = Integer.valueOf(bookIdStr);
            rating   = Integer.valueOf(ratingStr);

            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            message = "更新失敗！評分必須是 1~5 的數字！";
            response.sendRedirect(request.getContextPath() 
                    + "/UpdateReview?review_id=" + reviewIdStr
                    + "&error=" + URLEncoder.encode(message, "UTF-8"));
            return;
        }

        // === 組 Bean ===
        ReviewBean review = new ReviewBean();
        review.setReviewId(reviewId);
        review.setUserId(userId);
        review.setBookId(bookId);
        review.setRating(rating);
        review.setComment(comment);
        // createdAt 不動（更新不該改建立時間）
        
        ReviewsDAOImpl dao = new ReviewsDAOImpl();
        int result = dao.updateReview(review);

        try {
        	if (result > 0) {
                message = "評價更新成功！";
            } else {
                message = "更新失敗！找不到該評價或資料未變更！";
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "更新失敗！資料庫錯誤！";
        }
        
        
        response.sendRedirect(request.getContextPath() 
        	    + "/GetAllReviews?status=success&msg=" + URLEncoder.encode(message, "UTF-8"));
    }
}