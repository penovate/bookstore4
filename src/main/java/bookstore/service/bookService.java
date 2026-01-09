package bookstore.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import bookstore.bean.BookImageBean;
import bookstore.bean.BooksBean;
import bookstore.bean.GenreBean;
import bookstore.bean.ReviewBean;
import bookstore.exceptionCenter.BusinessException;
import bookstore.exceptionCenter.GlobalExceptionHandler;
import bookstore.repository.BookRepository;
import bookstore.repository.GenreRepository;
import bookstore.repository.OrderItemRepository;
import bookstore.repository.ReviewRepository;
import jakarta.transaction.Transactional;

@Service
public class bookService {

	private final GlobalExceptionHandler globalExceptionHandler;

	private static final Logger log = LoggerFactory.getLogger(bookService.class);
	@Autowired
	private BookRepository bookRepo;

	@Autowired
	private GenreRepository genreRepo;
	@Autowired
	private OrderItemRepository orderItemRepo;
	@Autowired
	private ReviewRepository reviewRepo;
	@Value("${file.upload.dir:C:/uploads/book-images/}")
	private String uploadDir;

	bookService(GlobalExceptionHandler globalExceptionHandler) {
		this.globalExceptionHandler = globalExceptionHandler;
	}

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

	// isbn for Ajax
	public Boolean existsByIsbn(String isbnStr) {
		return bookRepo.findByIsbn(isbnStr).isPresent();
	}

	// select by genre
	/*
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
	*/

	// select by onShelf
	@Transactional
	public Integer selectOnShelfById(Integer bookId) {
		Optional<BooksBean> opt = bookRepo.findById(bookId);
		if (opt.isPresent()) {
			Integer status = opt.get().getOnShelf();
			log.info("查詢成功 - 查詢ID:{} - 狀態:{}", bookId, status);
			return status;

		} else {
			log.warn("查詢失敗 - 無該ID相關書籍資料 - 查詢ID:{}", bookId);
			throw new BusinessException(404, "無該ID相關書籍資料");
		}
	}

