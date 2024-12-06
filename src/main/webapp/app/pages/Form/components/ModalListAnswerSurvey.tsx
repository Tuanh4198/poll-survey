import { Box, Flex, Loader, LoadingOverlay, Modal, ScrollArea, Table, Text, UnstyledButton } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { useFetchSurveySubmit } from 'app/pages/Form/hooks/useFetchSurveySubmit';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import React, { useCallback } from 'react';

export const ModalListAnswerSurvey = ({
  target,
  surveyId,
  fieldId,
  withReason,
}: {
  target?: string;
  surveyId?: number;
  fieldId: string;
  withReason?: boolean;
}) => {
  const { isLoading, data, viewportRef, onScrollPositionChange } = useFetchSurveySubmit({ target, surveyId, fieldId });

  const [opened, { open, close }] = useDisclosure(false);

  const postFix = useCallback((type: ComponentTypeEnum) => {
    switch (type) {
      case ComponentTypeEnum.STAR:
        return 'sao';
      case ComponentTypeEnum.POINT_SCALE:
        return 'điểm';
      default:
        return '';
    }
  }, []);

  return (
    <>
      <Modal
        radius={8}
        opened={opened}
        onClose={close}
        size="700px"
        title={
          <Text color="#111928" fw={600} fz={18}>
            Danh sách nhân sự trả lời
          </Text>
        }
      >
        <Box pos="relative" className="block-result">
          <LoadingOverlay
            style={{ borderRadius: '8px' }}
            zIndex={10}
            visible={(!data || data.length <= 0) && isLoading}
            loaderProps={{ children: <Loader /> }}
          />
          <ScrollArea.Autosize
            viewportRef={viewportRef}
            onScrollPositionChange={onScrollPositionChange}
            scrollHideDelay={5000}
            type="scroll"
            mah={500}
            mt={10}
            scrollbars="y"
          >
            <Table withRowBorders highlightOnHover>
              <Table.Thead style={{ position: 'sticky', top: 0, backgroundColor: '#f1f1f1', zIndex: 1 }}>
                <Table.Tr>
                  <Table.Th bg="#E5E7EB" c="#1F2A37" fw={400} w={100}>
                    YD
                  </Table.Th>
                  <Table.Th bg="#E5E7EB" c="#1F2A37" fw={400} w={250}>
                    Tên nhân sự
                  </Table.Th>
                  <Table.Th bg="#E5E7EB" c="#1F2A37" fw={400}>
                    Trả lời
                  </Table.Th>
                  {withReason && (
                    <Table.Th bg="#E5E7EB" c="#1F2A37" fw={400}>
                      Lý do
                    </Table.Th>
                  )}
                </Table.Tr>
              </Table.Thead>
              <Table.Tbody>
                {data &&
                  data?.length > 0 &&
                  data.map((item, index) => (
                    <Table.Tr key={index}>
                      <Table.Td c="#1F2A37" fw={500} fz={16}>
                        {item.code}
                      </Table.Td>
                      <Table.Td c="#1F2A37" fw={500} fz={16}>
                        {item.name ? item.name : '-'}
                      </Table.Td>
                      <Table.Td c="#1F2A37" fw={500} fz={16}>
                        {item.fieldValue} {postFix(item.type)}
                      </Table.Td>
                      {withReason && (
                        <Table.Td c="#1F2A37" fw={500} fz={16}>
                          -
                        </Table.Td>
                      )}
                    </Table.Tr>
                  ))}
              </Table.Tbody>
              {(!data || data?.length <= 0) && <Table.Caption>Chưa có dữ liệu</Table.Caption>}
            </Table>
            {data && data.length > 0 && isLoading && (
              <Flex align="center" justify="center" p={10}>
                <Loader size={16} />
              </Flex>
            )}
          </ScrollArea.Autosize>
        </Box>
      </Modal>
      <UnstyledButton fz={14} c="#1F2A37" fw={500} onClick={open}>
        Chi tiết
      </UnstyledButton>
    </>
  );
};
