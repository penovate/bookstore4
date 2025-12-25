package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import bookstore.dao.impl.bookService;

@WebServlet("/UpdateShelfStatus")
public class UpdateShelfStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String bookIdstr = request.getParameter("bookId");
		String statusStr = request.getParameter("status");

		Boolean isSuccess = false;

		try {
			if (bookIdstr != null && statusStr != null) {
				int bookId = Integer.parseInt(bookIdstr);
				boolean newStatus = Boolean.parseBoolean(statusStr);

				bookService bookService = new bookService();
				isSuccess = bookService.updateOnShelfStatus(bookId, newStatus);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		if (isSuccess) {
			response.getWriter().write("success");
		} else {
			response.getWriter().write("fail");
		}
	}

}
