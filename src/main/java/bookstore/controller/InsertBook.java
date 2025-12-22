package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
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
		GenreBean genreBean = new GenreBean();

		String genreIdStr = request.getParameter("genre");
		if (genreIdStr != null && !genreIdStr.isEmpty()) {
			Integer genreId = Integer.parseInt(genreIdStr);
			genreBean.setGenreId(genreId);
		}


		request.setCharacterEncoding("UTF-8");
		BooksBean book = new BooksBean();
		genreService genreService = new genreService();
		List<GenreBean> genreList = genreService.getAllGenres();
		book.setBookName(request.getParameter("bookName"));
		book.setAuthor(request.getParameter("author"));
		book.setTranslator(request.getParameter("translator"));
		book.setGenreBean(genreBean);

		String priceStr = request.getParameter("price");
		if (priceStr != null && !priceStr.isEmpty()) {
			BigDecimal price = new BigDecimal(priceStr);
		book.setPrice(price);
		}
		book.setIsbn(request.getParameter("isbn"));
		book.setStock(Integer.parseInt(request.getParameter("stock")));
		book.setShortDesc(request.getParameter("short_desc"));
		book.setPress(request.getParameter("press"));

		bookService bookService = new bookService();
		bookService.inserttBook(book);
		request.setAttribute("bookInsert", book);
		request.setAttribute("genreList", genreList);
		request.getRequestDispatcher("/books/InsertSuccess.jsp").forward(request, response);
	}

}