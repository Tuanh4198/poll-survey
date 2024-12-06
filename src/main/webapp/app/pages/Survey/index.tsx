/* eslint-disable no-constant-condition */
import React, { useRef } from 'react';
import './styles.scss';
import { Box, CloseButton, Flex, Grid, Input, Loader, LoadingOverlay, Pagination, Text } from '@mantine/core';
import { SurveyCard } from 'app/pages/Survey/components/SurveyCard';
import { Empty } from 'app/shared/components/Empty';
import { useFetchSurvey } from 'app/pages/Survey/hooks/useFetchSurvey';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import { MagnifyingGlass } from '@phosphor-icons/react';
import { useModalCreateForm } from 'app/pages/Form';

export const Survey = () => {
  const keysearchRef = useRef<HTMLInputElement>(null);

  const { onOpenModalUpsertForm, renderModalUpsertForm } = useModalCreateForm();

  const {
    isLoading: isLoadingSurvey,
    data: dataSurvey,
    total: totalSurvey,
    page: pageSurvey,
    keysearch: keysearchSurvey,
    handleNextPage: handleNextPageSurvey,
    handleChangeKeySearch,
  } = useFetchSurvey();

  return (
    <Flex mih="100%" direction="column">
      <Text color="black" size="24px" fw="700" mb="20px">
        Khảo sát của bạn
      </Text>
      <Box pos="relative" bg="white" p="20px" className="survey-box" flex={1}>
        <LoadingOverlay
          style={{ borderRadius: '8px' }}
          zIndex={10}
          visible={isLoadingSurvey && dataSurvey && dataSurvey?.length > 0}
          loaderProps={{ children: <Loader /> }}
        />
        <Input
          ref={keysearchRef}
          onChange={event => handleChangeKeySearch(event.target.value)}
          size="md"
          leftSection={<MagnifyingGlass size={20} />}
          rightSectionPointerEvents="painted"
          rightSection={
            keysearchSurvey && (
              <CloseButton
                onClick={() => {
                  handleChangeKeySearch(undefined);
                  if (keysearchRef.current) keysearchRef.current.value = '';
                }}
              />
            )
          }
          placeholder="Tìm kiếm bài khảo sát"
          w="40%"
          mb={20}
        />
        <Grid gutter="20px" columns={10} h="100%" flex={1}>
          {isLoadingSurvey && (!dataSurvey || dataSurvey?.length <= 0) ? (
            Array.from({ length: 5 }).map((_, i) => (
              <Grid.Col key={i} span={2}>
                <SurveyCard loading />
              </Grid.Col>
            ))
          ) : !dataSurvey || dataSurvey?.length <= 0 ? (
            keysearchSurvey?.trim() ? (
              <Grid.Col span={10} h="100%">
                <Flex py={50} h="100%" w="100%" align="center" justify="center">
                  <Empty withIcon description="Không có dữ liệu phù hợp" />
                </Flex>
              </Grid.Col>
            ) : (
              <Grid.Col span={10} h="100%">
                <Flex py={50} h="100%" w="100%" align="center" justify="center">
                  <Empty
                    onBtnClick={onOpenModalUpsertForm}
                    withBtn
                    withIcon
                    btnText="Tạo mới 1 khảo sát đánh giá"
                    description="Không có dữ liệu"
                  />
                </Flex>
                {renderModalUpsertForm()}
              </Grid.Col>
            )
          ) : (
            dataSurvey?.map(item => (
              <Grid.Col key={item.id} span={2}>
                <SurveyCard survey={item} />
              </Grid.Col>
            ))
          )}
        </Grid>
        {Math.ceil(totalSurvey / ITEMS_PER_PAGE) > 1 && (
          <Flex justify="flex-end" mt="20px">
            <Pagination
              value={pageSurvey}
              disabled={isLoadingSurvey}
              withControls={false}
              hideWithOnePage
              size="sm"
              total={Math.ceil(totalSurvey / ITEMS_PER_PAGE)}
              onChange={handleNextPageSurvey}
            />
          </Flex>
        )}
      </Box>
    </Flex>
  );
};
