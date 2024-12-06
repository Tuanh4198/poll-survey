package io.yody.yosurvey.survey.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.yody.yosurvey.IntegrationTest;
import io.yody.yosurvey.survey.domain.LogicsEntity;
import io.yody.yosurvey.survey.repository.LogicsRepository;
import io.yody.yosurvey.survey.service.dto.LogicsDTO;
import io.yody.yosurvey.survey.service.mapper.LogicsMapper;
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
 * Integration tests for the {@link LogicsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LogicsResourceIT {

    private static final String DEFAULT_COMPONENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPONENT_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/logics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LogicsRepository logicsRepository;

    @Autowired
    private LogicsMapper logicsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLogicsMockMvc;

    private LogicsEntity logicsEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LogicsEntity createEntity(EntityManager em) {
        LogicsEntity logicsEntity = new LogicsEntity().componentName(DEFAULT_COMPONENT_NAME);
        return logicsEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LogicsEntity createUpdatedEntity(EntityManager em) {
        LogicsEntity logicsEntity = new LogicsEntity().componentName(UPDATED_COMPONENT_NAME);
        return logicsEntity;
    }

    @BeforeEach
    public void initTest() {
        logicsEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createLogics() throws Exception {
        int databaseSizeBeforeCreate = logicsRepository.findAll().size();
        // Create the Logics
        LogicsDTO logicsDTO = logicsMapper.toDto(logicsEntity);
        restLogicsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logicsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeCreate + 1);
        LogicsEntity testLogics = logicsList.get(logicsList.size() - 1);
        assertThat(testLogics.getComponentName()).isEqualTo(DEFAULT_COMPONENT_NAME);
    }

    @Test
    @Transactional
    void createLogicsWithExistingId() throws Exception {
        // Create the Logics with an existing ID
        logicsEntity.setId(1L);
        LogicsDTO logicsDTO = logicsMapper.toDto(logicsEntity);

        int databaseSizeBeforeCreate = logicsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLogicsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logicsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkComponentNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = logicsRepository.findAll().size();
        // set the field null
        logicsEntity.setComponentName(null);

        // Create the Logics, which fails.
        LogicsDTO logicsDTO = logicsMapper.toDto(logicsEntity);

        restLogicsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logicsDTO))
            )
            .andExpect(status().isBadRequest());

        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLogics() throws Exception {
        // Initialize the database
        logicsRepository.saveAndFlush(logicsEntity);

        // Get all the logicsList
        restLogicsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(logicsEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].componentName").value(hasItem(DEFAULT_COMPONENT_NAME)));
    }

    @Test
    @Transactional
    void getLogics() throws Exception {
        // Initialize the database
        logicsRepository.saveAndFlush(logicsEntity);

        // Get the logics
        restLogicsMockMvc
            .perform(get(ENTITY_API_URL_ID, logicsEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(logicsEntity.getId().intValue()))
            .andExpect(jsonPath("$.componentName").value(DEFAULT_COMPONENT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingLogics() throws Exception {
        // Get the logics
        restLogicsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLogics() throws Exception {
        // Initialize the database
        logicsRepository.saveAndFlush(logicsEntity);

        int databaseSizeBeforeUpdate = logicsRepository.findAll().size();

        // Update the logics
        LogicsEntity updatedLogicsEntity = logicsRepository.findById(logicsEntity.getId()).get();
        // Disconnect from session so that the updates on updatedLogicsEntity are not directly saved in db
        em.detach(updatedLogicsEntity);
        updatedLogicsEntity.componentName(UPDATED_COMPONENT_NAME);
        LogicsDTO logicsDTO = logicsMapper.toDto(updatedLogicsEntity);

        restLogicsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, logicsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logicsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeUpdate);
        LogicsEntity testLogics = logicsList.get(logicsList.size() - 1);
        assertThat(testLogics.getComponentName()).isEqualTo(UPDATED_COMPONENT_NAME);
    }

    @Test
    @Transactional
    void putNonExistingLogics() throws Exception {
        int databaseSizeBeforeUpdate = logicsRepository.findAll().size();
        logicsEntity.setId(count.incrementAndGet());

        // Create the Logics
        LogicsDTO logicsDTO = logicsMapper.toDto(logicsEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogicsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, logicsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logicsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLogics() throws Exception {
        int databaseSizeBeforeUpdate = logicsRepository.findAll().size();
        logicsEntity.setId(count.incrementAndGet());

        // Create the Logics
        LogicsDTO logicsDTO = logicsMapper.toDto(logicsEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogicsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logicsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLogics() throws Exception {
        int databaseSizeBeforeUpdate = logicsRepository.findAll().size();
        logicsEntity.setId(count.incrementAndGet());

        // Create the Logics
        LogicsDTO logicsDTO = logicsMapper.toDto(logicsEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogicsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(logicsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLogicsWithPatch() throws Exception {
        // Initialize the database
        logicsRepository.saveAndFlush(logicsEntity);

        int databaseSizeBeforeUpdate = logicsRepository.findAll().size();

        // Update the logics using partial update
        LogicsEntity partialUpdatedLogicsEntity = new LogicsEntity();
        partialUpdatedLogicsEntity.setId(logicsEntity.getId());

        restLogicsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLogicsEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLogicsEntity))
            )
            .andExpect(status().isOk());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeUpdate);
        LogicsEntity testLogics = logicsList.get(logicsList.size() - 1);
        assertThat(testLogics.getComponentName()).isEqualTo(DEFAULT_COMPONENT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateLogicsWithPatch() throws Exception {
        // Initialize the database
        logicsRepository.saveAndFlush(logicsEntity);

        int databaseSizeBeforeUpdate = logicsRepository.findAll().size();

        // Update the logics using partial update
        LogicsEntity partialUpdatedLogicsEntity = new LogicsEntity();
        partialUpdatedLogicsEntity.setId(logicsEntity.getId());

        partialUpdatedLogicsEntity.componentName(UPDATED_COMPONENT_NAME);

        restLogicsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLogicsEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLogicsEntity))
            )
            .andExpect(status().isOk());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeUpdate);
        LogicsEntity testLogics = logicsList.get(logicsList.size() - 1);
        assertThat(testLogics.getComponentName()).isEqualTo(UPDATED_COMPONENT_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingLogics() throws Exception {
        int databaseSizeBeforeUpdate = logicsRepository.findAll().size();
        logicsEntity.setId(count.incrementAndGet());

        // Create the Logics
        LogicsDTO logicsDTO = logicsMapper.toDto(logicsEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogicsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, logicsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(logicsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLogics() throws Exception {
        int databaseSizeBeforeUpdate = logicsRepository.findAll().size();
        logicsEntity.setId(count.incrementAndGet());

        // Create the Logics
        LogicsDTO logicsDTO = logicsMapper.toDto(logicsEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogicsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(logicsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLogics() throws Exception {
        int databaseSizeBeforeUpdate = logicsRepository.findAll().size();
        logicsEntity.setId(count.incrementAndGet());

        // Create the Logics
        LogicsDTO logicsDTO = logicsMapper.toDto(logicsEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLogicsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(logicsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Logics in the database
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLogics() throws Exception {
        // Initialize the database
        logicsRepository.saveAndFlush(logicsEntity);

        int databaseSizeBeforeDelete = logicsRepository.findAll().size();

        // Delete the logics
        restLogicsMockMvc
            .perform(delete(ENTITY_API_URL_ID, logicsEntity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LogicsEntity> logicsList = logicsRepository.findAll();
        assertThat(logicsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
