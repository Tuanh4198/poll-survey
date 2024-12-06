import { Flex, Text, Box, Table, LoadingOverlay, Loader, ScrollArea } from '@mantine/core';
import { useFetchSurveySubmit } from 'app/pages/Form/hooks/useFetchSurveySubmit';
import { BlockMetafieldKey, BlockMetafieldRequiredValue, IBlock } from 'app/shared/model/block.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import React, { useMemo } from 'react';

export const BlockResultTypeEssay = ({ target, surveyId, block }: { target?: string; surveyId?: number; block: IBlock }) => {
  const blockRequired = useMemo(
    () => block?.metafields?.find(i => i.key === BlockMetafieldKey.required)?.value === BlockMetafieldRequiredValue.true,
    [block?.metafields]
  );

  const blockFieldTitle = useMemo(
    () => block?.blockFields?.find(i => i.type === FieldTypeEnum.TEXT && i.fieldName === ComponentTypeEnum.TITLE.toLowerCase())?.fieldValue,
    [block?.blockFields]
  );

  const fieldIds = useMemo(
    (): Array<{ type: FieldTypeEnum; id: string }> =>
      block?.blockFields
        ?.filter(
          bf =>
            bf.type &&
            bf.id &&
            (bf.type === FieldTypeEnum.TEXT_INPUT || bf.type === FieldTypeEnum.DATE_INPUT || bf.type === FieldTypeEnum.NUMBER_INPUT)
        )
        ?.map(bf => ({
          id: bf.id?.toString() || '',
          type: bf.type || FieldTypeEnum.TEXT,
        })) || [],
    [block?.blockFields]
  );

  return (
    <Box pos="relative" className="block-result">
      <Flex direction="column" gap={5}>
        <Text c="#1F2A37" fz={16} fw={600}>
          {blockRequired && (
            <Text c="#FA5252" mr={5} component="span">
              *
            </Text>
          )}
          {blockFieldTitle}
        </Text>
      </Flex>
      {fieldIds && fieldIds?.length > 0 && fieldIds.map(i => <RowResult target={target} surveyId={surveyId} fieldId={i.id} key={i.id} />)}
    </Box>
  );
};

const RowResult = ({ target, surveyId, fieldId }: { target?: string; surveyId?: number; fieldId: string }) => {
  const { isLoading, data, viewportRef, onScrollPositionChange } = useFetchSurveySubmit({ target, surveyId, fieldId });

  return (
    <Box pos="relative" mt={10}>
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
        mah={350}
        scrollbars="y"
      >
        <Table withRowBorders highlightOnHover>
          <Table.Thead style={{ position: 'sticky', top: 0, backgroundColor: '#f1f1f1', zIndex: 1 }}>
            <Table.Tr>
              <Table.Th bg="#E5E7EB" c="#1F2A37" fw={400} w={100}>
                YD
              </Table.Th>
              <Table.Th bg="#E5E7EB" c="#1F2A37" fw={400} w={250}>
                Tên
              </Table.Th>
              <Table.Th bg="#E5E7EB" c="#1F2A37" fw={400}>
                Trả lời
              </Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody mah={200}>
            {data &&
              data?.length > 0 &&
              data.map((item, index) => (
                <Table.Tr key={index} className={item.id.toString()}>
                  <Table.Td c="#1F2A37" fw={500} fz={16}>
                    {item.code}
                  </Table.Td>
                  <Table.Td c="#1F2A37" fw={500} fz={16}>
                    {item.name ? item.name : '-'}
                  </Table.Td>
                  <Table.Td c="#1F2A37" fw={500} fz={16}>
                    {item.fieldValue}
                  </Table.Td>
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
  );
};
