# 資料初始化與雜湊加密設定計畫書 (Hybrid 混合策略版)

## 1. 專案目標與變更

**核心需求**：

1.  **書籍資料 (Persistent)**：由使用者手動新增真實書籍（含真實圖片），伺服器重啟時 **不刪除、不覆蓋** 這些書。
2.  **變動資料 (Transient)**：會員、訂單、評價、讀書會等數據，每次重啟時 **自動重置並重新生成**，且必須與現有的書籍產生關聯。
3.  **解決痛點**：不需要在程式碼中塞入假圖片 URL，也不用擔心圖片破圖，完全模擬真實上線情境。

---

## 2. 關鍵設定變更

### 修改 application.properties

為了「保留」書籍資料，我們不能再使用 `create` (會刪表)。必須改用 `update`，並在程式中手動執行「部分清除」邏輯。

```properties
# 改為 update，啟動時不會刪除現有資料表，只會更新結構
spring.jpa.hibernate.ddl-auto=update
```

---

## 3. DataInitializer 邏輯重構

新邏輯分為三個階段：

1.  **清理階段 (Cleanup)**：刪除 訂單、評價、讀書會、會員 (保留書籍、分類)。
2.  **重建階段 (Rebuild)**：重新建立會員 (User)。
3.  **關聯階段 (Linking)**：讀取資料庫現有的書籍，分配給新會員產生訂單與評價。

### 完整程式碼實作 (DataInitializer.java)

請將 `src/main/java/bookstore/config/DataInitializer.java` 修改如下：

