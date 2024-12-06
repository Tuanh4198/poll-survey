import { Flex, Text, Box, Table, LoadingOverlay, Loader } from '@mantine/core';
import { Star } from '@phosphor-icons/react';
import { ModalListAnswerSurvey } from 'app/pages/Form/components/ModalListAnswerSurvey';
import { FieldCount, useFetchSurveySubmitFieldsCount } from 'app/pages/Form/hooks/useFetchSurveySubmitFieldsCount';
import { BlockFieldsMetafieldKey, IBlockFields } from 'app/shared/model/block-fields.model';
import { BlockMetafieldKey, BlockMetafieldRequiredValue, IBlock } from 'app/shared/model/block.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { sortBlockField } from 'app/shared/util/sort-block-field';
import React, { Fragment, useMemo } from 'react';

const star = <Star size={20} weight="fill" color="#FAB005" />;

export const BlockResultTypeStar = ({ target, surveyId, block }: { target?: string; surveyId?: number; block: IBlock }) => {
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
      block?.blockFields?.filter(bf => bf.type && bf.id && bf.type === FieldTypeEnum.STAR_OPTION)?.map(bf => bf.id?.toString() || '') || [],
    [block?.blockFields]
  );

  const starOption = useMemo(
    () =>
      block?.blockFields
        ?.filter(i => i.type === FieldTypeEnum.STAR_OPTION)
        ?.sort((bfA, bfB) => sortBlockField({ bfA, bfB, componentType: ComponentTypeEnum.STAR }))
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

  return (
    <Box pos="relative" className="block-result">
      <LoadingOverlay style={{ borderRadius: '8px' }} zIndex={10} visible={isLoading} loaderProps={{ children: <Loader /> }} />
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
      <Flex align="center">
        <Flex direction="column" align="center" justify="center" gap={10} w={200}>
          <Star size={64} weight="fill" color="#FAB005" />
          <Text c="#1F2A37" fw={700} fz="32px">
            {!data || Object.keys(data).length <= 0 ? '-' : `${avg.toFixed(1)}/5`}
          </Text>
        </Flex>
        <Table withRowBorders={false} highlightOnHover>
          <Table.Tbody>
            {starOption?.map(item => (
              <RowResult key={item.id} item={item} surveyId={surveyId} target={target} fieldCount={data} />
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
  item,
  fieldCount,
}: {
  target?: string;
  surveyId?: number;
  item: IBlockFields;
  fieldCount?: FieldCount;
}) => {
  return (
    <Table.Tr>
      <Table.Td ta="right" w={200}>
        <Text c="#1F2A37">{item.id && fieldCount?.[item.id] ? fieldCount?.[item.id] : 0} chọn</Text>
      </Table.Td>
      <Table.Td w={170}>
        <Flex gap={10}>
          {Array.from({ length: Number(item.fieldValue) }).map((__, index) => (
            <Fragment key={index}>{star}</Fragment>
          ))}
        </Flex>
      </Table.Td>
      <Table.Td fw={500} fz={16}>
        <Text c="#1F2A37">{item.metafields?.find(mf => mf.key === BlockFieldsMetafieldKey.description)?.value}</Text>
      </Table.Td>
      <Table.Td ta="right" w={90}>
        <ModalListAnswerSurvey target={target} surveyId={surveyId} fieldId={item.id?.toString() || ''} />
      </Table.Td>
    </Table.Tr>
  );
};
