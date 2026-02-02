# 錯誤檢查報告：Deadline 為 Null 與 Club ID 問題

## 1. 問題分析 (Issue Analysis)

### 問題 A：「送出時 Deadline 為 Null」

**徵兆**：送出表單時，即使使用者已選擇日期，後端接收到的 `deadline` 仍為 `null`。
**潛在原因**：時間格式不符 (Time Format Mismatch)。

- 前端 `<input type="datetime-local">` 通常產生的字串如 `2023-10-27T14:30` (不包含秒數)。
- 後端 `LocalDateTime` 的預設反序列化器通常預期包含秒數的 ISO-8601 格式 (例如 `2023-10-27T14:30:00`)。
- 若格式不符，JSON 解析器可能無法設定該欄位，導致其保持為 `null`。

### 問題 B：「儲存草稿時 club_id 為 Null」

**徵兆**：儲存草稿時，回傳的 `club_id` 為 null，或者使用者無法找到它。
**潛在原因**：

1. **前端邏輯**：目前的 `UserInsertBookClub.vue` 在建立讀書會後會忽略 API 的回應。它只是直接跳轉回列表頁面 (`/dev/user/bookclubs`)，並未儲存 `clubId` 以供立即「繼續編輯」。
2. **後端**：`BookClubService.createBookClub` 正確呼叫了 `repository.save()`，這會自動填入 `@GeneratedValue` 的 ID。Controller 也回傳了這個實體。因此 ID **確實**已產生並回傳，但潛在的 JSON 序列化問題 (如大小寫) 或前端忽略回應，使其在流程中看起來像是 "null"。

## 2. 建議解決方案 (Proposed Solutions)

### 針對 Deadline 的修正 (時間格式)

在 `BookClubRequestDTO` 中加入 `@JsonFormat` 註解，以嚴格定義預期的格式，允許省略秒數。

```java
// bookstore/dto/BookClubRequestDTO.java
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") // 處理不帶秒數的格式
private LocalDateTime eventDate;

@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
private LocalDateTime deadLine;
```

_較佳做法_：使用彈性的模式或確認前端發送的內容。Vue `v-model` 綁定 `datetime-local` 通常運作良好，但在前端補上秒數 `:00` 或調整後端模式是較安全的做法。(已採用調整後端模式)

### 針對 Club ID 的修正 (草稿流程)

若目的是允許「儲存草稿」->「留在頁面繼續編輯」，我們必須更新前端：

1. 接收 `createClub` 的回應。
2. 更新 `club.value.clubId` (若有此變數)。
3. 更改路由行為：若為草稿，不要跳轉；改為將表單切換至「編輯模式」(呼叫 Update API) 或僅顯示成功訊息。
4. _目前的實作_：跳轉至列表。這是一個有效的流程 (儲存 -> 離開)。若 "club_id is null" 指的是資料庫 `INSERT` 失敗，那已透過先前的 Schema 修正解決。若指的是先前的錯誤訊息中的 `null`，那很可能是因為 "deadline cannot be null" 的錯誤導致資料列根本沒有插入，ID 本就不存在。

## 3. 執行計畫 (Action Plan)

1. **後端 (Backend)**：在 `BookClubRequestDTO` 加入日期格式化註解，確保正確解析。(已完成)
2. **前端 (Frontend)**：確保日期字串包含秒數，或符合後端預期。(後端已配合前端調整)
3. **驗證 (Verification)**：重新執行邏輯。(已通過模擬測試)
