package bookstore.dao.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bookstore.bean.BooksBean;
import bookstore.dao.BookDao;
import bookstore.exceptionCenter.BusinessException;
import bookstore.repository.BookRepository;

@Service
public class bookService {
	private static final String REGEX_DIGITS = "^\\d+$";
	BookDao bookDao = new BookDao();
	private static final Logger log = LoggerFactory.getLogger(bookService.class);
	@Autowired
	private BookRepository bookRepo;
	@Autowired
	private BooksBean booksBean;

	// ------select all books-----------
	public List<BooksBean> selectAllBooks() {
		List<BooksBean> bookList = bookRepo.findAll();
		if (bookList != null && !bookList.isEmpty()) {
			log.warn("查詢全部失敗 - 資料庫內無任何書籍資料");
			throw new BusinessException(404, "無任何書籍資料");
		}
		log.info("查詢全部成功 書籍數量: {} 筆", bookList.size());
		return bookList;
	}

	// Select book by id-----------------
	public BooksBean selectBookByIdS(Integer bookIdStr) {
		Optional<BooksBean> opt = bookRepo.findById(bookIdStr);

		if (opt.isPresent()) {
			log.info("查詢單筆成功 - 查詢ID:{}", bookIdStr);
			return opt.get();
		} else {
			log.warn("查詢單筆失敗 - 無該ID相關書籍資料 - 查詢ID:{}", bookIdStr);
			throw new BusinessException(404, "無該ID相關書籍資料");
		}

	}

	// select by isbn
	public BooksBean selectBookByisbn(String isbnStr) {
		Optional<BooksBean> opt = bookRepo.findByIsbn(isbnStr);
		if (opt.isPresent()) {
			log.info("查詢ISBN成功 - 查詢ISBN:{}", isbnStr);
			return opt.get();
		} else {
			log.warn("查詢ISBN失敗 - 無相關ISBN書籍 - 查詢ISBN:{}", isbnStr);
			throw new BusinessException(404, "無相關ISBN書籍資料");
		}
	}

	// select by genre
	public List<BooksBean> selectByGenre(Integer genreId) {
		List<BooksBean> bookList = bookRepo.findGenreBeanByGenreId(genreId);
		if (bookList != null && !bookList.isEmpty()) {
			log.info("查詢成功 - 書籍數量:{}筆 - 類型ID:{}", bookList.size(), genreId);
			return bookList;
		} else {
			log.warn("查詢失敗 - 無該類型相關書籍資料");
			throw new BusinessException(404, "無該類型相關書籍資料");
		}
	}

	// select by onShelf
	public Boolean selectOnShelfById(Integer bookId) {
		Optional<BooksBean> opt = bookRepo.findById(bookId);
		if (opt.isPresent()) {
			Boolean status = opt.get().getOnShelf();
			log.info("查詢成功 - 查詢ID:{} - 上下架狀態:{}", bookId, status);
			return status;

		} else {
			log.warn("查詢失敗 - 無該ID相關書籍資料 - 查詢ID:{}", bookId);
			throw new BusinessException(404, "無該ID相關書籍資料");
		}
	}

	// insertbook
	public BooksBean insertBook(BooksBean book) {
		String isbnRegex = "^\\d{13}$";
		String priceRegex = "^[1-9]\\d*$";
		Optional<BooksBean> insertBook = bookRepo.findByIsbn(book.getIsbn());
		if (insertBook.isPresent()) {
			log.warn("新增失敗 - 已存在相同ISBN書籍資料 - ISBN:{}", book.getIsbn());
			throw new BusinessException(409, "新增失敗 - 已存在相同ISBN書籍資料");
		}
		if (book.getBookName() == null || book.getBookName().trim().isEmpty()) {
			log.warn("格式驗證失敗 - 書名不可為空白");
			throw new BusinessException(400, "新增失敗，書名不可為空白");
		}
		if (book.getIsbn() == null || !book.getIsbn().matches(isbnRegex)) {
			log.warn("格式驗證失敗 - ISBN只能為13位數字 ISBN:{}", book.getIsbn());
			throw new BusinessException(400, "格式驗證失敗 - ISBN只能為13位數字");
		}
		if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) <= 0
				|| book.getPrice().remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
			log.warn("格式驗證失敗 - 價格必須為正整數: {}", book.getPrice());
			throw new BusinessException(400, "新增失敗，價格必須為正整數");
		}
		if (book.getStock() < 0) {
			log.warn("格式驗證失敗 - 庫存量不可小於0");
			throw new BusinessException(400, "格式驗證失敗，庫存量不可小於0");
		}
		log.info("新增成功 - 書籍ID:{} - 書籍名稱:{}", book.getBookId(), book.getBookName());
		return bookRepo.save(book);

	}

	// UpDate Book-----------------
	public BooksBean updateBook(BooksBean book) {
		if (book == null) {
			throw new BusinessException(400, "書籍物件不可為空");
		}
		BooksBean existBook = bookDao.selectBooksByIsbn(book.getIsbn());
		if (existBook != null) {
			throw new BusinessException(409, "修改失敗，該ISBN已被其他書籍使用");
		}
		if (book.getBookName() == null || book.getBookName().trim().isEmpty()) {
			throw new BusinessException(400, "書名為必填欄位");
		}
		if (book.getIsbn() == null || !book.getIsbn().matches("^[0-9]{13}$")) {
			throw new BusinessException(400, "ISBN格式錯誤，必須為13位數字。");
		}
		if (book.getPrice() == null | book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
			throw new BusinessException(400, "價格不可為空白，且不能為負數");
		}
		log.info("書籍資料更新成功 - 書籍ID:{}", book.getBookId());
		return bookRepo.save(book);
	}

	// delete book-----------------
	public void deleteBookById(Integer bookIdStr) {
		Optional<BooksBean> opt = bookRepo.findById(bookIdStr);
		if (opt.isEmpty()) {
			log.warn("刪除失敗 - 無該ID相關書籍資料 - 書籍ID:{}", bookIdStr);
			throw new BusinessException(400, "刪除失敗 - 無該ID相關書籍資料");
		}
		try {
			bookRepo.deleteById(bookIdStr);
		} catch (Exception e) {
			log.error("刪除失敗 - 伺服器異常，請聯繫開發人員");
			throw new BusinessException(500, "刪除失敗 - 伺服器異常，請聯繫開發人員");
		}

	}

	// update onShelf
	public void updateOnShelfStatus(Integer bookId, boolean newStatus) {
		Optional<BooksBean> opt = bookRepo.findById(bookId);
		if (!opt.isPresent()) {
			log.warn("查詢失敗 - 無該ID相關書籍資料");
			throw new BusinessException(404, "查詢失敗 - 無該ID相關書籍資料");
		}
		BooksBean book = opt.get();
		book.setOnShelf(newStatus);
		bookRepo.save(book);
		log.info("狀態修改成功 - 書籍ID:{}", book.getBookId());

	}

}
