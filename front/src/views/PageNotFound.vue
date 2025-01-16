<template>
    <div class="not-found">
        <h1>404 - 页面未找到</h1>
        <p>您查找的页面不存在。这可能是由于拼写错误或页面已被移除。</p>
        <router-link to="/" class="custom-link">返回首页</router-link>
    </div>
</template>
  
<script>

</script>

<style scoped>
.not-found {
    text-align: center;
    padding: 50px;
}

.custom-link {
    text-decoration: none;            /* 移除下划线 */
    color: #2c3e50;                 /* 设置链接的颜色 */
    transition: color 0.3s ease;      /* 设置鼠标悬停时的颜色 */
}
.custom-link:hover,
.custom-link:active {
  
    color: #3498db;                 /* 鼠标悬停或激活时的颜色 */
}
.custom-link:visited {
    color: #2c3e50;                 /* 保持与原始颜色一致 */
}
</style>