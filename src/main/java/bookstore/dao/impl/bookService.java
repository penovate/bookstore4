package bookstore.dao.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import bookstore.bean.BooksBean;
import bookstore.dao.BookDao;

public class bookService {

	BookDao bookDao = new BookDao();

	// ------select all books-----------
	public List<BooksBean> selectAllBooks() {
		List<BooksBean> bookslist = bookDao.selectAllBooks();
		return bookslist;
	}

	// Select book by id-----------------
	public BooksBean selectBookByIdS(Integer bookIdStr) {
		BooksBean book = bookDao.selectBooksById(bookIdStr);
		return book;
	}

	// select by isbn
	public Boolean selectBookByisbn(String isbnStr) {
		Boolean check = null;
		BookDao bookDao = new BookDao();
		if (bookDao.selectBooksByIsbn(isbnStr) != null) {
			check = true;
		} else {
			check = false;
		}
		return check;
	}

	// ------insertBook-----------
	public BooksBean inserttBook(BooksBean booksBean) {
		BooksBean bookInsert = booksBean;
		bookDao.insertBooks(booksBean);

/*
			// genres轉型
			Integer genres = null;
			if (genresStr != null && !genresStr.isBlank()) {
				genres = Integer.valueOf(genresStr.trim());
			}

			// price轉型
			BigDecimal price = null;
			if (pressStr != null && !pressStr.isBlank()) {
				price = new BigDecimal(priceStr.trim());
			}

			// stock轉型
			Integer stock = null;
			if (stockStr != null && !stockStr.isBlank()) {
				stock = Integer.valueOf(stockStr.trim());
			}

			// translator保留空白
			if (translatorStr == null || translatorStr.isBlank()) {
				translatorStr = "-";
			}
			*/

		return bookInsert;
	}

	// UpDate Book-----------------
	public BooksBean upDateBook(String bookIdStr, String bookNameStr, String authorStr, String translatorStr,
			String genreStr, String pressStr, String priceStr, String isbnStr, String stockStr, String shortDescStr,
			String onShelfStr) {

		// bookId轉型
		Integer bookId = null;
		if (bookIdStr != null && !bookIdStr.isBlank()) {
			bookId = Integer.valueOf(bookIdStr.trim());

		}

		// genres轉型
		Integer genres = null;
		if (genreStr != null && !genreStr.isBlank()) {
			genres = Integer.valueOf(genreStr.trim());
		}

		// price轉型
		BigDecimal price = null;
		if (pressStr != null && !pressStr.isBlank()) {
			price = new BigDecimal(priceStr.trim());
		}

		// stock轉型
		Integer stock = null;
		if (stockStr != null && !stockStr.isBlank()) {
			stock = Integer.valueOf(stockStr.trim());
		}

		// translator保留空白
		if (translatorStr == null || translatorStr.isBlank()) {
			translatorStr = "-";
		}

		// onShelf轉型
		Boolean onShelf = null;
		if (onShelfStr != null && !onShelfStr.isBlank()) {
			onShelf = Boolean.valueOf(onShelfStr);
		}
		BooksBean bookUpdate = new BooksBean();
		BookDao bookStoreDao = new BookDao();
		bookUpdate = bookStoreDao.upDateBook(bookId, bookNameStr, authorStr, translatorStr, genres, price, isbnStr,
				stock, shortDescStr, pressStr, onShelf);
		return bookUpdate;
	}

	// delete book-----------------
	public void deleteBook(String bookIdStr) {
		Integer bookId = null;
		if (bookIdStr != null && !bookIdStr.isBlank()) {
			bookId = Integer.valueOf(bookIdStr.trim());
		}
		BookDao bookStoreDao = new BookDao();
		bookStoreDao.deleteBooks(bookId);

	}

	public boolean isbnCheck(String isbnStr) {

		return false;

	}
}
