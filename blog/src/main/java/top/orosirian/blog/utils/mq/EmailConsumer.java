package top.orosirian.blog.utils.mq;

import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.SendEmailRequest;
import com.tencentcloudapi.ses.v20201002.models.Template;

import lombok.extern.slf4j.Slf4j;

@Component
@RocketMQMessageListener(topic = "MAIL", consumerGroup = "mail_group", messageModel = MessageModel.CLUSTERING)
@Slf4j
public class EmailConsumer implements RocketMQListener<EmailTask>  {

    private static String fromEmailAddress = "orosirian <orosirian@mail.orosirian.top>";

    private static Long templateID = 30614L;

    @Autowired
    private SesClient sesClient;

    @Override
    public void onMessage(EmailTask task) {
        try {
            sendEmail(task.getEmailAddress(), task.getCode());
        } catch (Exception e) {
            log.error("邮件发送失败: {}", task.getEmailAddress(), e);
            e.printStackTrace();
        }
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
