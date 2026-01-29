# 讀書會功能修正與模擬測試報告

## 1. 修正項目摘要

本階段針對「送出審核時 Deadline 為 Null」以及「草稿編輯資料遺失」等問題進行了修復，並優化了前端的按鈕顯示邏輯。

### A. Deadline 為 Null 問題修復

- **問題原因**：前端 JSON 傳送的欄位名稱為 `deadline` (全小寫)，但後端 `BookClubRequestDTO` 定義的欄位名稱為 `deadLine` (駝峰式 naming)。預設的 Jackson 反序列化器可能因區分大小寫或命名策略不一致，導致無法正確映射數值。
- **解決方案**：在 `BookClubRequestDTO` 的 `deadLine` 欄位上加上 `@JsonProperty("deadline")` 註解，明確指定 JSON 屬性名稱對應。
  ```java
  @JsonProperty("deadline")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime deadLine;
  ```
- **驗證結果**：模擬測試 `BookClubFlowSimulationTest` 已通過，確認送出審核時時間欄位正確寫入。

### B. 草稿編輯資料遺失修復

- **問題原因**：原先後端 API `getClub` 僅回傳主表 `BookClubsBean`，未包含附表 `ClubDetail` (紀錄宗旨、議程、難度等)。導致前端編輯載入時，這些欄位呈現空白。
- **解決方案**：
  1.  **後端 Entity 關聯調整**：在 `BookClubsBean` (主表) 與 `ClubDetail` (附表) 之間建立雙向的一對一關聯 (`@OneToOne`)。
  2.  **避免無窮迴圈**：使用 `@JsonManagedReference` (主表) 與 `@JsonBackReference` (附表) 來控制 JSON 序列化方向，確保 API 回傳完整且正確的樹狀結構數據。
  3.  **前端映射**：更新 `UserInsertBookClub.vue`，在編輯模式載入資料時，從 nested 的 `clubDetail` 物件中讀取對應欄位。

### C. 按鈕顯示優化

- **調整內容**：在「我發起的讀書會」列表中，「報名明細」按鈕現在僅在讀書會狀態為 **已核准 (1)、已額滿 (3)、已截止 (4)、已結束 (5)** 時才會顯示。**草稿 (7)** 與 **已駁回 (2)** 狀態則顯示「編輯」按鈕。

## 2. 模擬測試結果

我們執行了自動化模擬測試 `BookClubFlowSimulationTest`，涵蓋以下情境：

1.  **草稿流程**：建立草稿 -> 更新草稿 (防止 Null 錯誤) -> 送出審核。
2.  **直接送審**：填寫完整資料直接送出 -> 狀態轉為審核中。
3.  **駁回後修正**：模擬管理員駁回 -> 使用者修正資料 -> 重新送出 -> 狀態正確轉回審核中。

**測試結論**：所有測試案例皆 **通過 (PASS)**。

- Build Success
- Tests run: 5, Failures: 0, Errors: 0

## 3. 後續建議

- 建議使用者在實際瀏覽器環境中進行一次完整的「建立草稿 -> 編輯 -> 送出」流程操作，確認體驗流暢。
- 下階段可開始著手開發「管理員審核介面」與「報名/取消報名」的詳細後端邏輯。
