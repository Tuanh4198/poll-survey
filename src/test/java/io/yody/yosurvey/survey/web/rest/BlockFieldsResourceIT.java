package io.yody.yosurvey.survey.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.yody.yosurvey.IntegrationTest;
import io.yody.yosurvey.survey.domain.BlockFieldsEntity;
import io.yody.yosurvey.survey.domain.enumeration.FieldTypeEnum;
import io.yody.yosurvey.survey.repository.BlockFieldsRepository;
import io.yody.yosurvey.survey.service.dto.BlockFieldsDTO;
import io.yody.yosurvey.survey.service.mapper.BlockFieldsMapper;
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
 * Integration tests for the {@link BlockFieldsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BlockFieldsResourceIT {

    private static final Long DEFAULT_BLOCK_ID = 1L;
    private static final Long UPDATED_BLOCK_ID = 2L;

    private static final Long DEFAULT_SURVEY_ID = 1L;
    private static final Long UPDATED_SURVEY_ID = 2L;

    private static final String DEFAULT_FIELD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIELD_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FIELD_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_FIELD_VALUE = "BBBBBBBBBB";

    private static final FieldTypeEnum DEFAULT_TYPE = FieldTypeEnum.MULTIPLE_CHOICE_OPTION;
    private static final FieldTypeEnum UPDATED_TYPE = FieldTypeEnum.DATE_INPUT;

    private static final String ENTITY_API_URL = "/api/block-fields";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BlockFieldsRepository blockFieldsRepository;

    @Autowired
    private BlockFieldsMapper blockFieldsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBlockFieldsMockMvc;

    private BlockFieldsEntity blockFieldsEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlockFieldsEntity createEntity(EntityManager em) {
        BlockFieldsEntity blockFieldsEntity = new BlockFieldsEntity()
            .surveyId(DEFAULT_SURVEY_ID)
            .fieldName(DEFAULT_FIELD_NAME)
            .fieldValue(DEFAULT_FIELD_VALUE)
            .type(DEFAULT_TYPE);
        return blockFieldsEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlockFieldsEntity createUpdatedEntity(EntityManager em) {
        BlockFieldsEntity blockFieldsEntity = new BlockFieldsEntity()
            .surveyId(UPDATED_SURVEY_ID)
            .fieldName(UPDATED_FIELD_NAME)
            .fieldValue(UPDATED_FIELD_VALUE)
            .type(UPDATED_TYPE);
        return blockFieldsEntity;
    }

    @BeforeEach
    public void initTest() {
        blockFieldsEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createBlockFields() throws Exception {
        int databaseSizeBeforeCreate = blockFieldsRepository.findAll().size();
        // Create the BlockFields
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);
        restBlockFieldsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeCreate + 1);
        BlockFieldsEntity testBlockFields = blockFieldsList.get(blockFieldsList.size() - 1);
        assertThat(testBlockFields.getSurveyId()).isEqualTo(DEFAULT_SURVEY_ID);
        assertThat(testBlockFields.getFieldName()).isEqualTo(DEFAULT_FIELD_NAME);
        assertThat(testBlockFields.getFieldValue()).isEqualTo(DEFAULT_FIELD_VALUE);
        assertThat(testBlockFields.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createBlockFieldsWithExistingId() throws Exception {
        // Create the BlockFields with an existing ID
        blockFieldsEntity.setId(1L);
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        int databaseSizeBeforeCreate = blockFieldsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlockFieldsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBlockIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockFieldsRepository.findAll().size();
        // set the field null
        // Create the BlockFields, which fails.
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        restBlockFieldsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isBadRequest());

        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSurveyIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockFieldsRepository.findAll().size();
        // set the field null
        blockFieldsEntity.setSurveyId(null);

        // Create the BlockFields, which fails.
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        restBlockFieldsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isBadRequest());

        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFieldNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockFieldsRepository.findAll().size();
        // set the field null
        blockFieldsEntity.setFieldName(null);

        // Create the BlockFields, which fails.
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        restBlockFieldsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isBadRequest());

        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFieldValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockFieldsRepository.findAll().size();
        // set the field null
        blockFieldsEntity.setFieldValue(null);

        // Create the BlockFields, which fails.
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        restBlockFieldsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isBadRequest());

        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockFieldsRepository.findAll().size();
        // set the field null
        blockFieldsEntity.setType(null);

        // Create the BlockFields, which fails.
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        restBlockFieldsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isBadRequest());

        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBlockFields() throws Exception {
        // Initialize the database
        blockFieldsRepository.saveAndFlush(blockFieldsEntity);

        // Get all the blockFieldsList
        restBlockFieldsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blockFieldsEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].blockId").value(hasItem(DEFAULT_BLOCK_ID.intValue())))
            .andExpect(jsonPath("$.[*].surveyId").value(hasItem(DEFAULT_SURVEY_ID.intValue())))
            .andExpect(jsonPath("$.[*].fieldName").value(hasItem(DEFAULT_FIELD_NAME)))
            .andExpect(jsonPath("$.[*].fieldValue").value(hasItem(DEFAULT_FIELD_VALUE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getBlockFields() throws Exception {
        // Initialize the database
        blockFieldsRepository.saveAndFlush(blockFieldsEntity);

        // Get the blockFields
        restBlockFieldsMockMvc
            .perform(get(ENTITY_API_URL_ID, blockFieldsEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(blockFieldsEntity.getId().intValue()))
            .andExpect(jsonPath("$.blockId").value(DEFAULT_BLOCK_ID.intValue()))
            .andExpect(jsonPath("$.surveyId").value(DEFAULT_SURVEY_ID.intValue()))
            .andExpect(jsonPath("$.fieldName").value(DEFAULT_FIELD_NAME))
            .andExpect(jsonPath("$.fieldValue").value(DEFAULT_FIELD_VALUE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBlockFields() throws Exception {
        // Get the blockFields
        restBlockFieldsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBlockFields() throws Exception {
        // Initialize the database
        blockFieldsRepository.saveAndFlush(blockFieldsEntity);

        int databaseSizeBeforeUpdate = blockFieldsRepository.findAll().size();

        // Update the blockFields
        BlockFieldsEntity updatedBlockFieldsEntity = blockFieldsRepository.findById(blockFieldsEntity.getId()).get();
        // Disconnect from session so that the updates on updatedBlockFieldsEntity are not directly saved in db
        em.detach(updatedBlockFieldsEntity);
        updatedBlockFieldsEntity
            .surveyId(UPDATED_SURVEY_ID)
            .fieldName(UPDATED_FIELD_NAME)
            .fieldValue(UPDATED_FIELD_VALUE)
            .type(UPDATED_TYPE);
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(updatedBlockFieldsEntity);

        restBlockFieldsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blockFieldsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isOk());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeUpdate);
        BlockFieldsEntity testBlockFields = blockFieldsList.get(blockFieldsList.size() - 1);
        assertThat(testBlockFields.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testBlockFields.getFieldName()).isEqualTo(UPDATED_FIELD_NAME);
        assertThat(testBlockFields.getFieldValue()).isEqualTo(UPDATED_FIELD_VALUE);
        assertThat(testBlockFields.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingBlockFields() throws Exception {
        int databaseSizeBeforeUpdate = blockFieldsRepository.findAll().size();
        blockFieldsEntity.setId(count.incrementAndGet());

        // Create the BlockFields
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlockFieldsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blockFieldsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBlockFields() throws Exception {
        int databaseSizeBeforeUpdate = blockFieldsRepository.findAll().size();
        blockFieldsEntity.setId(count.incrementAndGet());

        // Create the BlockFields
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockFieldsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBlockFields() throws Exception {
        int databaseSizeBeforeUpdate = blockFieldsRepository.findAll().size();
        blockFieldsEntity.setId(count.incrementAndGet());

        // Create the BlockFields
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockFieldsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBlockFieldsWithPatch() throws Exception {
        // Initialize the database
        blockFieldsRepository.saveAndFlush(blockFieldsEntity);

        int databaseSizeBeforeUpdate = blockFieldsRepository.findAll().size();

        // Update the blockFields using partial update
        BlockFieldsEntity partialUpdatedBlockFieldsEntity = new BlockFieldsEntity();
        partialUpdatedBlockFieldsEntity.setId(blockFieldsEntity.getId());

        partialUpdatedBlockFieldsEntity
            .surveyId(UPDATED_SURVEY_ID)
            .fieldValue(UPDATED_FIELD_VALUE)
            .type(UPDATED_TYPE);

        restBlockFieldsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlockFieldsEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBlockFieldsEntity))
            )
            .andExpect(status().isOk());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeUpdate);
        BlockFieldsEntity testBlockFields = blockFieldsList.get(blockFieldsList.size() - 1);
        assertThat(testBlockFields.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testBlockFields.getFieldName()).isEqualTo(DEFAULT_FIELD_NAME);
        assertThat(testBlockFields.getFieldValue()).isEqualTo(UPDATED_FIELD_VALUE);
        assertThat(testBlockFields.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateBlockFieldsWithPatch() throws Exception {
        // Initialize the database
        blockFieldsRepository.saveAndFlush(blockFieldsEntity);

        int databaseSizeBeforeUpdate = blockFieldsRepository.findAll().size();

        // Update the blockFields using partial update
        BlockFieldsEntity partialUpdatedBlockFieldsEntity = new BlockFieldsEntity();
        partialUpdatedBlockFieldsEntity.setId(blockFieldsEntity.getId());

        partialUpdatedBlockFieldsEntity
            .surveyId(UPDATED_SURVEY_ID)
            .fieldName(UPDATED_FIELD_NAME)
            .fieldValue(UPDATED_FIELD_VALUE)
            .type(UPDATED_TYPE);

        restBlockFieldsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlockFieldsEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBlockFieldsEntity))
            )
            .andExpect(status().isOk());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeUpdate);
        BlockFieldsEntity testBlockFields = blockFieldsList.get(blockFieldsList.size() - 1);
        assertThat(testBlockFields.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testBlockFields.getFieldName()).isEqualTo(UPDATED_FIELD_NAME);
        assertThat(testBlockFields.getFieldValue()).isEqualTo(UPDATED_FIELD_VALUE);
        assertThat(testBlockFields.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingBlockFields() throws Exception {
        int databaseSizeBeforeUpdate = blockFieldsRepository.findAll().size();
        blockFieldsEntity.setId(count.incrementAndGet());

        // Create the BlockFields
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlockFieldsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, blockFieldsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBlockFields() throws Exception {
        int databaseSizeBeforeUpdate = blockFieldsRepository.findAll().size();
        blockFieldsEntity.setId(count.incrementAndGet());

        // Create the BlockFields
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockFieldsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBlockFields() throws Exception {
        int databaseSizeBeforeUpdate = blockFieldsRepository.findAll().size();
        blockFieldsEntity.setId(count.incrementAndGet());

        // Create the BlockFields
        BlockFieldsDTO blockFieldsDTO = blockFieldsMapper.toDto(blockFieldsEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockFieldsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(blockFieldsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BlockFields in the database
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBlockFields() throws Exception {
        // Initialize the database
        blockFieldsRepository.saveAndFlush(blockFieldsEntity);

        int databaseSizeBeforeDelete = blockFieldsRepository.findAll().size();

        // Delete the blockFields
        restBlockFieldsMockMvc
            .perform(delete(ENTITY_API_URL_ID, blockFieldsEntity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BlockFieldsEntity> blockFieldsList = blockFieldsRepository.findAll();
        assertThat(blockFieldsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
