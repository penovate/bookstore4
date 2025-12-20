package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;

import bookstore.dao.impl.ReviewsDAOImpl;

@WebServlet("/DeleteReview")
public class DeleteReview extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String message = "";
		
		String reviewId = request.getParameter("review_id");
		
		if (reviewId == null || reviewId.isEmpty()) {
			message = "刪除失敗：缺少評價ID！";
		} else {
			ReviewsDAOImpl dao = new ReviewsDAOImpl();
			int count = dao.deleteReview(reviewId); 
			
			if (count > 0) {
				message = "評價資料刪除成功！";
			} else {
				message = "刪除失敗：找不到該評價資料！";
			}
		}
		
		String encodedMessage = URLEncoder.encode(message, "UTF-8");
        
		response.sendRedirect(request.getContextPath() 
		    + "/GetAllReviews?status=success&msg=" + encodedMessage);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
