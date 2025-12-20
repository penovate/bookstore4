package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import bookstore.bean.ReviewBean;
import bookstore.dao.impl.ReviewsDAOImpl;

@WebServlet("/GetAllReviews")
public class GetAllReviews extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ReviewsDAOImpl dao = new ReviewsDAOImpl();
		List<ReviewBean> reviews = dao.selectAllReviews();
		
		request.setAttribute("reviews", reviews);
		request.getRequestDispatcher("/reviews/ReviewList.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
