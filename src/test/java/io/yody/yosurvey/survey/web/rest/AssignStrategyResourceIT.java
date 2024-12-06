package io.yody.yosurvey.survey.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.yody.yosurvey.IntegrationTest;
import io.yody.yosurvey.survey.domain.AssignStrategyEntity;
import io.yody.yosurvey.survey.repository.AssignStrategyRepository;
import io.yody.yosurvey.survey.service.dto.AssignStrategyDTO;
import io.yody.yosurvey.survey.service.mapper.AssignStrategyMapper;
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
 * Integration tests for the {@link AssignStrategyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AssignStrategyResourceIT {

    private static final Long DEFAULT_SURVEY_ID = 1L;
    private static final Long UPDATED_SURVEY_ID = 2L;

    private static final String ENTITY_API_URL = "/api/assign-strategies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AssignStrategyRepository assignStrategyRepository;

    @Autowired
    private AssignStrategyMapper assignStrategyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssignStrategyMockMvc;

    private AssignStrategyEntity assignStrategyEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssignStrategyEntity createEntity(EntityManager em) {
        AssignStrategyEntity assignStrategyEntity = new AssignStrategyEntity();
//            .surveyId(DEFAULT_SURVEY_ID);
        return assignStrategyEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssignStrategyEntity createUpdatedEntity(EntityManager em) {
        AssignStrategyEntity assignStrategyEntity = new AssignStrategyEntity();
//            .surveyId(UPDATED_SURVEY_ID);
        return assignStrategyEntity;
    }

    @BeforeEach
    public void initTest() {
        assignStrategyEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createAssignStrategy() throws Exception {
        int databaseSizeBeforeCreate = assignStrategyRepository.findAll().size();
        // Create the AssignStrategy
        AssignStrategyDTO assignStrategyDTO = assignStrategyMapper.toDto(assignStrategyEntity);
        restAssignStrategyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assignStrategyDTO))
            )
            .andExpect(status().isCreated());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeCreate + 1);
        AssignStrategyEntity testAssignStrategy = assignStrategyList.get(assignStrategyList.size() - 1);
//        assertThat(testAssignStrategy.getSurveyId()).isEqualTo(DEFAULT_SURVEY_ID);
    }

    @Test
    @Transactional
    void createAssignStrategyWithExistingId() throws Exception {
        // Create the AssignStrategy with an existing ID
        assignStrategyEntity.setId(1L);
        AssignStrategyDTO assignStrategyDTO = assignStrategyMapper.toDto(assignStrategyEntity);

        int databaseSizeBeforeCreate = assignStrategyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssignStrategyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assignStrategyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSurveyIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = assignStrategyRepository.findAll().size();
        // set the field null
//        assignStrategyEntity.setSurveyId(null);

        // Create the AssignStrategy, which fails.
        AssignStrategyDTO assignStrategyDTO = assignStrategyMapper.toDto(assignStrategyEntity);

        restAssignStrategyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assignStrategyDTO))
            )
            .andExpect(status().isBadRequest());

        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAssignStrategies() throws Exception {
        // Initialize the database
        assignStrategyRepository.saveAndFlush(assignStrategyEntity);

        // Get all the assignStrategyList
        restAssignStrategyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assignStrategyEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].surveyId").value(hasItem(DEFAULT_SURVEY_ID.intValue())));
    }

    @Test
    @Transactional
    void getAssignStrategy() throws Exception {
        // Initialize the database
        assignStrategyRepository.saveAndFlush(assignStrategyEntity);

        // Get the assignStrategy
        restAssignStrategyMockMvc
            .perform(get(ENTITY_API_URL_ID, assignStrategyEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assignStrategyEntity.getId().intValue()))
            .andExpect(jsonPath("$.surveyId").value(DEFAULT_SURVEY_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingAssignStrategy() throws Exception {
        // Get the assignStrategy
        restAssignStrategyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssignStrategy() throws Exception {
        // Initialize the database
        assignStrategyRepository.saveAndFlush(assignStrategyEntity);

        int databaseSizeBeforeUpdate = assignStrategyRepository.findAll().size();

        // Update the assignStrategy
        AssignStrategyEntity updatedAssignStrategyEntity = assignStrategyRepository.findById(assignStrategyEntity.getId()).get();
        // Disconnect from session so that the updates on updatedAssignStrategyEntity are not directly saved in db
        em.detach(updatedAssignStrategyEntity);
//        updatedAssignStrategyEntity.surveyId(UPDATED_SURVEY_ID);
        AssignStrategyDTO assignStrategyDTO = assignStrategyMapper.toDto(updatedAssignStrategyEntity);

        restAssignStrategyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignStrategyDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assignStrategyDTO))
            )
            .andExpect(status().isOk());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeUpdate);
        AssignStrategyEntity testAssignStrategy = assignStrategyList.get(assignStrategyList.size() - 1);
//        assertThat(testAssignStrategy.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
    }

    @Test
    @Transactional
    void putNonExistingAssignStrategy() throws Exception {
        int databaseSizeBeforeUpdate = assignStrategyRepository.findAll().size();
        assignStrategyEntity.setId(count.incrementAndGet());

        // Create the AssignStrategy
        AssignStrategyDTO assignStrategyDTO = assignStrategyMapper.toDto(assignStrategyEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignStrategyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assignStrategyDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assignStrategyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssignStrategy() throws Exception {
        int databaseSizeBeforeUpdate = assignStrategyRepository.findAll().size();
        assignStrategyEntity.setId(count.incrementAndGet());

        // Create the AssignStrategy
        AssignStrategyDTO assignStrategyDTO = assignStrategyMapper.toDto(assignStrategyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignStrategyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assignStrategyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssignStrategy() throws Exception {
        int databaseSizeBeforeUpdate = assignStrategyRepository.findAll().size();
        assignStrategyEntity.setId(count.incrementAndGet());

        // Create the AssignStrategy
        AssignStrategyDTO assignStrategyDTO = assignStrategyMapper.toDto(assignStrategyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignStrategyMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assignStrategyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssignStrategyWithPatch() throws Exception {
        // Initialize the database
        assignStrategyRepository.saveAndFlush(assignStrategyEntity);

        int databaseSizeBeforeUpdate = assignStrategyRepository.findAll().size();

        // Update the assignStrategy using partial update
        AssignStrategyEntity partialUpdatedAssignStrategyEntity = new AssignStrategyEntity();
        partialUpdatedAssignStrategyEntity.setId(assignStrategyEntity.getId());

        restAssignStrategyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignStrategyEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAssignStrategyEntity))
            )
            .andExpect(status().isOk());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeUpdate);
        AssignStrategyEntity testAssignStrategy = assignStrategyList.get(assignStrategyList.size() - 1);
//        assertThat(testAssignStrategy.getSurveyId()).isEqualTo(DEFAULT_SURVEY_ID);
    }

    @Test
    @Transactional
    void fullUpdateAssignStrategyWithPatch() throws Exception {
        // Initialize the database
        assignStrategyRepository.saveAndFlush(assignStrategyEntity);

        int databaseSizeBeforeUpdate = assignStrategyRepository.findAll().size();

        // Update the assignStrategy using partial update
        AssignStrategyEntity partialUpdatedAssignStrategyEntity = new AssignStrategyEntity();
        partialUpdatedAssignStrategyEntity.setId(assignStrategyEntity.getId());

//        partialUpdatedAssignStrategyEntity.surveyId(UPDATED_SURVEY_ID);

        restAssignStrategyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssignStrategyEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAssignStrategyEntity))
            )
            .andExpect(status().isOk());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeUpdate);
        AssignStrategyEntity testAssignStrategy = assignStrategyList.get(assignStrategyList.size() - 1);
