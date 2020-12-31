package com.kgc.kmall.manager;

import com.kgc.kmall.bean.PmsBaseCatalog1;
import com.kgc.kmall.manager.util.RedisUtil;
import com.kgc.kmall.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class KmallManagerServiceApplicationTests {

    @Resource
    CatalogService catalogService;

    @Resource
    RedisUtil redisUtil;
    @Test
    void contextLoads() {
        try {
            Jedis jedis = redisUtil.getJedis();
            String name = jedis.get("name1");
            System.out.println(name);
        }catch (JedisConnectionException e){
            e.printStackTrace();
        }
    }

}
