package io.yody.yosurvey.survey.service.helpers;

import io.yody.yosurvey.survey.domain.constant.NextIdConst;
import io.yody.yosurvey.survey.service.business.redis.IDGenerator;
import io.yody.yosurvey.survey.web.rest.request.AssignStrategyRequest;
import io.yody.yosurvey.survey.web.rest.request.BlockFieldsRequest;
import io.yody.yosurvey.survey.web.rest.request.BlockRequest;
import io.yody.yosurvey.survey.web.rest.request.SurveyRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class IdHelper {
    private final IDGenerator idGenerator;

    public IdHelper(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public void processSurveyId(List<SurveyRequest> requests) {
        for (SurveyRequest request:requests) {
            if (Objects.isNull(request.getId())) {
                request.setTempId(idGenerator.nextId(NextIdConst.SURVEY));
            } else {
                request.setTempId(request.getId());
            }
        }
        List<BlockRequest> blockRequests = requests.stream()
            .flatMap(surveyRequest -> surveyRequest.getBlocks().stream())
            .collect(Collectors.toList());
        List<AssignStrategyRequest> assignStrategyRequests = requests.stream()
            .flatMap(surveyRequest -> surveyRequest.getAssignStrategies().stream())
            .collect(Collectors.toList());
        processBlockId(blockRequests);
        processAssignStrategyId(assignStrategyRequests);
    }

    public void processBlockId(List<BlockRequest> requests) {
        for (BlockRequest request:requests) {
            if (Objects.isNull(request.getId())) {
                request.setTempId(idGenerator.nextId(NextIdConst.BLOCK));
            } else {
                request.setTempId(request.getId());
            }
        }
        List<BlockFieldsRequest> blockFieldsRequests = requests.stream()
            .flatMap(surveyRequest -> surveyRequest.getBlockFields().stream())
            .collect(Collectors.toList());
        processBlockFieldId(blockFieldsRequests);
    }

    public void processBlockFieldId(List<BlockFieldsRequest> requests) {
        for (BlockFieldsRequest request:requests) {
            if (Objects.isNull(request.getId())) {
                request.setTempId(idGenerator.nextId(NextIdConst.BLOCK_FIELD));
            } else {
                request.setTempId(request.getId());
            }
        }
    }
    public void processAssignStrategyId(List<AssignStrategyRequest> requests) {
        for (AssignStrategyRequest request:requests) {
            if (Objects.isNull(request.getId())) {
                request.setTempId(idGenerator.nextId(NextIdConst.ASSIGN_STRATEGY));
            } else {
                request.setTempId(request.getId());
            }
        }
    }
}
