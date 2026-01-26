# 讀書會報名功能修改總結報告書

## 1. 專案修改總結
本次修改主要針對「讀書會報名系統」進行全端功能的實作與優化。
目標是建立一個安全、邏輯嚴謹且使用者體驗良好的報名流程。

**主要變更點：**
*   **後端 (Backend)**: 重構 Controller 與 Service，從「依賴前端傳送大量參數」改為「後端自動驗證 Token 與權限」，並修復 Repository 的 JPA 命名錯誤。
*   **前端 (Frontend)**: 將單一列表介面升級為「三分頁」管理介面（所有/發起/參與），並新增彈出式視窗供發起人管理報到狀態。

---

## 2. 差異性比較表 (Comparison Table)

| 檔案 (File) | 修改類型 (Type) | 修改前 (Before) | 修改後 (After) | 目的 (Purpose) |
| :--- | :--- | :--- | :--- | :--- |
| **ClubRegistrationsRepository.java** | <span style="color:orange">Refactor</span> | `findByClubIdAndUserId` | `findByBookClub_ClubIdAndUser_UserId` | 修正 JPA 方法命名以符合 Entity 關聯路徑，避免啟動錯誤。 |
| **ClubRegistrationService.java** | <span style="color:green">Feature</span> | `createRegistration` (空泛/未實作) | 實作 `register`, `updateUserCheckIn`, `updateCancel` | 增加完整的報名、取消、防重複報名檢查、人數扣減與報到邏輯。 |
| **ClubRegistrationController.java** | <span style="color:orange">Refactor</span> | 使用 `@PostMapping` 查詢資料；需傳入 UserBean | 使用 `@GetMapping` 查詢；透過 `jwtUtil` 自動解析 UserID | 符合 RESTful 規範，提升安全性 (不需前端傳送敏感 UserID)。 |
| **bookClubService.js** | <span style="color:green">Feature</span> | 僅有基本的 CRUD | 新增 `register`, `cancelRegistration`, `checkInUser` 等 | 支援前端呼叫新的後端報名介面。 |
| **UserBookClub.vue** | <span style="color:blue">UI/UX</span> | 單一列表；功能僅預留 | 三分頁 (所有/發起/參與)；實作報名/取消按鈕 | 提供清晰的視圖與操作流程，讓使用者與發起人各取所需。 |
| **RegistrationDetails.vue** | <span style="color:green">New</span> | (無) | 新增元件，顯示報名者列表與狀態 | 讓發起人能檢視詳細名單並執行報到動作。 |

---

## 3. 詳細程式碼前後差異 (Detailed Code Diff)

### A. Repository 層 (`ClubRegistrationsRepository.java`)
**差異分析**：修正方法名稱以符合 Spring Data JPA 規範。

|Before|After|
|---|---|
|`findByClubIdAndUserId`|`findByBookClub_ClubIdAndUser_UserId`|
|(無)|`existsByBookClub_ClubIdAndUser_UserId`|

### B. Service 層 (`ClubRegistrationService.java`)
**差異分析**：實作核心業務邏輯。

```java
// [Before] 舊有程式碼僅有空殼或簡單 CRUD
public ClubRegistrationsBean createRegistration(...) {
    // 邏輯不完整，只存檔未檢查
    return clubRegistrationsRepository.save(reg);
}

// [After] 新增完整驗證邏輯
public ClubRegistrationsBean register(Integer clubId, Integer userId) {
    // 1. 檢查讀書會是否存在與狀態
    // 2. 檢查是否重複報名
    // 3. 檢查人數上限
    // 4. 設定初始狀態 (Status=1)
    // 5. 更新讀書會目前人數
    updateClubParticipants(club, 1);
    return clubRegistrationsRepository.save(newReg);
}
```

### C. Controller 層 (`ClubRegistrationController.java`)
**差異分析**：移除冗餘參數，強化安全性。

```java
// [Before] 不安全的參數傳遞與非標準 HTTP Method
@PostMapping("/regcreate/{clubId}")
public ResponseEntity<?> createResgitration(@RequestBody ClubRegistrationsBean reg) {
    // 前端需自行組裝 Bean，包含 UserID，易被篡改
}

// [After] 使用 Token 解析身分
@PostMapping("/register/{clubId}")
public ResponseEntity<?> register(@PathVariable Integer clubId, HttpServletRequest req) {
    Integer userId = getUserIdFromToken(req); // 安全解析
    return ResponseEntity.ok(regService.register(clubId, userId));
}
```

### D. 前端視圖 (`UserBookClub.vue`)
**差異分析**：
*   **Before**: 簡單的 `v-data-table` 顯示所有資料，按鈕功能未綁定。
*   **After**: 
    *   引入 `bookClubService` 的新方法。
    *   `watch(tab)`: 根據分頁動態載入資料。
    *   `loadAllClubs`: 過濾僅顯示 Status=1 (報名中) 的活動。
    *   `handleRegister`/`handleCancel`: 整合 SweetAlert2 進行確認與執行。

---

## 4. 總結
經過本次重構，讀書會報名模組已具備商業運行的基礎能力。
1.  **安全性**：透過後端 Token 解析，確保使用者只能操作自己的報名紀錄。
2.  **完整性**：涵蓋了從報名、審核(預留)、報到至取消的完整生命週期。
3.  **易用性**：前端介面直觀，資訊分流清晰。

報告生成時間：2026-01-26
報告產生者：AI Assistant
