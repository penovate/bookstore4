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

@WebServlet("/InsertBook")
public class InsertBook extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public InsertBook() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		genreService genreService = new genreService();
		List<GenreBean> genresList = genreService.getAllGenres();
		request.setAttribute("genreList", genresList);
		request.getRequestDispatcher("/books/InsertBook.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BooksBean bookInsert = new BooksBean();

		request.setCharacterEncoding("UTF-8");
		genreService genreService = new genreService();
		List<GenreBean> genreList = genreService.getAllGenres();
		String bookNameStr = request.getParameter("bookName");
		String authorStr = request.getParameter("author");
		String translatorStr = request.getParameter("translator");
		String genresStr = request.getParameter("genre");
		String priceStr = request.getParameter("price");
		String isbnStr = request.getParameter("isbn");
		String stockStr = request.getParameter("stock");
		String shortDescStr = request.getParameter("short_desc");
		String pressStr = request.getParameter("press");

		bookService bookService = new bookService();
		bookInsert = bookService.inserttBook(bookNameStr, authorStr, translatorStr, pressStr, genresStr, priceStr,
				isbnStr, stockStr, shortDescStr);
		request.setAttribute("bookInsert", bookInsert);
		request.setAttribute("genreList", genreList);
		request.getRequestDispatcher("/books/InsertSuccess.jsp").forward(request, response);
	}

}