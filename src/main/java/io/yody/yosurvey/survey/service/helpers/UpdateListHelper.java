package io.yody.yosurvey.survey.service.helpers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UpdateListHelper {
    public static <PARENT, BO, REQUEST> void updateListBO(
        PARENT parent,
        List<BO> bos,
        List<REQUEST> requests,
        Function<BO, Long> getBOIdFunction,
        Function<REQUEST, Long> getRequestIdFunction,
        TriConsumer<BO, PARENT, REQUEST> updateBOFunction,
        BiFunction<PARENT, REQUEST, BO> newBOFunction
    ) {
        Map<Long, REQUEST> requestMap = requests.stream()
            .collect(Collectors.toMap(getRequestIdFunction, request -> request));
        Iterator<BO> iterator = bos.iterator();
        while (iterator.hasNext()) {
            BO bo = iterator.next();
            Long id = getBOIdFunction.apply(bo);
            // Check if the current BO's id exists in the requestMap
            if (requestMap.containsKey(id)) {
                // Update the BO using the corresponding request
                updateBOFunction.accept(bo, parent, requestMap.get(id));
                // Remove the request from the map as it's already processed
                requestMap.remove(id);
            } else {
                // Remove the BO from the list if it doesn't exist in the incoming requests
                iterator.remove();
            }
        }
        // Any remaining requests in the requestMap are new and should be added as BOs
        for (REQUEST request : requestMap.values()) {
            // Assuming AssignStrategyBO has a constructor that takes AssignStrategyRequest
            BO newBO = newBOFunction.apply(parent, request);
            bos.add(newBO);
        }
    }
}
