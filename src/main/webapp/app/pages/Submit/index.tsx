/* eslint-disable @typescript-eslint/no-misused-promises */
import React, { Fragment, useMemo } from 'react';
import './styles.scss';
import { Button, Flex, Text, Pill, Box, AspectRatio, Overlay, Image, ScrollArea, Skeleton, LoadingOverlay, Loader } from '@mantine/core';
import { useParams, useSearchParams } from 'react-router-dom';
import { useFetchSurveyDetail } from 'app/pages/Submit/hooks/useFetchSurveyDetail';
import { getBlocks } from 'app/shared/util/builder-form-from-data-server';
import { useHandleSubmitForm } from 'app/pages/Submit/hooks/useHandleSubmitForm';
import { useHandlePage } from 'app/pages/Submit/hooks/useHandlePage';
import { useHandleComponents } from 'app/pages/Submit/hooks/useHandleComponents';
import { hiddenField1, hiddenField2, hiddenField3 } from 'app/shared/model/survey.model';
import { replaceHiddenFields } from 'app/shared/util/replace-all-hidden-field';

export const Submit = () => {
  const { id } = useParams();
  const [searchParams] = useSearchParams();

  const field1 = searchParams.get(hiddenField1);
  const field2 = searchParams.get(hiddenField2);
  const field3 = searchParams.get(hiddenField3);

  const { isLoading, data } = useFetchSurveyDetail({ id });

  const blocks = useMemo(() => {
    return getBlocks(data?.blocks);
  }, [data?.blocks]);

  const { submitting, formValueRef, errors, onChangeFieldValue, onValidate, onSubmit } = useHandleSubmitForm({ blocks, id });

  const { totalPage, activePageIndex, blockPerPage, onPrevPage, onNextPage } = useHandlePage({
    blocks,
    onValidate,
  });

  const { renderComponent } = useHandleComponents();

  return (
    <Box className="submit-page" bg="#F9FAFB">
      <LoadingOverlay zIndex={10} visible={submitting} loaderProps={{ children: <Loader /> }} />
      {isLoading ? (
        <Flex direction="column" gap={20}>
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} height={100} />
          ))}
        </Flex>
      ) : (
        <>
          <Flex bg="#fff" align="center" justify="space-between" px="20px" py="15px" style={{ borderBottom: '1px solid #E5E7EB' }}>
            <Image src={'../../../../content/images/logo.svg'} />
          </Flex>
          <Box pos="relative" mb={20} bg="white">
            <AspectRatio pos="absolute" top={0} left={0} bottom={0} w={`${(activePageIndex / totalPage) * 100}%`} style={{ zIndex: 10 }}>
              <Overlay color="#D0EBFF" w="100%" h="100%" backgroundOpacity={1} />
            </AspectRatio>
            <Flex align="center" justify="center" gap={10} p={10} pos="relative" style={{ zIndex: 10 }}>
              <Text c="#000000" fw={600}>
                Trang {activePageIndex}
              </Text>
              <Pill radius={24} c="#000000" bg="#FAB005">
                {activePageIndex}/{totalPage}
              </Pill>
            </Flex>
          </Box>
          <ScrollArea m="auto" p="0px 15px" w="950px" h="calc(100vh - 100px)" scrollbars="y">
            <Flex gap={20} direction="column" align="flex-start">
              {data?.presignThumbUrl ? (
                <AspectRatio ratio={2 / 1} w="100%">
                  <Image src={data?.presignThumbUrl} fallbackSrc="../../../../../content/images/poll-survey.jpg" fit="cover" />
                </AspectRatio>
              ) : null}
              {data && 'isRequired' in data && data.isRequired ? (
                <Pill c="#FFFFFF" fw={500} fz={14} bg="#FD7E14">
                  Bắt buộc
                </Pill>
              ) : (
                <Pill c="#FFFFFF" fw={500} fz={14} bg="#12B886">
                  Không bắt buộc
                </Pill>
              )}
              {data?.title && (
                <Text c="#1F2A37" fw={700} style={{ fontSize: '20px' }}>
                  {replaceHiddenFields({
                    fieldValue: data?.title,
                    field1,
                    field2,
                    field3,
                  })}
                </Text>
              )}
              {data?.description && (
                <Text c="#1F2A37" fw={400} style={{ fontSize: '14px' }}>
                  {replaceHiddenFields({
                    fieldValue: data?.description,
                    field1,
                    field2,
                    field3,
                  })}
                </Text>
              )}
              {blockPerPage &&
                blockPerPage.length > 0 &&
                blockPerPage.map(item => (
                  <Fragment key={item.id}>
                    {renderComponent({
                      block: item,
                      componentType: item.type,
                      onChangeFieldValue,
                      defaultValue: formValueRef.current?.[item.id],
                      isError: errors.includes(item.id),
                    })}
                  </Fragment>
                ))}
              <Flex gap={10} align="center" justify="center" mt={20} w="100%" mb={50}>
                {activePageIndex > 1 && (
                  <Button disabled={submitting} onClick={onPrevPage} variant="outline" color="#111928" bd="1px solid #E5E7EB">
                    Trước
                  </Button>
                )}
                {activePageIndex < totalPage && (
                  <Button disabled={submitting} onClick={onNextPage} variant="outline" color="#111928" bd="1px solid #E5E7EB">
                    Tiếp theo
                  </Button>
                )}
                {activePageIndex === totalPage && (
                  <Button loading={submitting} onClick={onSubmit}>
                    Submit
                  </Button>
                )}
              </Flex>
            </Flex>
          </ScrollArea>
        </>
      )}
    </Box>
  );
};
