import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './utils/router.ts'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { useUserStore } from './utils/stores.ts'

const pinia = createPinia()
const app = createApp(App)

app.use(pinia);
app.use(router);
app.use(ElementPlus);

const userStore = useUserStore();
await userStore.update()

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')

