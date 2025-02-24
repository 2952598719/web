<template>
	<footer class="footer">
		<div class="container">
			<div class="contact">
				<p>联系方式：{{ phone }}</p>
				<p>邮箱：{{ email }}</p>
				<p>Github：<a :href="githubUrl" target="_blank">{{ username }}</a></p>
				<p>Gitee：<a :href="giteeUrl" target="_blank">{{ username }}</a></p>
			</div>
			<div class="record">
				<p>备案号：{{ recordNumber }}</p>
			</div>
		</div>
	</footer>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const phone = ref('17782913637');
const email = ref('2952598719@qq.com');
const username = ref('orosirian');
const githubUrl = ref(`https://github.com/2952598719`)
const giteeUrl = ref(`https://gitee.com/${username.value}`);
const recordNumber = ref('闽ICP备2024060507');
</script>

<style scoped>
.footer {
	/* margin-top: 100px; */
	padding-top: 50px;
	padding-bottom: 50px;
	background-color: #333;	/* 背景颜色 */ 
	color: #fff;				/* 字体颜色 */ 
	text-align: center;
}

.container {
	max-width: 1200px;
	margin: 0 auto;
	padding: 0 20px;
	display: flex;						/* 弹性盒子布局，用于元素的一些排列和分布 */
	justify-content: space-between;		/* 元素之间等间距分布 */
	align-items: center;				/* 垂直居中对齐 */
}

.contact p,								/* 设置其中的p标签上下外边距为5px，也就是让每行的行距增加 */
.record p {
	margin: 5px 0;
}

.contact a {
	color: #fff;						/* 默认超链接永久为白色，而不是点了就变紫色了 */
	text-decoration: none;				 /* 没有下划线 */
	transition: color 0.3s;
}

.contact a:hover {
	color: #409eff;
}
</style>