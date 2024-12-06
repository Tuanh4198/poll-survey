import { Flex, Text, Box, Table, LoadingOverlay, Loader, Progress, Tooltip } from '@mantine/core';
import { ModalListAnswerSurvey } from 'app/pages/Form/components/ModalListAnswerSurvey';
import { useFetchSurveySubmitFieldsCount } from 'app/pages/Form/hooks/useFetchSurveySubmitFieldsCount';
import { IBlockFields } from 'app/shared/model/block-fields.model';
import { BlockMetafieldKey, BlockMetafieldRequiredValue, IBlock } from 'app/shared/model/block.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { sortBlockField } from 'app/shared/util/sort-block-field';
import React, { useMemo } from 'react';

export const BlockResultTypePointScale = ({ target, surveyId, block }: { target?: string; surveyId?: number; block: IBlock }) => {
  const blockRequired = useMemo(
    () => block?.metafields?.find(i => i.key === BlockMetafieldKey.required)?.value === BlockMetafieldRequiredValue.true,
    [block?.metafields]
  );

  const blockFieldTitle = useMemo(
    () => block?.blockFields?.find(i => i.type === FieldTypeEnum.TEXT && i.fieldName === ComponentTypeEnum.TITLE.toLowerCase())?.fieldValue,
    [block?.blockFields]
  );

  const fieldIds = useMemo(
    (): string[] =>
      block?.blockFields
        ?.filter(bf => bf.type && bf.id && bf.type === FieldTypeEnum.POINT_SCALE_OPTION)
        ?.map(bf => bf.id?.toString() || '') || [],
    [block?.blockFields]
  );

  const pointScaleOption = useMemo(
    () =>
      block?.blockFields
        ?.filter(i => i.type === FieldTypeEnum.POINT_SCALE_OPTION)
        ?.sort((bfA, bfB) => sortBlockField({ bfA, bfB, componentType: ComponentTypeEnum.POINT_SCALE }))
        ?.reverse(),
    [block?.blockFields]
  );

  const { isLoading, data } = useFetchSurveySubmitFieldsCount({ target, surveyId, fieldIds });

  const avg = useMemo(() => {
    let totalPoint = 0;
    let totalVote = 0;
    block?.blockFields?.forEach(i => {
      if (i.id && data?.[i.id]) {
        totalPoint += Number(i.fieldValue) * data?.[i.id];
        totalVote += data?.[i.id];
      }
    });
    return totalVote ? totalPoint / totalVote : 0;
  }, [data, block?.blockFields]);

  const max = useMemo(() => {
    if (!data) return 0;
    const arrValue = Object.values(data).map(i => (typeof i === 'number' ? i : 0));
    return Math.max(...arrValue) + Math.min(...arrValue);
  }, [data]);

  return (
    <Box pos="relative" className="block-result">
      <LoadingOverlay style={{ borderRadius: '8px' }} zIndex={10} visible={isLoading} loaderProps={{ children: <Loader /> }} />
      <Flex direction="column" gap={5} mb={10}>
        <Text c="#1F2A37" fz={16} fw={600}>
          {blockRequired && (
            <Text c="#FA5252" mr={5} component="span">
              *
            </Text>
          )}
          {blockFieldTitle}
        </Text>
      </Flex>
      <Flex direction="column">
        <Text c="#1F2A37" fw={700} fz={20}>
          Trung bình:{' '}
          {!data || Object.keys(data).length <= 0 || !pointScaleOption || pointScaleOption?.length <= 0
            ? '-'
            : `${avg.toFixed(1)}/${pointScaleOption?.length} điểm`}
        </Text>
        <Table flex={1} withRowBorders={false} mt={20} highlightOnHover>
          <Table.Tbody>
            {pointScaleOption?.map(item => (
              <RowResult
                key={item.id}
                item={item}
                surveyId={surveyId}
                target={target}
                percentVote={item?.id && data?.[item?.id] ? (data[item.id] / max) * 100 : 0}
                totalVote={item?.id && data?.[item?.id] ? data[item.id] : 0}
              />
            ))}
          </Table.Tbody>
        </Table>
      </Flex>
    </Box>
  );
};

const RowResult = ({
  target,
  surveyId,
  totalVote,
  percentVote,
  item,
}: {
  target?: string;
  surveyId?: number;
  totalVote: number;
  percentVote: number;
  item: IBlockFields;
}) => {
  return (
    <Table.Tr>
      <Table.Td w={50} ta="center">
        {item.fieldValue}
      </Table.Td>
      <Table.Td fw={500} fz={16}>
        <Tooltip label={`${totalVote} người chọn`} withArrow>
          <Progress h={30} value={percentVote} size="xl" transitionDuration={200} radius={0} />
        </Tooltip>
      </Table.Td>
      <Table.Td ta="right" w={200}>
        <ModalListAnswerSurvey target={target} surveyId={surveyId} fieldId={item.id?.toString() || ''} />
      </Table.Td>
    </Table.Tr>
  );
};
