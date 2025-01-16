import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue';
// import UserCenter from '../views/UserCenter.vue';
// import UserPage from '../views/UserPage.vue';
import ArticlePage from '../views/ArticlePage.vue'
import WriteArticle from '../views/WriteArticle.vue';
// import ArticleListPage from '../views/ArticleListPage.vue';
import PageNotFound from '../views/PageNotFound.vue';


const blogRoutes = [
	{
		path: '/',
		name: 'Home',
		component: Home,
	},
	{
		path: '/page/:page',
		name: 'HomeWithPage',
		component: Home,
	},
	// 用户相关
	// {
	// 	path: '/center',
	// 	name: 'UserCenter',
	// 	component: UserCenter,
	// },
	// {
	// 	path: '/user/:userName',
	// 	name: 'UserPage',
	// 	component: UserPage,
	// },
	// 文章相关
	{
		path: '/article/:articleUid',
		name: 'ArticlePage',
		component: ArticlePage,
	},
	{
		path: '/write',
		name: 'WriteArticle',
		component: WriteArticle,
	},
	{
		path: '/write/:articleUid',
		name: 'ModifyArticle',
		component: WriteArticle,
	},
	// {
	// 	path: '/articleList/:cond/:condStr',
	// 	name: 'ArticleList',
	// 	component: ArticleListPage,
	// },
	// 捕获路径
	{
		path: '/PageNotFound',
		component: PageNotFound,
	},
	{
		path: '/:pathMatch(.*)*',
		component: PageNotFound,
	},
]
const blogRouter = createRouter({   // createRouter这个函数接受一个对象，这个对象包含history和routes两个属性
	history: createWebHistory(),
	routes: blogRoutes,
});

export default blogRouter
