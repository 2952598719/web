package top.orosirian.blog.utils.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {    // 不加这一条会报错IllegalArgumentException
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 发信
     */

    // 创建发信交换机
    @Bean
    public DirectExchange emailDirectExchange() {
        // durable=true表示持久化（服务器重启后保留），autoDelete=false表示不自动删除
        return new DirectExchange("email.direct", true, false);
    }

    // 创建发信队列
    @Bean
    public Queue emailVerifyQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "email.dlx");
        args.put("x-dead-letter-routing-key", "email.dead");
        return new Queue("email.verify.queue", true, false, false, args);
    }

    // 绑定队列到发信交换机
    @Bean
    public Binding emailVerifyBinding() {
        return BindingBuilder.bind(emailVerifyQueue())
                            .to(emailDirectExchange())
                            .with("email.verify"); // 绑定键(routing key)
    }

    /**
     * 死信
     */

    // 创建死信交换机
    @Bean
    public DirectExchange emailDlxExchange() {
        return new DirectExchange("email.dlx", true, false);
    }
    
    // 创建死信队列
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("email.dead.queue", true, false, false);
    }

    // 绑定队列到死信交换机
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                            .to(emailDlxExchange())
                            .with("email.dead");
    }

    /**
     * 关注
     */
    // @Bean
    // public DirectExchange notificationExchange() {
    //     return new DirectExchange("notification.direct", true, false);
    // }

    // // 创建通知队列
    // @Bean
    // public Queue notificationQueue() {
    //     return new Queue("follow.notifications.queue", true, false, false);
    // }

    // // 绑定队列到通知交换机
    // @Bean
    // public Binding followBinding() {
    //     return BindingBuilder.bind(notificationQueue())
    //                         .to(notificationExchange())
    //                         .with("follow.#");  // 动态路由键，匹配上follow.xxxxx就转发到这个queue中间
    // }

}
