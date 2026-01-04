package bookstore.dao.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import bookstore.bean.BooksBean;
import bookstore.bean.GenreBean;
import bookstore.dao.BookDao;

public class bookService {

	BookDao bookDao = new BookDao();

	// ------select all books-----------
	public List<BooksBean> selectAllBooks() {
		List<BooksBean> bookslist = bookDao.selectAllBooks();

		/*
		 * 資料驗證區域
		 */
		return bookslist;
	}

	// Select book by id-----------------
	public BooksBean selectBookByIdS(Integer bookIdStr) {
		/*
		 * 資料驗證區域
		 */
		BooksBean book = bookDao.selectBooksById(bookIdStr);

		return book;
	}

	// select by isbn
	public Boolean selectBookByisbn(String isbnStr) {
		/*
		 * 資料驗證區域
		 */
		Boolean check = null;
		BooksBean book = bookDao.selectBooksByIsbn(isbnStr);
		if (book != null) {
			check = true;
		} else {
			check = false;
		}
		return check;
	}

	// ------insertBook-----------
	public BooksBean inserttBook(BooksBean booksBean) {
		/*
		 * 資料驗證區域
		 */
		BooksBean bookInsert = bookDao.insertBooks(booksBean);

		/*
		 * // genres轉型 Integer genres = null; if (genresStr != null &&
		 * !genresStr.isBlank()) { genres = Integer.valueOf(genresStr.trim()); }
		 * 
		 * // price轉型 BigDecimal price = null; if (pressStr != null &&
		 * !pressStr.isBlank()) { price = new BigDecimal(priceStr.trim()); }
		 * 
		 * // stock轉型 Integer stock = null; if (stockStr != null && !stockStr.isBlank())
		 * { stock = Integer.valueOf(stockStr.trim()); }
		 * 
		 * // translator保留空白 if (translatorStr == null || translatorStr.isBlank()) {
		 * translatorStr = "-"; }
		 */

		return bookInsert;
	}

	// UpDate Book-----------------
	public BooksBean upDateBook(BooksBean booksBean) {
		/*
		 * 資料驗證區域
		 */
		BooksBean beanUpdate = bookDao.upDateBook(booksBean);
		return beanUpdate;
	}

	// delete book-----------------
	public void deleteBook(Integer bookIdStr) {
		/*
		 * 資料驗證區域
		 */
		bookDao.deleteBooks(bookIdStr);

	}

	// select genre
	public GenreBean selectGenreById(Integer genreId) {
		/*
		 * 資料驗證、業務邏輯區域
		 */
		GenreBean genreBean = bookDao.selectGenreById(genreId);
		return genreBean;
	}

	public boolean isbnCheck(String isbnStr) {

		return false;
	}

	// update onShelf
	public boolean updateOnShelfStatus(int bookId, boolean newStatus) {
		/*
		 * 資料驗證區域
		 */
		BookDao bookDao = new BookDao();
		boolean isSuccess = bookDao.updateOnShelfStatus(bookId, newStatus);
		return isSuccess;
	}

}
