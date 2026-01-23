package bookstore.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import bookstore.aop.BusinessException;
import bookstore.bean.BookClubsBean;
import bookstore.bean.BooksBean;
import bookstore.bean.ClubCategoriesBean;
import bookstore.bean.UserBean;
import bookstore.config.WebConfig;
import bookstore.repository.BookClubsRepository;
import bookstore.repository.BookRepository;
import bookstore.repository.ClubCategoriesRepository;
import bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class BookClubService {



	@Autowired
	private BookClubsRepository bookClubsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private ClubCategoriesRepository categoriesRepository;

	private final String PROPOSAL_DIR = "C:\\uploads\\proposal\\";

	private final String PROOF_DIR = "C:\\uploads\\proof\\";




	// 查詢全部讀書會
	public List<BookClubsBean> getAllClubs() {
		List<BookClubsBean> clubList = bookClubsRepository.findAll();
		log.info("查詢成功，共 {} 筆資料", clubList.size());
		return clubList;
	}

	// 查詢單筆讀書會
	public BookClubsBean getClub(Integer clubId) {
		Optional<BookClubsBean> opt = bookClubsRepository.findById(clubId);
		if (opt.isPresent()) {
			log.info("查詢成功");
			return opt.get();
		}
		log.warn("查無ID:[]讀書會資料", clubId);
		throw new BusinessException(404, "查無ID:[" + clubId + "]讀書會資料");
	}

	// 根據類型查詢讀書會
	public List<BookClubsBean> findByCategory(Integer category) {
		List<BookClubsBean> list = bookClubsRepository.findByCategoriesBean_CategoryId(category);
		if (list.isEmpty()) {
			log.warn("查無該類型相關讀書會");
			return list;
		}
		log.info("查詢成功 - 共 [] 筆資料", list.size());
		return list;
	}

	// 根據狀態查詢讀書會
	public List<BookClubsBean> findByStatus(Integer status) {
		List<BookClubsBean> list = bookClubsRepository.findByStatus(status);
		if (list.isEmpty()) {
			log.warn("查無狀態為{}的讀書會", status);
			return list;
		}
		log.info("查詢成功 - 共{}筆資料", list.size());
		return list;
	}

	// 根據書籍查詢讀書會
	public BookClubsBean findByBooks(BooksBean booksBean) {
		Optional<BookClubsBean> opt = bookClubsRepository.findByBook(booksBean);
		if (opt.isEmpty()) {
			log.warn("查無{}相關讀書會", booksBean.getBookName());
			throw new BusinessException(404, "查無該書相關讀書會");
		}
		return opt.get();
	}

	// 根據發起人查詢讀書會
	public List<BookClubsBean> findByHost(UserBean userBean) {
		List<BookClubsBean> list = bookClubsRepository.findByHost(userBean);
		if (list.isEmpty()) {
			log.warn("查無{}所發起的讀書會", userBean.getUserName());
			return list;
		}
		log.info("查詢成功 - 由{}所發起的讀書會，共{}筆", userBean.getUserName(), list.size());
		return list;
	}

	// 發起讀書會
	public BookClubsBean createBookClub(BookClubsBean bookClub, MultipartFile proposal, MultipartFile proof,
			Integer userId) throws IOException {
		UserBean host = userRepository.findById(userId).get();
		bookClub.setHost(host);

		if (bookClub.getCategoriesBean() != null) {
			ClubCategoriesBean category = categoriesRepository.findById(bookClub.getCategoriesBean().getCategoryId())
					.get();
			bookClub.setCategoriesBean(category);
		}

		if (bookClub.getBook() != null && bookClub.getBook().getBookId() != null) {
			BooksBean book = bookRepository.findById(bookClub.getBook().getBookId()).get();
			bookClub.setBook(book);
		}

		if (proposal != null) {
			bookClub.setProposalPath(saveToDisk(proposal, PROPOSAL_DIR));
		}

		if (proof != null) {
			bookClub.setProofPath(saveToDisk(proof, PROOF_DIR));
		}

		return bookClubsRepository.save(bookClub);

	}

	// 檔案上傳
	private String saveToDisk(MultipartFile file, String path) throws IOException {
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdir();
		}
		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		File destination = new File(path + fileName);
		file.transferTo(destination);
		return destination.getAbsolutePath();
	}
	
	
	//刪除讀書會
	public void deleteClubId(Integer clubId) {
		Optional<BookClubsBean> opt = bookClubsRepository.findById(clubId);
		if (opt.isEmpty()) {
			log.warn("刪除失敗 - 無ID{}相關讀書會資料",clubId);
			throw new BusinessException(400, "刪除失敗 - 無ID"+clubId+"相關讀書會資料");
		}
		bookClubsRepository.deleteById(clubId);
	}

}
