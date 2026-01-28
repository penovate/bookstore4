# Insert Stock Log One-Click Input 功能實作報告

**日期**: 2026-01-28  
**專案模組**: Bookstore Frontend / Admin / Logs / Insert  
**技術堆疊**: Vue 3 (Composition API), SweetAlert2

---

## 1. 修改目標 (Objective)

**需求描述**:  
在「新增進貨單」頁面 (`InsertStockLog.vue`) 新增「一鍵輸入」功能，以便在測試或演示時能快速填入合理的隨機數據，減少手動輸入時間。

**功能規格**:

1.  **UI 位置**: 在頁面標題「新增進貨單」右方新增「一鍵輸入」按鈕。
2.  **功能行為**: 點擊後，**不立即送出**，而是自動填入表單欄位供使用者檢視。
3.  **填入規則**:
    - 廠商名稱: 自動填入「一鍵輸入測試廠商」。
    - 書籍選擇: 從現有書籍清單中隨機選擇 5 本。
    - 明細內容: 數量隨機 (1-20)，成本自動計算 (售價 \* 0.6)。

---

## 2. 邏輯差異比較 (Logic Comparison)

### 修改前 (Before)

- **操作模式**: 使用者進入頁面後，表單為空。
- **測試痛點**: 若要測試「新增進貨單」功能，需手動輸入廠商 -> 點選新增書籍 -> 搜尋/選擇書籍 -> 輸入數量 -> 輸入成本 -> 重複 5 次。耗時約 60 秒。

### 修改後 (After)

- **操作模式**: 使用者進入頁面，點擊「一鍵輸入」。
- **體驗提升**: 表單瞬間自動填滿 5 筆隨機且合規的書籍資料，使用者僅需點擊「確認送出」。操作時間 < 1 秒。
- **安全性**: 僅填入前端表單，最終送出權限仍在使用者手上，避免誤操作直接寫入資料庫。

### 資料流圖 (Data Flow)

```mermaid
graph TD
    A[使用者進入 Insert 頁面] --> B[API 載入書籍清單]
    B --> C(使用者點擊「一鍵輸入」)
    C --> D{書籍數量 >= 5?}
    D -- No --> E[提示錯誤]
    D -- Yes --> F[執行隨機填入邏輯]
    F --> G[設定廠商 = 測試廠商]
    F --> H[隨機挑選 5 本書]
    H --> I[迴圈生成明細 (Qty:1-20, Cost: 0.6*Price)]
    I --> J[更新 Vue Ref (logData)]
    J --> K[畫面自動渲染資料]
    K --> L[使用者檢視並送出]
```

---

## 3. 重點程式碼比較 (Code Diff)

檔案位置: `src/views/admin/logs/InsertStockLog.vue`

```diff
  <template>
      <div class="pa-4">
-         <h2 class="text-h4 font-weight-bold text-primary mb-6">新增進貨單</h2>
+         <div class="d-flex align-center mb-6">
+             <h2 class="text-h4 font-weight-bold text-primary">新增進貨單</h2>
+             <v-btn color="secondary" prepend-icon="mdi-flash" class="ml-4" @click="oneClickInput">
+                 一鍵輸入
+             </v-btn>
+         </div>
          <!-- form... -->
```

```diff
  <script setup>
  // ...
+ // [新增] 一鍵輸入測試資料
+ const oneClickInput = () => {
+     // 1. 檢查書籍數量
+     if (books.value.length < 5) return;
+
+     // 2. 填入廠商
+     logData.value.wholesaler = '一鍵輸入測試廠商';
+
+     // 3. 隨機選書與填入明細
+     const selectedBooks = getRandomSubarray(books.value, 5);
+     logData.value.logItemBeans = selectedBooks.map(book => ({
+         booksBean: book,
+         changeQty: Math.floor(Math.random() * 20) + 1,
+         costPrice: Math.floor(book.price * 0.6)
+     }));
+ };
  </script>
```

---

## 4. 專家建議 (Architect Suggestions)

1.  **測試數據隔離 (Data Isolation)**:
    - 目前的實作會產生真實的庫存異動記錄。若在正式環境 (Production) 誤用，會導致帳務庫存混亂。
    - **建議**: 可以在 `oneClickInput` 中加入環境變數檢查 (如 `import.meta.env.MODE !== 'production'`)，在正式環境隱藏該按鈕。

2.  **批次輸入模式 (Batch Input)**:
    - 如果倉管人員有從 Excel 或掃描槍匯入大量資料的需求，目前的「隨機輸入」可作為雛形，進一步擴展為「匯入 CSV / Excel」功能，邏輯類似 (Parsing -> Fill Form -> Review -> Submit)。

3.  **防呆機制 (Validation)**:
    - 雖然「一鍵輸入」會產生合規數據，但若後端的價格波動導致 `price * 0.6` 出現小數點 (如 $100.5)，需確保 UI 與後端都能處理浮點數或自動四捨五入 (目前前端使用了 `Math.floor` 取整，相對安全)。
