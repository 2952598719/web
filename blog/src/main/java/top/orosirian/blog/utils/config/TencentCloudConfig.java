package top.orosirian.blog.utils.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TencentCloudConfig {

    @Value("${tengxun.secretId}")
    private String secretId;

    @Value("${tengxun.secretKey}")
    private String secretKey;

    @Value("${tengxun.httpProfile}")
    private String httpProfile;

    @Value("${tengxun.sesClient}")
    private String sesClient;

    @Value("${tengxun.fromEmailAddress}")
    private String fromEmailAddress;

    @Value("${tengxun.templateID}")
    private Long templateID;

    // 省略getter和setter方法
}
