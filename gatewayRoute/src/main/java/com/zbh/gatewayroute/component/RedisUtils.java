package com.zbh.gatewayroute.component;

import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 */
@Component
public class RedisUtils {
    /*@Resource
    private RedisTemplate<String, Object> redisTemplate;*/

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**  默认过期时长，单位：秒 */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**  不设置过期时长 */
    public final static long NOT_EXPIRE = -1;

    public void set(String key, Object value, long expire){
        stringRedisTemplate.opsForValue().set(key, toJson(value));
        if(expire != NOT_EXPIRE){
            stringRedisTemplate.opsForValue().set(key, toJson(value), expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value){
        set(key, value, DEFAULT_EXPIRE);
    }

    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if(expire != NOT_EXPIRE){
            stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }
    
    public void expire(String key, long expire, TimeUnit unit) {
        stringRedisTemplate.expire(key, expire, unit);
    }
    
    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public String get(String key, long expire) {
    	if(key==null) {
    		return null;
    	}
    	
        String value = stringRedisTemplate.opsForValue().get(key);
        if(expire != NOT_EXPIRE){
            stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
    
    /**
    *
    * @param key
    * @param liveTime
    * @return
    */
   public Long incr(String key, long liveTime) {
       RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, stringRedisTemplate.getConnectionFactory());
       Long increment = entityIdCounter.getAndIncrement();

       if ((null == increment || increment.longValue() == 0) && liveTime > 0) {//初始设置过期时间
           entityIdCounter.expire(liveTime, TimeUnit.SECONDS);
       }

       return increment;
   }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object){
        if(object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String){
            return String.valueOf(object);
        }
        return JSONUtil.toJsonStr(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz){
        return JSONUtil.toBean(json, clazz);
    }
}
