package io.yody.yosurvey.survey.service.helpers;

import io.yody.yosurvey.survey.domain.EmployeeSurveyEntity;
import io.yody.yosurvey.survey.domain.enumeration.SurveyStatusEnum;
import io.yody.yosurvey.survey.web.rest.request.NotifyListRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NotifyHelper {
    public static void sendNotifyBySurvey(String thumbUrl, List<EmployeeSurveyEntity> employeeSurveys,
                                    Consumer<NotifyListRequest> notifyFunction, SurveyStatusEnum status) {
        if (status.equals(SurveyStatusEnum.PENDING)) return;
        // Group employeeSurveys by targetName
        Map<String, List<EmployeeSurveyEntity>> surveysGroupedByTargetName = employeeSurveys.stream()
            .collect(Collectors.groupingBy(EmployeeSurveyEntity::getTargetName));

        // Iterate over each group and send a notification
        for (Map.Entry<String, List<EmployeeSurveyEntity>> entry : surveysGroupedByTargetName.entrySet()) {
            String targetName = entry.getKey();
            List<EmployeeSurveyEntity> surveysForTargetName = entry.getValue();

            // Create codeIdMap for the current targetName
            Map<String, Long> codeIdMap = new HashMap<>();
            for (EmployeeSurveyEntity employeeSurvey : surveysForTargetName) {
                codeIdMap.put(employeeSurvey.getCode(), employeeSurvey.getId());
            }

            // Create and populate NotifyListRequest
            NotifyListRequest notifyListRequest = new NotifyListRequest();
            notifyListRequest.setCodeIdMap(codeIdMap);
            notifyListRequest.setName(targetName);
            notifyListRequest.setThumbUrl(thumbUrl);

            // Send the notification
            notifyFunction.accept(notifyListRequest);
        }
    }
}
