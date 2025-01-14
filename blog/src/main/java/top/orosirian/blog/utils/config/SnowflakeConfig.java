package top.orosirian.blog.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.hutool.core.lang.Snowflake;

@Configuration
public class SnowflakeConfig {

    private Integer workerId = 1;

    private Integer dataCenterId = 1;

    @Bean
    public Snowflake snowflake() {
        return new Snowflake(workerId, dataCenterId);
    }
    
}
