package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import bookstore.dao.impl.bookService;

@WebServlet("/DeleteBook")
public class DeleteBook extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DeleteBook() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String[] checked = request.getParameterValues("selectedBookId");
		if (checked != null && checked.length > 0) {
			bookService bookService = new bookService();
			for (String bookIdStr : checked) {
				bookService.deleteBook(bookIdStr);
			}
		} else {
			String bookId = request.getParameter("bookId");
			bookService bookService = new bookService();
			bookService.deleteBook(bookId);
		}
		response.sendRedirect(request.getContextPath() + "/GetAllBooks?deleteStatus=success");

	}

}
