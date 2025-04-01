package top.orosirian.blog.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SaInterceptor())
				.addPathPatterns("/**") // 注册 Sa-Token 拦截器，打开注解式鉴权功能
				.excludePathPatterns("/ws/notifications/**");
	}

	@Bean
	public SaServletFilter getSaServletFilter() {
		return new SaServletFilter()

				// 指定 [拦截路由] 与 [放行路由]
				.addInclude("/**")
				.addExclude("/favicon.ico")

				// 认证函数: 每次请求执行
				.setAuth(obj -> {
					SaManager.getLog().debug("----- 请求path={}  提交token={}", SaHolder.getRequest().getRequestPath(),
							StpUtil.getTokenValue());
					// ...
				})

				// 异常处理函数：每次认证函数发生异常时执行此函数
				.setError(e -> {
					return SaResult.error(e.getMessage());
				})

				// 前置函数：在每次认证函数之前执行
				.setBeforeAuth(obj -> {
					SaHolder.getResponse()

							// ---------- 设置跨域响应头 ----------
							// 允许指定域访问跨域资源"http://47.121.178.97"
							// .setHeader("Access-Control-Allow-Origin", "http://www.orosirian.top")
							.setHeader("Access-Control-Allow-Origin", "http://localhost:5173")
							// .setHeader("Access-Control-Allow-Origin", "*")
							// 允许所有请求方式
							.setHeader("Access-Control-Allow-Methods", "*")
							// 允许的header参数
							.setHeader("Access-Control-Allow-Headers", "*")
							// 允许携带凭证（如cookies）
							.setHeader("Access-Control-Allow-Credentials", "true")
							// 有效时间
							.setHeader("Access-Control-Max-Age", "3600");

					// 如果是预检请求，则立即返回到前端
					SaRouter.match(SaHttpMethod.OPTIONS)
						.free(r -> System.out.println("--------OPTIONS预检请求，不做处理"))
						.back();
				});
	}

}
