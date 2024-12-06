package io.yody.yosurvey.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Yosurvey.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    // jhipster-needle-application-properties-property
    // jhipster-needle-application-properties-property-getter
    // jhipster-needle-application-properties-property-class
    private final IdGeneratorProperties idGenerator = new IdGeneratorProperties();
    public static class IdGeneratorProperties {

        private final RedisIdGeneratorProperties customer = new RedisIdGeneratorProperties("yosurvey");
        private String redisKeyPrefix = "";

        public String getRedisKeyPrefix() {
            return redisKeyPrefix;
        }

        public void setRedisKeyPrefix(String redisKeyPrefix) {
            this.redisKeyPrefix = redisKeyPrefix;
        }

        public RedisIdGeneratorProperties getCustomer() {
            return customer;
        }

        public static class RedisIdGeneratorProperties {

            private String redisKey;

            public RedisIdGeneratorProperties() {}

            public RedisIdGeneratorProperties(String redisKey) {
                this.redisKey = redisKey;
            }

            public String getRedisKey() {
                return redisKey;
            }

            public void setRedisKey(String redisKey) {
                this.redisKey = redisKey;
            }
        }
    }

    public IdGeneratorProperties getIdGenerator() {
        return idGenerator;
    }
}
