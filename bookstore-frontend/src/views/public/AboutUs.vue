<template>
  <div class="about-wrapper">
    <v-parallax
      :src="aboutImage"
      height="550"
      class="about-parallax-header"
    >
      <div class="fill-height hero-dark-overlay d-flex align-center">
        <v-container>
          <v-row justify="center">
            <v-col cols="12" class="text-center">
              <h1 class="text-h2 font-weight-black mb-4 text-white">關於森林書屋</h1>
              <h4 class="text-h5 font-weight-medium text-white opacity-90">
                一群熱愛閱讀的人，在這裡為您築起一座心靈森林。
              </h4>
            </v-col>
          </v-row>
        </v-container>
      </div>
    </v-parallax>

<v-container class="py-15">
    <v-row justify="center">
      <v-col cols="12" md="10" lg="9">
        <h2 class="text-h4 font-weight-bold text-primary mb-8">森林書屋屋主</h2>
        <v-row class="mb-12">
          
          <v-col cols="12" md="6">
            <v-card class="member-card pa-5 rounded-xl bg-light-green h-100" flat border>
              <v-row no-gutters align="center">
                <v-col cols="auto" class="me-4">
                  <v-avatar size="80" class="bg-white elevation-1">
                    <v-img :src="alexFan" alt="范珈禎"></v-img>
                  </v-avatar>
                </v-col>
                <v-col>
                  <div class="d-flex align-baseline mb-1 flex-wrap">
                    <span class="text-h5 font-weight-bold text-primary">范珈禎</span>
                    <span class="text-subtitle-1 ms-2 text-grey-darken-1">Alex Fan</span>
                    <v-chip size="x-small" color="primary" class="ms-2 font-weight-bold" variant="flat">組長</v-chip>
                  </div>
                  <div class="text-subtitle-2 text-grey-darken-1 mb-2">愛書成痴症末期的軟體工程師</div>
                  <p class="text-caption text-grey-darken-2 leading-tight">
                    <strong>負責模組：</strong><br>
                    註冊登入、忘記密碼、個人資料維護、自動寄發驗證信、客服聊天室、管理員日誌、書本收藏清單、瀏覽紀錄
                  </p>
                  <div class="d-flex align-center mt-4">
                    <v-icon size="16" color="grey-darken-1" class="me-1">mdi-email-outline</v-icon>
                    <a href="mailto:okingopen@gmail.com" class="text-caption text-decoration-none email-link">
                      okingopen@gmail.com
                    </a>
                  </div>
                </v-col>
              </v-row>
            </v-card>
          </v-col>

          <v-col cols="12" md="6">
            <v-card class="member-card pa-5 rounded-xl bg-light-green h-100" flat border>
              <v-row no-gutters align="center">
                <v-col cols="auto" class="me-4">
                  <v-avatar size="80" class="bg-white elevation-1">
                    <v-img src="@/assets/imgs/avatar_alex_hsieh.jpg" alt="謝承翰"></v-img>
                  </v-avatar>
                </v-col>
                <v-col>
                  <div class="d-flex align-baseline mb-1 flex-wrap">
                    <span class="text-h5 font-weight-bold text-primary">謝承翰</span>
                    <span class="text-subtitle-1 ms-2 text-grey-darken-1">Alex Hsieh</span>
                    <v-chip size="x-small" color="secondary" class="ms-2 font-weight-bold" variant="flat">副組長</v-chip>
                  </div>
                  <div class="text-subtitle-2 text-grey-darken-1 mb-2">專注細節的後端開發者</div>
                  <p class="text-caption text-grey-darken-2 leading-tight">
                    <strong>負責模組：</strong><br>
                    購物車模組、庫存檢核機制、結帳流程優化、第三方物流與金流串接、透明化訂單歷程、訂單取消退貨、後台管理維運與數據分析、優惠券彈性配置與領取
                  </p>
                  <div class="d-flex align-center mt-4">
                    <v-icon size="16" color="grey-darken-1" class="me-1">mdi-email-outline</v-icon>
                    <a href="mailto:alexalex09280928@gmail.com" class="text-caption text-decoration-none email-link">
                      alexalex09280928@gmail.com
                    </a>
                  </div>
                </v-col>
              </v-row>
            </v-card>
          </v-col>

          <v-col cols="12" md="6">
            <v-card class="member-card pa-5 rounded-xl bg-light-green h-100" flat border>
              <v-row no-gutters align="center">
                <v-col cols="auto" class="me-4">
                  <v-avatar size="80" class="bg-white elevation-1">
                    <v-img src="@/assets/imgs/avatar_alex_sung.jpg" alt="宋泓孝"></v-img>
                  </v-avatar>
                </v-col>
                <v-col>
                  <div class="d-flex align-baseline mb-1 flex-wrap">
                    <span class="text-h5 font-weight-bold text-primary">宋泓孝</span>
                    <span class="text-subtitle-1 ms-2 text-grey-darken-1">Alex Sung</span>
                    <v-chip size="x-small" color="success" class="ms-2 font-weight-bold" variant="flat">技術長</v-chip>
                  </div>
                  <div class="text-subtitle-2 text-grey-darken-1 mb-2">追求卓越的技術架構師</div>
                  <p class="text-caption text-grey-darken-2 leading-tight">
                    <strong>負責模組：</strong><br>
                    書籍檢索系統、多條件篩選演算法、讀書會場次預約、Redis 緩存優化與資料庫正規化設計
                  </p>
                  <div class="d-flex align-center mt-4">
                    <v-icon size="16" color="grey-darken-1" class="me-1">mdi-email-outline</v-icon>
                    <a href="mailto:alex122694@gmail.com" class="text-caption text-decoration-none email-link">
                      alex122694@gmail.com
                    </a>
                  </div>
                </v-col>
              </v-row>
            </v-card>
          </v-col>

          <v-col cols="12" md="6">
            <v-card class="member-card pa-5 rounded-xl bg-light-green h-100" flat border>
              <v-row no-gutters align="center">
                <v-col cols="auto" class="me-4">
                  <v-avatar size="80" class="bg-white elevation-1">
                    <v-img :src="jamesLai" alt="賴郁仁"></v-img>
                  </v-avatar>
                </v-col>
                <v-col>
                  <div class="d-flex align-baseline mb-1 flex-wrap">
                    <span class="text-h5 font-weight-bold text-primary">賴郁仁</span>
                    <span class="text-subtitle-1 ms-2 text-grey-darken-1">James Lai</span>
                    <v-chip size="x-small" color="grey" class="ms-2 font-weight-bold" variant="flat">營運長</v-chip>
                  </div>
                  <div class="text-subtitle-2 text-grey-darken-1 mb-2">遊走於山海與程式碼之間的工程師</div>
                  <p class="text-caption text-grey-darken-2 leading-tight">
                    <strong>負責模組：</strong><br>
                    書籍評價撰寫與編輯、星級評分動態運算、評價篩選與排序、違規內容檢舉流程、後台檢舉審核管理
                  </p>
                  <div class="d-flex align-center mt-4">
                    <v-icon size="16" color="grey-darken-1" class="me-1">mdi-email-outline</v-icon>
                    <a href="mailto:jameslai0224@gmail.com" class="text-caption text-decoration-none email-link">
                      jameslai0224@gmail.com
                    </a>
                  </div>
                </v-col>
              </v-row>
            </v-card>
          </v-col>
          
        </v-row>
          <div class="section-content mb-12">
            <h2 class="text-h4 font-weight-bold text-primary mb-6">我們的故事</h2>
            <p class="text-body-1 text-grey-darken-2 leading-loose">
              「森林書屋（Forest Bookstore）」的起點，並非來自什麼資深的商業團隊，而是源於一群正走在軟體工程師修煉之路上的「初學者」。我們在代碼的邏輯中探索未來，卻在文字的溫度裡找回初心。<br><br>因為自己熱愛學習，更不願看到閱讀在碎片化時代中孤島化，於是我們發揮所長，將這份對成功的渴望與對書本的執著，一磚一瓦地築成這座森林。<br><br>在這裡，你不只能找尋好書，更能透過我們專屬的讀書會模組與志同道合的書友相聚。森林書屋是我們邀請你一同成長的實踐，讓閱讀不再只是獨自翻閱，而是一場溫暖的集體相遇！
            </p>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script setup>
