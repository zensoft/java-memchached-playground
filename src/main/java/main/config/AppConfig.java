package main.config;

import main.service.LongRununigServie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pl.media4u.bonprix.memcached.CacheProxy;
import pl.media4u.bonprix.memcached.MemcachedDao;
import pl.media4u.bonprix.memcached.MemcachedDaoBinary;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tomek on 28.04.17.
 */
@Configuration
public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public MemcachedDaoBinary memcachedDaoBinary() {
        MemcachedDaoBinary memcachedDaoBinary = new MemcachedDaoBinary();
        memcachedDaoBinary.setKeyPrefix("tomek-dom");
        memcachedDaoBinary.setConnectionPoolName("primary");
        memcachedDaoBinary.setServersString("172.17.0.2:11211");

        memcachedDaoBinary.init();
        return memcachedDaoBinary;
    }

    @Bean
    public CacheProxy cacheProxy() {
        CacheProxy cacheProxy = new CacheProxy();
        //cacheProxy.setLongCacheExpirationTime(66);
        cacheProxy.setNamespaceKeyExpirationTime(600);//determinuje najkrotszy czas? nie ma znaczenia czas ponizej jesli jest wyzszy?
        cacheProxy.setShortCacheExpirationTime(600);
        cacheProxy.setMediumCacheExpirationTime(600);
        cacheProxy.setCacheEnabled(true);
        cacheProxy.setMemcachedDaos(daos());
        return cacheProxy;
    }

    @Bean
    public List<MemcachedDao> daos() {
        List<MemcachedDao> daos = new ArrayList<>();
        daos.add(memcachedDaoBinary());
        return daos;
    }

    @Bean
    public LongRununigServie longRununigServie() {
        return new LongRununigServie();
    }

    @Bean
    @Primary
    public ProxyFactoryBean proxyFactoryBean() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        String interceptors = "cacheProxy";
        proxyFactoryBean.setInterceptorNames(interceptors);
        proxyFactoryBean.setTarget(longRununigServie());
        return proxyFactoryBean;
    }

}
