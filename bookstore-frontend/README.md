# 書店專案前端 (Bookstore4 Frontend)

本專案為 Bookstore4 的前端部分，採用 Vue 3 與 Vite 建置，並針對繁體中文語系使用者進行最佳化。

## 推薦開發環境 (Recommended IDE Setup)

- [VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (請停用 Vetur)。

## 推薦瀏覽器設定 (Recommended Browser Setup)

- **Chromium-based 瀏覽器 (Chrome, Edge, Brave 等):**
  - 安裝 [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd)
  - 在 Chrome DevTools 中開啟 [Custom Object Formatter](http://bit.ly/object-formatters) 以獲得更佳的除錯體驗。
- **Firefox:**
  - 安裝 [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - 在 Firefox DevTools 中開啟 [Custom Object Formatter](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## 專案設定與執行 (Project Setup)

### 安裝依賴套件 (Install Dependencies)

```sh
npm install
```

### 啟動開發伺服器 (Start Development Server)

包含熱重載 (Hot-Reload) 功能，適合開發時使用。

```sh
npm run dev
```

### 建置生產版本 (Build for Production)

編譯並壓縮程式碼，準備部署。

```sh
npm run build
```

### 程式碼格式化 (Format Code)

```sh
npm run format
```

## 技術堆疊 (Tech Stack)

- **核心框架**: Vue 3
- **建置工具**: Vite
- **狀態管理**: Pinia
- **路由管理**: Vue Router
- **UI 元件**: Vuetify / Sass
- **API 串接**: Axios

## 自定義設定 (Customize Configuration)

請參考 [Vite 配置指南](https://vite.dev/config/)。
