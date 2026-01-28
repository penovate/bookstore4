# 書籍批量匯入功能規格書

## 1. 專案規格與架構總覽

### 項目說明

- **目標**: 實作書籍批量匯入功能，Excel 由前端解析，後端僅接收 JSON 陣列。
- **圖片策略**: 批量匯入不處理圖片。圖片採「先建檔、後上傳」策略，由另外的 API 更新。

### 職責劃分

- **前端職責**:
  1.  讀取 Excel (.xlsx)。
  2.  驗證基礎格式（必填、數字格式）。
  3.  將表格轉為 JSON。
  4.  呼叫後端 API。
- **後端職責**:
  1.  接收 `List<BookBatchDTO>`。
  2.  進行商業邏輯驗證（ISBN 重複、Genre 是否存在）。
  3.  執行寫入或收集錯誤。
  4.  回傳「匯入結果報告」。

### 容錯機制

- **Partial Success (部分成功模式)**: 合法的資料寫入，不合法的資料回傳錯誤原因清單，讓使用者知道哪幾筆失敗。

## 2. 制式 Excel 格式規範 (給使用者)

請依此格式製作 Excel 範本供使用者下載。

- **檔名建議**: `books_import_template.xlsx`
- **分頁**: 讀取第 1 個分頁 (Sheet1)
- **多值分隔符**: 書籍類型使用半形逗號 `,` 分隔

### 欄位 (Header) 定義

| 欄位 (Header) | 必填 | 範例資料      | 格式限制/備註                    |
| :------------ | :--- | :------------ | :------------------------------- |
| 書名          | 是   | 深入淺出 Java | 文字，最大 50 字                 |
| ISBN          | 是   | 9789571234567 | 13 碼數字，需設定為文字格式      |
| 作者          | 是   | 張三          | 文字                             |
| 出版社        | 是   | 歐萊禮        | 文字                             |
| 價格          | 是   | 580           | 正整數                           |
| 庫存          | 是   | 1000          | 1000 或正整數                    |
| 書籍類型      | 否   | 電腦,程式設計 | 以 `,` 分隔。需填寫類型名稱      |
| 狀態          | 否   | 上架          | 填寫「上架」或「下架」(預設下架) |
| 譯者          | 否   | 李四          | 文字                             |
| 簡述          | 否   | 適合初學者... | 文字                             |

## 3. 資料傳輸物件 (DTO) 設計

由於 Excel 內的「類型」是中文名稱（如 "文學"），但 `BooksBean` 關聯需要 ID 或物件，且為了隔離 Entity 與輸入資料，建議建立專用的 DTO。

### 3.1 前端傳送的 JSON 結構 (Request)

後端接收的 Body 為 `List<BookBatchImportDTO>`。

```json
[
  {
    "bookName": "深入淺出 Java",
    "isbn": "9789571234567",
    "author": "張三",
    "press": "歐萊禮",
    "price": 580,
    "stock": 100,
    "genreNames": "電腦,程式設計",  // 前端直接傳字串，後端拆解
    "statusStr": "上架",          // 前端傳中文，後端轉代碼
    "translator": "李四",
    "shortDesc": "簡述內容..."
  },
  { ...下一筆... }
]
```

### 3.2 後端回傳的錯誤報告結構 (Response)

為了讓前端顯示：「成功 10 筆，失敗 2 筆（第 2、5 行錯誤）」。

```json
{
  "totalCount": 12,
  "successCount": 10,
  "failureCount": 2,
  "errorDetails": [
    {
      "index": 1, // 對應原始 List 的 index (即 Excel 第 2 筆資料)
      "isbn": "978957...", // 方便辨識
      "reason": "ISBN 已存在"
    },
    {
      "index": 4,
      "isbn": "978988...",
      "reason": "找不到類型名稱：[奇幻]"
    }
  ]
}
```

## 4. 前端實作建議 (Frontend Strategy)

前端建議使用 `SheetJS (xlsx)` 庫進行解析。
**虛擬程式碼流程 (JavaScript/React/Vue):**

1.  **File Input**: 監聽檔案上傳事件。
2.  **Read File**: 使用 FileReader 讀取為 ArrayBuffer。
3.  **Parse**:
    ```javascript
    const wb = XLSX.read(data, { type: "array" });
    const ws = wb.Sheets[wb.SheetNames[0]];
    // header: 1 代表轉為二維陣列，方便處理 Header mapping
    // 或是直接用 json_to_sheet 但需處理 Key Mapping
    const jsonData = XLSX.utils.sheet_to_json(ws);
    ```
4.  **Mapping**: 將 Excel 的中文 Key (書名) 對應到 DTO Key (`bookName`)。同時將 `類型` 轉為 String (e.g., "A,B")。同時將 ISBN 強制轉為字串 (避免 Excel 科學記號)。
5.  **API Call**: `axios.post('/api/books/batch', mappedData)`。
6.  **Show Result**: 接收後端的 `errorDetails` 並繪製成表格告知使用者。

## 5. 後端實作規劃 (Backend Logic)

### 5.1 DTO 定義 (BookBatchImportDTO.java)

```java
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookBatchImportDTO {
    // 透過 Excel 匯入的原始資料
    private String bookName;
    private String isbn;
    private String author;
    private String press;
    private BigDecimal price;
    private Integer stock;
    private String genreNames; // 接收 "文學,科幻"
    private String statusStr;  // 接收 "上架/下架"
    private String translator;
    private String shortDesc;
}
```

### 5.2 結果回傳物件 (BatchResultDTO.java)

