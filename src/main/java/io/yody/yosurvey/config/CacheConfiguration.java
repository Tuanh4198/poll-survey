package io.yody.yosurvey.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import io.yody.yosurvey.survey.domain.AssignStrategyTemplateEntity;
import org.hibernate.cache.jcache.ConfigSettings;
import org.redisson.Redisson;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;

    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration(JHipsterProperties jHipsterProperties) {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>();

        URI redisUri = URI.create(jHipsterProperties.getCache().getRedis().getServer()[0]);

        Config config = new Config();
        if (jHipsterProperties.getCache().getRedis().isCluster()) {
            ClusterServersConfig clusterServersConfig = config
                .useClusterServers()
                .setMasterConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setMasterConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .addNodeAddress(jHipsterProperties.getCache().getRedis().getServer());

            if (redisUri.getUserInfo() != null) {
                clusterServersConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        } else {
            SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .setAddress(jHipsterProperties.getCache().getRedis().getServer()[0]);

            if (redisUri.getUserInfo() != null) {
                singleServerConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        }
        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(
            CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, jHipsterProperties.getCache().getRedis().getExpiration()))
        );
        return RedissonConfiguration.fromInstance(Redisson.create(config), jcacheConfig);
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cm) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cm);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cm -> {
            createCache(cm, org.nentangso.core.repository.NtsUserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            createCache(cm, org.nentangso.core.repository.NtsUserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            createCache(cm, org.nentangso.core.domain.NtsUserEntity.class.getName(), jcacheConfiguration);
            createCache(cm, org.nentangso.core.domain.NtsAuthority.class.getName(), jcacheConfiguration);
            createCache(cm, org.nentangso.core.domain.NtsUserEntity.class.getName() + ".authorities", jcacheConfiguration);
            createCache(cm, org.nentangso.core.domain.NtsTagsEntity.class.getName(), jcacheConfiguration);
            createCache(cm, org.nentangso.core.domain.NtsNoteEntity.class.getName(), jcacheConfiguration);
            createCache(cm, org.nentangso.core.domain.NtsMetafieldEntity.class.getName(), jcacheConfiguration);
            createCache(cm, org.nentangso.core.domain.NtsOutboxEventEntity.class.getName(), jcacheConfiguration);
            createCache(cm, org.nentangso.core.domain.NtsOptionEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.SurveyEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.SurveyEntity.class.getName() + ".ids", jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.SurveyTemplateEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.AssignStrategyEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.BlockEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.BlockEntity.class.getName() + ".ids", jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.BlockFieldsEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.BlockFieldsEntity.class.getName() + ".ids", jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.EmployeeSurveyEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.SurveySubmitEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.LogicsEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.MetafieldEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.BlockTemplateEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.BlockFieldsTemplateEntity.class.getName(), jcacheConfiguration);
            createCache(cm, io.yody.yosurvey.survey.domain.ExportEntity.class.getName(), jcacheConfiguration);
            createCache(cm, AssignStrategyTemplateEntity.class.getName(), jcacheConfiguration);
            // jhipster-needle-redis-add-entry
        };
    }

    private void createCache(
        javax.cache.CacheManager cm,
        String cacheName,
        javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration
    ) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
