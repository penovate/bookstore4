<template>
  <v-app>
    <v-main class="bg-grey-lighten-4">
      <v-container class="fill-height d-flex justify-center" fluid>
        <v-card width="100%" max-width="700" class="pa-8 elevation-10" rounded="lg">
          <v-card-item class="text-center">
            <v-icon icon="mdi-account-plus" size="large" color="brown" class="mb-2"></v-icon>
            <v-card-title class="text-h5 font-weight-bold text-brown-darken-2">
              新增會員資料
            </v-card-title>
          </v-card-item>

          <v-divider class="my-4"></v-divider>

          <v-form ref="insertForm" @submit.prevent="handleSubmit">
            <v-container>
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.email"
                    label="帳號 (Email)"
                    type="email"
                    variant="outlined"
                    density="compact"
                    :rules="[
                      (v) => !!v || 'Email 為必填項',
                      (v) => /.+@.+\..+/.test(v) || 'Email 格式不正確',
                    ]"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.userPwd"
                    label="密碼"
                    type="password"
                    variant="outlined"
                    density="compact"
                    :rules="[(v) => !!v || '密碼為必填項']"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.userName"
                    label="姓名"
                    variant="outlined"
                    density="compact"
                    :rules="[(v) => !!v || '姓名為必填項']"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.phoneNum"
                    label="手機號碼"
                    variant="outlined"
                    density="compact"
                    placeholder="請輸入開頭為 09 的 10 位數字"
                    maxlength="10"
                    counter="10"
                    persistent-counter
                    :rules="[
                      (v) => !!v || '電話為必填項',
                      (v) => /^09\d{8}$/.test(v) || '必須是 09 開頭的 10 位數字',
                    ]"
                    @input="formData.phoneNum = formData.phoneNum.replace(/[^\d]/g, '')"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                  <v-select
                    v-model="formData.gender"
                    label="性別"
                    :items="[
                      { title: '男', value: 'M' },
                      { title: '女', value: 'F' },
                    ]"
                    variant="outlined"
                    density="compact"
                  ></v-select>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="formData.birth"
                    label="生日"
                    type="date"
                    variant="outlined"
                    density="compact"
                  ></v-text-field>
                </v-col>

                <v-col cols="12">
                  <v-text-field
                    v-model="formData.address"
                    label="地址"
                    variant="outlined"
                    density="compact"
                  ></v-text-field>
                </v-col>

                <v-col cols="12">
                  <v-select
                    v-model="formData.userType"
                    label="權限等級"
                    :items="[
                      { title: '超級管理員', value: 0 },
                      { title: '一般管理員', value: 1 },
                    ]"
                    :rules="[(v) => (v !== null && v !== '') || '請選擇權限等級']"
                    variant="outlined"
                    density="compact"
                    color="brown"
                    required
                  ></v-select>
                </v-col>
              </v-row>
            </v-container>

            <v-divider class="my-4"></v-divider>

            <v-card-actions class="justify-center">
              <v-btn type="submit" color="brown" variant="elevated" size="large" class="px-8 mr-4">
                新增會員資料
              </v-btn>
              <v-btn
                color="grey-darken-1"
                variant="outlined"
                size="large"
                class="px-8"
                @click="router.push('/dev/admin/users')"
              >
                取消返回
              </v-btn>
            </v-card-actions>
          </v-form>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import Swal from 'sweetalert2'

const router = useRouter()
const insertForm = ref(null)

const formData = reactive({
  email: '',
  userPwd: '',
  userName: '',
  gender: '',
  birth: '',
  phoneNum: '',
  address: '',
  userType: '',
})

const handleSubmit = async () => {
  const { valid } = await insertForm.value.validate()

  if (!valid) return

  try {
    const response = await axios.post('http://localhost:8080/api/users/insert', formData)

    if (response.data.success) {
      Swal.fire({
        icon: 'success',
        title: '新增成功！',
        text: `會員 ${formData.userName} 已成功加入`,
        confirmButtonColor: '#a07d58',
      }).then(() => {
        router.push('/dev/admin/users')
      })
    } else {
      Swal.fire('失敗', response.data.message, 'error')
    }
  } catch (error) {
    console.error('API 請求失敗:', error)
    Swal.fire('系統錯誤', '新增失敗，請檢查資料是否重複或伺服器狀態', 'error')
  }
}
</script>

<style scoped>
.fill-height {
  background: linear-gradient(135deg, #fcf8f0 0%, #ede0d4 100%);
}

.text-brown-darken-2 {
  color: #5d4037 !important;
}

:deep(.v-label.v-field-label--floating) {
  color: #7b5e47 !important;
}
</style>
