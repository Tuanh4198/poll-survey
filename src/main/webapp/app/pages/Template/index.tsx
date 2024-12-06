/* eslint-disable no-constant-condition */
import React from 'react';
import './styles.scss';
import { Box, Container, Flex, Grid, Loader, LoadingOverlay, Pagination, Text } from '@mantine/core';
import { SurveyTemplateCard } from 'app/pages/Template/components/SurveyTemplateCard';
import { Empty } from 'app/shared/components/Empty';
import { SurveyBlockCreate } from 'app/pages/Template/components/SurveyBlockCreate';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import { useFetchSurveyTemplate } from 'app/pages/Template/hooks/useFetchSurveyTemplate';
import { useModalCreateForm } from 'app/pages/Form';

export const Template = () => {
  const { onOpenModalUpsertForm, renderModalUpsertForm } = useModalCreateForm();

  const {
    isLoading: isLoadingSurveyTemplate,
    data: dataSurveyTemplate,
    total: totalSurveyTemplate,
    page: pageSurveyTemplate,
    handleNextPage: handleNextPageSurveyTemplate,
  } = useFetchSurveyTemplate();

  return (
    <>
      <Text color="black" size="24px" fw="700" mb="20px">
        Tạo mới khảo sát
      </Text>
      <Box bg="white" p="20px">
        {/* Section most used */}
        <Box mb="30px" pos="relative">
          {/* <LoadingOverlay
            style={{ borderRadius: '8px' }}
            zIndex={10}
            visible={isLoadingSurvey && dataSurvey && dataSurvey?.length > 0}
            loaderProps={{ children: <Loader /> }}
          /> */}
          <Text color="#4B5563" fw="700" mb="20px">
            Sử dụng nhiều nhất
          </Text>
          <Grid gutter="20px" columns={10}>
            <Grid.Col span={2}>
              <SurveyBlockCreate />
            </Grid.Col>
            {/* {isLoadingSurvey && (!dataSurvey || dataSurvey?.length <= 0)
              ? Array.from({ length: 4 }).map((_, i) => (
                  <Grid.Col key={i} span={2}>
                    <SurveyCard loading />
                  </Grid.Col>
                ))
              : !dataSurvey || dataSurvey?.length <= 0
              ? null
              : dataSurvey?.map(item => (
                  <Grid.Col key={item.id} span={2}>
                    <SurveyCard survey={item} />
                  </Grid.Col>
                ))} */}
          </Grid>
          {/* {Math.ceil(totalSurvey / ITEMS_PER_PAGE) > 1 && (
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
          )} */}
        </Box>
        {/* Section sample library */}
        <Box pos="relative">
          <LoadingOverlay
            style={{ borderRadius: '8px' }}
            zIndex={10}
            visible={isLoadingSurveyTemplate && dataSurveyTemplate && dataSurveyTemplate?.length > 0}
            loaderProps={{ children: <Loader /> }}
          />
          <Text color="#4B5563" fw="700" mb="20px">
            Thư viện mẫu
          </Text>
          <Grid gutter="20px" columns={10}>
            {isLoadingSurveyTemplate && (!dataSurveyTemplate || dataSurveyTemplate?.length <= 0) ? (
              Array.from({ length: 5 }).map((_, i) => (
                <Grid.Col key={i} span={2}>
                  <SurveyTemplateCard loading />
                </Grid.Col>
              ))
            ) : !dataSurveyTemplate || dataSurveyTemplate?.length <= 0 ? (
              <Container py={30}>
                <Flex py={50} h="100%" w="100%" align="center" justify="center">
                  <Empty onBtnClick={onOpenModalUpsertForm} withIcon description="Không có dữ liệu" />
                </Flex>
                {renderModalUpsertForm()}
              </Container>
            ) : (
              dataSurveyTemplate?.map(item => (
                <Grid.Col key={item.id} span={2}>
                  <SurveyTemplateCard surveyTemplate={item} />
                </Grid.Col>
              ))
            )}
          </Grid>
          {Math.ceil(totalSurveyTemplate / ITEMS_PER_PAGE) > 1 && (
            <Flex justify="flex-end" mt="20px">
              <Pagination
                value={pageSurveyTemplate}
                disabled={isLoadingSurveyTemplate}
                withControls={false}
                hideWithOnePage
                size="sm"
                total={Math.ceil(totalSurveyTemplate / ITEMS_PER_PAGE)}
                onChange={handleNextPageSurveyTemplate}
              />
            </Flex>
          )}
        </Box>
      </Box>
    </>
  );
};