	// insertbook
	@Transactional
	@
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
				imageBean.setImageUrl("/upload-images/" + fileName);
				imageBean.setIsMain(true);
				book.addImage(imageBean);

			} catch (IOException e) {
				e.printStackTrace();
				throw new BusinessException(500, "圖片上傳失敗");
			}
		}

		if (book.getGenres() != null && !book.getGenres().isEmpty()) {
			Set<GenreBean> manageGenres = new HashSet<GenreBean>();

			for (GenreBean genre : book.getGenres()) {
				Optional<GenreBean> genreOpt = genreRepo.findById(genre.getGenreId());
				if (genreOpt.isPresent()) {
					manageGenres.add(genreOpt.get());
				} else {
					log.warn("新增失敗 - 找不到指定的類型ID:{}", genre.getGenreId());
					throw new BusinessException(404, "新增失敗 - 指不到指定的類型ID:" + genre.getGenreId());
				}

			}
			book.setGenres(manageGenres);
		}

		BooksBean booksBean = bookRepo.save(book);
		log.info("新增成功 - 書籍ID:{} - 書籍名稱:{}", book.getBookId(), book.getBookName());
		return booksBean;

	}

	//update Check
	private void updateBasicFields(BooksBean existing, BooksBean incoming) {
		// 書名

		if (incoming.getBookName() != null) {
			if (incoming.getBookName().trim().isEmpty())
				throw new BusinessException(400, "書名不可為空白");
			existing.setBookName(incoming.getBookName());
		}
		// 作者
		if (incoming.getAuthor() != null) {
			if (incoming.getAuthor().trim().isEmpty())
				throw new BusinessException(400, "作者不可為空白");
			existing.setAuthor(incoming.getAuthor());
		}

		// 出版社
		if (incoming.getPress() != null) {
			if (incoming.getPress().trim().isEmpty())
				throw new BusinessException(400, "出版社不可為空白");
			existing.setPress(incoming.getPress());
		}

		// 價格 (驗證正整數)
		if (incoming.getPrice() != null) {
			if (incoming.getPrice().compareTo(BigDecimal.ZERO) <= 0
					|| incoming.getPrice().remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
				throw new BusinessException(400, "價格必須為正整數");
			}
			existing.setPrice(incoming.getPrice());
		}

		// ISBN (格式驗證 + 唯一性檢查)
		if (incoming.getIsbn() != null) {
			if (!incoming.getIsbn().matches("^[0-9]{13}$")) {
				throw new BusinessException(400, "ISBN 格式錯誤，必須為 13 位數字");
			}
			Optional<BooksBean> isbnCheck = bookRepo.findByIsbn(incoming.getIsbn());
			if (isbnCheck.isPresent() && !isbnCheck.get().getBookId().equals(incoming.getBookId())) {
				throw new BusinessException(409, "此 ISBN 已被其他書籍使用");
			}
			existing.setIsbn(incoming.getIsbn());
		}

		// 庫存
		if (incoming.getStock() != null) {
			if (incoming.getStock() < 0)
				throw new BusinessException(400, "庫存量不可小於 0");
			existing.setStock(incoming.getStock());
		}

		// 狀態
		if (incoming.getOnShelf() != null) {
			existing.setOnShelf(incoming.getOnShelf());
		}

		// 譯者與簡述
		if (incoming.getTranslator() != null)
			existing.setTranslator(incoming.getTranslator());
		if (incoming.getShortDesc() != null)
			existing.setShortDesc(incoming.getShortDesc());
	}
	
	
	private void processImageUpload(BooksBean book,MultipartFile file) {
		if (file!=null&&!file.isEmpty()) {
			try {
				String originalFileName = file.getOriginalFilename();
				String extension = "";
				if (originalFileName!=null&&originalFileName.contains(".")) {
					extension = originalFileName.substring(originalFileName.lastIndexOf("."));
				}
				String fileName = UUID.randomUUID().toString()+extension;
				String savePath = uploadDir+fileName;
				
				File dest = new File(savePath);
				if (!dest.getParentFile().exists()) {
					dest.getParentFile().mkdir();
				}
				
				file.transferTo(dest);
				
				if(book.get) {
					
				}
			} catch (Exception e) {
			}
		}
	}
	

	// UpDate Book-----------------
	@Transactional
	public BooksBean updateBook(BooksBean book, MultipartFile file) {
		if (book.getBookId() == null) {
			throw new BusinessException(400, "修改失敗：必須提供書籍 ID");
		}

		Optional<BooksBean> opt = bookRepo.findById(book.getBookId());
		if (opt.isEmpty()) {
			throw new BusinessException(404, "修改失敗：找不到 ID 為 " + book.getBookId() + " 的書籍");
		}

		BooksBean existingBook = opt.get();

		if (book.getOnShelf() == 2) {
			throw new BusinessException(400, "該書籍處於封存狀態，不可修改");
		}

		updateBasicFields(existingBook, book);

		if (book.getGenres() != null) {
			Set<GenreBean> newManagedGenres = new HashSet<GenreBean>();
			for (GenreBean genre : book.getGenres()) {
				Optional<GenreBean> genreOpt = genreRepo.findById(genre.getGenreId());
				if (genreOpt.isPresent()) {
					newManagedGenres.add(genreOpt.get());
				} else {
					throw new BusinessException(404, "修改失敗:找不到類型ID" + genre.getGenreId());
				}
			}
			existingBook.setGenres(newManagedGenres);
		}

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
			log.warn("查詢失敗 - 無ID:{} 相關書籍資料", bookId);
			throw new BusinessException(404, "查詢失敗 - 無該ID相關書籍資料");
		}
		BooksBean book = opt.get();
		if (book.getOnShelf() == 2) {
			log.warn("修改失敗 - 已封存書籍不可上架");
			return;
		}
		book.setOnShelf(newStatus ? 1 : 0);
		bookRepo.save(book);
		log.info("狀態修改成功 - 書籍ID:{} - 狀態:{}", book.getBookId(), book.getOnShelf());

	}

	// archive book
	@Transactional
	public void archiveBook(Integer bookId) {
		Optional<BooksBean> opt = bookRepo.findById(bookId);
		if (!opt.isPresent()) {
			log.warn("查詢失敗 - 無ID:{} 相關書籍資料", bookId);
		} else {
			BooksBean book = opt.get();
			book.setOnShelf(2);
			bookRepo.save(book);
			log.info("書籍 ID:{}已成功封存", bookId);
		}
	}
	//unarchive book
	@Transactional
	public void unarchiveBook(Integer bookId) {
		Optional<BooksBean> opt = bookRepo.findById(bookId);
		if (!opt.isPresent()) {
			log.warn("查詢失敗 -無:ID{}相關書籍資料", bookId);
		} else {
			BooksBean book = opt.get();
			book.setOnShelf(0);
			bookRepo.save(book);
			log.info("書籍ID:{}已成功解封", bookId);
		}
	}
	
	//封存檢查(訂單/評價)
	@Transactional
	public boolean isBookAvailable(Integer bookId) {
		Optional<BooksBean> opt = bookRepo.findById(bookId);
		if (!opt.isPresent()) {
			log.warn("查詢失敗 -無:ID{}相關書籍資料", bookId);
			throw new BusinessException(404, "無該ID相關書籍");
		}
		Integer onShelf = opt.get().getOnShelf();
		if (onShelf == null || onShelf != 1) {
			log.info("書籍ID:{} 狀態:{} 處於封存狀態，禁止新增關聯資料", bookId, onShelf);
			return false;
		}
		return true;
	}

	

	@Transactional
	public List<GenreBean> getAllGenres() {
		return genreRepo.findAll();
	}

}
