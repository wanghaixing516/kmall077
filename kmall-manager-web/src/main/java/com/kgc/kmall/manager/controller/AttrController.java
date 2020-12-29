package com.kgc.kmall.manager.controller;

import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrValue;
import com.kgc.kmall.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@Api(tags = "显示平台属性列表", description = "提供显示平台属性Rest API")
public class AttrController {
    @Reference
    AttrService attrService;

    @ApiOperation("显示平台属性")
    @GetMapping(value = "/attrInfoList", produces = "application/json;charset=UTF-8")
    public List<PmsBaseAttrInfo> attrInfoList(Long catalog3Id) {
        List<PmsBaseAttrInfo> infoList = attrService.select(catalog3Id);
        return infoList;
    }

    @ApiOperation("新增平台属性")
    @PostMapping(value = "/saveAttrInfo", produces = "application/json;charset=UTF-8")
    public Integer saveAttrInfo(@RequestBody PmsBaseAttrInfo attrInfo) {
        Integer i = attrService.add(attrInfo);
        return i;
    }

    //    根据属性id查询属性值
    @ApiOperation("根据属性id查询属性值")
    @PostMapping(value = "/getAttrValueList", produces = "application/json;charset=UTF-8")
    public List<PmsBaseAttrValue> getAttrValueList(Long attrId) {
        List<PmsBaseAttrValue> valueList = attrService.getAttrValueList(attrId);
        return valueList;
    }

}
