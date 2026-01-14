/**
 * 此檔案為全站樣式顏色的單一管理點。
 * 修改此處顏色會自動套用到所有使用 Vuetify Theme 的元件。
 * 在標籤中加入color屬性並自訂義名稱，在forestTheme中加入對應名稱即可。
 */
export const forestTheme = {
  dark: false,
  colors: {
    background: '#F5F5DC', // 米色 (全站背景)
    surface: '#FFFFFF', // 白色 (卡片/容器背景)
    primary: '#2E5C43', // 深森林綠 (主視覺/導航/主要按鈕)
    secondary: '#833737a8', // 大地棕 (次要強調/頁尾/封存按鈕)
    accent: '#81C784', // 嫩葉綠 (高亮/選中狀態)
    error: '#B00020', // 紅色 (錯誤)
    info: '#16af30ff', // 資訊色
    success: '#4CAF50', // 綠色 (成功/開關開啟)
    warning: '#FB8C00', // 橘色 (警告)
  },
}

/**
 * User Layout 專用主題
 * 針對前台使用者設計，色調較為溫暖、活潑
 */
export const userTheme = {
  dark: false,
  colors: {
    background: '#FFF8E1', // 淺米黃色 (溫暖背景)
    surface: '#FFFFFF', // 白色
    primary: '#5D4037', // 深褐色 (主要按鈕/導航)
    secondary: '#8D6E63', // 淺褐色 (次要)
    accent: '#FFB74D', // 橘黃色 (強調/促銷)
    error: '#D32F2F', // 紅色
    info: '#1976D2', // 藍色
    success: '#388E3C', // 綠色
    warning: '#FBC02D', // 黃色
  },
}
