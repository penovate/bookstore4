# 高併發潛在風險評估與解決方案報告

## 1. 前言

本報告針對 **書籍模組**、**進退貨模組**（庫存管理）、**讀書會模組** 進行高併發場景下的風險評估。在高流量或多使用者同時操作的環境下（如搶購、熱門活動報名、同時後台管理），若無適當的鎖定機制，將導致「資料遺失更新」(Lost Update)、「超賣」(Overselling) 或「超收」(Overbooking) 等嚴重資料不一致問題。

本報告提供 **樂觀鎖 (Optimistic Locking)** 與 **悲觀鎖 (Pessimistic Locking)** 的核心邏輯與實作方案，並針對不同模組特性提出最佳建議。

---

## 2. 核心邏輯與差異分析

### 2.1 樂觀鎖 (Optimistic Locking)

- **核心邏輯**：
  假設衝突發生的機率較低。在資料讀取時不加鎖，而是在 **更新 (Update)** 時檢查資料是否在讀取後被修改過。
- **實作方式**：
  通常在資料表中增加一個版本號欄位（`version`）。
  - **讀取**：SELECT id, name, stock, version FROM table ...
  - **更新**：UPDATE table SET stock = new_stock, version = version + 1 WHERE id = ? AND **version = old_version**
  - 若更新筆數為 0，代表該數據已被其他人修改（版本號已變），此時拋出異常（如 `OptimisticLockingFailureException`），由應用程式決定重試或報錯。
- **優點**：
  - 資料庫負擔小（無長事務鎖定）。
  - 適合「讀多寫少」或「衝突較少」的場景。
  - 不會造成死鎖 (Deadlock)。
- **缺點**：
  - 在高衝突場景下（如秒殺），會有大量請求失敗，使用者體驗較差（頻繁重試）。

### 2.2 悲觀鎖 (Pessimistic Locking)

- **核心邏輯**：
  假設衝突發生的機率很高。在資料 **讀取 (Select)** 時就直接對該筆資料加鎖，直到交易結束 (Commit/Rollback) 前，其他人都無法修改或讀取（視鎖的層級而定）該筆資料。
- **實作方式**：
  使用 SQL 的 `SELECT ... FOR UPDATE` 語法。
- **優點**：
  - 資料一致性極高，杜絕了並發修改的可能性。
  - 適合「寫多讀多」或「資料正確性要求極高」（如金額、庫存扣減）的場景。
  - 避免了應用層的重試邏輯複雜度。
- **缺點**：
  - 效能開銷大，鎖定期間其他交易需等待。
  - 若交易時間過長，會嚴重拖垮系統吞吐量。
  - 風險：可能導致死鎖。

### 2.3 兩種鎖的比較表

| 特性           | 樂觀鎖 (Optimistic / Version)    | 悲觀鎖 (Pessimistic / For Update) |
| :------------- | :------------------------------- | :-------------------------------- |
| **資料庫機制** | 無 (依賴 WHERE 條件檢查)         | 資料庫行鎖 (Row Lock)             |
| **並發性能**   | 高 (無鎖等待)                    | 低 (需排隊等待鎖釋放)             |
| **適用場景**   | 搶票(非秒殺)、編輯資料、狀態變更 | 金流、庫存扣減、嚴格順序操作      |
| **失敗處理**   | 拋出異常，需 Retry 或通知失敗    | 等待直到超時 (Timeout)            |

---

## 3. 各模組風險評估與建議方案

### 3.1 書籍模組 (基本資訊維護)

- **風險**：兩位管理員同時編輯同一本書的資訊（如 A 修改價格，B 修改書名），後送出者會覆蓋前者的修改。
- **建議方案**：**樂觀鎖 (Optimistic Lock)**
- **理由**：書籍資訊修改頻率低，衝突機率極小。使用樂觀鎖可以避免長時間鎖定資料庫，且實作簡單（加 `@Version` 即可）。

### 3.2 讀書會模組 (會員報名)

- **風險**：
  讀書會有人數上限（如限 10 人）。若目前 9 人，A 與 B 同時報名，兩人讀取到的 `currentParticipants` 都是 9，檢查都通過，結果最後人數變成 11 人（超收）。
- **建議方案**：**樂觀鎖 (Optimistic Lock)**
- **理由**：
  雖然報名可能會有並發，但通常不如電商秒殺般激烈。樂觀鎖能確保人數不超收（透過版本檢查）。若發生衝突，直接告知使用者「報名失敗，請重試」是可接受的 UX，避免資料庫因鎖定而卡頓。

### 3.3 進退貨模組 (庫存管理)

