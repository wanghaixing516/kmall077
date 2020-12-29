package com.kgc.kmall.manager.controller;

import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.service.SkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@Api(tags = "保存SKU", description = "提供保存SKURest API")
public class SkuController {
    @Reference
    SkuService skuService;

    //    @ApiOperation("保存SKU")
//    @RequestMapping(value = "/saveSkuInfo",produces = "application/json;charset=UTF-8")
    @RequestMapping("/saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo skuInfo) {
        String result = skuService.saveSkuInfo(skuInfo);
        return result;
    }
}
