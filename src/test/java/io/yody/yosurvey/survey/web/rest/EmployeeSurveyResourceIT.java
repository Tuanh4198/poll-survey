package io.yody.yosurvey.survey.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.yody.yosurvey.IntegrationTest;
import io.yody.yosurvey.survey.domain.EmployeeSurveyEntity;
import io.yody.yosurvey.survey.domain.enumeration.SurveyStatusEnum;
import io.yody.yosurvey.survey.repository.EmployeeSurveyRepository;
import io.yody.yosurvey.survey.service.dto.EmployeeSurveyDTO;
import io.yody.yosurvey.survey.service.mapper.EmployeeSurveyMapper;
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
 * Integration tests for the {@link EmployeeSurveyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EmployeeSurveyResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_SURVEY_ID = 1L;
    private static final Long UPDATED_SURVEY_ID = 2L;

    private static final SurveyStatusEnum DEFAULT_STATUS = SurveyStatusEnum.NOT_ATTENDED;
    private static final SurveyStatusEnum UPDATED_STATUS = SurveyStatusEnum.COMPLETED;

    private static final String ENTITY_API_URL = "/api/employee-surveys";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EmployeeSurveyRepository employeeSurveyRepository;

    @Autowired
    private EmployeeSurveyMapper employeeSurveyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmployeeSurveyMockMvc;

    private EmployeeSurveyEntity employeeSurveyEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmployeeSurveyEntity createEntity(EntityManager em) {
        EmployeeSurveyEntity employeeSurveyEntity = new EmployeeSurveyEntity()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .surveyId(DEFAULT_SURVEY_ID)
            .status(DEFAULT_STATUS);
        return employeeSurveyEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmployeeSurveyEntity createUpdatedEntity(EntityManager em) {
        EmployeeSurveyEntity employeeSurveyEntity = new EmployeeSurveyEntity()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .surveyId(UPDATED_SURVEY_ID)
            .status(UPDATED_STATUS);
        return employeeSurveyEntity;
    }

    @BeforeEach
    public void initTest() {
        employeeSurveyEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createEmployeeSurvey() throws Exception {
        int databaseSizeBeforeCreate = employeeSurveyRepository.findAll().size();
        // Create the EmployeeSurvey
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);
        restEmployeeSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isCreated());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeCreate + 1);
        EmployeeSurveyEntity testEmployeeSurvey = employeeSurveyList.get(employeeSurveyList.size() - 1);
        assertThat(testEmployeeSurvey.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testEmployeeSurvey.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEmployeeSurvey.getSurveyId()).isEqualTo(DEFAULT_SURVEY_ID);
        assertThat(testEmployeeSurvey.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createEmployeeSurveyWithExistingId() throws Exception {
        // Create the EmployeeSurvey with an existing ID
        employeeSurveyEntity.setId(1L);
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        int databaseSizeBeforeCreate = employeeSurveyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeeSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeSurveyRepository.findAll().size();
        // set the field null
        employeeSurveyEntity.setCode(null);

        // Create the EmployeeSurvey, which fails.
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        restEmployeeSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isBadRequest());

        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeSurveyRepository.findAll().size();
        // set the field null
        employeeSurveyEntity.setName(null);

        // Create the EmployeeSurvey, which fails.
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        restEmployeeSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isBadRequest());

        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSurveyIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeSurveyRepository.findAll().size();
        // set the field null
        employeeSurveyEntity.setSurveyId(null);

        // Create the EmployeeSurvey, which fails.
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        restEmployeeSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isBadRequest());

        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeSurveyRepository.findAll().size();
        // set the field null
        employeeSurveyEntity.setStatus(null);

        // Create the EmployeeSurvey, which fails.
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        restEmployeeSurveyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isBadRequest());

        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEmployeeSurveys() throws Exception {
        // Initialize the database
        employeeSurveyRepository.saveAndFlush(employeeSurveyEntity);

        // Get all the employeeSurveyList
        restEmployeeSurveyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employeeSurveyEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].surveyId").value(hasItem(DEFAULT_SURVEY_ID.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getEmployeeSurvey() throws Exception {
        // Initialize the database
        employeeSurveyRepository.saveAndFlush(employeeSurveyEntity);

        // Get the employeeSurvey
        restEmployeeSurveyMockMvc
            .perform(get(ENTITY_API_URL_ID, employeeSurveyEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(employeeSurveyEntity.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.surveyId").value(DEFAULT_SURVEY_ID.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEmployeeSurvey() throws Exception {
        // Get the employeeSurvey
        restEmployeeSurveyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEmployeeSurvey() throws Exception {
        // Initialize the database
        employeeSurveyRepository.saveAndFlush(employeeSurveyEntity);

        int databaseSizeBeforeUpdate = employeeSurveyRepository.findAll().size();

        // Update the employeeSurvey
        EmployeeSurveyEntity updatedEmployeeSurveyEntity = employeeSurveyRepository.findById(employeeSurveyEntity.getId()).get();
        // Disconnect from session so that the updates on updatedEmployeeSurveyEntity are not directly saved in db
        em.detach(updatedEmployeeSurveyEntity);
        updatedEmployeeSurveyEntity.code(UPDATED_CODE).name(UPDATED_NAME).surveyId(UPDATED_SURVEY_ID).status(UPDATED_STATUS);
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(updatedEmployeeSurveyEntity);

        restEmployeeSurveyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, employeeSurveyDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isOk());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeUpdate);
        EmployeeSurveyEntity testEmployeeSurvey = employeeSurveyList.get(employeeSurveyList.size() - 1);
        assertThat(testEmployeeSurvey.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testEmployeeSurvey.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmployeeSurvey.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testEmployeeSurvey.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingEmployeeSurvey() throws Exception {
        int databaseSizeBeforeUpdate = employeeSurveyRepository.findAll().size();
        employeeSurveyEntity.setId(count.incrementAndGet());

        // Create the EmployeeSurvey
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeSurveyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, employeeSurveyDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmployeeSurvey() throws Exception {
        int databaseSizeBeforeUpdate = employeeSurveyRepository.findAll().size();
        employeeSurveyEntity.setId(count.incrementAndGet());

        // Create the EmployeeSurvey
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeSurveyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmployeeSurvey() throws Exception {
        int databaseSizeBeforeUpdate = employeeSurveyRepository.findAll().size();
        employeeSurveyEntity.setId(count.incrementAndGet());

        // Create the EmployeeSurvey
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeSurveyMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEmployeeSurveyWithPatch() throws Exception {
        // Initialize the database
        employeeSurveyRepository.saveAndFlush(employeeSurveyEntity);

        int databaseSizeBeforeUpdate = employeeSurveyRepository.findAll().size();

        // Update the employeeSurvey using partial update
        EmployeeSurveyEntity partialUpdatedEmployeeSurveyEntity = new EmployeeSurveyEntity();
        partialUpdatedEmployeeSurveyEntity.setId(employeeSurveyEntity.getId());

        partialUpdatedEmployeeSurveyEntity.code(UPDATED_CODE).name(UPDATED_NAME).surveyId(UPDATED_SURVEY_ID).status(UPDATED_STATUS);

        restEmployeeSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployeeSurveyEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmployeeSurveyEntity))
            )
            .andExpect(status().isOk());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeUpdate);
        EmployeeSurveyEntity testEmployeeSurvey = employeeSurveyList.get(employeeSurveyList.size() - 1);
        assertThat(testEmployeeSurvey.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testEmployeeSurvey.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmployeeSurvey.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testEmployeeSurvey.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateEmployeeSurveyWithPatch() throws Exception {
        // Initialize the database
        employeeSurveyRepository.saveAndFlush(employeeSurveyEntity);

        int databaseSizeBeforeUpdate = employeeSurveyRepository.findAll().size();

        // Update the employeeSurvey using partial update
        EmployeeSurveyEntity partialUpdatedEmployeeSurveyEntity = new EmployeeSurveyEntity();
        partialUpdatedEmployeeSurveyEntity.setId(employeeSurveyEntity.getId());

        partialUpdatedEmployeeSurveyEntity.code(UPDATED_CODE).name(UPDATED_NAME).surveyId(UPDATED_SURVEY_ID).status(UPDATED_STATUS);

        restEmployeeSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployeeSurveyEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmployeeSurveyEntity))
            )
            .andExpect(status().isOk());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeUpdate);
        EmployeeSurveyEntity testEmployeeSurvey = employeeSurveyList.get(employeeSurveyList.size() - 1);
        assertThat(testEmployeeSurvey.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testEmployeeSurvey.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEmployeeSurvey.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testEmployeeSurvey.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingEmployeeSurvey() throws Exception {
        int databaseSizeBeforeUpdate = employeeSurveyRepository.findAll().size();
        employeeSurveyEntity.setId(count.incrementAndGet());

        // Create the EmployeeSurvey
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, employeeSurveyDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmployeeSurvey() throws Exception {
        int databaseSizeBeforeUpdate = employeeSurveyRepository.findAll().size();
        employeeSurveyEntity.setId(count.incrementAndGet());

        // Create the EmployeeSurvey
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmployeeSurvey() throws Exception {
        int databaseSizeBeforeUpdate = employeeSurveyRepository.findAll().size();
        employeeSurveyEntity.setId(count.incrementAndGet());

        // Create the EmployeeSurvey
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurveyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeSurveyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employeeSurveyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EmployeeSurvey in the database
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEmployeeSurvey() throws Exception {
        // Initialize the database
        employeeSurveyRepository.saveAndFlush(employeeSurveyEntity);

        int databaseSizeBeforeDelete = employeeSurveyRepository.findAll().size();

        // Delete the employeeSurvey
        restEmployeeSurveyMockMvc
            .perform(delete(ENTITY_API_URL_ID, employeeSurveyEntity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EmployeeSurveyEntity> employeeSurveyList = employeeSurveyRepository.findAll();
        assertThat(employeeSurveyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
