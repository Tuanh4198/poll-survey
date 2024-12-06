import { Flex, Text, Box, Table, LoadingOverlay, Loader } from '@mantine/core';
import HighchartsReact from 'highcharts-react-official';
import Highcharts from 'highcharts';
import React, { useMemo } from 'react';
import { getRandomDarkColor } from 'app/pages/Form/helper';
import { BlockMetafieldKey, BlockMetafieldRequiredValue, IBlock } from 'app/shared/model/block.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { sortBlockField } from 'app/shared/util/sort-block-field';
import { FieldCount, useFetchSurveySubmitFieldsCount } from 'app/pages/Form/hooks/useFetchSurveySubmitFieldsCount';
import { IBlockFields } from 'app/shared/model/block-fields.model';
import { ModalListAnswerSurvey } from 'app/pages/Form/components/ModalListAnswerSurvey';

type EnrichBlockField = IBlockFields & { color: string; total: number };

export const BlockResultTypeMultipleChoice = ({ target, surveyId, block }: { target?: string; surveyId?: number; block: IBlock }) => {
  const blockRequired = useMemo(
    () => block?.metafields?.find(i => i.key === BlockMetafieldKey.required)?.value === BlockMetafieldRequiredValue.true,
    [block?.metafields]
  );

  const blockTitle = useMemo(
    () => block?.blockFields?.find(i => i.type === FieldTypeEnum.TEXT && i.fieldName === ComponentTypeEnum.TITLE.toLowerCase())?.fieldValue,
    [block?.blockFields]
  );

  const fieldIds = useMemo(
    (): string[] =>
      block?.blockFields
        ?.filter(
          bf =>
            bf.type && bf.id && (bf.type === FieldTypeEnum.MULTIPLE_CHOICE_OPTION || bf.fieldName === ComponentTypeEnum.ESSAY.toLowerCase())
        )
        ?.map(bf => bf.id?.toString() || '') || [],
    [block?.blockFields]
  );

  const { isLoading, data } = useFetchSurveySubmitFieldsCount({ target, surveyId, fieldIds });

  const multipleChoiceOption = useMemo(
    () =>
      block?.blockFields
        ?.filter(i => i.type === FieldTypeEnum.MULTIPLE_CHOICE_OPTION)
        ?.sort((bfA, bfB) => sortBlockField({ bfA, bfB, componentType: ComponentTypeEnum.MULTIPLE_CHOICE }))
        ?.map(i => ({
          ...i,
          color: getRandomDarkColor(),
          total: i.id && data?.[i.id] ? data?.[i.id] : 0,
        })),
    [block?.blockFields, data]
  );

  const ownOption = useMemo(
    () =>
      block?.blockFields
        ?.filter(i => i.fieldName === ComponentTypeEnum.ESSAY.toLowerCase())
        .map(i => ({
          ...i,
          fieldValue: 'Khác',
          color: getRandomDarkColor(),
          total: i.id && data?.[i.id] ? data?.[i.id] : 0,
        })),
    [block?.blockFields, data]
  );

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
          {blockTitle}
        </Text>
      </Flex>
      <Flex gap={20} align="flex-start">
        <Chart data={data} multipleChoiceOption={multipleChoiceOption} />
        <Table mah={500} flex={1} withRowBorders={false} mt={20} stickyHeader>
          <Table.Tbody>
            {multipleChoiceOption?.map(item => (
              <RowResult key={item.id} item={item} surveyId={surveyId} target={target} />
            ))}
            {ownOption &&
              ownOption.length > 0 &&
              ownOption?.map(item => <RowResult key={item.id} item={item} surveyId={surveyId} target={target} />)}
          </Table.Tbody>
        </Table>
      </Flex>
    </Box>
  );
};

const RowResult = ({ target, surveyId, item }: { target?: string; surveyId?: number; item: EnrichBlockField }) => {
  return (
    <Table.Tr>
      <Table.Td fw={500} fz={16}>
        <Flex align="center" gap={10}>
          <div
            style={{
              width: '26px',
              height: '26px',
              borderRadius: '100%',
              background: item.color,
            }}
          />
          {item.fieldValue}
        </Flex>
      </Table.Td>
      <Table.Td ta="right" w={90}>
        <ModalListAnswerSurvey target={target} surveyId={surveyId} fieldId={item.id?.toString() || ''} />
      </Table.Td>
    </Table.Tr>
  );
};

const Chart = ({
  data,
  multipleChoiceOption,
  ownOption,
}: {
  data?: FieldCount;
  multipleChoiceOption?: Array<EnrichBlockField>;
  ownOption?: Array<EnrichBlockField>;
}) => {
  const total = useMemo(() => {
    let totalVote = 0;
    multipleChoiceOption?.forEach(i => {
      if (i.id && data?.[i.id]) {
        totalVote += data?.[i.id];
      }
    });
    return totalVote;
  }, [data, multipleChoiceOption]);

  return (
    <HighchartsReact
      highcharts={Highcharts}
      options={{
        chart: {
          type: 'pie',
          width: 300,
          height: 300,
        },
        title: {
          text: undefined,
        },
        accessibility: {
          announceNewData: {
            enabled: true,
          },
          point: {
            valueSuffix: '%',
          },
        },
        plotOptions: {
          series: {
            borderRadius: 0,
            dataLabels: [
              {
                enabled: false,
              },
              {
                enabled: true,
                distance: '-30%',
                filter: {
                  property: 'percentage',
                  operator: '>',
                  value: 5,
                },
                format: '{point.y:.1f}%',
                style: {
                  fontSize: '0.9em',
                  textOutline: 'none',
                },
              },
            ],
          },
        },
        tooltip: {
          useHTML: true,
          followPointer: true,
          backgroundColor: '#1F2A37',
          padding: 10,
          formatter(this: any) {
            return `<p style="margin: 0; color: white;">${
              this.point.drilldown
            } người chọn</p><p style="margin: 0; text-align: center; color: white;">${this.y.toFixed(1)}%</p>`;
          },
        },
        series: [
          {
            name: ComponentTypeEnum.MULTIPLE_CHOICE,
            colorByPoint: true,
            data: [
              ...(multipleChoiceOption?.map(o => ({
                name: o.fieldName,
                y: o?.id && data?.[o?.id] ? (data?.[o?.id] / total) * 100 : 0,
                drilldown: o.total,
                color: o.color,
              })) || []),
              ...(ownOption?.map(o => ({
                name: o.fieldName,
                y: o?.id && data?.[o?.id] ? (data?.[o?.id] / total) * 100 : 0,
                drilldown: o.total,
                color: o.color,
              })) || []),
            ],
          },
        ],
      }}
    />
  );
};
