package io.yody.yosurvey.survey.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.yody.yosurvey.IntegrationTest;
import io.yody.yosurvey.survey.domain.SurveySubmitEntity;
import io.yody.yosurvey.survey.domain.enumeration.ComponentTypeEnum;
import io.yody.yosurvey.survey.repository.SurveySubmitRepository;
import io.yody.yosurvey.survey.service.dto.SurveySubmitDTO;
import io.yody.yosurvey.survey.service.mapper.SurveySubmitMapper;
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
 * Integration tests for the {@link SurveySubmitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SurveySubmitResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_SURVEY_ID = 1L;
    private static final Long UPDATED_SURVEY_ID = 2L;

    private static final Long DEFAULT_BLOCK_ID = 1L;
    private static final Long UPDATED_BLOCK_ID = 2L;

    private static final ComponentTypeEnum DEFAULT_TYPE = ComponentTypeEnum.MULTIPLE_CHOICE;
    private static final ComponentTypeEnum UPDATED_TYPE = ComponentTypeEnum.ESSAY;

    private static final Long DEFAULT_FIELD_ID = 1L;
    private static final Long UPDATED_FIELD_ID = 2L;

    private static final String DEFAULT_FIELD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIELD_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FIELD_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_FIELD_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/survey-submits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SurveySubmitRepository surveySubmitRepository;

    @Autowired
    private SurveySubmitMapper surveySubmitMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSurveySubmitMockMvc;

    private SurveySubmitEntity surveySubmitEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SurveySubmitEntity createEntity(EntityManager em) {
        SurveySubmitEntity surveySubmitEntity = new SurveySubmitEntity()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .surveyId(DEFAULT_SURVEY_ID)
            .blockId(DEFAULT_BLOCK_ID)
            .type(DEFAULT_TYPE)
            .fieldId(DEFAULT_FIELD_ID)
            .fieldName(DEFAULT_FIELD_NAME)
            .fieldValue(DEFAULT_FIELD_VALUE);
        return surveySubmitEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SurveySubmitEntity createUpdatedEntity(EntityManager em) {
        SurveySubmitEntity surveySubmitEntity = new SurveySubmitEntity()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .surveyId(UPDATED_SURVEY_ID)
            .blockId(UPDATED_BLOCK_ID)
            .type(UPDATED_TYPE)
            .fieldId(UPDATED_FIELD_ID)
            .fieldName(UPDATED_FIELD_NAME)
            .fieldValue(UPDATED_FIELD_VALUE);
        return surveySubmitEntity;
    }

    @BeforeEach
    public void initTest() {
        surveySubmitEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createSurveySubmit() throws Exception {
        int databaseSizeBeforeCreate = surveySubmitRepository.findAll().size();
        // Create the SurveySubmit
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);
        restSurveySubmitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeCreate + 1);
        SurveySubmitEntity testSurveySubmit = surveySubmitList.get(surveySubmitList.size() - 1);
        assertThat(testSurveySubmit.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testSurveySubmit.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSurveySubmit.getSurveyId()).isEqualTo(DEFAULT_SURVEY_ID);
        assertThat(testSurveySubmit.getBlockId()).isEqualTo(DEFAULT_BLOCK_ID);
        assertThat(testSurveySubmit.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSurveySubmit.getFieldId()).isEqualTo(DEFAULT_FIELD_ID);
        assertThat(testSurveySubmit.getFieldName()).isEqualTo(DEFAULT_FIELD_NAME);
        assertThat(testSurveySubmit.getFieldValue()).isEqualTo(DEFAULT_FIELD_VALUE);
    }

    @Test
    @Transactional
    void createSurveySubmitWithExistingId() throws Exception {
        // Create the SurveySubmit with an existing ID
        surveySubmitEntity.setId(1L);
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        int databaseSizeBeforeCreate = surveySubmitRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSurveySubmitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveySubmitRepository.findAll().size();
        // set the field null
        surveySubmitEntity.setCode(null);

        // Create the SurveySubmit, which fails.
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        restSurveySubmitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveySubmitRepository.findAll().size();
        // set the field null
        surveySubmitEntity.setName(null);

        // Create the SurveySubmit, which fails.
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        restSurveySubmitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSurveyIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveySubmitRepository.findAll().size();
        // set the field null
        surveySubmitEntity.setSurveyId(null);

        // Create the SurveySubmit, which fails.
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        restSurveySubmitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBlockIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveySubmitRepository.findAll().size();
        // set the field null
        surveySubmitEntity.setBlockId(null);

        // Create the SurveySubmit, which fails.
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        restSurveySubmitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveySubmitRepository.findAll().size();
        // set the field null
        surveySubmitEntity.setType(null);

        // Create the SurveySubmit, which fails.
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        restSurveySubmitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFieldIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveySubmitRepository.findAll().size();
        // set the field null
        surveySubmitEntity.setFieldId(null);

        // Create the SurveySubmit, which fails.
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        restSurveySubmitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFieldNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveySubmitRepository.findAll().size();
        // set the field null
        surveySubmitEntity.setFieldName(null);

        // Create the SurveySubmit, which fails.
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        restSurveySubmitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFieldValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveySubmitRepository.findAll().size();
        // set the field null
        surveySubmitEntity.setFieldValue(null);

        // Create the SurveySubmit, which fails.
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        restSurveySubmitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSurveySubmits() throws Exception {
        // Initialize the database
        surveySubmitRepository.saveAndFlush(surveySubmitEntity);

        // Get all the surveySubmitList
        restSurveySubmitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(surveySubmitEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surveyId").value(hasItem(DEFAULT_SURVEY_ID.intValue())))
            .andExpect(jsonPath("$.[*].blockId").value(hasItem(DEFAULT_BLOCK_ID.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fieldId").value(hasItem(DEFAULT_FIELD_ID.intValue())))
            .andExpect(jsonPath("$.[*].fieldName").value(hasItem(DEFAULT_FIELD_NAME)))
            .andExpect(jsonPath("$.[*].fieldValue").value(hasItem(DEFAULT_FIELD_VALUE)));
    }

    @Test
    @Transactional
    void getSurveySubmit() throws Exception {
        // Initialize the database
        surveySubmitRepository.saveAndFlush(surveySubmitEntity);

        // Get the surveySubmit
        restSurveySubmitMockMvc
            .perform(get(ENTITY_API_URL_ID, surveySubmitEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(surveySubmitEntity.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.surveyId").value(DEFAULT_SURVEY_ID.intValue()))
            .andExpect(jsonPath("$.blockId").value(DEFAULT_BLOCK_ID.intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.fieldId").value(DEFAULT_FIELD_ID.intValue()))
            .andExpect(jsonPath("$.fieldName").value(DEFAULT_FIELD_NAME))
            .andExpect(jsonPath("$.fieldValue").value(DEFAULT_FIELD_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingSurveySubmit() throws Exception {
        // Get the surveySubmit
        restSurveySubmitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSurveySubmit() throws Exception {
        // Initialize the database
        surveySubmitRepository.saveAndFlush(surveySubmitEntity);

        int databaseSizeBeforeUpdate = surveySubmitRepository.findAll().size();

        // Update the surveySubmit
        SurveySubmitEntity updatedSurveySubmitEntity = surveySubmitRepository.findById(surveySubmitEntity.getId()).get();
        // Disconnect from session so that the updates on updatedSurveySubmitEntity are not directly saved in db
        em.detach(updatedSurveySubmitEntity);
        updatedSurveySubmitEntity
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .surveyId(UPDATED_SURVEY_ID)
            .blockId(UPDATED_BLOCK_ID)
            .type(UPDATED_TYPE)
            .fieldId(UPDATED_FIELD_ID)
            .fieldName(UPDATED_FIELD_NAME)
            .fieldValue(UPDATED_FIELD_VALUE);
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(updatedSurveySubmitEntity);

        restSurveySubmitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveySubmitDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isOk());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeUpdate);
        SurveySubmitEntity testSurveySubmit = surveySubmitList.get(surveySubmitList.size() - 1);
        assertThat(testSurveySubmit.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSurveySubmit.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSurveySubmit.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testSurveySubmit.getBlockId()).isEqualTo(UPDATED_BLOCK_ID);
        assertThat(testSurveySubmit.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSurveySubmit.getFieldId()).isEqualTo(UPDATED_FIELD_ID);
        assertThat(testSurveySubmit.getFieldName()).isEqualTo(UPDATED_FIELD_NAME);
        assertThat(testSurveySubmit.getFieldValue()).isEqualTo(UPDATED_FIELD_VALUE);
    }

    @Test
    @Transactional
    void putNonExistingSurveySubmit() throws Exception {
        int databaseSizeBeforeUpdate = surveySubmitRepository.findAll().size();
        surveySubmitEntity.setId(count.incrementAndGet());

        // Create the SurveySubmit
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveySubmitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, surveySubmitDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSurveySubmit() throws Exception {
        int databaseSizeBeforeUpdate = surveySubmitRepository.findAll().size();
        surveySubmitEntity.setId(count.incrementAndGet());

        // Create the SurveySubmit
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveySubmitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSurveySubmit() throws Exception {
        int databaseSizeBeforeUpdate = surveySubmitRepository.findAll().size();
        surveySubmitEntity.setId(count.incrementAndGet());

        // Create the SurveySubmit
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveySubmitMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSurveySubmitWithPatch() throws Exception {
        // Initialize the database
        surveySubmitRepository.saveAndFlush(surveySubmitEntity);

        int databaseSizeBeforeUpdate = surveySubmitRepository.findAll().size();

        // Update the surveySubmit using partial update
        SurveySubmitEntity partialUpdatedSurveySubmitEntity = new SurveySubmitEntity();
        partialUpdatedSurveySubmitEntity.setId(surveySubmitEntity.getId());

        partialUpdatedSurveySubmitEntity
            .name(UPDATED_NAME)
            .surveyId(UPDATED_SURVEY_ID)
            .fieldId(UPDATED_FIELD_ID)
            .fieldName(UPDATED_FIELD_NAME)
            .fieldValue(UPDATED_FIELD_VALUE);

        restSurveySubmitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurveySubmitEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSurveySubmitEntity))
            )
            .andExpect(status().isOk());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeUpdate);
        SurveySubmitEntity testSurveySubmit = surveySubmitList.get(surveySubmitList.size() - 1);
        assertThat(testSurveySubmit.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testSurveySubmit.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSurveySubmit.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testSurveySubmit.getBlockId()).isEqualTo(DEFAULT_BLOCK_ID);
        assertThat(testSurveySubmit.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSurveySubmit.getFieldId()).isEqualTo(UPDATED_FIELD_ID);
        assertThat(testSurveySubmit.getFieldName()).isEqualTo(UPDATED_FIELD_NAME);
        assertThat(testSurveySubmit.getFieldValue()).isEqualTo(UPDATED_FIELD_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateSurveySubmitWithPatch() throws Exception {
        // Initialize the database
        surveySubmitRepository.saveAndFlush(surveySubmitEntity);

        int databaseSizeBeforeUpdate = surveySubmitRepository.findAll().size();

        // Update the surveySubmit using partial update
        SurveySubmitEntity partialUpdatedSurveySubmitEntity = new SurveySubmitEntity();
        partialUpdatedSurveySubmitEntity.setId(surveySubmitEntity.getId());

        partialUpdatedSurveySubmitEntity
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .surveyId(UPDATED_SURVEY_ID)
            .blockId(UPDATED_BLOCK_ID)
            .type(UPDATED_TYPE)
            .fieldId(UPDATED_FIELD_ID)
            .fieldName(UPDATED_FIELD_NAME)
            .fieldValue(UPDATED_FIELD_VALUE);

        restSurveySubmitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSurveySubmitEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSurveySubmitEntity))
            )
            .andExpect(status().isOk());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeUpdate);
        SurveySubmitEntity testSurveySubmit = surveySubmitList.get(surveySubmitList.size() - 1);
        assertThat(testSurveySubmit.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSurveySubmit.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSurveySubmit.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testSurveySubmit.getBlockId()).isEqualTo(UPDATED_BLOCK_ID);
        assertThat(testSurveySubmit.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSurveySubmit.getFieldId()).isEqualTo(UPDATED_FIELD_ID);
        assertThat(testSurveySubmit.getFieldName()).isEqualTo(UPDATED_FIELD_NAME);
        assertThat(testSurveySubmit.getFieldValue()).isEqualTo(UPDATED_FIELD_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingSurveySubmit() throws Exception {
        int databaseSizeBeforeUpdate = surveySubmitRepository.findAll().size();
        surveySubmitEntity.setId(count.incrementAndGet());

        // Create the SurveySubmit
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSurveySubmitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, surveySubmitDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSurveySubmit() throws Exception {
        int databaseSizeBeforeUpdate = surveySubmitRepository.findAll().size();
        surveySubmitEntity.setId(count.incrementAndGet());

        // Create the SurveySubmit
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveySubmitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSurveySubmit() throws Exception {
        int databaseSizeBeforeUpdate = surveySubmitRepository.findAll().size();
        surveySubmitEntity.setId(count.incrementAndGet());

        // Create the SurveySubmit
        SurveySubmitDTO surveySubmitDTO = surveySubmitMapper.toDto(surveySubmitEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSurveySubmitMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(surveySubmitDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SurveySubmit in the database
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSurveySubmit() throws Exception {
        // Initialize the database
        surveySubmitRepository.saveAndFlush(surveySubmitEntity);

        int databaseSizeBeforeDelete = surveySubmitRepository.findAll().size();

        // Delete the surveySubmit
        restSurveySubmitMockMvc
            .perform(delete(ENTITY_API_URL_ID, surveySubmitEntity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SurveySubmitEntity> surveySubmitList = surveySubmitRepository.findAll();
        assertThat(surveySubmitList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
