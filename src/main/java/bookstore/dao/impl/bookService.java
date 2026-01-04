package bookstore.dao.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import bookstore.bean.BookImageBean;
import bookstore.bean.BooksBean;
import bookstore.bean.GenreBean;
import bookstore.exceptionCenter.BusinessException;
import bookstore.repository.BookRepository;
import jakarta.transaction.Transactional;

@Service
public class bookService {

	private static final Logger log = LoggerFactory.getLogger(bookService.class);
	@Autowired
	private BookRepository bookRepo;
	@Value("${file.upload.dir:C:/uploads/book-images/}")
	private String uploadDir;

	// ------select all books-----------
	@Transactional
	public List<BooksBean> selectAllBooks() {
		List<BooksBean> bookList = bookRepo.findAll();
		if (bookList == null && bookList.isEmpty()) {
			log.warn("查詢全部失敗 - 資料庫內無任何書籍資料");
			throw new BusinessException(404, "無任何書籍資料");
		}
		log.info("查詢全部成功 書籍數量: {} 筆", bookList.size());
		return bookList;
	}

	// Select book by id-----------------
	@Transactional
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
	@Transactional
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
	@Transactional
	public List<BooksBean> selectByGenre(Integer genreId) {
		List<BooksBean> bookList = bookRepo.findGenreBean_GenreId(genreId);
		if (bookList != null && !bookList.isEmpty()) {
			log.info("查詢成功 - 書籍數量:{}筆 - 類型ID:{}", bookList.size(), genreId);
			return bookList;
		} else {
			log.warn("查詢失敗 - 無該類型相關書籍資料");
			throw new BusinessException(404, "無該類型相關書籍資料");
		}
	}

	// select by onShelf
	@Transactional
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
	@Transactional
	public BooksBean insertBook(BooksBean book, MultipartFile file) {
		String isbnRegex = "^\\d{13}$";
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

		if (file != null && !file.isEmpty()) {
			try {
				String fileName = file.getOriginalFilename();
				String savePath = uploadDir + fileName;

				File dest = new File(savePath);
				if (!dest.getParentFile().exists()) {
					dest.getParentFile().mkdir();
				}

				file.transferTo(dest);
				BookImageBean imageBean = new BookImageBean();
				imageBean.setImageUrl("/upload-images" + fileName);
				imageBean.setIsMain(true);
				book.addImage(imageBean);

			} catch (IOException e) {
				e.printStackTrace();
				throw new BusinessException(500, "圖片上傳失敗");
			}
		}
		BooksBean booksBean = bookRepo.save(book);
		log.info("新增成功 - 書籍ID:{} - 書籍名稱:{}", book.getBookId(), book.getBookName());
		return booksBean;

	}

	// UpDate Book-----------------
	@Transactional
	public BooksBean updateBook(BooksBean book, Integer genreId) {
		if (book.getBookId() == null) {
			throw new BusinessException(400, "修改失敗：必須提供書籍 ID");
		}

		Optional<BooksBean> opt = bookRepo.findById(book.getBookId());
		if (opt.isEmpty()) {
			throw new BusinessException(404, "修改失敗：找不到 ID 為 " + book.getBookId() + " 的書籍");
		}

		BooksBean existingBook = opt.get();

		// 書名
		if (book.getBookName() != null) {
			if (book.getBookName().trim().isEmpty())
				throw new BusinessException(400, "書名不可為空白");
			existingBook.setBookName(book.getBookName());
		}

		// 作者
		if (book.getAuthor() != null) {
			if (book.getAuthor().trim().isEmpty())
				throw new BusinessException(400, "作者不可為空白");
			existingBook.setAuthor(book.getAuthor());
		}

		// 出版社
		if (book.getPress() != null) {
			if (book.getPress().trim().isEmpty())
				throw new BusinessException(400, "出版社不可為空白");
			existingBook.setPress(book.getPress());
		}

		// 價格 (驗證正整數)
		if (book.getPrice() != null) {
			if (book.getPrice().compareTo(BigDecimal.ZERO) <= 0
					|| book.getPrice().remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
				throw new BusinessException(400, "價格必須為正整數");
			}
			existingBook.setPrice(book.getPrice());
		}

		// ISBN (格式驗證 + 唯一性檢查)
		if (book.getIsbn() != null) {
			if (!book.getIsbn().matches("^[0-9]{13}$")) {
				throw new BusinessException(400, "ISBN 格式錯誤，必須為 13 位數字");
			}
			Optional<BooksBean> isbnCheck = bookRepo.findByIsbn(book.getIsbn());
			if (isbnCheck.isPresent() && !isbnCheck.get().getBookId().equals(book.getBookId())) {
				throw new BusinessException(409, "此 ISBN 已被其他書籍使用");
			}
			existingBook.setIsbn(book.getIsbn());
		}

		// 庫存
		if (book.getStock() != null) {
			if (book.getStock() < 0)
				throw new BusinessException(400, "庫存量不可小於 0");
			existingBook.setStock(book.getStock());
		}

		// 上下架狀態
		if (book.getOnShelf() != null) {
			existingBook.setOnShelf(book.getOnShelf());
		}

		// 類別更新
		if (genreId != null) {
			GenreBean gBean = new GenreBean();
			gBean.setGenreId(genreId);
			existingBook.setGenreBean(gBean);
		}

		// 譯者與簡述
		if (book.getTranslator() != null)
			existingBook.setTranslator(book.getTranslator());
		if (book.getShortDesc() != null)
			existingBook.setShortDesc(book.getShortDesc());

		log.info("書籍修改成功 - ID: {}, 名稱: {}", existingBook.getBookId(), existingBook.getBookName());
		return bookRepo.save(existingBook);
	}

	// delete book-----------------
	@Transactional
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
	@Transactional
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
