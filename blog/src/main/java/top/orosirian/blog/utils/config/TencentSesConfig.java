package top.orosirian.blog.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ses.v20201002.SesClient;

@Configuration
public class TencentSesConfig {

    private static String secretId = "AKIDa2Jn7II7kApMjvuhZMyRagebXJG9vXzK";
    private static String secretKey = "BAzd5MTPwKYeZLxVu78wYyDG7nHZfZfc";
    private static String httpProfileUrl = "ses.tencentcloudapi.com";
    private static String sesClient = "ap-guangzhou";

    @Bean
    public SesClient sesClient() {
        Credential cred = new Credential(secretId, secretKey);
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(httpProfileUrl);
        
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        return new SesClient(cred, sesClient, clientProfile);
    }

}