import aboutImage from '@/components/imgs/S__34324484_0.jpg'
import alexFan from '@/components/imgs/alex_fan.png'
import jamesLai from '@/components/imgs/james_lai.jpg'
</script>

<style scoped>
.about-wrapper {
  background-color: #fdfbf7;
}

.hero-dark-overlay {
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.3) 0%,
    rgba(0, 0, 0, 0.5) 50%,
    rgba(0, 0, 0, 0.3) 100%
  );
}

.about-parallax-header :deep(.v-img__img) {
  object-position: center 65% !important; 
  object-fit: cover !important; 
}

.leading-loose {
  line-height: 1.8;
  letter-spacing: 0.5px;
}

.leading-tight {
  line-height: 1.4;
  letter-spacing: 0.2px;
}

.opacity-90 {
  opacity: 0.9;
}

.bg-light-green {
  background-color: rgba(46, 92, 67, 0.05) !important;
  border-color: rgba(46, 92, 67, 0.1) !important;
}

.member-card {
  transition: all 0.3s ease;
}

.member-card:hover {
  transform: translateY(-5px);
  background-color: rgba(46, 92, 67, 0.08) !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05) !important;
}

.text-primary {
  color: #2e5c43 !important;
}

h2 {
  position: relative;
  padding-bottom: 12px;
}

h2::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: 0;
  width: 50px;
  height: 4px;
  background-color: #2e5c43;
  border-radius: 2px;
}

.email-link {
  color: #616161; 
  transition: color 0.2s ease;
}

.email-link:hover {
  color: #2e5c43 !important; 
  text-decoration: underline !important;
}

.member-card .v-row {
  height: 100%;
}
</style>