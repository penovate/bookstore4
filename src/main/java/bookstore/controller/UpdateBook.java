package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import bookstore.bean.BooksBean;
import bookstore.bean.GenreBean;
import bookstore.dao.impl.bookService;
import bookstore.dao.impl.genreService;

@WebServlet("/UpdateBook")
public class UpdateBook extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UpdateBook() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String bookid = request.getParameter("bookId");
		bookService bookService = new bookService();
		genreService genreService = new genreService();
		BooksBean book = bookService.selectBookByIdS(bookid);
		List<GenreBean> genresList = genreService.getAllGenres();
		request.setAttribute("book", book);
		request.setAttribute("genresList", genresList);
		request.getRequestDispatcher("/books/UpdateBook.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		genreService genreService = new genreService();
		List<GenreBean> genresList = genreService.getAllGenres();
		bookService bookService = new bookService();
		String BookIdStr = request.getParameter("bookId");
		String BookNameStr = request.getParameter("bookName");
		String authorStr = request.getParameter("author");
		String translatorStr = request.getParameter("translator");
		String genreStr = request.getParameter("genre");
		String pressStr = request.getParameter("press");
		String priceStr = request.getParameter("price");
		String isbnStr = request.getParameter("isbn");
		String stockStr = request.getParameter("stock");
		String shortDescStr = request.getParameter("short_desc");
		String onShelfStr = request.getParameter("on_shelf");
		BooksBean bookUpdate = new BooksBean();
		bookUpdate = bookService.upDateBook(BookIdStr, BookNameStr, authorStr, translatorStr, genreStr, pressStr,
				priceStr, isbnStr, stockStr, shortDescStr, onShelfStr);
		request.setAttribute("bookUpdate", bookUpdate);
		request.setAttribute("genresList", genresList);
		request.getRequestDispatcher("/books/UpdateSuccess.jsp").forward(request, response);
	}

}
