package io.yody.yosurvey.survey.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.yody.yosurvey.IntegrationTest;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.repository.SurveyRepository;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import io.yody.yosurvey.survey.service.mapper.SurveyMapper;
import io.yody.yosurvey.web.rest.TestUtil;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SurveyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SurveyResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_THUMB_URL = "AAAAAAAAAA";
    private static final String UPDATED_THUMB_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_APPLY_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_APPLY_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_REQUIRED = false;
    private static final Boolean UPDATED_IS_REQUIRED = true;

    private static final String ENTITY_API_URL = "/api/surveys";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSurveyMockMvc;

    private SurveyEntity surveyEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SurveyEntity createEntity(EntityManager em) {
        SurveyEntity surveyEntity = new SurveyEntity()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .thumbUrl(DEFAULT_THUMB_URL)
            .applyTime(DEFAULT_APPLY_TIME)
            .endTime(DEFAULT_END_TIME)
            .isRequired(DEFAULT_IS_REQUIRED);
        return surveyEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SurveyEntity createUpdatedEntity(EntityManager em) {
        SurveyEntity surveyEntity = new SurveyEntity()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbUrl(UPDATED_THUMB_URL)
            .applyTime(UPDATED_APPLY_TIME)
            .endTime(UPDATED_END_TIME)
            .isRequired(UPDATED_IS_REQUIRED);
        return surveyEntity;
    }

    @BeforeEach
    public void initTest() {
        surveyEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createSurvey() throws Exception {
        int databaseSizeBeforeCreate = surveyRepository.findAll().size();
        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);
        restSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeCreate + 1);
        SurveyEntity testSurvey = surveyList.get(surveyList.size() - 1);
        assertThat(testSurvey.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSurvey.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSurvey.getThumbUrl()).isEqualTo(DEFAULT_THUMB_URL);
        assertThat(testSurvey.getApplyTime()).isEqualTo(DEFAULT_APPLY_TIME);
        assertThat(testSurvey.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testSurvey.getIsRequired()).isEqualTo(DEFAULT_IS_REQUIRED);
    }

    @Test
    @Transactional
    void createSurveyWithExistingId() throws Exception {
        // Create the Survey with an existing ID
        surveyEntity.setId(1L);
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        int databaseSizeBeforeCreate = surveyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        surveyEntity.setTitle(null);

        // Create the Survey, which fails.
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        restSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkThumbUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        surveyEntity.setThumbUrl(null);

        // Create the Survey, which fails.
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        restSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkApplyTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        surveyEntity.setApplyTime(null);

        // Create the Survey, which fails.
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        restSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        surveyEntity.setEndTime(null);

        // Create the Survey, which fails.
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        restSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsRequiredIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        surveyEntity.setIsRequired(null);

        // Create the Survey, which fails.
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        restSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSurveys() throws Exception {
        // Initialize the database
        surveyRepository.saveAndFlush(surveyEntity);

        // Get all the surveyList
        restSurveyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(surveyEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].thumbUrl").value(hasItem(DEFAULT_THUMB_URL)))
            .andExpect(jsonPath("$.[*].applyTime").value(hasItem(DEFAULT_APPLY_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].isRequired").value(hasItem(DEFAULT_IS_REQUIRED.booleanValue())));
    }

    @Test
    @Transactional
    void getSurvey() throws Exception {
        // Initialize the database
        surveyRepository.saveAndFlush(surveyEntity);

        // Get the survey
        restSurveyMockMvc
            .perform(get(ENTITY_API_URL_ID, surveyEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(surveyEntity.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.thumbUrl").value(DEFAULT_THUMB_URL))
            .andExpect(jsonPath("$.applyTime").value(DEFAULT_APPLY_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.isRequired").value(DEFAULT_IS_REQUIRED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingSurvey() throws Exception {
        // Get the survey
        restSurveyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSurvey() throws Exception {
        // Initialize the database
        surveyRepository.saveAndFlush(surveyEntity);

        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();

        // Update the survey
        SurveyEntity updatedSurveyEntity = surveyRepository.findById(surveyEntity.getId()).get();
        // Disconnect from session so that the updates on updatedSurveyEntity are not directly saved in db
        em.detach(updatedSurveyEntity);
        updatedSurveyEntity
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbUrl(UPDATED_THUMB_URL)
            .applyTime(UPDATED_APPLY_TIME)
            .endTime(UPDATED_END_TIME)
            .isRequired(UPDATED_IS_REQUIRED);
        SurveyDTO surveyDTO = surveyMapper.toDto(updatedSurveyEntity);

        restSurveyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveyDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate);
        SurveyEntity testSurvey = surveyList.get(surveyList.size() - 1);
        assertThat(testSurvey.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSurvey.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSurvey.getThumbUrl()).isEqualTo(UPDATED_THUMB_URL);
        assertThat(testSurvey.getApplyTime()).isEqualTo(UPDATED_APPLY_TIME);
        assertThat(testSurvey.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testSurvey.getIsRequired()).isEqualTo(UPDATED_IS_REQUIRED);
    }

    @Test
    @Transactional
    void putNonExistingSurvey() throws Exception {
        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();
        surveyEntity.setId(count.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveyDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSurvey() throws Exception {
        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();
        surveyEntity.setId(count.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSurvey() throws Exception {
        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();
        surveyEntity.setId(count.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSurveyWithPatch() throws Exception {
        // Initialize the database
        surveyRepository.saveAndFlush(surveyEntity);

        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();

        // Update the survey using partial update
        SurveyEntity partialUpdatedSurveyEntity = new SurveyEntity();
        partialUpdatedSurveyEntity.setId(surveyEntity.getId());

        partialUpdatedSurveyEntity.endTime(UPDATED_END_TIME);

        restSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurveyEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSurveyEntity))
            )
            .andExpect(status().isOk());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate);
        SurveyEntity testSurvey = surveyList.get(surveyList.size() - 1);
        assertThat(testSurvey.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSurvey.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSurvey.getThumbUrl()).isEqualTo(DEFAULT_THUMB_URL);
        assertThat(testSurvey.getApplyTime()).isEqualTo(DEFAULT_APPLY_TIME);
        assertThat(testSurvey.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testSurvey.getIsRequired()).isEqualTo(DEFAULT_IS_REQUIRED);
    }

    @Test
    @Transactional
    void fullUpdateSurveyWithPatch() throws Exception {
        // Initialize the database
        surveyRepository.saveAndFlush(surveyEntity);

        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();

        // Update the survey using partial update
        SurveyEntity partialUpdatedSurveyEntity = new SurveyEntity();
        partialUpdatedSurveyEntity.setId(surveyEntity.getId());

        partialUpdatedSurveyEntity
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbUrl(UPDATED_THUMB_URL)
            .applyTime(UPDATED_APPLY_TIME)
            .endTime(UPDATED_END_TIME)
            .isRequired(UPDATED_IS_REQUIRED);

        restSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurveyEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSurveyEntity))
            )
            .andExpect(status().isOk());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate);
        SurveyEntity testSurvey = surveyList.get(surveyList.size() - 1);
        assertThat(testSurvey.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSurvey.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSurvey.getThumbUrl()).isEqualTo(UPDATED_THUMB_URL);
        assertThat(testSurvey.getApplyTime()).isEqualTo(UPDATED_APPLY_TIME);
        assertThat(testSurvey.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testSurvey.getIsRequired()).isEqualTo(UPDATED_IS_REQUIRED);
    }

    @Test
    @Transactional
    void patchNonExistingSurvey() throws Exception {
        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();
        surveyEntity.setId(count.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, surveyDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSurvey() throws Exception {
        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();
        surveyEntity.setId(count.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSurvey() throws Exception {
        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();
        surveyEntity.setId(count.incrementAndGet());

        // Create the Survey
        SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(surveyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Survey in the database
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSurvey() throws Exception {
        // Initialize the database
        surveyRepository.saveAndFlush(surveyEntity);

        int databaseSizeBeforeDelete = surveyRepository.findAll().size();

        // Delete the survey
        restSurveyMockMvc
            .perform(delete(ENTITY_API_URL_ID, surveyEntity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SurveyEntity> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