```java
@Data
public class BatchResultDTO {
    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<BatchErrorDetail> errorDetails = new ArrayList<>();

    public void addError(int index, String isbn, String reason) {
        this.errorDetails.add(new BatchErrorDetail(index, isbn, reason));
    }

    @Data
    @AllArgsConstructor
    public static class BatchErrorDetail {
        private int index;      // 資料索引 (第幾筆)
        private String isbn;    // 識別用
        private String reason;  // 錯誤原因
    }
}
```

### 5.3 Service 層邏輯 (BookService.java)

**關鍵改動**：

- 不使用 `@Transactional` 包裹整個方法：因為我們要允許「部分成功」。若整個方法 rollback，成功的資料也會消失。
- 個別 `Try-Catch`：針對每一筆資料單獨開啟 Transaction 或驗證。

```java
@Autowired
private GenreRepository genreRepo; // 需要查詢類型

// 預先載入所有 Genre 以提升效能 (避免迴圈內查詢)
private Map<String, GenreBean> getGenreMap() {
    return genreRepo.findAll().stream()
            .collect(Collectors.toMap(GenreBean::getGenreName, g -> g));
}

public BatchResultDTO batchInsertBooks(List<BookBatchImportDTO> dtoList) {
    BatchResultDTO result = new BatchResultDTO();
    result.setTotalCount(dtoList.size());

    // 1. 準備快取資料 (避免 N+1 查詢)
    Map<String, GenreBean> genreMap = getGenreMap();

    // 2. 收集本次請求中的 ISBN 以檢查「批次內重複」
    Set<String> batchIsbns = new HashSet<>();

    for (int i = 0; i < dtoList.size(); i++) {
        BookBatchImportDTO dto = dtoList.get(i);

        try {
            // --- 步驟 A: 基礎格式驗證 ---
            validateDto(dto); // 檢查必填、價格>0、ISBN格式 (抽離為私有方法)

            // --- 步驟 B: 批次內重複檢查 ---
            if (batchIsbns.contains(dto.getIsbn())) {
                throw new BusinessException(400, "Excel 清單內 ISBN 重複");
            }
            batchIsbns.add(dto.getIsbn());

            // --- 步驟 C: 執行轉換與儲存 (封裝成獨立 Transaction 方法) ---
            // 注意：因為要部分提交，這裡呼叫另一個 public 方法或透過 self-invocation 處理事務
            // 簡單作法：saveSingleBookForBatch 標記為 @Transactional(Propagation.REQUIRES_NEW)
            saveSingleBookForBatch(dto, genreMap);

            result.setSuccessCount(result.getSuccessCount() + 1);

        } catch (Exception e) {
            // 捕獲所有異常，記錄錯誤並繼續下一筆
            result.setFailureCount(result.getFailureCount() + 1);
            String errorMsg = (e instanceof BusinessException) ? e.getMessage() : "系統異常: " + e.getMessage();
            result.addError(i + 1, dto.getIsbn(), errorMsg);

            // 若發生異常，需從 batchIsbns 移除，避免誤判
            batchIsbns.remove(dto.getIsbn());
            log.error("批量匯入第 {} 筆失敗: {}", i + 1, e.getMessage());
        }
    }

    return result;
}

// 獨立的事務方法，確保單筆失敗不影響其他筆
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void saveSingleBookForBatch(BookBatchImportDTO dto, Map<String, GenreBean> genreMap) {
    // 1. DB ISBN 檢查
    if (bookRepo.findByIsbn(dto.getIsbn()).isPresent()) {
        throw new BusinessException(409, "資料庫已存在相同 ISBN");
    }

    // 2. DTO 轉 Entity
    BooksBean book = new BooksBean();
    book.setBookName(dto.getBookName());
    book.setIsbn(dto.getIsbn());
    book.setAuthor(dto.getAuthor());
    book.setPress(dto.getPress());
    book.setPrice(dto.getPrice());
    book.setStock(dto.getStock());
    book.setTranslator(dto.getTranslator());
    book.setShortDesc(dto.getShortDesc());

    // 狀態轉換
    int status = "上架".equals(dto.getStatusStr()) ? 1 : 0;
    book.setOnShelf(status);

    // 3. 類型處理 (Genre)
    if (dto.getGenreNames() != null && !dto.getGenreNames().isEmpty()) {
        Set<GenreBean> genres = new HashSet<>();
        String[] names = dto.getGenreNames().split("[,，]"); // 支援中英文逗號
        for (String name : names) {
            String cleanName = name.trim();
            if (genreMap.containsKey(cleanName)) {
                genres.add(genreMap.get(cleanName));
            } else {
                // 嚴格模式：找不到類型就報錯
                throw new BusinessException(404, "系統內無此書籍類型: " + cleanName);
            }
        }
        book.setGenres(genres);
    }

    // 4. 存檔
    bookRepo.save(book);
}
```

## 6. 資深工程師的建議 (Summary)

- **前端解析的優勢**：您選擇前端解析 Excel 是正確的決策。這讓後端 API 變得單純（只處理 JSON），且方便 Swagger 測試，同時避免了 Java 處理 Excel 大型檔案時的 OOM (Out Of Memory) 風險。
- **事務隔離 (Propagation.REQUIRES_NEW)**：在 `saveSingleBookForBatch` 使用 `REQUIRES_NEW` 是實現「部分成功」的關鍵。這保證了即使第 2 筆失敗 rollback，第 1 筆成功的資料已經提交進資料庫，不會被牽連。
- **效能瓶頸**：
  - **Genre 查詢**：透過 `getGenreMap()` 一次撈取，避免了每新增一本書就 query 一次 Genre 表。
  - **ISBN 檢查**：目前的寫法是單筆查 DB。若匯入量大 (如 > 1000 筆)，建議先用 `findByIsbnIn(List<String> isbns)` 一次查出既有的 ISBN Set 進行比對，進一步降低 DB I/O。
