package bookstore.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import bookstore.bean.BookClubsBean;
import bookstore.bean.BooksBean;
import bookstore.bean.ClubCategoriesBean;
import bookstore.bean.ClubDetail;
import bookstore.bean.OrderItem;
import bookstore.bean.Orders;
import bookstore.bean.ReviewBean;
import bookstore.bean.UserBean;
import bookstore.repository.BookClubsRepository;
import bookstore.repository.BookRepository;
import bookstore.repository.BrowsingHistoryRepository;
import bookstore.repository.CartRepository;
import bookstore.repository.ClubCategoriesRepository;
import bookstore.repository.ClubDetailRepository;
import bookstore.repository.ClubRegistrationsRepository;
import bookstore.repository.CouponRepository;
import bookstore.repository.GenreRepository;
import bookstore.repository.OrderItemRepository;
import bookstore.repository.OrdersRepository;
import bookstore.repository.ReviewRepository;
import bookstore.repository.UserLogRepository;
import bookstore.repository.UserRepository;
import bookstore.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

	private final UserRepository userRepository;
	private final BookRepository bookRepository;
	private final OrdersRepository ordersRepository;
	private final OrderItemRepository orderItemRepository;
	private final ReviewRepository reviewRepository;
	private final BookClubsRepository bookClubsRepository;
	private final ClubRegistrationsRepository clubRegistrationsRepository;
	private final ClubCategoriesRepository clubCategoriesRepository;
	private final ClubDetailRepository clubDetailRepository;
	private final PasswordEncoder passwordEncoder;
	private final BrowsingHistoryRepository browsingHistoryRepository;
	private final UserLogRepository userLogRepository;
	private final CartRepository cartRepository;
	private final CouponRepository couponRepository;
	private final WishlistRepository wishlistRepository;

	private final Random random = new Random();

	@Bean
	public CommandLineRunner initData() {
		return args -> {
			log.info("=== 系統啟動：開始資料初始化流程 ===");

			// 1. 檢查是否有書籍 (核心主軸)
			long bookCount = bookRepository.count();
			if (bookCount == 0) {
				log.warn("!!! 警告：資料庫中沒有書籍 !!!");
				log.warn("請先登入後台手動新增至少 1 本書籍，下次啟動時才會自動產生關聯數據。");

				if (userRepository.count() == 0) {
					createAdmin();
				}
				return;
			}

			log.info("偵測到資料庫已有 {} 本書籍，保留書籍資料，重置其他數據...", bookCount);

			// 2. 清除舊數據
			clearTransientData();

			// 3. 重建會員
			List<UserBean> users = createUsers();

			// 4. 重建分類
			if (clubCategoriesRepository.count() == 0) {
				createClubCategories();
			}
			List<ClubCategoriesBean> clubCats = clubCategoriesRepository.findAll();

			// 5. 載入現有書籍 (Persistence)
			List<BooksBean> existingBooks = bookRepository.findAll();

			// 6. 產生關聯數據
			createOrders(10, users, existingBooks);
			createReviews(existingBooks, users, 5); // 每本書 5 則評價
			createBookClubs(10, users, existingBooks, clubCats);

			log.info("=== 資料初始化完成：模擬真實數據已生成 ===");
		};
	}

	// 刪除現有資料
	@Transactional
	public void clearTransientData() {
		cartRepository.deleteAllInBatch();
		wishlistRepository.deleteAllInBatch();
		couponRepository.deleteAllInBatch();
		orderItemRepository.deleteAllInBatch();
		reviewRepository.deleteAllInBatch();
		clubRegistrationsRepository.deleteAllInBatch();
		clubDetailRepository.deleteAllInBatch();
		browsingHistoryRepository.deleteAllInBatch();
		

		bookClubsRepository.deleteAllInBatch();
		ordersRepository.deleteAllInBatch();

		userRepository.flush();

		userLogRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();

		log.info("已清除：訂單、評價、讀書會、會員資料");
	}

	@Transactional
	public void createAdmin() {
		UserBean superAdmin = new UserBean();
		superAdmin.setEmail("onlinebookstoreforjava@gmail.com");
		superAdmin.setUserName("林木森");
		superAdmin.setUserPwd(passwordEncoder.encode("123456"));
		superAdmin.setUserType(0);
		superAdmin.setStatus(1);
		superAdmin.setPoints(9999);
		superAdmin.setCreatedAt(new Date());
		userRepository.save(superAdmin);
		log.info("已建立初始管理員 (僅供新增書籍用)");

	}

	// 會員初始資料
	@Transactional
	public List<UserBean> createUsers() {
		List<UserBean> users = new ArrayList<>();

		UserBean superAdmin = new UserBean();
		superAdmin.setEmail("onlinebookstoreforjava@gmail.com");
		superAdmin.setUserName("林木森");
		superAdmin.setUserPwd(passwordEncoder.encode("123456"));
		superAdmin.setUserType(0);
		superAdmin.setGender("M");
		superAdmin.setPhoneNum("0987452145");
		superAdmin.setStatus(1);
		superAdmin.setPoints(9999);
		superAdmin.setCreatedAt(new Date());
		userRepository.save(superAdmin);

		// Admin
		UserBean admin = new UserBean();
		admin.setEmail("cl3vul42006@gmail.com");
		admin.setUserName("宋泓孝");
		admin.setUserPwd(passwordEncoder.encode("alex74586"));
		admin.setUserType(1);
		admin.setStatus(1);
		admin.setPhoneNum("0962050445");
		admin.setAddress("桃園市中壢區中華路999號");
		admin.setPoints(9999);
		admin.setCreatedAt(new Date());
		users.add(userRepository.save(admin));

		// Member
		UserBean member = new UserBean();
		member.setUserName("李梅");
		member.setEmail("leemei122694@gmail.com");
		member.setUserPwd(passwordEncoder.encode("alex74586"));
		member.setUserType(0);
		member.setStatus(1);
		member.setGender("M");
		member.setAddress("宜蘭縣羅東鎮中正北路999號");
		member.setPoints(500);
		member.setCreatedAt(new Date());
		users.add(userRepository.save(member));

		// Members
		String[] name = { "王曉明", "李鐵柱", "王翠花", "林志玲", "張大寶", "陳汁瀚", "周餅倫", "王小陸", "范承仁", "李建輝" };
		String[] phone = { "0987654321", "0987454135", "0974745241", "0954123547", "0985412534", "0958745666",
				"0965845333", "0954254855", "0985412578", "0985412544" };
		for (int i = 1; i <= 9; i++) {
			UserBean user = new UserBean();
			user.setEmail("user" + i + "@yahoo.com");
			user.setUserName(name[i]);
			user.setUserPwd(passwordEncoder.encode("123456"));
			user.setUserType(2);
			user.setGender("M");
			user.setPhoneNum(phone[i]);
			user.setStatus(1);
			user.setPoints(random.nextInt(2000));
			user.setAddress("桃園市中壢區新生路99" + i + "號");
			user.setCreatedAt(new Date());
			users.add(userRepository.save(user));
		}
		return users;
	}

	// 讀書會類別初始資料
	@Transactional
	public void createClubCategories() {
		String[] names = { "心靈健康", "投資理財", "專題講座", "作者見面會", "醫學蓋論", "程式設計", "工業管理" };
		for (String name : names) {
			ClubCategoriesBean c = new ClubCategoriesBean();
			c.setCategoryName(name);
			clubCategoriesRepository.save(c);
		}
	}

	// 訂單初始資料
	@Transactional
	public void createOrders(int count, List<UserBean> users, List<BooksBean> books) {
		if (books.isEmpty())
			return;

		for (int i = 1; i <= count; i++) {
			UserBean user = users.get(random.nextInt(users.size()));
			Orders order = new Orders();
			order.setUserBean(user);
			order.setOrderStatus(random.nextBoolean() ? "已完成" : "待出貨");
			order.setPaymentStatus("已付款");
			order.setPaymentMethod("信用卡");
			order.setRecipientAt(user.getUserName());
			order.setPhone("0912345678");
			order.setAddress("台北市信義區測試路" + i + "段");
			order.setDeliveryMethod("宅配到府");
			order.setShippedAt(new java.sql.Timestamp(System.currentTimeMillis()));
			order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

			order = ordersRepository.save(order);

			BigDecimal total = BigDecimal.ZERO;
			int itemCount = random.nextInt(3) + 1; // 1~3本書

			for (int k = 0; k < itemCount; k++) {
				BooksBean book = books.get(random.nextInt(books.size()));
				OrderItem item = new OrderItem();
				item.setOrders(order);
				item.setBooksBean(book);
				int qty = random.nextInt(2) + 1;
				item.setQuantity(qty);
				item.setPrice(book.getPrice());

				BigDecimal sub = book.getPrice().multiply(new BigDecimal(qty));
				item.setSubtotal(sub);
				total = total.add(sub);

				orderItemRepository.save(item);
			}

			order.setTotalAmount(total);
			order.setShippingFee(new BigDecimal(60));
			order.setFinalAmount(total.add(new BigDecimal(60)));
			ordersRepository.save(order);
		}
		log.info("已生成 {} 筆訂單 (基於現有 {} 本書)", count, books.size());
	}

	// 評價初始資料
	@Transactional
	public void createReviews(List<BooksBean> books, List<UserBean> users, int reviewsPerBook) {
		if (books.isEmpty())
			return;

		int total = 0;
		for (BooksBean book : books) {
			for (int i = 0; i < reviewsPerBook; i++) {
				ReviewBean r = new ReviewBean();
				UserBean u = users.get(random.nextInt(users.size()));
				r.setUserId(u.getUserId());
				r.setBookId(book.getBookId());
				r.setRating(random.nextInt(3) + 3); // 3-5 星
				r.setComment("這本書真的不錯！模擬評價 #" + i);
				r.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
				reviewRepository.save(r);
				total++;
			}
		}
		log.info("已生成 {} 筆評價", total);
	}

	// 讀書會初始資料
	@Transactional
	public void createBookClubs(int count, List<UserBean> users, List<BooksBean> books, List<ClubCategoriesBean> cats) {
		if (books.isEmpty() || cats.isEmpty())
			return;

//		BookClubsBean club1 = new BookClubsBean();
//		UserBean host1 = userRepository.findByEmail("cl3vul42006@gmail.com");
//		club1.setClubName("書友共讀:探討投資的真諦");
//		club1.setHost(host1);
//		club1.setCategoriesBean(cats.get(2));
//		club1.setBook(books.get(3));
//		club1.setStatus(0);
//		club1.setMaxParticipants(30);
//		club1.setCurrentParticipants(random.nextInt(10));
//		club1.setEventDate(LocalDateTime.now().plusDays(random.nextInt(14) + 7));
//		club1.setDeadline(LocalDateTime.now().plusDays(5));
//		club1.setLocation("聖德基督學院203教室");
//		ClubDetail detail2 = new ClubDetail();
//		detail2.setMainClub(club1);
//		detail2.setPurpose("探討金流背後的核心邏輯");
//		detail2.setAgenda("導讀 -> 分組 -> 總結");
//		detail2.setDiffultLevel(2);
//		clubDetailRepository.save(detail2);

		for (int i = 1; i <= count; i++) {
			UserBean host = users.get(random.nextInt(users.size()));

			BookClubsBean club = new BookClubsBean();
			club.setClubName("讀書會聚會 #" + i + " - " + books.get(random.nextInt(books.size())).getBookName());
			club.setHost(host);
			club.setCategoriesBean(cats.get(random.nextInt(cats.size())));
			club.setBook(books.get(random.nextInt(books.size())));
			club.setStatus(1);
			club.setMaxParticipants(30);
			club.setCurrentParticipants(0);
			club.setEventDate(LocalDateTime.now().plusDays(random.nextInt(14) + 7));
			club.setDeadline(LocalDateTime.now().plusDays(5));
			club.setLocation("線上會議 / 台北市大安區");

			club = bookClubsRepository.save(club);

			ClubDetail detail = new ClubDetail();
			detail.setMainClub(club);
			detail.setPurpose("精讀分享");
			detail.setAgenda("導讀 -> 分組 -> 總結");
			detail.setRequirement("無");
			detail.setDiffultLevel(1);
			clubDetailRepository.save(detail);
		}
		log.info("已生成 {} 筆讀書會", count);
	}
}
