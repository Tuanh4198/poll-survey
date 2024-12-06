package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.BlockFieldsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockFieldsTemplateRepository extends JpaRepository<BlockFieldsEntity, Long> {
}
