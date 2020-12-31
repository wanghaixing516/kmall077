package com.kgc.kmall.manager.controller;

import com.kgc.kmall.bean.PmsBaseSaleAttr;
import com.kgc.kmall.bean.PmsProductImage;
import com.kgc.kmall.bean.PmsProductInfo;
import com.kgc.kmall.bean.PmsProductSaleAttr;
import com.kgc.kmall.service.SpuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@Api(tags = "商品spu管理-显示spu列表", description = "提供商品spu管理-显示spu列表Rest API")
public class SpuController {
    @Reference
    SpuService spuService;

    @Value("${fileServer.url}")
    String fileUrl;

    //    显示spu列表
//    @RequestMapping("/spuList")
    @ApiOperation(value = "显示spu列表")
    @RequestMapping(value = "/spuList")
    public List<PmsProductInfo> spuList(Long catalog3Id) {
        List<PmsProductInfo> infoList = spuService.spuList(catalog3Id);
        return infoList;
    }


    //    @RequestMapping("/fileUpload")
//    public String fileUpload(@RequestParam("file") MultipartFile file){
//        //文件上传
//        //返回文件上传后的路径
//        return "https://m.360buyimg.com/babel/jfs/t5137/20/1794970752/352145/d56e4e94/591417dcN4fe5ef33.jpg";
//    }
    /*
     * 文件上传
     * */
    @ApiOperation("上传spu图片")
        @RequestMapping(value = "/fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile file)
            throws IOException, MyException {
        try {
            //文件上传
            //返回文件上传后的路径
            String imgUrl = fileUrl;
            if (file != null) {
//            System.out.println("multipartFile = " + file.getName()+"|"+file.getSize());

                String configFile = this.getClass().getResource("/tracker.conf").getFile();
                ClientGlobal.init("E:\\IDEAR文件\\kmall077\\kmall-manager-web\\src\\main\\resources\\tracker.conf");
                TrackerClient trackerClient = new TrackerClient();
                TrackerServer trackerServer = trackerClient.getTrackerServer();
                StorageClient storageClient = new StorageClient(trackerServer, null);
                String filename = file.getOriginalFilename();
                String extName = FilenameUtils.getExtension(filename);

                String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
                imgUrl = fileUrl;
                for (int i = 0; i < upload_file.length; i++) {
                    String path = upload_file[i];
                    imgUrl += "/" + path;
                }
            }
            System.out.println(imgUrl);
            return imgUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @ApiOperation("显示销售属性列表")
    @RequestMapping(value = "/baseSaleAttrList")
//    @RequestMapping("/baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        List<PmsBaseSaleAttr> saleAttrList = spuService.baseSaleAttrList();
        return saleAttrList;
    }


    @ApiOperation("显示销售属性列表")
    @RequestMapping(value = "/saveSpuInfo")
//    @RequestMapping("/saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        //保存数据库
        Integer integer = spuService.saveSpuInfo(pmsProductInfo);
        return integer > 0 ? "success" : "fail";
    }

    @ApiOperation("显示销售属性列表")
    @RequestMapping(value = "/spuSaleAttrList")
//    @RequestMapping("/spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(Long spuId) {
        List<PmsProductSaleAttr> pmsProductSaleAttrList = spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrList;
    }

    @ApiOperation("显示销售属性列表")
    @RequestMapping(value = "/spuImageList")
//    @RequestMapping("/spuImageList")
    public List<PmsProductImage> spuImageList(Long spuId) {
        List<PmsProductImage> pmsProductImageList = spuService.spuImageList(spuId);
        return pmsProductImageList;
    }

}


