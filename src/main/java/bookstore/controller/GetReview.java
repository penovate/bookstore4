package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import bookstore.bean.ReviewBean;
import bookstore.dao.impl.ReviewsDAOImpl;

@WebServlet("/GetReview")
public class GetReview extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String reviewIdStr = request.getParameter("reviewId");
		Integer reviewId = Integer.valueOf(reviewIdStr);
		ReviewsDAOImpl dao = new ReviewsDAOImpl();
		ReviewBean review = dao.selectReviewById(reviewId);
		request.setAttribute("review", review);
		request.getRequestDispatcher("/reviews/GetReview.jsp").forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
