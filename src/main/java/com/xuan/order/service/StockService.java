package com.xuan.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import com.xuan.order.mapper.ProductMapper;
import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * 库存扣减服务
 * 优先用 Redis + Lua 原子扣减；Redis 不可用时自动降级到数据库乐观锁扣减
 */
@Service
public class StockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductMapper productMapper;

    private DefaultRedisScript<Long> deductScript;

    /** 是否成功连接 Redis */
    private boolean redisAvailable = false;

    @PostConstruct
    public void init() {
        deductScript = new DefaultRedisScript<>();
        deductScript.setLocation(new org.springframework.core.io.ClassPathResource("lua/deduct_stock.lua"));
        deductScript.setResultType(Long.class);

        // 探测 Redis 是否可用
        try {
            redisTemplate.opsForValue().get("ping");
            redisAvailable = true;
        } catch (Exception e) {
            redisAvailable = false;
        }
    }

    /**
     * 扣减库存
     * @param productId 商品ID
     * @param qty 数量
     * @return true=成功 false=库存不足
     */
    public boolean deduct(Long productId, int qty) {
        if (redisAvailable) {
            return deductByRedis(productId, qty);
        }
        // 降级: 数据库乐观锁
        return productMapper.deductStock(productId, qty) > 0;
    }

    private boolean deductByRedis(Long productId, int qty) {
        String key = "stock:product:" + productId;
        // 若 Redis 中无库存缓存，从 DB 初始化
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            Integer dbStock = productMapper.selectById(productId).getStock();
            redisTemplate.opsForValue().set(key, String.valueOf(dbStock));
        }
        Long ret = redisTemplate.execute(deductScript, Collections.singletonList(key), String.valueOf(qty));
        return ret != null && ret == 1L;
    }
}
