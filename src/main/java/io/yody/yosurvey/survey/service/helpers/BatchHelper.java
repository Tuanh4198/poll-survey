package io.yody.yosurvey.survey.service.helpers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class BatchHelper {
        public static <T> void saveInBatches(List<T> entities, JpaRepository<T, ?> repository, int batchSize) {
        for (int i = 0; i < entities.size(); i += batchSize) {
            int end = Math.min(i + batchSize, entities.size());
            List<T> batch = entities.subList(i, end);
            repository.saveAll(batch);
            repository.flush(); // Optional: Flush to ensure the batch is immediately written to the database
        }
    }
}
