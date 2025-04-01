package top.orosirian.blog.utils.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailTask {

    private String emailAddress;
    
    private String code;
    
}
