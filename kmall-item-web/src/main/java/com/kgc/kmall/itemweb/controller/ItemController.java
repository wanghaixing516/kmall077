package com.kgc.kmall.itemweb.controller;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.PmsProductSaleAttr;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.bean.PmsSkuSaleAttrValue;
import com.kgc.kmall.service.SkuService;
import com.kgc.kmall.service.SpuService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;


    @RequestMapping("{skuId}.html")
    public String item(@PathVariable Long skuId, Model model){
        PmsSkuInfo pmsSkuInfo = skuService.selectBySkuId(skuId);
        model.addAttribute("skuInfo",pmsSkuInfo);

        //根据spuid获取销售属性和属性和属性值
        List<PmsProductSaleAttr> spuSaleAttrListCheckBySku=spuService.spuSaleAttrListIsCheck(pmsSkuInfo.getSpuId(),pmsSkuInfo.getId());
        model.addAttribute("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);

        //根据spuid获得skuid和销售属性值id的对应关系，并拼接成字符串
        List<PmsSkuInfo> pmsSkuInfos = skuService.selectBySpuId(pmsSkuInfo.getSpuId());
        System.out.println(pmsSkuInfos.size());
        Map<String, Long> skuSaleAttrHash = new HashMap<>();
        if(pmsSkuInfos.size()!=0 && pmsSkuInfos!=null){
            for (PmsSkuInfo skuInfo : pmsSkuInfos) {
                String k = "";
//                String v=skuInfo.getId()+"";
                if(skuInfo.getSkuSaleAttrValueList()!=null && skuInfo.getSkuSaleAttrValueList().size()!=0){
                    for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
                        k+=pmsSkuSaleAttrValue.getSaleAttrValueId()+"|";
                        System.out.println(k);
                        System.out.println("====");

                    }
                    skuSaleAttrHash.put(k,skuInfo.getId());
                }
            }
        }
        // 将sku的销售属性hash表放到页面
        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrHash);
        model.addAttribute("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);
        return "item";
    }

}