- **風險**：後台進貨（+庫存）與前台訂單（-庫存）或退貨操作同時發生。若使用 `stock = stock + 10` 的覆蓋寫法，會導致庫存數量錯誤。
- **建議方案**：**悲觀鎖 (Pessimistic Lock)**
- **理由**：
  庫存是電商最核心的資產數據，必須保證絕對準確。在進退貨或訂單扣減庫存時，使用 `FOR UPDATE` 鎖定該書籍，確保計算基礎（原庫存）是最新的，且執行期間不容許插隊。這能防止一切並發導致的計算錯誤。

---

## 4. 實作程式碼 (修改前後對照)

### 4.1 實體類別修改 (啟用樂觀鎖)

針對 **書籍 (`BooksBean`)** 與 **讀書會 (`BookClubsBean`)**，新增 `version` 欄位。

**修改前 (BooksBean.java)**

```java
public class BooksBean {
    // ... 其他欄位
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

**修改後 (BooksBean.java)**

```java
import jakarta.persistence.Version;

public class BooksBean {
    // ... 其他欄位
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // [新增] 樂觀鎖版本號
    @Version
    @Column(name = "version")
    private Integer version;
}
```

_(BookClubsBean 同理增加 @Version)_

---

### 4.2 讀書會報名其實作 (樂觀鎖應用)

**修改前 (ClubRegistrationService.java)**

```java
// 風險：多執行緒下可能導致超收
private void updateClubParticipants(BookClubsBean club, int delta) {
    int current = club.getCurrentParticipants() == null ? 0 : club.getCurrentParticipants();
    club.setCurrentParticipants(current + delta);
    bookClubsRepository.save(club); // 直接覆蓋
}
```

**修改後 (ClubRegistrationService.java)**

```java
import org.springframework.orm.ObjectOptimisticLockingFailureException;

public ClubRegistrationsBean register(Integer clubId, Integer userId) {
    // ... (前略 檢查邏輯)

    try {
        updateClubParticipants(club, 1);
        return clubRegistrationsRepository.save(newReg);
    } catch (ObjectOptimisticLockingFailureException e) {
        // [新增] 捕獲版本衝突異常
        throw new BusinessException(409, "報名人數更動頻繁，請稍後重試");
    }
}

private void updateClubParticipants(BookClubsBean club, int delta) {
    int current = club.getCurrentParticipants() == null ? 0 : club.getCurrentParticipants();
    club.setCurrentParticipants(current + delta);
    // [新增] 使用 saveAndFlush 確保立即觸發版本檢查
    bookClubsRepository.saveAndFlush(club);
}
```

---

### 4.3 進退貨/庫存其實作 (悲觀鎖應用)

**步驟 1: Repository 增加鎖定方法 (BookRepository.java)**

```java
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<BooksBean, Integer> {

    // [新增] 悲觀鎖查詢 (FOR UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BooksBean b WHERE b.bookId = :id")
    Optional<BooksBean> findByIdWithLock(@Param("id") Integer id);
}
```

**步驟 2: Service 使用鎖定方法 (StockLogService.java)**

**修改前**

```java
// 風險：讀取舊庫存 -> 計算 -> 寫回，可能覆蓋同時發生的其他變更
Optional<BooksBean> bookOpt = bookRepository.findById(bookId);
if (bookOpt.isPresent()) {
    BooksBean book = bookOpt.get();
    book.setStock(book.getStock() + qty);
    bookRepository.save(book);
}
```

**修改後**

```java
@Transactional
public StockLogBean insertStockLog(StockLogBean stockLogBean) {
    // ...
    for (LogItemBean item : items) {
        Integer bookId = item.getBooksBean().getBookId();

        // [修改] 使用悲觀鎖讀取 (此時 DB 該行被鎖定，直到 Transaction 結束)
        Optional<BooksBean> bookOpt = bookRepository.findByIdWithLock(bookId); // 原為 findById

        if (bookOpt.isPresent()) {
            BooksBean book = bookOpt.get();
            // 安全地更新庫存
            Integer currentStock = book.getStock();
            // ... 計算邏輯 ...
            book.setStock(newStock);
            bookRepository.save(book);
        }
    }
    // ...
}
```

## 5. 總結

- **讀書會報名** 採用 **樂觀鎖**：避免資料庫瓶頸，利用 JPA `@Version` 自動檢測衝突，失敗時請使用者重試即可。
- **庫存進退貨** 採用 **悲觀鎖**：利用 `SELECT ... FOR UPDATE` 確保庫存準確性，雖然會犧牲少許並發效能，但對於後台管理操作（如進貨）是完全可接受的，且能保證資料絕對一致。
