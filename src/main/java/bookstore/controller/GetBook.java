package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import bookstore.bean.BooksBean;
import bookstore.dao.impl.bookService;

@WebServlet("/GetBook")
public class GetBook extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GetBook() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer bookIdStr = Integer.parseInt(request.getParameter("bookId"));
		bookService bookService = new bookService();
		BooksBean book = new BooksBean();
		book = bookService.selectBookByIdS(bookIdStr);
		request.setAttribute("book", book);
		request.getRequestDispatcher("/books/GetBook.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

}
