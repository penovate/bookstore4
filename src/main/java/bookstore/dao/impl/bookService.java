package bookstore.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import bookstore.bean.BooksBean;
import bookstore.bean.GenreBean;
import bookstore.dao.BookDao;
import bookstore.exceptionCenter.BaseException;

public class bookService {
	private static final String REGEX_DIGITS = "^\\d+$";
	BookDao bookDao = new BookDao();

	// ------select all books-----------
	public List<BooksBean> selectAllBooks() {
		List<BooksBean> bookslist = bookDao.selectAllBooks();
		if (bookslist == null) {
			throw new BaseException(404, "無書籍資料。");
		}
		return bookslist;
	}

	// Select book by id-----------------
	public BooksBean selectBookByIdS(Integer bookIdStr) {

		if (bookIdStr == null) {
			throw new BaseException(400, "書籍ID不可為空白。");
		}

		String bookid = String.valueOf(bookIdStr);
		if (!bookid.matches(REGEX_DIGITS)) {
			throw new BaseException(400, "ID格式錯誤:只允許輸入正整數。");
		}

		BooksBean book = bookDao.selectBooksById(bookIdStr);
		if (book == null) {
			throw new BaseException(404, "無ID相關書籍資料。");
		}

		return book;
	}

	// select by isbn
	public Boolean selectBookByisbn(String isbnStr) {

		if (isbnStr == null || isbnStr.trim().isEmpty()) {
			throw new BaseException(400, "ISBN不可為空白");
		}
		if (!isbnStr.matches("^[0-9]{13}$")) {
			throw new BaseException(400, "ISBN格式錯誤，必須為13位數字");
		}
		BooksBean book = bookDao.selectBooksByIsbn(isbnStr);

		return book != null;
	}

	public Boolean selectOnShelfById(Integer bookId) {
		if (bookId == null) {
			throw new BaseException(400, "書籍ID不可為空白");
		}

		return bookDao.selectOnShelfById(bookId);
	}

	// ------insertBook-----------
	public BooksBean insertBook(BooksBean book) {
		if (book == null) {
			throw new BaseException(400, "書籍物件不可為空");
		}
		if (book.getBookName() == null || book.getBookName().trim().isEmpty()) {
			throw new BaseException(400, "書名為必填欄位");
		}
		if (book.getIsbn() == null || !book.getIsbn().matches("^[0-9]{13}$")) {
			throw new BaseException(400, "ISBN格式錯誤，必須為13位數字。");
		}
		if (book.getPrice() == null | book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
			throw new BaseException(400, "價格不可為空白，且不能為負數");
		}
		if (book.getGenreBean() == null || book.getGenreBean().getGenreId() == null) {
			throw new BaseException(400, "請選擇書籍分類");
		}
		BooksBean result = bookDao.selectBooksByIsbn(book.getIsbn());
		if (result != null) {
			throw new BaseException(409, "新增失敗，已有相同ISBN的書籍資料。");
		}
		BooksBean bookInsert = bookDao.insertBooks(book);

		return bookInsert;
	}

	// UpDate Book-----------------
	public BooksBean upDateBook(BooksBean book) {
		if (book == null) {
			throw new BaseException(400, "書籍物件不可為空");
		}
		BooksBean existBook = bookDao.selectBooksByIsbn(book.getIsbn());
		if (existBook != null) {
			throw new BaseException(409, "修改失敗，該ISBN已被其他書籍使用");
		}
		if (book.getBookName() == null || book.getBookName().trim().isEmpty()) {
			throw new BaseException(400, "書名為必填欄位");
		}
		if (book.getIsbn() == null ||! book.getIsbn().matches("^[0-9]{13}$")) {
			throw new BaseException(400, "ISBN格式錯誤，必須為13位數字。");
		}
		if (book.getPrice() == null | book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
			throw new BaseException(400, "價格不可為空白，且不能為負數");
		}

		BooksBean beanUpdate = bookDao.upDateBook(book);
		return beanUpdate;
	}

	// delete book-----------------
	public void deleteBook(Integer bookIdStr) {
		if (bookIdStr == null || bookIdStr <= 0) {
			throw new BaseException(400, "書籍ID不可為空白");
		}
		if (bookDao.selectOnShelfById(bookIdStr)==true) {
			throw new BaseException(409, "刪除失敗，該書目前為上架中，請先將書籍下架");
		}
		if (bookDao.selectReviewByBookId(bookIdStr)) {
			throw new BaseException(409, "刪除失敗，該書已有平價內容，不可刪除");
		}
		if (bookDao.selectOrderItemByBookId(bookIdStr)) {
			throw new BaseException(409, "刪除失敗，該書已有訂單紀錄，不可刪除");
		}

		bookDao.deleteBooks(bookIdStr);

	}

	// select genre
	public GenreBean selectGenreById(Integer genreIdStr) {
		if (genreIdStr == null) {
			throw new RuntimeException("類別ID不可為空白");
		}
		String genreId = String.valueOf(genreIdStr);
		if (!genreId.matches(genreId)) {
			throw new RuntimeException("ID格式錯誤，只允許輸入正整數");
		}

		GenreBean genreBean = bookDao.selectGenreById(genreIdStr);
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

	public class BusinessException extends RuntimeException {
		private int errorCode;

		public BusinessException(String message, int errorCode) {
			super(message);
			this.errorCode = errorCode;
		}

		public int getErrorCode() {
			return errorCode;
		}
	}

}
