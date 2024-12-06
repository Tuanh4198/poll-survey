package io.yody.yosurvey.survey.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.yody.yosurvey.IntegrationTest;
import io.yody.yosurvey.survey.domain.BlockEntity;
import io.yody.yosurvey.survey.domain.enumeration.ComponentTypeEnum;
import io.yody.yosurvey.survey.repository.BlockRepository;
import io.yody.yosurvey.survey.service.dto.BlockDTO;
import io.yody.yosurvey.survey.service.mapper.BlockMapper;
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
 * Integration tests for the {@link BlockResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BlockResourceIT {

    private static final ComponentTypeEnum DEFAULT_TYPE = ComponentTypeEnum.MULTIPLE_CHOICE;
    private static final ComponentTypeEnum UPDATED_TYPE = ComponentTypeEnum.ESSAY;

    private static final Long DEFAULT_SURVEY_ID = 1L;
    private static final Long UPDATED_SURVEY_ID = 2L;

    private static final Long DEFAULT_PAGE_NUM = 1L;
    private static final Long UPDATED_PAGE_NUM = 2L;

    private static final Long DEFAULT_NUM = 1L;
    private static final Long UPDATED_NUM = 2L;

    private static final String ENTITY_API_URL = "/api/blocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockMapper blockMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBlockMockMvc;

    private BlockEntity blockEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlockEntity createEntity(EntityManager em) {
        BlockEntity blockEntity = new BlockEntity()
            .type(DEFAULT_TYPE)
//            .surveyId(DEFAULT_SURVEY_ID)
            .pageNum(DEFAULT_PAGE_NUM)
            .num(DEFAULT_NUM);
        return blockEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlockEntity createUpdatedEntity(EntityManager em) {
        BlockEntity blockEntity = new BlockEntity()
            .type(UPDATED_TYPE)
//            .surveyId(UPDATED_SURVEY_ID)
            .pageNum(UPDATED_PAGE_NUM)
            .num(UPDATED_NUM);
        return blockEntity;
    }

    @BeforeEach
    public void initTest() {
        blockEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createBlock() throws Exception {
        int databaseSizeBeforeCreate = blockRepository.findAll().size();
        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);
        restBlockMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeCreate + 1);
        BlockEntity testBlock = blockList.get(blockList.size() - 1);
        assertThat(testBlock.getType()).isEqualTo(DEFAULT_TYPE);
//        assertThat(testBlock.getSurveyId()).isEqualTo(DEFAULT_SURVEY_ID);
        assertThat(testBlock.getPageNum()).isEqualTo(DEFAULT_PAGE_NUM);
        assertThat(testBlock.getNum()).isEqualTo(DEFAULT_NUM);
    }

    @Test
    @Transactional
    void createBlockWithExistingId() throws Exception {
        // Create the Block with an existing ID
        blockEntity.setId(1L);
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        int databaseSizeBeforeCreate = blockRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlockMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockRepository.findAll().size();
        // set the field null
        blockEntity.setType(null);

        // Create the Block, which fails.
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        restBlockMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSurveyIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockRepository.findAll().size();
        // set the field null
//        blockEntity.setSurveyId(null);

        // Create the Block, which fails.
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        restBlockMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPageNumIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockRepository.findAll().size();
        // set the field null
        blockEntity.setPageNum(null);

        // Create the Block, which fails.
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        restBlockMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockRepository.findAll().size();
        // set the field null
        blockEntity.setNum(null);

        // Create the Block, which fails.
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        restBlockMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBlocks() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(blockEntity);

        // Get all the blockList
        restBlockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blockEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].surveyId").value(hasItem(DEFAULT_SURVEY_ID.intValue())))
            .andExpect(jsonPath("$.[*].pageNum").value(hasItem(DEFAULT_PAGE_NUM.intValue())))
            .andExpect(jsonPath("$.[*].num").value(hasItem(DEFAULT_NUM.intValue())));
    }

    @Test
    @Transactional
    void getBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(blockEntity);

        // Get the block
        restBlockMockMvc
            .perform(get(ENTITY_API_URL_ID, blockEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(blockEntity.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.surveyId").value(DEFAULT_SURVEY_ID.intValue()))
            .andExpect(jsonPath("$.pageNum").value(DEFAULT_PAGE_NUM.intValue()))
            .andExpect(jsonPath("$.num").value(DEFAULT_NUM.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingBlock() throws Exception {
        // Get the block
        restBlockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(blockEntity);

        int databaseSizeBeforeUpdate = blockRepository.findAll().size();

        // Update the block
        BlockEntity updatedBlockEntity = blockRepository.findById(blockEntity.getId()).get();
        // Disconnect from session so that the updates on updatedBlockEntity are not directly saved in db
        em.detach(updatedBlockEntity);
//        updatedBlockEntity.type(UPDATED_TYPE).surveyId(UPDATED_SURVEY_ID).pageNum(UPDATED_PAGE_NUM).num(UPDATED_NUM);
        BlockDTO blockDTO = blockMapper.toDto(updatedBlockEntity);

        restBlockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blockDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isOk());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
        BlockEntity testBlock = blockList.get(blockList.size() - 1);
        assertThat(testBlock.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testBlock.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testBlock.getPageNum()).isEqualTo(UPDATED_PAGE_NUM);
        assertThat(testBlock.getNum()).isEqualTo(UPDATED_NUM);
    }

    @Test
    @Transactional
    void putNonExistingBlock() throws Exception {
        int databaseSizeBeforeUpdate = blockRepository.findAll().size();
        blockEntity.setId(count.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blockDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBlock() throws Exception {
        int databaseSizeBeforeUpdate = blockRepository.findAll().size();
        blockEntity.setId(count.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBlock() throws Exception {
        int databaseSizeBeforeUpdate = blockRepository.findAll().size();
        blockEntity.setId(count.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBlockWithPatch() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(blockEntity);

        int databaseSizeBeforeUpdate = blockRepository.findAll().size();

        // Update the block using partial update
        BlockEntity partialUpdatedBlockEntity = new BlockEntity();
        partialUpdatedBlockEntity.setId(blockEntity.getId());

        partialUpdatedBlockEntity.type(UPDATED_TYPE).pageNum(UPDATED_PAGE_NUM);

        restBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlockEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBlockEntity))
            )
            .andExpect(status().isOk());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
        BlockEntity testBlock = blockList.get(blockList.size() - 1);
        assertThat(testBlock.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testBlock.getSurveyId()).isEqualTo(DEFAULT_SURVEY_ID);
        assertThat(testBlock.getPageNum()).isEqualTo(UPDATED_PAGE_NUM);
        assertThat(testBlock.getNum()).isEqualTo(DEFAULT_NUM);
    }

    @Test
    @Transactional
    void fullUpdateBlockWithPatch() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(blockEntity);

        int databaseSizeBeforeUpdate = blockRepository.findAll().size();

        // Update the block using partial update
        BlockEntity partialUpdatedBlockEntity = new BlockEntity();
        partialUpdatedBlockEntity.setId(blockEntity.getId());

//        partialUpdatedBlockEntity.type(UPDATED_TYPE).surveyId(UPDATED_SURVEY_ID).pageNum(UPDATED_PAGE_NUM).num(UPDATED_NUM);

        restBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlockEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBlockEntity))
            )
            .andExpect(status().isOk());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
        BlockEntity testBlock = blockList.get(blockList.size() - 1);
        assertThat(testBlock.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testBlock.getSurveyId()).isEqualTo(UPDATED_SURVEY_ID);
        assertThat(testBlock.getPageNum()).isEqualTo(UPDATED_PAGE_NUM);
        assertThat(testBlock.getNum()).isEqualTo(UPDATED_NUM);
    }

    @Test
    @Transactional
    void patchNonExistingBlock() throws Exception {
        int databaseSizeBeforeUpdate = blockRepository.findAll().size();
        blockEntity.setId(count.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, blockDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBlock() throws Exception {
        int databaseSizeBeforeUpdate = blockRepository.findAll().size();
        blockEntity.setId(count.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBlock() throws Exception {
        int databaseSizeBeforeUpdate = blockRepository.findAll().size();
        blockEntity.setId(count.incrementAndGet());

        // Create the Block
        BlockDTO blockDTO = blockMapper.toDto(blockEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(blockDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Block in the database
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(blockEntity);

        int databaseSizeBeforeDelete = blockRepository.findAll().size();

        // Delete the block
        restBlockMockMvc
            .perform(delete(ENTITY_API_URL_ID, blockEntity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BlockEntity> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
