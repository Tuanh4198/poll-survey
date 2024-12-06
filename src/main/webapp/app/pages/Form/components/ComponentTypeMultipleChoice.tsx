import { ActionIcon, Box, Text, FileButton, Flex, Divider, Pill, Switch, Tooltip, Button, Textarea } from '@mantine/core';
import { IMAGE_MIME_TYPE } from '@mantine/dropzone';
import { CopySimple, DotsSix, Image, PlusCircle, RadioButton, Trash, X } from '@phosphor-icons/react';
import { BlockFieldImage } from 'app/pages/Form/components/BlockFieldImage';
import { FormContext, TBlock } from 'app/pages/Form/context/FormContext';
import { errorEachBlockLabel, fieldValidate } from 'app/pages/Form/hooks/useValidateForm';
import { BlockMetafieldKey, BlockMetafieldRequiredValue } from 'app/shared/model/block.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { sortBlockField } from 'app/shared/util/sort-block-field';
import React, { useContext, useMemo } from 'react';

interface ComponentTypeMultipleChoiceProps {
  block: TBlock;
  onRemove: () => void;
}
export const ComponentTypeMultipleChoice = ({ block, onRemove }: ComponentTypeMultipleChoiceProps) => {
  const {
    isStarting,
    errors,
    onDuplicateBlock,
    onChangeFieldRequired,
    onChangeFieldTitle,
    onAddFieldImage,
    onChangeFieldImage,
    onRemoveFieldImage,
    onAddMultipleChoiceOption,
    onRemoveMultipleChoiceOption,
    onChangeMultipleChoiceOptionValue,
    onToggleOwnAnswerOnMultipleChoice,
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

  const errorDesc = useMemo(
    () => errors?.[errorEachBlockLabel({ id: block.id?.toString(), componentType: block.type, errType: fieldValidate.description })],
    [errors, block]
  );

  const multipleChoiceOption = useMemo(
    () =>
      block?.blockFields
        ?.filter(i => i.type === FieldTypeEnum.MULTIPLE_CHOICE_OPTION)
        ?.sort((bfA, bfB) => sortBlockField({ bfA, bfB, componentType: ComponentTypeEnum.MULTIPLE_CHOICE })),
    [block?.blockFields]
  );

  const ownAnswer = useMemo(
    () => (block?.blockFields?.filter(i => i.type === FieldTypeEnum.TEXT_INPUT)?.length || 0) > 0,
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
        <Flex direction="column" gap="14px" w="100%">
          {multipleChoiceOption?.map((item, index) => (
            <Flex key={item.id} align="center" gap="8px">
              <RadioButton size={20} color="#9CA3AF" />
              <Textarea
                disabled={isStarting}
                onBlur={event =>
                  onChangeMultipleChoiceOptionValue({
                    id: block.id,
                    pageId: block.pageId,
                    blockFieldId: item.id?.toString() || '',
                    value: event.currentTarget.value,
                  })
                }
                autosize
                minRows={1}
                maxRows={2}
                error={errorDesc && !item.fieldValue && errorDesc}
                defaultValue={item.fieldValue}
                className={`multiple-choice-title ${errorDesc && !item.fieldValue && 'error'}`}
                placeholder={`Lựa chọn  ${index + 1}`}
                flex={1}
              />
              {multipleChoiceOption.length > 2 && (
                <ActionIcon
                  disabled={isStarting}
                  onClick={() =>
                    onRemoveMultipleChoiceOption({ id: block.id, pageId: block.pageId, blockFieldId: item.id?.toString() || '' })
                  }
                  variant="transparent"
                  color="#4B5563"
                >
                  <X color="#111928" size={28} />
                </ActionIcon>
              )}
            </Flex>
          ))}
          {ownAnswer && (
            <Flex align="center" gap="8px">
              <RadioButton size={20} color="#9CA3AF" />
              <Text flex={1} color="#1F2A37">
                Câu trả lời khác
              </Text>
              <ActionIcon
                disabled={isStarting}
                onClick={() => onToggleOwnAnswerOnMultipleChoice({ id: block.id, pageId: block.pageId })}
                variant="transparent"
                color="#4B5563"
              >
                <X color="#111928" size={28} />
              </ActionIcon>
            </Flex>
          )}
          <Flex gap={12}>
            <Button
              disabled={isStarting}
              onClick={() => onAddMultipleChoiceOption({ id: block.id, pageId: block.pageId })}
              variant="outline"
              size="sm"
              bd="1px solid #E5E7EB"
              color="#111928"
              leftSection={<PlusCircle size="16px" />}
            >
              Thêm 1 lựa chọn
            </Button>
            <Button
              disabled={isStarting}
              onClick={() => onToggleOwnAnswerOnMultipleChoice({ id: block.id, pageId: block.pageId })}
              variant={ownAnswer ? 'light' : 'outline'}
              size="sm"
              bd="1px solid #E5E7EB"
              color="#111928"
              leftSection={<PlusCircle size="16px" />}
            >
              Câu trả lời khác (Tự nhập câu trả lời)
            </Button>
          </Flex>
        </Flex>
        <Divider w="100%" color="#E5E7EB" />
        <Flex align="center" justify="space-between" w="100%">
          <Pill c="#1E1E73" fw={500} fz="14px" radius="24px" bg="#D4D4F0">
            Câu hỏi trắc nghiệm
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
