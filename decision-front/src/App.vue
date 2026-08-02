<script setup>
import { ref } from 'vue'
import axios from 'axios'

// ==================== 响应式数据 ====================

// 当前推荐结果（null 表示还没决定）
const decision = ref(null)

// 加载状态
const loading = ref(false)

// 请求失败时显示在页面内的提示信息
const errorMessage = ref('')

// 预设的卡片列表（用于首页展示）
const cardList = ref([
  {
    type: '美食',
    name: '今天吃什么？',
    description: '从丰富的美食选项中抽取今天的答案。'
  },
  {
    type: '娱乐',
    name: '今天玩什么？',
    description: '电影、游戏或户外活动，交给随机选择。'
  }
])

// 默认选中第一个类别，进入页面后可以直接开始决策
const selectedCard = ref(cardList.value[0])

// 类型对应的 emoji 图标
const typeEmoji = {
  '美食': '🍜',
  '娱乐': '🎮'
}

// ==================== 方法 ====================

/**
 * 点击卡片切换类别
 */
function selectCard(card) {
  selectedCard.value = card
  decision.value = null
  errorMessage.value = ''
}

/**
 * 点击"帮我决定"按钮
 * 调用后端接口获取随机推荐
 */
async function fetchDecision() {
  loading.value = true
  errorMessage.value = ''

  try {
    // 将当前选中的类别传给后端
    const params = { type: selectedCard.value.type }

    // 使用相对路径，通过 Vite 代理转发到后端
    const response = await axios.get('/api/decision/random', { params })
    decision.value = response.data
  } catch (error) {
    console.error('请求失败：', error)
    // 输出更详细的错误信息帮助调试
    if (error.response) {
      console.error('后端响应错误：', error.response.status, error.response.data)
    } else if (error.request) {
      console.error('未收到响应，请检查网络或后端是否启动：', error.message)
    }
    errorMessage.value = '暂时无法获取决定，请检查后端服务是否已经启动，然后再试一次。'
  } finally {
    loading.value = false
  }
}

/**
 * 根据类型获取对应 emoji
 */
function getEmoji(type) {
  return typeEmoji[type] || '🎯'
}
</script>

<template>
  <div class="app-container">
    <!-- ===== 头部标题区 ===== -->
    <header class="header">
      <span class="eyebrow">DECISION SAVIOR</span>
      <h1 class="title">选择困难症<span>救星</span></h1>
      <p class="subtitle">吃什么、玩什么，让随机选择帮你快速决定</p>
      <div class="feature-list" aria-label="项目特点">
        <span>✦ 两种场景</span>
        <span>✦ 50+ 灵感</span>
        <span>✦ 一键决定</span>
      </div>
    </header>

    <main class="decision-panel">
      <!-- ===== 分类卡片区 ===== -->
      <section class="card-section">
        <div class="section-heading">
          <div>
            <span class="step-label">STEP 01</span>
            <h2 class="section-title">选择决策类别</h2>
          </div>
          <p class="section-hint">点击卡片切换场景</p>
        </div>
        <div class="card-list">
          <button
            v-for="card in cardList"
            :key="card.name"
            type="button"
            class="card"
            :class="{ 'card-selected': selectedCard === card }"
            :aria-pressed="selectedCard === card"
            @click="selectCard(card)"
          >
            <span class="card-emoji">{{ getEmoji(card.type) }}</span>
            <span class="card-info">
              <span class="card-type">{{ card.type }}</span>
              <span class="card-name">{{ card.name }}</span>
              <span class="card-desc">{{ card.description }}</span>
            </span>
            <span v-if="selectedCard === card" class="card-check">已选择</span>
          </button>
        </div>
      </section>

      <!-- ===== 按钮区 ===== -->
      <div class="action-area">
        <span class="step-label">STEP 02</span>
        <button
          class="decide-btn"
          :disabled="loading"
          :aria-busy="loading"
          @click="fetchDecision"
        >
          {{ decision ? `↻ 再来一次「${selectedCard.type}」` : `✨ 帮我决定「${selectedCard.type}」` }}
        </button>
        <p>别纠结，相信这一次随机选择</p>
      </div>

      <div v-if="errorMessage" class="error-message" role="alert">
        <span>!</span>
        <div>
          <strong>请求没有成功</strong>
          <p>{{ errorMessage }}</p>
        </div>
      </div>

      <!-- ===== 结果展示区 ===== -->
      <section v-if="decision" class="result-section">
        <span class="result-kicker">YOUR DECISION · 本次决定</span>
        <div class="result-card">
          <div :key="`emoji-${decision.type}-${decision.name}`" class="result-emoji">{{ getEmoji(decision.type) }}</div>
          <div :key="`info-${decision.type}-${decision.name}`" class="result-info">
            <span class="result-type">{{ decision.type }}灵感</span>
            <span class="result-name">{{ decision.name }}</span>
            <span class="result-desc">{{ decision.description }}</span>
          </div>
        </div>
      </section>
    </main>

    <!-- ===== 底部流程说明 ===== -->
    <footer class="footer">
      <p>Vue 3 × Spring Boot · 让每一次选择都轻松一点</p>
    </footer>
  </div>
