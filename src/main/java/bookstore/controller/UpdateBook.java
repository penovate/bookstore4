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

@WebServlet("/UpdateBook")
public class UpdateBook extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UpdateBook() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer bookid = Integer.parseInt(request.getParameter("bookId"));
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
		GenreBean genreBean = new GenreBean();
		BooksBean book = new BooksBean();
		bookService bookService = new bookService();
		genreService genreService = new genreService();
		Integer bookId = Integer.parseInt(request.getParameter("bookId"));
		BigDecimal price = new BigDecimal(request.getParameter("price"));
		Integer stock = Integer.parseInt(request.getParameter("stock"));
		Integer genreId = Integer.parseInt(request.getParameter("genre"));
		genreBean = genreService.selectGenreById(genreId);
		List<GenreBean> genresList = genreService.getAllGenres();
		book.setBookId(bookId);
		book.setBookName(request.getParameter("bookName"));
		book.setAuthor(request.getParameter("author"));
		book.setTranslator(request.getParameter("translator"));
		book.setPrice(price);
		book.setGenreBean(genreBean);
		book.setIsbn(request.getParameter("isbn"));
		book.setStock(stock);
		book.setShortDesc(request.getParameter("short_desc"));
		book.setPress(request.getParameter("press"));
		Boolean isBoolean = Boolean.parseBoolean(request.getParameter("on_shelf"));
		book.setOnShelf(isBoolean);
		BooksBean booksBean = bookService.upDateBook(book);
		request.setAttribute("bookUpdate", booksBean);
		request.setAttribute("genresList", genresList);
		request.getRequestDispatcher("/books/UpdateSuccess.jsp").forward(request, response);
	}

}
