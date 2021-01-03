package com.kgc.kmall.manager.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.bean.PmsSkuImage;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.bean.PmsSkuSaleAttrValue;
import com.kgc.kmall.manager.mapper.PmsSkuAttrValueMapper;
import com.kgc.kmall.manager.mapper.PmsSkuImageMapper;
import com.kgc.kmall.manager.mapper.PmsSkuInfoMapper;
import com.kgc.kmall.manager.mapper.PmsSkuSaleAttrValueMapper;
import com.kgc.kmall.manager.util.RedisUtil;
import com.kgc.kmall.service.SkuService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Component
@Service
public class SkuServiceImpl implements SkuService {
    @Resource
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Resource
    PmsSkuImageMapper pmsSkuImageMapper;
    @Resource
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Resource
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Resource
    RedisUtil redisUtil;

    @Override
    public String saveSkuInfo(PmsSkuInfo skuInfo) {
        pmsSkuInfoMapper.insert(skuInfo);
        Long skuInfoId = skuInfo.getId();
        for (PmsSkuImage pmsSkuImage : skuInfo.getSkuImageList()) {
            pmsSkuImage.setSkuId(skuInfoId);
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
        for (PmsSkuAttrValue pmsSkuAttrValue : skuInfo.getSkuAttrValueList()) {
            pmsSkuAttrValue.setSkuId(skuInfoId);
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
        }
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
            pmsSkuSaleAttrValue.setSkuId(skuInfoId);
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
        return "success";
    }

    @Override
    public PmsSkuInfo selectBySkuId(Long skuId) {
        PmsSkuInfo pmsSkuInfo=null;
        //获得jedis对象
        Jedis jedis=redisUtil.getJedis();
        String key="sku:"+skuId+":info";
        String skuJosn = jedis.get(key);
        if(skuJosn!=null){
            //缓存中有数据
             pmsSkuInfo= JSON.parseObject(skuJosn,PmsSkuInfo.class);
            jedis.close();
            return pmsSkuInfo;
        }else{
            //获取分布式锁
            String skuLockKey="sku"+skuId+":lock";
            String ok=jedis.set(skuLockKey,"OK","NX","PX",60*100);
            //拿到分布式锁
            if(ok.equals("OK")){
                //缓存中没有数据，从数据库调 并写入redis
                pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);
                //保存到redis
                if(pmsSkuInfo!=null){
                    String json=JSON.toJSONString(pmsSkuInfo);
                    //有效期随机，防止缓存雪崩
                    Random random=new Random();
                    int i=random.nextInt(10);
                    jedis.setex(key,i*60*1000,json);
                    return pmsSkuInfo;
                }else {
                    jedis.setex(key,5*60*1000,"empty");
                }
                jedis.del(skuLockKey);
            }else {
                //未拿到锁，线程睡眠3s，递归调用
                try{
                    Thread.sleep(3000);
                }catch (Exception e){
                    selectBySpuId(skuId);
                }
            }
        }
        return pmsSkuInfo;
//        return pmsSkuInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PmsSkuInfo> selectBySpuId(Long spuId) {
        return pmsSkuInfoMapper.selectBySpuId(spuId);
    }
}
