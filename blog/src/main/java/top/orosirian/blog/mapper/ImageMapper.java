package top.orosirian.blog.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageMapper {

    void insertImage(Long imageUid, String imageUrl, String imageHash);

    void deleteImage(Long imageUid);

    void updateImage(Long imageUid, Long imageUrl);

    // ImageVO selectImage(Long imageUid);

}
