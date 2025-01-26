package top.orosirian.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import jakarta.validation.Valid;
import top.orosirian.blog.entity.param.CollectionParam;
import top.orosirian.blog.entity.vo.CollectionVO;
import top.orosirian.blog.service.CollectionService;
import top.orosirian.blog.utils.ResultCodeEnum;

@Validated
@RestController
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @PostMapping("/collection")
    @SaCheckLogin
    public SaResult postCollection(@RequestBody @Valid CollectionParam collectionParam) {
        Long userUid = StpUtil.getLoginIdAsLong();
        collectionService.addCollection(userUid, collectionParam.getCollectionName());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "新增合集成功", null);
    }

    @DeleteMapping("/collection/{collectionUid}")
    @SaCheckLogin
    public SaResult deleteCollection(@PathVariable String collectionUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        collectionService.removeCollection(userUid, Long.parseLong(collectionUid));
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "删除合集成功", null);
    }

    @PutMapping("/collection/{collectionUid}")
    @SaCheckLogin
    public SaResult putCollection(@PathVariable String collectionUid, @RequestBody @Valid CollectionParam collectionParam) {
        Long userUid = StpUtil.getLoginIdAsLong();
        collectionService.modifyCollection(userUid, Long.parseLong(collectionUid), collectionParam.getCollectionName());
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "修改合集成功", null);
    }

    @GetMapping("/collection/self")
    @SaCheckLogin
    public SaResult getMyCollectionList() {
        Long userUid = StpUtil.getLoginIdAsLong();
        List<CollectionVO> result = collectionService.searchMyCollectionList(userUid);
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "得到个人所有合集成功", result);
    }

    @GetMapping("/collection/article/{articleUid}")
    public SaResult getCollectionOfArticleList(@PathVariable String articleUid) {
        Long userUid = StpUtil.getLoginIdAsLong();
        List<CollectionVO> result = collectionService.searchCollectionOfArticleList(userUid, Long.parseLong(articleUid));
        return new SaResult(ResultCodeEnum.SUCCESS.getCode(), "得到文章对应的合集成功", result);
    }

}
