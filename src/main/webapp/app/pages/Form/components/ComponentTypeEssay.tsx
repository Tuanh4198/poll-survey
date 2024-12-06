import React, { useContext, useMemo } from 'react';
import { ActionIcon, Box, Divider, FileButton, Flex, Pill, Select, Switch, Text, Textarea, Tooltip } from '@mantine/core';
import { CaretDown, CopySimple, DotsSix, Image, Trash } from '@phosphor-icons/react';
import { IMAGE_MIME_TYPE } from '@mantine/dropzone';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { FormContext, TBlock } from 'app/pages/Form/context/FormContext';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { errorEachBlockLabel, fieldValidate } from 'app/pages/Form/hooks/useValidateForm';
import { BlockMetafieldKey, BlockMetafieldRequiredValue } from 'app/shared/model/block.model';
import { BlockFieldImage } from 'app/pages/Form/components/BlockFieldImage';

interface ComponentTypeEssayProps {
  block: TBlock;
  onRemove: () => void;
}
export const ComponentTypeEssay = ({ block, onRemove }: ComponentTypeEssayProps) => {
  const {
    isStarting,
    errors,
    onChangeFieldRequired,
    onChangeFieldTitle,
    onDuplicateBlock,
    onAddFieldImage,
    onChangeFieldImage,
    onRemoveFieldImage,
    onChangeEssayFieldType,
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

  const fieldType = useMemo(
    () => block?.blockFields?.find(i => i.fieldName === ComponentTypeEnum.ESSAY.toLowerCase())?.type,
    [block?.blockFields]
  );

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
        <Select
          disabled={isStarting}
          onChange={value => onChangeEssayFieldType({ id: block.id, pageId: block.pageId, type: value as FieldTypeEnum })}
          defaultValue={fieldType}
          w="100%"
          placeholder="Kiểu dữ liệu"
          color="#4B5563"
          rightSection={<CaretDown size={16} />}
          data={[
            {
              label: 'Nhập văn bản',
              value: FieldTypeEnum.TEXT_INPUT,
            },
            {
              label: 'Nhập số',
              value: FieldTypeEnum.NUMBER_INPUT,
            },
            {
              label: 'Nhập thời gian',
              value: FieldTypeEnum.DATE_INPUT,
            },
          ]}
        />
        <Divider w="100%" color="#E5E7EB" />
        <Flex align="center" justify="space-between" w="100%">
          <Pill c="#099268" fw={500} fz="14px" radius="24px" bg="#C3FAE8">
            Câu hỏi tự luận
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