//        assertThat(testAssignStrategy.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
    }

    @Test
    @Transactional
    void patchNonExistingAssignStrategy() throws Exception {
        int databaseSizeBeforeUpdate = assignStrategyRepository.findAll().size();
        assignStrategyEntity.setId(count.incrementAndGet());

        // Create the AssignStrategy
        AssignStrategyDTO assignStrategyDTO = assignStrategyMapper.toDto(assignStrategyEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssignStrategyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assignStrategyDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(assignStrategyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssignStrategy() throws Exception {
        int databaseSizeBeforeUpdate = assignStrategyRepository.findAll().size();
        assignStrategyEntity.setId(count.incrementAndGet());

        // Create the AssignStrategy
        AssignStrategyDTO assignStrategyDTO = assignStrategyMapper.toDto(assignStrategyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignStrategyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(assignStrategyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssignStrategy() throws Exception {
        int databaseSizeBeforeUpdate = assignStrategyRepository.findAll().size();
        assignStrategyEntity.setId(count.incrementAndGet());

        // Create the AssignStrategy
        AssignStrategyDTO assignStrategyDTO = assignStrategyMapper.toDto(assignStrategyEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssignStrategyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(assignStrategyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssignStrategy in the database
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssignStrategy() throws Exception {
        // Initialize the database
        assignStrategyRepository.saveAndFlush(assignStrategyEntity);

        int databaseSizeBeforeDelete = assignStrategyRepository.findAll().size();

        // Delete the assignStrategy
        restAssignStrategyMockMvc
            .perform(delete(ENTITY_API_URL_ID, assignStrategyEntity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AssignStrategyEntity> assignStrategyList = assignStrategyRepository.findAll();
        assertThat(assignStrategyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
