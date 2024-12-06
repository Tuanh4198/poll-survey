import React, { Fragment, useContext, useMemo } from 'react';
import { ActionIcon, Box, Checkbox, Divider, FileButton, Flex, Pill, Switch, Text, Textarea, TextInput, Tooltip } from '@mantine/core';
import { CopySimple, DotsSix, Image, Star, Trash } from '@phosphor-icons/react';
import { IMAGE_MIME_TYPE } from '@mantine/dropzone';
import { FormContext, TBlock } from 'app/pages/Form/context/FormContext';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { errorEachBlockLabel, fieldValidate } from 'app/pages/Form/hooks/useValidateForm';
import { BlockMetafieldKey, BlockMetafieldRequiredValue } from 'app/shared/model/block.model';
import { BlockFieldsMetafieldKey } from 'app/shared/model/block-fields.model';
import { sortBlockField } from 'app/shared/util/sort-block-field';
import { BlockFieldImage } from 'app/pages/Form/components/BlockFieldImage';

interface ComponentTypeStarProps {
  block: TBlock;
  onRemove: () => void;
}
export const ComponentTypeStar = ({ block, onRemove }: ComponentTypeStarProps) => {
  const {
    isStarting,
    errors,
    onDuplicateBlock,
    onChangeFieldRequired,
    onChangeFieldTitle,
    onAddFieldImage,
    onChangeFieldImage,
    onRemoveFieldImage,
    onChangeDescriptionStar,
  } = useContext(FormContext);

  const blockRequired = useMemo(
    () => block?.metafields?.find(i => i.key === BlockMetafieldKey.required)?.value === BlockMetafieldRequiredValue.true,
    [block?.metafields]
  );

  const blockFieldTitle = useMemo(
    () => block?.blockFields?.find(i => i.type === FieldTypeEnum.TEXT && i.fieldName === ComponentTypeEnum.TITLE.toLowerCase())?.fieldValue,
    [block?.blockFields]
  );

  const blockFieldImage = useMemo(
    () => block?.blockFields?.find(i => i.type === FieldTypeEnum.IMAGE && i.fieldName === ComponentTypeEnum.TITLE.toLowerCase()),
    [block?.blockFields]
  );

  const errorTitle = useMemo(
    () => errors?.[errorEachBlockLabel({ id: block.id?.toString(), componentType: block.type, errType: fieldValidate.title })],
    [errors, block]
  );

  const starOption = useMemo(
    () =>
      block?.blockFields
        ?.filter(i => i.type === FieldTypeEnum.STAR_OPTION)
        ?.sort((bfA, bfB) => sortBlockField({ bfA, bfB, componentType: ComponentTypeEnum.STAR })),
    [block?.blockFields]
  );

  const star = useMemo(() => <Star size={20} weight="fill" color="#FAB005" />, []);

  return (
    <Box pos="relative" className="component-type-section">
      <Flex gap="15px" direction="column" w="100%" align="center">
        <DotsSix size={32} color="#9CA3AF" />
        <Textarea
          disabled={isStarting}
          autosize
          minRows={1}
          maxRows={3}
          error={errorTitle}
          defaultValue={blockFieldTitle}
          onChange={event => onChangeFieldTitle({ id: block.id, pageId: block.pageId, title: event.currentTarget.value })}
          w="100%"
          className={`title-component ${errorTitle && 'error'}`}
          placeholder="Nội dung câu hỏi"
          rightSectionPointerEvents="painted"
          rightSection={
            <FileButton
              disabled={isStarting}
              onChange={file => {
                if (blockFieldImage) {
                  onChangeFieldImage({ id: block.id, pageId: block.pageId, file });
                } else {
                  onAddFieldImage({ id: block.id, pageId: block.pageId, file });
                }
              }}
              accept={IMAGE_MIME_TYPE.join(',')}
              multiple={false}
            >
              {props => (
                <Tooltip label="Thêm ảnh">
                  <ActionIcon {...props} variant="transparent" color="#4B5563">
                    <Image color="#4B5563" size={28} />
                  </ActionIcon>
                </Tooltip>
              )}
            </FileButton>
          }
        />
        {blockFieldImage && (
          <Flex w="100%" justify="flex-start">
            <BlockFieldImage
              file={blockFieldImage.file}
              urlFile={blockFieldImage.fieldValue}
              onChangeFile={(file: File | null) => {
                onChangeFieldImage({ id: block.id, pageId: block.pageId, file });
              }}
              onRemoveFile={() => {
                onRemoveFieldImage({ id: block.id, pageId: block.pageId, blockFieldId: blockFieldImage.id?.toString() });
              }}
            />
          </Flex>
        )}
        <Text size="14px" c="#4B5563" fw={600} w="100%">
          Câu trả lời
        </Text>
        <Flex direction="column-reverse" align="center" justify="flex-start" gap="14px" w="100%">
          {starOption?.map(item => (
            <Flex key={item.id} align="center" gap="45px" w="100%">
              <Flex gap="8px" w="150px">
                {Array.from({ length: Number(item.fieldValue) }).map((__, idx) => (
                  <Fragment key={idx}>{star}</Fragment>
                ))}
              </Flex>
              <TextInput
                disabled={isStarting}
                onBlur={event =>
                  onChangeDescriptionStar({
                    id: block.id,
                    pageId: block.pageId,
                    value: item.fieldValue || '',
                    desc: event.currentTarget.value,
                  })
                }
                defaultValue={item.metafields?.find(mf => mf.key === BlockFieldsMetafieldKey.description)?.value}
                flex={1}
                placeholder="Nhập tiêu đề"
                className="star-title"
              />
              <Checkbox disabled={isStarting} styles={{ label: { fontSize: '14px' } }} c="#111928" label="Nhập lý do" />
            </Flex>
          ))}
        </Flex>
        <Divider w="100%" color="#E5E7EB" />
        <Flex align="center" justify="space-between" w="100%">
          <Pill c="#FD7E14" fw={500} fz="14px" radius="24px" bg="#FFEECD">
            Câu hỏi vote sao
          </Pill>
          <Flex gap={12} align="center">
            <Switch
              disabled={isStarting}
              checked={blockRequired}
              onChange={event => onChangeFieldRequired({ id: block.id, pageId: block.pageId, required: event.currentTarget.checked })}
              label="Bắt buộc trả lời"
            />
            <Tooltip label="Sao chép">
              <ActionIcon
                disabled={isStarting}
                onClick={() => onDuplicateBlock({ id: block.id, pageId: block.pageId })}
                variant="transparent"
                color="#4B5563"
              >
                <CopySimple size={28} />
              </ActionIcon>
            </Tooltip>
            <Tooltip label="Xóa">
              <ActionIcon disabled={isStarting} onClick={onRemove} variant="transparent" color="#4B5563">
                <Trash size={28} />
              </ActionIcon>
            </Tooltip>
          </Flex>
        </Flex>
      </Flex>
    </Box>
  );
};