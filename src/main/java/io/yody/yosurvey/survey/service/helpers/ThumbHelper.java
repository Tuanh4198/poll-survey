package io.yody.yosurvey.survey.service.helpers;

import io.yody.yosurvey.service.MediaProvider;
import io.yody.yosurvey.service.MediaProviderFactory;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import io.yody.yosurvey.survey.web.rest.SurveyResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
@Component
public class ThumbHelper {
    private final Logger log = LoggerFactory.getLogger(ThumbHelper.class);
    private MediaProviderFactory mediaProviderFactory;

    public ThumbHelper(MediaProviderFactory mediaProviderFactory) {
        this.mediaProviderFactory = mediaProviderFactory;
    }

    public <SURVEY_DTO> void enrichThumb(List<SURVEY_DTO> surveyDTOS, Function<SURVEY_DTO, String> getThumbFunction, BiConsumer<SURVEY_DTO, String> setThumbUrl) {
        try {
            MediaProvider mediaProvider = mediaProviderFactory.getMediaProvider();
            ExecutorService executorService = Executors.newFixedThreadPool(5);

            List<CompletableFuture<Void>> futures = surveyDTOS.stream()
                .map(surveyDTO -> CompletableFuture.runAsync(() -> {
                    String thumbUrl = getThumbFunction.apply(surveyDTO);
                    if (!ObjectUtils.isEmpty(thumbUrl)) {
                        String presignedThumbUrl = mediaProvider.createPreSignedGetUrl(thumbUrl);
                        setThumbUrl.accept(surveyDTO, presignedThumbUrl);
                    }
                }, executorService))
                .collect(Collectors.toList());

            // Wait for all tasks to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            executorService.shutdown(); // Shut down the executor service
        } catch (Exception e) {
            log.error("enrich thumb err", e.getMessage());
        }
    }
}
