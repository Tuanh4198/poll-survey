package io.yody.yosurvey.survey.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.yody.yosurvey.IntegrationTest;
import io.yody.yosurvey.survey.domain.SurveyTemplateEntity;
import io.yody.yosurvey.survey.repository.SurveyTemplateRepository;
import io.yody.yosurvey.survey.service.dto.SurveyTemplateDTO;
import io.yody.yosurvey.survey.service.mapper.SurveyTemplateMapper;
import io.yody.yosurvey.web.rest.TestUtil;
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
 * Integration tests for the {@link SurveyTemplateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SurveyTemplateResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_THUMB_URL = "AAAAAAAAAA";
    private static final String UPDATED_THUMB_URL = "BBBBBBBBBB";

    private static final Long DEFAULT_USED_TIME = 1L;
    private static final Long UPDATED_USED_TIME = 2L;

    private static final String ENTITY_API_URL = "/api/survey-templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SurveyTemplateRepository surveyTemplateRepository;

    @Autowired
    private SurveyTemplateMapper surveyTemplateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSurveyTemplateMockMvc;

    private SurveyTemplateEntity surveyTemplateEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SurveyTemplateEntity createEntity(EntityManager em) {
        SurveyTemplateEntity surveyTemplateEntity = new SurveyTemplateEntity()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .thumbUrl(DEFAULT_THUMB_URL)
            .usedTime(DEFAULT_USED_TIME);
        return surveyTemplateEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SurveyTemplateEntity createUpdatedEntity(EntityManager em) {
        SurveyTemplateEntity surveyTemplateEntity = new SurveyTemplateEntity()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbUrl(UPDATED_THUMB_URL)
            .usedTime(UPDATED_USED_TIME);
        return surveyTemplateEntity;
    }

    @BeforeEach
    public void initTest() {
        surveyTemplateEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createSurveyTemplate() throws Exception {
        int databaseSizeBeforeCreate = surveyTemplateRepository.findAll().size();
        // Create the SurveyTemplate
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateMapper.toDto(surveyTemplateEntity);
        restSurveyTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyTemplateDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeCreate + 1);
        SurveyTemplateEntity testSurveyTemplate = surveyTemplateList.get(surveyTemplateList.size() - 1);
        assertThat(testSurveyTemplate.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSurveyTemplate.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSurveyTemplate.getThumbUrl()).isEqualTo(DEFAULT_THUMB_URL);
        assertThat(testSurveyTemplate.getUsedTime()).isEqualTo(DEFAULT_USED_TIME);
    }

    @Test
    @Transactional
    void createSurveyTemplateWithExistingId() throws Exception {
        // Create the SurveyTemplate with an existing ID
        surveyTemplateEntity.setId(1L);
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateMapper.toDto(surveyTemplateEntity);

        int databaseSizeBeforeCreate = surveyTemplateRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSurveyTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyTemplateRepository.findAll().size();
        // set the field null
        surveyTemplateEntity.setTitle(null);

        // Create the SurveyTemplate, which fails.
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateMapper.toDto(surveyTemplateEntity);

        restSurveyTemplateMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSurveyTemplates() throws Exception {
        // Initialize the database
        surveyTemplateRepository.saveAndFlush(surveyTemplateEntity);

        // Get all the surveyTemplateList
        restSurveyTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(surveyTemplateEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].thumbUrl").value(hasItem(DEFAULT_THUMB_URL)))
            .andExpect(jsonPath("$.[*].usedTime").value(hasItem(DEFAULT_USED_TIME.intValue())));
    }

    @Test
    @Transactional
    void getSurveyTemplate() throws Exception {
        // Initialize the database
        surveyTemplateRepository.saveAndFlush(surveyTemplateEntity);

        // Get the surveyTemplate
        restSurveyTemplateMockMvc
            .perform(get(ENTITY_API_URL_ID, surveyTemplateEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(surveyTemplateEntity.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.thumbUrl").value(DEFAULT_THUMB_URL))
            .andExpect(jsonPath("$.usedTime").value(DEFAULT_USED_TIME.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingSurveyTemplate() throws Exception {
        // Get the surveyTemplate
        restSurveyTemplateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSurveyTemplate() throws Exception {
        // Initialize the database
        surveyTemplateRepository.saveAndFlush(surveyTemplateEntity);

        int databaseSizeBeforeUpdate = surveyTemplateRepository.findAll().size();

        // Update the surveyTemplate
        SurveyTemplateEntity updatedSurveyTemplateEntity = surveyTemplateRepository.findById(surveyTemplateEntity.getId()).get();
        // Disconnect from session so that the updates on updatedSurveyTemplateEntity are not directly saved in db
        em.detach(updatedSurveyTemplateEntity);
        updatedSurveyTemplateEntity
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbUrl(UPDATED_THUMB_URL)
            .usedTime(UPDATED_USED_TIME);
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateMapper.toDto(updatedSurveyTemplateEntity);

        restSurveyTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveyTemplateDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyTemplateDTO))
            )
            .andExpect(status().isOk());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeUpdate);
        SurveyTemplateEntity testSurveyTemplate = surveyTemplateList.get(surveyTemplateList.size() - 1);
        assertThat(testSurveyTemplate.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSurveyTemplate.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSurveyTemplate.getThumbUrl()).isEqualTo(UPDATED_THUMB_URL);
        assertThat(testSurveyTemplate.getUsedTime()).isEqualTo(UPDATED_USED_TIME);
    }

    @Test
    @Transactional
    void putNonExistingSurveyTemplate() throws Exception {
        int databaseSizeBeforeUpdate = surveyTemplateRepository.findAll().size();
        surveyTemplateEntity.setId(count.incrementAndGet());

        // Create the SurveyTemplate
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateMapper.toDto(surveyTemplateEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveyTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveyTemplateDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSurveyTemplate() throws Exception {
        int databaseSizeBeforeUpdate = surveyTemplateRepository.findAll().size();
        surveyTemplateEntity.setId(count.incrementAndGet());

        // Create the SurveyTemplate
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateMapper.toDto(surveyTemplateEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSurveyTemplate() throws Exception {
        int databaseSizeBeforeUpdate = surveyTemplateRepository.findAll().size();
        surveyTemplateEntity.setId(count.incrementAndGet());

        // Create the SurveyTemplate
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateMapper.toDto(surveyTemplateEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyTemplateMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveyTemplateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSurveyTemplateWithPatch() throws Exception {
        // Initialize the database
        surveyTemplateRepository.saveAndFlush(surveyTemplateEntity);

        int databaseSizeBeforeUpdate = surveyTemplateRepository.findAll().size();

        // Update the surveyTemplate using partial update
        SurveyTemplateEntity partialUpdatedSurveyTemplateEntity = new SurveyTemplateEntity();
        partialUpdatedSurveyTemplateEntity.setId(surveyTemplateEntity.getId());

        partialUpdatedSurveyTemplateEntity.description(UPDATED_DESCRIPTION).thumbUrl(UPDATED_THUMB_URL);

        restSurveyTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurveyTemplateEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSurveyTemplateEntity))
            )
            .andExpect(status().isOk());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeUpdate);
        SurveyTemplateEntity testSurveyTemplate = surveyTemplateList.get(surveyTemplateList.size() - 1);
        assertThat(testSurveyTemplate.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSurveyTemplate.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSurveyTemplate.getThumbUrl()).isEqualTo(UPDATED_THUMB_URL);
        assertThat(testSurveyTemplate.getUsedTime()).isEqualTo(DEFAULT_USED_TIME);
    }

    @Test
    @Transactional
    void fullUpdateSurveyTemplateWithPatch() throws Exception {
        // Initialize the database
        surveyTemplateRepository.saveAndFlush(surveyTemplateEntity);

        int databaseSizeBeforeUpdate = surveyTemplateRepository.findAll().size();

        // Update the surveyTemplate using partial update
        SurveyTemplateEntity partialUpdatedSurveyTemplateEntity = new SurveyTemplateEntity();
        partialUpdatedSurveyTemplateEntity.setId(surveyTemplateEntity.getId());

        partialUpdatedSurveyTemplateEntity
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .thumbUrl(UPDATED_THUMB_URL)
            .usedTime(UPDATED_USED_TIME);

        restSurveyTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurveyTemplateEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSurveyTemplateEntity))
            )
            .andExpect(status().isOk());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeUpdate);
        SurveyTemplateEntity testSurveyTemplate = surveyTemplateList.get(surveyTemplateList.size() - 1);
        assertThat(testSurveyTemplate.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSurveyTemplate.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSurveyTemplate.getThumbUrl()).isEqualTo(UPDATED_THUMB_URL);
        assertThat(testSurveyTemplate.getUsedTime()).isEqualTo(UPDATED_USED_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingSurveyTemplate() throws Exception {
        int databaseSizeBeforeUpdate = surveyTemplateRepository.findAll().size();
        surveyTemplateEntity.setId(count.incrementAndGet());

        // Create the SurveyTemplate
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateMapper.toDto(surveyTemplateEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveyTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, surveyTemplateDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(surveyTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSurveyTemplate() throws Exception {
        int databaseSizeBeforeUpdate = surveyTemplateRepository.findAll().size();
        surveyTemplateEntity.setId(count.incrementAndGet());

        // Create the SurveyTemplate
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateMapper.toDto(surveyTemplateEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(surveyTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSurveyTemplate() throws Exception {
        int databaseSizeBeforeUpdate = surveyTemplateRepository.findAll().size();
        surveyTemplateEntity.setId(count.incrementAndGet());

        // Create the SurveyTemplate
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateMapper.toDto(surveyTemplateEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveyTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(surveyTemplateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SurveyTemplate in the database
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSurveyTemplate() throws Exception {
        // Initialize the database
        surveyTemplateRepository.saveAndFlush(surveyTemplateEntity);

        int databaseSizeBeforeDelete = surveyTemplateRepository.findAll().size();

        // Delete the surveyTemplate
        restSurveyTemplateMockMvc
            .perform(delete(ENTITY_API_URL_ID, surveyTemplateEntity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SurveyTemplateEntity> surveyTemplateList = surveyTemplateRepository.findAll();
        assertThat(surveyTemplateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