</template>

<style scoped>
/* ===== 整体布局 ===== */
.app-container {
  max-width: 560px;
  margin: 0 auto;
  padding: 32px 20px 60px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  color: #2c3e50;
}

/* ===== 头部 ===== */
.header {
  text-align: center;
  margin-bottom: 36px;
}

.title {
  font-size: 32px;
  font-weight: 800;
  margin: 0 0 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 15px;
  color: #7f8c8d;
  margin: 0;
}

/* ===== 区域标题 ===== */
.section-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 14px;
  color: #34495e;
}

/* ===== 卡片列表 ===== */
.card-section {
  margin-bottom: 24px;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.section-hint {
  font-size: 13px;
  color: #95a5a6;
  margin: -8px 0 14px;
}

.card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 2px solid #e9ecef;
  cursor: pointer;
  transition: transform 0.15s, border-color 0.2s, background 0.2s, box-shadow 0.2s;
  user-select: none;
}

.card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.card-selected {
  background: #f0f0ff;
  border-color: #667eea;
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.2);
}

.card-check {
  font-size: 18px;
  flex-shrink: 0;
  margin-left: auto;
}

.card-emoji {
  font-size: 28px;
  flex-shrink: 0;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.card-type {
  font-size: 12px;
  color: #667eea;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.card-name {
  font-size: 16px;
  font-weight: 600;
}

.card-desc {
  font-size: 13px;
  color: #95a5a6;
}

/* ===== 按钮 ===== */
.action-area {
  text-align: center;
  margin-bottom: 30px;
}

.decide-btn {
  padding: 14px 48px;
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.decide-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.decide-btn:active:not(:disabled) {
  transform: translateY(0);
}

.decide-btn:disabled {
  opacity: 1;
  cursor: wait;
}

/* ===== 结果卡片 ===== */
.result-section {
  margin-bottom: 30px;
}

.result-card {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 20px;
  background: linear-gradient(135deg, #f0f0ff 0%, #faf0ff 100%);
  border-radius: 16px;
  border: 2px solid #667eea;
  animation: popIn 0.35s ease-out;
}

@keyframes popIn {
  0% {
    opacity: 0;
    transform: scale(0.9);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

.result-emoji {
  font-size: 48px;
  flex-shrink: 0;
}

.result-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.result-type {
  font-size: 12px;
  color: #667eea;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.result-name {
  font-size: 22px;
  font-weight: 800;
  color: #2c3e50;
}

.result-desc {
  font-size: 14px;
  color: #7f8c8d;
}

/* ===== 底部 ===== */
.footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.footer p {
  font-size: 12px;
  color: #bdc3c7;
  margin: 0;
  line-height: 1.6;
}

/* ===== 录屏展示版视觉设计 ===== */
.app-container {
  position: relative;
  max-width: 980px;
  min-height: 100vh;
  margin: 0 auto;
  padding: 64px 24px 32px;
  color: #252338;
}

.header {
  margin-bottom: 30px;
}

.eyebrow,
.step-label,
.result-kicker {
  display: inline-block;
  color: #7357d8;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.18em;
}

.eyebrow {
  margin-bottom: 13px;
  padding: 7px 12px;
  background: rgba(115, 87, 216, 0.1);
  border: 1px solid rgba(115, 87, 216, 0.14);
  border-radius: 999px;
}

.title {
  margin: 0;
  color: #282538;
  background: none;
  font-size: clamp(38px, 6vw, 62px);
  line-height: 1.08;
  letter-spacing: -0.045em;
  -webkit-text-fill-color: initial;
}

.title span {
  margin-left: 0.14em;
  background: linear-gradient(135deg, #7357d8, #f18454);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  margin-top: 14px;
  color: #757086;
  font-size: 16px;
}

.feature-list {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px 20px;
  margin-top: 20px;
  color: #8b8499;
  font-size: 12px;
  font-weight: 600;
}

.decision-panel {
  padding: 34px;
  background: rgba(255, 255, 255, 0.78);
  border: 1px solid rgba(255, 255, 255, 0.92);
  border-radius: 30px;
  box-shadow: 0 24px 70px rgba(71, 54, 119, 0.12);
  backdrop-filter: blur(18px);
}

.card-section {
  margin-bottom: 0;
}

.section-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
}

.section-title {
  margin: 5px 0 0;
  color: #302d40;
  font-size: 23px;
}

.section-hint {
  margin: 0 0 2px;
  color: #9a94a5;
  font-size: 12px;
}

.card-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.card {
  position: relative;
  min-height: 150px;
  padding: 22px;
  gap: 16px;
  overflow: hidden;
  color: #302d40;
  text-align: left;
  background: #fbfaff;
  border: 1px solid #ebe7f5;
  border-radius: 20px;
  box-shadow: none;
}

.card::after {
  position: absolute;
  right: -34px;
  bottom: -48px;
  width: 120px;
  height: 120px;
  background: rgba(115, 87, 216, 0.07);
  border-radius: 50%;
  content: "";
  transition: transform 0.25s ease;
}

.card:hover {
  transform: translateY(-3px);
  border-color: #d9d1ef;
  box-shadow: 0 14px 26px rgba(80, 62, 124, 0.09);
}

.card:hover::after {
  transform: scale(1.25);
}

.card-selected {
  background: linear-gradient(145deg, #f5f1ff, #fffafd);
  border-color: #8c73df;
  box-shadow: 0 12px 30px rgba(115, 87, 216, 0.15);
}

.card-emoji {
  display: grid;
  width: 58px;
  height: 58px;
  place-items: center;
  flex: 0 0 58px;
  font-size: 29px;
  background: #ebe5ff;
  border-radius: 17px;
}

.card:nth-child(2) .card-emoji {
  background: #ffeadc;
}

.card-info {
  gap: 5px;
  min-width: 0;
}

.card-type {
  color: #7357d8;
  font-size: 11px;
}

.card-name {
  color: #302d40;
  font-size: 18px;
}

.card-desc {
  color: #8b8498;
  line-height: 1.55;
}

.card-check {
  position: absolute;
  top: 13px;
  right: 13px;
  padding: 5px 8px;
  color: #7357d8;
  font-size: 10px;
  font-weight: 700;
  background: #ebe5ff;
  border-radius: 999px;
}

.action-area {
  margin: 30px 0 0;
  padding-top: 27px;
  border-top: 1px solid #eeeaf5;
}

.action-area .step-label {
  display: block;
  margin-bottom: 12px;
}

.decide-btn {
  width: min(100%, 430px);
  padding: 16px 28px;
  font-size: 17px;
  background: linear-gradient(135deg, #7357d8 0%, #8c6ee7 58%, #ef8b60 125%);
  border-radius: 16px;
  box-shadow: 0 12px 28px rgba(115, 87, 216, 0.28);
}

.decide-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 16px 34px rgba(115, 87, 216, 0.34);
}

.action-area p {
  margin-top: 11px;
  color: #a09aa9;
  font-size: 11px;
}

.error-message {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  max-width: 650px;
  margin: 22px auto 0;
  padding: 14px 16px;
  color: #82412f;
  text-align: left;
  background: #fff3ec;
  border: 1px solid #f3cbbd;
  border-radius: 14px;
}

.error-message > span {
  display: grid;
  width: 24px;
  height: 24px;
  place-items: center;
  flex: 0 0 24px;
  color: #fff;
  font-size: 13px;
  font-weight: 800;
  background: #df7959;
  border-radius: 50%;
}

.error-message strong {
  display: block;
  margin-bottom: 3px;
  font-size: 13px;
}

.error-message p {
  color: #9b5c49;
  font-size: 12px;
  line-height: 1.55;
}

.result-section {
  margin: 30px 0 0;
  padding-top: 28px;
  text-align: center;
  border-top: 1px solid #eeeaf5;
}

.result-kicker {
  margin-bottom: 13px;
}

.result-card {
  max-width: 650px;
  margin: 0 auto;
  padding: 25px 28px;
  background: linear-gradient(135deg, #f3efff 0%, #fff6ee 100%);
  border: 1px solid #dcd3f4;
  border-radius: 22px;
  box-shadow: 0 16px 36px rgba(85, 63, 138, 0.11);
}

.result-emoji {
  display: grid;
  width: 70px;
  height: 70px;
  place-items: center;
  flex: 0 0 70px;
  font-size: 37px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  box-shadow: 0 8px 18px rgba(88, 69, 130, 0.09);
}

.result-info {
  gap: 5px;
  text-align: left;
  animation: resultSlide 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}

.result-emoji {
  animation: emojiNudge 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}

@keyframes resultSlide {
  from {
    transform: translateY(7px);
  }
  to {
    transform: translateY(0);
  }
}

@keyframes emojiNudge {
  from {
    transform: translateY(5px) rotate(-5deg) scale(0.94);
  }
  to {
    transform: translateY(0) rotate(0) scale(1);
  }
}

.result-type {
  color: #7357d8;
  font-size: 11px;
}

.result-name {
  color: #292638;
  font-size: 26px;
}

.result-desc {
  color: #7e778a;
  line-height: 1.6;
}

.footer {
  padding-top: 25px;
  border-top: 0;
}

.footer p {
  color: #aaa3b3;
  letter-spacing: 0.04em;
}

@media (max-width: 680px) {
  .app-container {
    padding: 38px 16px 24px;
  }

  .decision-panel {
    padding: 22px 16px;
    border-radius: 23px;
  }

  .card-list {
    grid-template-columns: 1fr;
  }

  .section-heading {
    display: block;
  }

  .section-hint {
    margin-top: 7px;
  }

  .result-card {
    align-items: flex-start;
    padding: 20px;
  }
}
</style>
