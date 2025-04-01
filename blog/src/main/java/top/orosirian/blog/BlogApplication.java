package top.orosirian.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BlogApplication {

	// is not eligible for getting processed by all BeanPostProcessors怀疑是RocketMQ自己的问题，暂时不处理

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

}
