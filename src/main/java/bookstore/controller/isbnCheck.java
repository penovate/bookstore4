package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import bookstore.bean.BooksBean;
import bookstore.dao.impl.bookService;

@WebServlet("/isbnCheck")
public class isbnCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;
	bookService bookService = new bookService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");
		String isbn = request.getParameter("isbn");
		Boolean isbncheck = bookService.selectBookByisbn(isbn);
		String isbnChckready;
		if (isbncheck) {
			isbnChckready = "true";
		} else {
			isbnChckready = "false";
		}
		System.out.println(isbnChckready);
		response.getWriter().write(isbnChckready);
		response.getWriter().flush();

	}

}
