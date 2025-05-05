package top.orosirian.blog.utils.mq;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.SendEmailRequest;
import com.tencentcloudapi.ses.v20201002.models.Template;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailHandler {

    private static String fromEmailAddress = "orosirian <orosirian@mail.orosirian.top>";

    private static Long templateID = 30614L;

    @Autowired
    private SesClient sesClient;

    @RabbitListener(queues = "email.verify.queue")
    public void handleEmailTask(EmailTask task) {
        try {
            sendEmail(task.getEmailAddress(), task.getCode());
        } catch (Exception e) {
            log.error("邮件发送失败: {}", task.getEmailAddress(), e);
            throw new AmqpRejectAndDontRequeueException(e); // 触发重试机制
        }
    }

    @RabbitListener(queues = "email.dead.queue")
    public void handleDeadLetter(EmailTask task, Message failedMessage) {
        log.warn("邮件 {} 发送最终失败: {}", task.getEmailAddress(), failedMessage);
    }

    void sendEmail(String emailAddress, String code) throws TencentCloudSDKException {
        SendEmailRequest req = new SendEmailRequest();
        req.setFromEmailAddress(fromEmailAddress);
        req.setSubject("Orosirian登录验证码");
        String[] destination = new String[]{emailAddress};
        req.setDestination(destination);
        Template template = new Template();
        template.setTemplateID(templateID);
        template.setTemplateData("{\"Verify\":\""+code+"\"}");
        req.setTemplate(template);

        sesClient.SendEmail(req);
    }
    
}
