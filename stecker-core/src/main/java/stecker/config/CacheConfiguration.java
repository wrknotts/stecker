package stecker.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import stecker.environment.ApplicationEnvironment;

import com.google.common.cache.CacheBuilder;

/**
 * Handles configuration of the cache implementation for source code retrieved
 * from an SCM.
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(value = ApplicationEnvironment.class)
public class CacheConfiguration implements CachingConfigurer {

    /**
     * Name assigned to the code sample cache - Value: {@value}
     */
    public final static String CODE_SAMPLE_CACHE_NAME = "sampleCache";

    /**
     * Name assigned to the {@code ScmDescriptor} cache - Value: {@value}
     */
    public final static String SCM_DESCRIPTOR_CACHE_NAME = "scmDescriptorCache";

   /**
     * Name assigned to the source code cache - Value: {@value}
     */
    public final static String SOURCE_CACHE_NAME = "sourceCache";

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfiguration.class);

    private ApplicationEnvironment appEnv;

    /**
     * @see org.springframework.cache.annotation.CachingConfigurer#cacheManager()
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        GuavaCache scmDescriptorCache = new GuavaCache(SCM_DESCRIPTOR_CACHE_NAME, CacheBuilder.newBuilder().initialCapacity(1).build());

        GuavaCache sourceCache = new GuavaCache(SOURCE_CACHE_NAME, CacheBuilder.newBuilder()
                .expireAfterWrite(appEnv.getCacheMinutes(), TimeUnit.MINUTES).build());

        GuavaCache codeSampleCache = new GuavaCache(CODE_SAMPLE_CACHE_NAME, CacheBuilder.newBuilder().initialCapacity(5).build());

        LOGGER.debug(
                String.format("source code cache expiration set to '{}' minute%s",
                        (1 == appEnv.getCacheMinutes() ? "" : "s")), appEnv.getCacheMinutes());

        cacheManager.setCaches(Arrays.asList(scmDescriptorCache, sourceCache, codeSampleCache));

        return cacheManager;
    }

    /**
     * @see org.springframework.cache.annotation.CachingConfigurer#cacheResolver()
     */
    @Override
    public CacheResolver cacheResolver() {
        return null;
    }

    /**
     * @see org.springframework.cache.annotation.CachingConfigurer#errorHandler()
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return null;
    }

    /**
     * @see org.springframework.cache.annotation.CachingConfigurer#keyGenerator()
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    /**
     * Provides access to {@code ApplicationEnvironment} that represents to core
     * application configuration.
     * 
     * @param appEnv represents current application configuration
     */
    @Autowired
    public void setAppEnv(ApplicationEnvironment appEnv) {
        this.appEnv = appEnv;
    }
}