```java
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
import bookstore.repository.ClubCategoriesRepository;
import bookstore.repository.ClubDetailRepository;
import bookstore.repository.ClubRegistrationsRepository;
import bookstore.repository.GenreRepository;
import bookstore.repository.OrderItemRepository;
import bookstore.repository.OrdersRepository;
import bookstore.repository.ReviewRepository;
import bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final BookClubsRepository bookClubsRepository;
    private final ClubRegistrationsRepository clubRegistrationsRepository;
    private final ClubCategoriesRepository clubCategoriesRepository;
    private final ClubDetailRepository clubDetailRepository;
    private final PasswordEncoder passwordEncoder;

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

                // 確保至少有 Admin 帳號讓你能登入加書
                if (userRepository.count() == 0) {
                    createAdmin();
                }
                return;
            }

            log.info("偵測到資料庫已有 {} 本書籍，保留書籍資料，重置其他數據...", bookCount);

            // 2. 清除舊數據 (依賴順序：先刪子表再刪主表)
            clearTransientData();

            // 3. 重建會員
            List<UserBean> users = createUsers();

            // 4. 重建分類 (如果分類也被清空或需要預設)
            // 通常分類跟書籍綁定，如果書在，分類應該也在。這裡假設分類不需重建，或檢查後補建。
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

    @Transactional
    public void clearTransientData() {
        // 先刪除關聯最強的 (子表)
        orderItemRepository.deleteAll();
        reviewRepository.deleteAll();
        clubRegistrationsRepository.deleteAll();
        clubDetailRepository.deleteAll();

        // 再刪除主表
        ordersRepository.deleteAll();
        bookClubsRepository.deleteAll();

        // 分類與書籍不刪除
        // genreRepository.deleteAll();
        // bookRepository.deleteAll();

        // 會員最後刪 (因為訂單、讀書會都綁會員，但因為前面已刪，這裡可以刪)
        // 注意：若書籍有設定 'created_by' 關聯會員，則不可刪除會員。
        // 假設 BooksBean 沒有 FK 關聯到 UserBean，則可刪除。
        userRepository.deleteAll();

        log.info("已清除：訂單、評價、讀書會、會員資料");
    }

    @Transactional
    public void createAdmin() {
        UserBean admin = new UserBean();
        admin.setEmail("admin@bookstore.com");
        admin.setUserName("超級管理員");
        admin.setUserPwd(passwordEncoder.encode("123456"));
        admin.setUserType(1);
        admin.setStatus(1);
        admin.setPoints(9999);
        admin.setCreatedAt(new Date());
        userRepository.save(admin);
        log.info("已建立初始管理員 (僅供新增書籍用)");
    }

    @Transactional
    public List<UserBean> createUsers() {
        List<UserBean> users = new ArrayList<>();

        // Admin
        UserBean admin = new UserBean();
        admin.setEmail("admin@bookstore.com");
        admin.setUserName("超級管理員");
        admin.setUserPwd(passwordEncoder.encode("123456"));
        admin.setUserType(1);
        admin.setStatus(1);
        admin.setPoints(9999);
        admin.setCreatedAt(new Date());
        users.add(userRepository.save(admin));

        // Members
        for (int i = 1; i <= 9; i++) {
            UserBean user = new UserBean();
            user.setEmail("user" + i + "@test.com");
            user.setUserName("模擬會員" + i);
            user.setUserPwd(passwordEncoder.encode("123456"));
            user.setUserType(2);
            user.setStatus(1);
            user.setPoints(random.nextInt(2000));
            user.setCreatedAt(new Date());
            users.add(userRepository.save(user));
        }
        return users;
    }

    @Transactional
    public void createClubCategories() {
        String[] names = {"線上讀書會", "實體交流", "專題講座", "作者見面會"};
        for (String name : names) {
            ClubCategoriesBean c = new ClubCategoriesBean();
            c.setCategoryName(name);
            clubCategoriesRepository.save(c);
        }
    }

    @Transactional
    public void createOrders(int count, List<UserBean> users, List<BooksBean> books) {
        if (books.isEmpty()) return;

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

    @Transactional
    public void createReviews(List<BooksBean> books, List<UserBean> users, int reviewsPerBook) {
        if (books.isEmpty()) return;

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

    @Transactional
    public void createBookClubs(int count, List<UserBean> users, List<BooksBean> books, List<ClubCategoriesBean> cats) {
        if (books.isEmpty() || cats.isEmpty()) return;

        for (int i = 1; i <= count; i++) {
            UserBean host = users.get(random.nextInt(users.size()));

            BookClubsBean club = new BookClubsBean();
            club.setClubName("讀書會聚會 #" + i + " - " + books.get(random.nextInt(books.size())).getBookName());
            club.setHost(host);
            club.setCategoriesBean(cats.get(random.nextInt(cats.size())));
            club.setBook(books.get(random.nextInt(books.size())));
            club.setStatus(1);
            club.setMaxParticipants(30);
            club.setCurrentParticipants(random.nextInt(10));
            club.setEventDate(LocalDateTime.now().plusDays(random.nextInt(14) + 7));
            club.setDeadline(LocalDateTime.now().plusDays(5));
            club.setLocation("線上會議 / 台北市大安區");

            club = bookClubsRepository.save(club);

            ClubDetail detail = new ClubDetail();
            detail.setMainClub(club);
            detail.setPurpose("精讀分享");
            detail.setAgenda("導讀 -> 分組 -> 總結");
            detail.setRequirement("無");
            detail.setModeType("線上");
            detail.setDiffultLevel(1);
            clubDetailRepository.save(detail);
        }
        log.info("已生成 {} 筆讀書會", count);
    }
}
```

## 4. 操作流程 (SOP)

1.  **第一次啟動**：
    - 此時資料庫全空。
    - Console 會顯示 `!!! 警告：資料庫中沒有書籍 !!!`。
    - 系統會自動建立一個 `admin@bookstore.com` / `123456` 的帳號。
2.  **手動建立種子資料**：
    - 登入後台。
    - 手動新增 20 本書籍，並上傳真實的封面圖片。
    - 這些書和圖片就是你的「真實種子」。
3.  **第二次 (及以後) 啟動**：
    - 系統偵測到有書。
    - **刪除** 舊的訂單、評價、讀書會、會員。
    - **保留** 那 20 本書。
    - **重新生成** 全新的 10 個會員、10 筆訂單、100 筆評價、10 場讀書會，並全部關聯到你那 20 本真實書籍上。

這樣就能完美達成「書籍圖片真實、其他數據每次重置」的需求！
