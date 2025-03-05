package top.orosirian.blog.utils.mq;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailTask {

    private String emailAddress;
    
    private String code;
    
}
