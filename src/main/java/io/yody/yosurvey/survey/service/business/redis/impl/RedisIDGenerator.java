package io.yody.yosurvey.survey.service.business.redis.impl;

import io.yody.yosurvey.config.ApplicationProperties;
import io.yody.yosurvey.survey.repository.IDGeneratorRepository;
import io.yody.yosurvey.survey.service.business.redis.CachingService;
import io.yody.yosurvey.survey.service.business.redis.IDGenerator;
import org.nentangso.core.web.rest.errors.BadRequestAlertException;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisIDGenerator implements IDGenerator {
    private static final String ENTITY_NAME = "RedisIDGenerator";

    private static final Logger log = LoggerFactory.getLogger(RedisIDGenerator.class);

    private final CachingService<Long> cachingService;

    private final IDGeneratorRepository idGeneratorRepository;

    private final ApplicationProperties applicationProperties;

    public RedisIDGenerator(
        CachingService<Long> cachingService,
        IDGeneratorRepository idGeneratorRepository,
        ApplicationProperties applicationProperties
    ) {
        this.cachingService = cachingService;
        this.idGeneratorRepository = idGeneratorRepository;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public Long nextId(String entity) {
        try {
            String key = applicationProperties.getIdGenerator().getRedisKeyPrefix() + entity;
            RAtomicLong id = cachingService.getId("id-" + key);
            id.expire(1, TimeUnit.DAYS);
            if (id.get() == 0) {
                RLock lock = cachingService.getLock("lock-" + key);
                try {
                    lock.tryLock(5, TimeUnit.SECONDS);
                    long lastId = idGeneratorRepository.getLastId(entity);
                    id.set(lastId);
                    return id.incrementAndGet();
                } catch (Exception e) {
                    log.error("Failed to try lock when get next Id");
                    throw new BadRequestAlertException("Có lỗi khi tạo ID (lock)", ENTITY_NAME, "error");
                } finally {
                    lock.unlock();
                }
            }
            return id.incrementAndGet();
        } catch (Exception e) {
            throw new BadRequestAlertException("Có lỗi khi tạo ID", ENTITY_NAME, "error");
        }
    }
}

