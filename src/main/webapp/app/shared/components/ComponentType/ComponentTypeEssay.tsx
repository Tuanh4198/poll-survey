import React, { useMemo } from 'react';
import { Box, Flex, NumberInput, Text, Image, Loader, Textarea } from '@mantine/core';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { TBlock } from 'app/pages/Form/context/FormContext';
import { BlockMetafieldKey, BlockMetafieldRequiredValue } from 'app/shared/model/block.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { DateInput } from '@mantine/dates';
import { useFetchRealUrlFile } from 'app/pages/Form/hooks/useFetchRealUrlFile';
import { convertDateFromServer, convertStringToDate } from 'app/shared/util/date-utils';
import { useSearchParams } from 'react-router-dom';
import { hiddenField1, hiddenField2, hiddenField3 } from 'app/shared/model/survey.model';
import { replaceHiddenFields } from 'app/shared/util/replace-all-hidden-field';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

interface ComponentTypeEssayProps {
  block: TBlock;
  isError?: boolean;
  defaultValue?: {
    blockId?: number;
    fieldId: number | null;
    fieldName: string | null;
    fieldValue: string | null;
    fieldType: FieldTypeEnum | null;
  };
  onChangeFieldValue?: ({
    blockId,
    fieldId,
    fieldName,
    fieldValue,
    fieldType,
  }: {
    blockId?: number;
    fieldId: number | null;
    fieldName: string | null;
    fieldValue: string | null;
    fieldType: FieldTypeEnum | null;
  }) => void;
}

export const ComponentTypeEssay = ({ defaultValue, isError, block, onChangeFieldValue }: ComponentTypeEssayProps) => {
  const [searchParams] = useSearchParams();

  const field1 = searchParams.get(hiddenField1);
  const field2 = searchParams.get(hiddenField2);
  const field3 = searchParams.get(hiddenField3);

  const title = useMemo(
    () => block?.blockFields?.filter(bf => bf.type === FieldTypeEnum.TEXT)?.map(bf => bf.fieldValue),
    [block?.blockFields]
  );

  const image = useMemo(
    () => block?.blockFields?.find(i => i.type === FieldTypeEnum.IMAGE && i.fieldName === ComponentTypeEnum.TITLE.toLowerCase()),
    [block?.blockFields]
  );

  const isRequired = useMemo(
    () => block?.metafields?.find(i => i.key === BlockMetafieldKey.required)?.value === BlockMetafieldRequiredValue.true,
    [block?.metafields]
  );

  const input = useMemo(
    () => block?.blockFields?.filter(bf => bf.fieldName === ComponentTypeEnum.ESSAY.toLowerCase())?.map(bf => bf),
    [block?.blockFields]
  );

  const { urlImg } = useFetchRealUrlFile(image?.fieldValue);

  const onChangeValue = ({
    fieldId,
    fieldName,
    fieldValue,
    fieldType,
  }: {
    fieldId: number | null;
    fieldName: string | null;
    fieldValue: string | null;
    fieldType: FieldTypeEnum | null;
  }) => {
    onChangeFieldValue &&
      onChangeFieldValue({
        blockId: block.id ? Number(block.id) : undefined,
        fieldId,
        fieldName,
        fieldValue,
        fieldType,
      });
  };

  return (
    <Box pos="relative" className="component-type-section" bd={isError ? '1px solid red' : undefined}>
      <Flex direction="column" gap={15} mb={10}>
        {image && (
          <Image
            flex={1}
            radius={8}
            fit="cover"
            src={urlImg}
            style={{ aspectRatio: '2 / 1' }}
            onLoad={() => <Loader />}
            fallbackSrc="../../../../../content/images/poll-survey.jpg"
          />
        )}
        {title?.map((item, index) => (
          <Text key={index} c="#1F2A37" fz={16} fw={600}>
            {index === 0 && isRequired && (
              <Text c="#FA5252" mr={5} component="span">
                *
              </Text>
            )}
            {replaceHiddenFields({
              fieldValue: item,
              field1,
              field2,
              field3,
            })}
          </Text>
        ))}
      </Flex>
      {input?.map((item, index) => {
        if (item.type === FieldTypeEnum.TEXT_INPUT) {
          return (
            <Textarea
              autosize
              minRows={1}
              maxRows={5}
              key={index}
              onChange={event =>
                onChangeValue({
                  fieldId: item.id ? Number(item.id) : null,
                  fieldName: item.fieldName || null,
                  fieldType: item.type || null,
                  fieldValue: event.target.value,
                })
              }
              defaultValue={defaultValue?.fieldValue || undefined}
              placeholder="Nhập đáp án"
            />
          );
        } else if (item.type === FieldTypeEnum.DATE_INPUT) {
          return (
            <DateInput
              key={index}
              onChange={value =>
                onChangeValue({
                  fieldId: item.id ? Number(item.id) : null,
                  fieldName: item.fieldName || null,
                  fieldType: item.type || null,
                  fieldValue: convertDateFromServer(value),
                })
              }
              defaultValue={defaultValue?.fieldValue ? convertStringToDate(defaultValue.fieldValue) : undefined}
              valueFormat={APP_LOCAL_DATE_FORMAT}
              placeholder="Nhập ngày tháng (dd/mm/yyyy)"
            />
          );
        } else if (item.type === FieldTypeEnum.NUMBER_INPUT) {
          return (
            <NumberInput
              key={index}
              onChange={value =>
                onChangeValue({
                  fieldId: item.id ? Number(item.id) : null,
                  fieldName: item.fieldName || null,
                  fieldType: item.type || null,
                  fieldValue: value.toString(),
                })
              }
              defaultValue={defaultValue?.fieldValue || undefined}
              placeholder="Nhập số"
              hideControls
            />
          );
        } else {
          return null;
        }
      })}
    </Box>
  );
};
