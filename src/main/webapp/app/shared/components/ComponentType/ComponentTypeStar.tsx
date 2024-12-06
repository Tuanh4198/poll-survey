import React, { useMemo, useState } from 'react';
import { Box, Flex, Rating, Text, Image, Loader } from '@mantine/core';
import { TBlock } from 'app/pages/Form/context/FormContext';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { BlockMetafieldKey, BlockMetafieldRequiredValue } from 'app/shared/model/block.model';
import { BlockFieldsMetafieldKey } from 'app/shared/model/block-fields.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { useFetchRealUrlFile } from 'app/pages/Form/hooks/useFetchRealUrlFile';
import { useSearchParams } from 'react-router-dom';
import { hiddenField1, hiddenField2, hiddenField3 } from 'app/shared/model/survey.model';
import { replaceHiddenFields } from 'app/shared/util/replace-all-hidden-field';

interface ComponentTypeStarProps {
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

export const ComponentTypeStar = ({ defaultValue, isError, block, onChangeFieldValue }: ComponentTypeStarProps) => {
  const [searchParams] = useSearchParams();

  const field1 = searchParams.get(hiddenField1);
  const field2 = searchParams.get(hiddenField2);
  const field3 = searchParams.get(hiddenField3);

  const [star, setStar] = useState<number>(defaultValue?.fieldValue ? Number(defaultValue?.fieldValue) : 0);

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

  const description = useMemo(
    () =>
      block?.blockFields?.find(i => i.fieldValue === star.toString())?.metafields?.find(i => i.key === BlockFieldsMetafieldKey.description)
        ?.value,
    [block?.metafields, star]
  );

  const { urlImg } = useFetchRealUrlFile(image?.fieldValue);

  const onChangeValue = (value: number) => {
    const selectedField = block?.blockFields?.find(i => i.fieldValue === value.toString());
    const fieldId = selectedField?.id ? Number(selectedField?.id) : null;
    const fieldName = selectedField?.fieldName || null;
    const fieldValue = selectedField?.fieldValue || null;
    const fieldType = selectedField?.type || null;
    setStar(value);
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
      <Flex direction="column" gap={5} mb={10}>
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
      <Flex align="center" justify="center" direction="column" gap={15}>
        <Rating value={star} onChange={onChangeValue} color="#FAB005" size={45} />
        {description && (
          <Text c="#1F2A37" fw={600}>
            {replaceHiddenFields({
              fieldValue: description,
              field1,
              field2,
              field3,
            })}
          </Text>
        )}
      </Flex>
    </Box>
  );
};
