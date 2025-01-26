package top.orosirian.blog.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionVO {
    
    private String collectionUid;

    private String collectionName;

    private boolean selected;

}
