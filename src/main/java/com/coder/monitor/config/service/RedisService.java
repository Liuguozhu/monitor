package com.coder.monitor.config.service;

import com.coder.monitor.model.SystemConfig;
import com.coder.monitor.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;

import java.util.ArrayList;
import java.util.List;

@Component
@DependsOn("mybatisPlusConfig")
public class RedisService implements BaseService<JedisPool> {

    @Autowired
    private SystemConfigService systemConfigService;
    private JedisPool jedisPool;
    private Logger log = LoggerFactory.getLogger(RedisService.class);

    public void setJedis(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public JedisPool instance() {
        try {
            if (this.jedisPool != null) return this.jedisPool;
            // 获取redis的连接
            // host
            SystemConfig systemConfigHost = systemConfigService.selectByKey("redis_host");
            String host = systemConfigHost.getValue();
            // port
            SystemConfig systemConfigPort = systemConfigService.selectByKey("redis_port");
            String port = systemConfigPort.getValue();
            // password
            SystemConfig systemConfigPassword = systemConfigService.selectByKey("redis_password");
            String password = systemConfigPassword.getValue();
            password = StringUtils.isEmpty(password) ? null : password;
            // database
            SystemConfig systemConfigDatabase = systemConfigService.selectByKey("redis_database");
            String database = systemConfigDatabase.getValue();
            // timeout
            SystemConfig systemConfigTimeout = systemConfigService.selectByKey("redis_timeout");
            String timeout = systemConfigTimeout.getValue();

//      if (StringUtils.isEmpty(host)
//          || StringUtils.isEmpty(port)
//          || StringUtils.isEmpty(database)
//          || StringUtils.isEmpty(timeout)) {
//        log.info("redis配置信息不全或没有配置...");
//        return null;
//      }
            if (!this.isRedisConfig()) {
                log.info("redis配置信息不全或没有配置...");
                return null;
            }
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            // 配置jedis连接池最多空闲多少个实例，源码默认 8
            jedisPoolConfig.setMaxIdle(7);
            // 配置jedis连接池最多创建多少个实例，源码默认 8
            jedisPoolConfig.setMaxTotal(20);
            //在borrow(引入)一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            jedisPoolConfig.setTestOnBorrow(true);
            //return 一个jedis实例给pool时，是否检查连接可用性（ping()）
            jedisPoolConfig.setTestOnReturn(true);
            jedisPool = new JedisPool(
                    jedisPoolConfig,
                    host,
                    Integer.parseInt(port),
                    Integer.parseInt(timeout),
                    password,
                    Integer.parseInt(database)
            );
            log.info("redis连接对象获取成功...");
            return this.jedisPool;
        } catch (Exception e) {
            log.error("配置redis连接池报错，错误信息: {}", e.getMessage());
            return null;
        }
    }

    // 判断redis是否配置了
    public boolean isRedisConfig() {
        SystemConfig systemConfigHost = systemConfigService.selectByKey("redis_host");
        String host = systemConfigHost.getValue();
        // port
        SystemConfig systemConfigPort = systemConfigService.selectByKey("redis_port");
        String port = systemConfigPort.getValue();
        // database
        SystemConfig systemConfigDatabase = systemConfigService.selectByKey("redis_database");
        String database = systemConfigDatabase.getValue();
        // timeout
        SystemConfig systemConfigTimeout = systemConfigService.selectByKey("redis_timeout");
        String timeout = systemConfigTimeout.getValue();

        return !StringUtils.isEmpty(host)
                && !StringUtils.isEmpty(port)
                && !StringUtils.isEmpty(database)
                && !StringUtils.isEmpty(timeout);
    }

    // 获取String值
    public String getString(String key) {
        JedisPool instance = this.instance();
        if (StringUtils.isEmpty(key) || instance == null) return null;
        Jedis jedis = instance.getResource();
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    public void setString(String key, String value) {
        this.setString(key, value, 300); // 如果不指定过时时间，默认为5分钟
    }

    /**
     * 带有过期时间的保存数据到redis，到期自动删除
     *
     * @param key
     * @param value
     * @param expireTime 单位 秒
     */
    public void setString(String key, String value, int expireTime) {
        JedisPool instance = this.instance();
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value) || instance == null) return;
        Jedis jedis = instance.getResource();
        SetParams params = new SetParams();
        params.px(expireTime * 1000);
        jedis.set(key, value, params);
        jedis.close();
    }

    public void delString(String key) {
        JedisPool instance = this.instance();
        if (StringUtils.isEmpty(key) || instance == null) return;
        Jedis jedis = instance.getResource();
        jedis.del(key); // 返回值成功是 1
        jedis.close();
    }

    // TODO 后面会补充获取 list, map 等方法


    //将指定的地理空间位置（纬度、经度、名称）添加到指定的key中。
    public void geoAdd(String key, Float lng, Float lat, String member) {
        JedisPool instance = this.instance();
        if (StringUtils.isEmpty(key) || instance == null) return;
        Jedis jedis = instance.getResource();
        jedis.geoadd(key, lng, lat, member);
    }

    /**
     * 返回两个给定位置之间的距离。如果两个位置之间的其中一个不存在， 那么命令返回空值。指定单位的参数 unit 必须是以下单位的其中一个：
     * <p>
     * m 表示单位为米。
     * km 表示单位为千米。
     * mi 表示单位为英里。
     * ft 表示单位为英尺。
     *
     * @param key     key
     * @param member1 元素1
     * @param member2 元素2
     * @param units   单位
     */
    public Double geoIst(String key, String member1, String member2, GeoUnit units) {
        Double distance = 0D;
        JedisPool instance = this.instance();
        if (StringUtils.isEmpty(key) || instance == null) return null;
        Jedis jedis = instance.getResource();
        distance = jedis.geodist(key, member1, member2, units);
        return distance;
    }

    /**
     * 以给定的经纬度为中心， 返回键包含的位置元素当中， 与中心的距离不超过给定最大距离的所有位置元素。
     *
     * @param key    key
     * @param lng    经度
     * @param lat    维度
     * @param radius 半径
     * @param units  单位
     * @return
     */
    public List<String> geoRadius(String key, Float lng, Float lat, Float radius, GeoUnit units) {
        List<String> members = new ArrayList<>();
        List<GeoRadiusResponse> responseList;
        JedisPool instance = this.instance();
        if (StringUtils.isEmpty(key) || instance == null) return null;
        Jedis jedis = instance.getResource();
        responseList = jedis.georadius(key, lng, lat, radius, units);
        responseList.forEach(response -> {
            String member = response.getMemberByString();
            members.add(member);
        });
        return members;
    }


    public  void geoRemove(String key,String member){
        List<String> members = new ArrayList<>();
        List<GeoRadiusResponse> responseList;
        JedisPool instance = this.instance();
        if (StringUtils.isEmpty(key) || instance == null) return ;
        Jedis jedis = instance.getResource();
        jedis.zrem(key,member);
    }

}
