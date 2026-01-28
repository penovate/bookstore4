package bookstore.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import bookstore.aop.BusinessException;
import bookstore.bean.BookClubsBean;
import bookstore.bean.ClubConstants;
import bookstore.bean.BooksBean;
import bookstore.bean.ClubCategoriesBean;
import bookstore.bean.UserBean;
import bookstore.repository.BookClubsRepository;
import bookstore.repository.BookRepository;
import bookstore.repository.ClubCategoriesRepository;
import bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import bookstore.bean.UserPoint;

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

	private static final String ROOT_PATH = "C:/uploads/";

	// 查詢全部讀書會
	public List<BookClubsBean> getAllClubs() {
		List<BookClubsBean> clubList = bookClubsRepository.findAll();
		log.info("查詢成功，共 {} 筆資料", clubList.size());
		return clubList;
	}

	// 查詢所有讀書會分類
	public List<ClubCategoriesBean> getAllCategories() {
		return categoriesRepository.findAll();
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

	// 根據發起人(會員)查詢讀書會
	public List<BookClubsBean> findByHost(UserBean userBean) {
		List<BookClubsBean> list = bookClubsRepository.findByHost(userBean);
		if (list.isEmpty()) {
			log.warn("查無{}所發起的讀書會", userBean.getUserName());
			return list;
		}
		log.info("查詢成功 - 由{}所發起的讀書會，共{}筆", userBean.getUserName(), list.size());
		return list;
	}

	// 根據 Host ID 查詢 (Controller 專用)
	public List<BookClubsBean> findByHostId(Integer userId) {
		Optional<UserBean> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			throw new BusinessException(404, "查無使用者");
		}
		return findByHost(userOpt.get());
	}

	// 讀書會發起，發起人等級驗證
	public void HostPointCheck(Integer userId) {
		Optional<UserBean> user = userRepository.findById(userId);
		Integer userPoint = user.get().getPoints();

	}

	// 發起讀書會
	public BookClubsBean createBookClub(BookClubsBean bookClub, Integer userId) throws IOException {

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

		bookClub.setStatus(ClubConstants.STATUS_PEDING);

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

	// 通用檔案上傳
	public String uploadFile(MultipartFile file, String folderName) throws IllegalStateException, IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}

		try {
			File diretory = new File(ROOT_PATH + folderName);
			if (!diretory.exists()) {
				diretory.mkdirs();
			}

			String originalFileName = file.getOriginalFilename();
			if (originalFileName == null)
				originalFileName = "unknown";
			String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

			File destFile = new File(diretory, uniqueFileName);
			file.transferTo(destFile);
			return "/" + folderName + "/" + uniqueFileName;
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(500, "檔案上傳失敗");
		}
	}

	// 修改讀書會第一階段 權限與狀態檢查
	public boolean VaidatePermissionAndState(BookClubsBean existing, Integer userRole, Integer userId) {
		int status = existing.getStatus();
		boolean needTest = false;

		// 會員邏輯
		if (userRole == ClubConstants.ROLE_MEMBER) {
			if (!Objects.equals(existing.getHost().getUserId(), userId)) {
				throw new BusinessException(403, "您無權修改其他人發起的讀書會" + "");
			}

			// 狀態檢查
			if (status == ClubConstants.STATUS_APPROVED) {
				throw new BusinessException(400, "讀書會已審核通過報名中，不可修改內部資料");
			} else if (status == ClubConstants.STATUS_ENDED) {
				throw new BusinessException(400, "讀書會已結束，不可修改");
			} else if (status == ClubConstants.STATUS_CANCELLED) {
				throw new BusinessException(400, "讀書會已取消，不可修改");
			} else if (status == ClubConstants.STATUS_REJECTED) {
				needTest = true;
			} else if (status != ClubConstants.STATUS_PEDING) {
				throw new BusinessException(400, "目前狀態無法進行修改");
			}

			// 管理員邏輯
		} else if (userRole == ClubConstants.ROLE_ADMIN) {
			throw new BusinessException(403, "活動已結束，一般管理員不可修改");
		}
		return needTest;
	}

	// 修改讀書會第二階段 資料欄位正確性驗證
	public void validateFileds(BookClubsBean existing, BookClubsBean incoming) {

		// 人數上限檢查
		if (incoming.getMaxParticipants() != null) {
			int current = (existing.getMaxParticipants() == null) ? 0 : existing.getMaxParticipants();
			if (incoming.getMaxParticipants() < current) {
				throw new BusinessException(400, "修改人數上限不可低於現在報名人數");
			}
		}

		// 時間邏輯檢查
		LocalDateTime newEvenTime = incoming.getEventDate() != null ? incoming.getEventDate() : existing.getEventDate();
		LocalDateTime newDeadLine = incoming.getDeadline() != null ? incoming.getDeadline() : existing.getDeadline();

		// 截止日必須在活動開始之前
		if (newDeadLine.isAfter(newEvenTime)) {
			throw new BusinessException(400, "報名截止時間不可早於活動開始時間");
		}

		// 若修改活動時間，不能設為過去
		if (newEvenTime.isBefore(LocalDateTime.now())) {
			throw new BusinessException(400, "活動時間不可設在過去");
		}

		// 名稱檢查
		if (incoming.getClubName() != null) {
			if (incoming.getClubName().trim().isEmpty() || incoming.getClubName().length() > 100) {
				throw new BusinessException(400, "輸入格式錯誤，讀書會名稱需在100字以內");
			}
		}
	}

	// 資料搬移
	private void mergeClubData(BookClubsBean target, BookClubsBean source) {
		if (source.getClubName() != null)
			target.setClubName(source.getClubName());
		if (source.getLocation() != null)
			target.setLocation(source.getLocation());
		if (source.getExternalBookInfo() != null)
			target.setExternalBookInfo(source.getExternalBookInfo());
		if (source.getEventDate() != null)
			target.setEventDate(source.getEventDate());
		if (source.getDeadline() != null)
			target.setDeadline(source.getDeadline());
		if (source.getMaxParticipants() != null)
			target.setMaxParticipants(source.getMaxParticipants());
		if (source.getCategoriesBean() != null)
			target.setCategoriesBean(source.getCategoriesBean());
		if (source.getBook() != null)
			target.setBook(source.getBook());
	}

	// 修改讀書會主方法
	public BookClubsBean updateBookclub(Integer clubId, BookClubsBean incomingClub, Integer currentUserId,
			Integer currentUserRole) throws IllegalStateException, IOException {

		Optional<BookClubsBean> opt = bookClubsRepository.findById(clubId);
		if (opt.isEmpty()) {
			throw new BusinessException(404, "查無相關讀書會資料");
		}
		BookClubsBean existingclub = opt.get();

		// 檢查權限與狀態(取得是否需要重置旗標)
		boolean shouldResetStatus = VaidatePermissionAndState(existingclub, currentUserRole, currentUserId);

		// 檢查欄位邏輯
		validateFileds(existingclub, incomingClub);

		// 執行資料合併
		mergeClubData(existingclub, incomingClub);

		// 執行狀態重置
		// 如果判斷是被駁回後的修正則重置為審核中，並清空駁回原因欄位
		if (shouldResetStatus) {
			existingclub.setStatus((Integer) ClubConstants.STATUS_PEDING);
			existingclub.setRejectionReason(null);
		}

		bookClubsRepository.save(existingclub);
		return existingclub;

	}

	// 刪除讀書會
	public void deleteClubId(Integer clubId) {
		Optional<BookClubsBean> opt = bookClubsRepository.findById(clubId);
		if (opt.isEmpty()) {
			log.warn("刪除失敗 - 無ID{}相關讀書會資料", clubId);
			throw new BusinessException(400, "刪除失敗 - 無ID" + clubId + "相關讀書會資料");
		}
		bookClubsRepository.deleteById(clubId);
	}

}
