import { Box, Flex, NumberInput, Radio, Text, Image, Loader, Textarea } from '@mantine/core';
import { DateInput } from '@mantine/dates';
import { TBlock } from 'app/pages/Form/context/FormContext';
import { useFetchRealUrlFile } from 'app/pages/Form/hooks/useFetchRealUrlFile';
import { BlockMetafieldKey, BlockMetafieldRequiredValue } from 'app/shared/model/block.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { sortBlockField } from 'app/shared/util/sort-block-field';
import React, { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { hiddenField1, hiddenField2, hiddenField3 } from 'app/shared/model/survey.model';
import { replaceHiddenFields } from 'app/shared/util/replace-all-hidden-field';

interface ComponentTypeMultipleChoiceProps {
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

const ownOption = 'own';

export const ComponentTypeMultipleChoice = ({ defaultValue, isError, block, onChangeFieldValue }: ComponentTypeMultipleChoiceProps) => {
  const [searchParams] = useSearchParams();

  const field1 = searchParams.get(hiddenField1);
  const field2 = searchParams.get(hiddenField2);
  const field3 = searchParams.get(hiddenField3);

  const [selected, setSelected] = useState<string | undefined>();

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

  const multipleChoiceOption = useMemo(
    () =>
      block?.blockFields
        ?.filter(bf => bf.type === FieldTypeEnum.MULTIPLE_CHOICE_OPTION)
        ?.map(bf => bf)
        ?.sort((bfA, bfB) => sortBlockField({ bfA, bfB, componentType: ComponentTypeEnum.MULTIPLE_CHOICE })),
    [block?.blockFields]
  );

  const input = useMemo(
    () => block?.blockFields?.filter(bf => bf.fieldName === ComponentTypeEnum.ESSAY.toLowerCase())?.map(bf => bf),
    [block?.blockFields]
  );

  const { urlImg } = useFetchRealUrlFile(image?.fieldValue);

  useEffect(() => {
    if (defaultValue?.fieldValue) {
      if (defaultValue.fieldType === FieldTypeEnum.MULTIPLE_CHOICE_OPTION) {
        setSelected(defaultValue.fieldId?.toString() || undefined);
      } else {
        setSelected(ownOption);
      }
    }
  }, []);

  const onChangeValueRadius = (value: string) => {
    setSelected(value);
    const selectedField = block?.blockFields?.find(i => i.id?.toString() === value);
    const fieldId = selectedField?.id ? Number(selectedField?.id) : null;
    const fieldName = selectedField?.fieldName || null;
    const fieldValue = value === ownOption ? '' : selectedField?.fieldValue || '';
    const fieldType = selectedField?.type || null;
    onChangeFieldValue &&
      onChangeFieldValue({
        blockId: block.id ? Number(block.id) : undefined,
        fieldId,
        fieldName,
        fieldValue: replaceHiddenFields({ fieldValue, field1, field2, field3 }),
        fieldType,
      });
  };

  const onChangeValueOwn = ({
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
      <Radio.Group mb={15} value={selected} onChange={onChangeValueRadius}>
        <Flex direction="column" gap={20}>
          {multipleChoiceOption &&
            multipleChoiceOption.length > 0 &&
            multipleChoiceOption.map(i => (
              <Radio
                key={i.id}
                value={i.id?.toString()}
                label={replaceHiddenFields({
                  fieldValue: i.fieldValue,
                  field1,
                  field2,
                  field3,
                })}
              />
            ))}
          {input?.map((__, idx) => (
            <Radio key={idx} value={ownOption} label="Câu trả lời khác" />
          ))}
        </Flex>
      </Radio.Group>
      {selected === ownOption &&
        input?.map((item, index) => {
          if (item.type === FieldTypeEnum.TEXT_INPUT) {
            return (
              <Textarea
                autosize
                minRows={1}
                maxRows={5}
                className="own-input"
                key={index}
                onChange={event =>
                  onChangeValueOwn({
                    fieldId: item.id ? Number(item.id) : null,
                    fieldName: item.fieldName || null,
                    fieldType: item.type || null,
                    fieldValue: event.target.value,
                  })
                }
                defaultValue={
                  defaultValue && defaultValue.fieldType === FieldTypeEnum.TEXT_INPUT && defaultValue.fieldValue
                    ? defaultValue.fieldValue
                    : undefined
                }
                placeholder="Nhập đáp án"
              />
            );
          } else if (item.type === FieldTypeEnum.DATE_INPUT) {
            return <DateInput key={index} placeholder="Nhập đáp án" />;
          } else if (item.type === FieldTypeEnum.NUMBER_INPUT) {
            return <NumberInput key={index} placeholder="Nhập đáp án" hideControls />;
          } else {
            return null;
          }
        })}
    </Box>
  );
};
