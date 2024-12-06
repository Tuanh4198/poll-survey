import React, { useContext, useEffect, useMemo } from 'react';
import { RichTextEditor, Link } from '@mantine/tiptap';
import { EditorEvents, useEditor } from '@tiptap/react';
import Highlight from '@tiptap/extension-highlight';
import StarterKit from '@tiptap/starter-kit';
import Underline from '@tiptap/extension-underline';
import TextAlign from '@tiptap/extension-text-align';
import Superscript from '@tiptap/extension-superscript';
import SubScript from '@tiptap/extension-subscript';
import Placeholder from '@tiptap/extension-placeholder';
import { ActionIcon, Box, Divider, Flex, Pill, Tooltip } from '@mantine/core';
import { CopySimple, DotsSix, Trash } from '@phosphor-icons/react';
import { FormContext, TBlock } from 'app/pages/Form/context/FormContext';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';

interface ComponentTypeTitleProps {
  block: TBlock;
  onRemove: () => void;
}
export const ComponentTypeTitle = ({ block, onRemove }: ComponentTypeTitleProps) => {
  const { isStarting, onDuplicateBlock, onChangeFieldTitle } = useContext(FormContext);

  const blockTitle = useMemo(
    () => block?.blockFields?.find(i => i.type === FieldTypeEnum.TEXT && i.fieldName === ComponentTypeEnum.TITLE.toLowerCase())?.fieldValue,
    [block]
  );

  const editor = useEditor({
    onUpdate({ editor: _editor }: EditorEvents['update']) {
      const html = _editor.getHTML();
      onChangeFieldTitle({ id: block.id, pageId: block.pageId, title: html });
    },
    extensions: [
      StarterKit,
      Underline,
      Link,
      Superscript,
      SubScript,
      Highlight,
      TextAlign.configure({ types: ['heading', 'paragraph'] }),
      Placeholder.configure({ placeholder: 'Nhập nội dung' }),
    ],
    editable: !isStarting,
  });

  useEffect(() => {
    editor?.commands.setContent(blockTitle || '');
  }, []);

  return (
    <Box pos="relative" className="component-type-section">
      <Flex gap="15px" direction="column" w="100%" align="center">
        <DotsSix size={32} color="#9CA3AF" />
        <Box w="100%">
          <RichTextEditor editor={editor}>
            <RichTextEditor.Toolbar sticky stickyOffset={60}>
              <RichTextEditor.ControlsGroup>
                <RichTextEditor.Bold disabled={isStarting} />
                <RichTextEditor.Italic disabled={isStarting} />
                <RichTextEditor.Underline disabled={isStarting} />
                <RichTextEditor.Strikethrough disabled={isStarting} />
                <RichTextEditor.ClearFormatting disabled={isStarting} />
                <RichTextEditor.Highlight disabled={isStarting} />
                <RichTextEditor.Code disabled={isStarting} />
              </RichTextEditor.ControlsGroup>
              <RichTextEditor.ControlsGroup>
                <RichTextEditor.H1 disabled={isStarting} />
                <RichTextEditor.H2 disabled={isStarting} />
                <RichTextEditor.H3 disabled={isStarting} />
                <RichTextEditor.H4 disabled={isStarting} />
              </RichTextEditor.ControlsGroup>
              <RichTextEditor.ControlsGroup>
                <RichTextEditor.Blockquote disabled={isStarting} />
                <RichTextEditor.Hr disabled={isStarting} />
                <RichTextEditor.BulletList disabled={isStarting} />
                <RichTextEditor.OrderedList disabled={isStarting} />
                <RichTextEditor.Subscript disabled={isStarting} />
                <RichTextEditor.Superscript disabled={isStarting} />
              </RichTextEditor.ControlsGroup>
              <RichTextEditor.ControlsGroup>
                <RichTextEditor.Link disabled={isStarting} />
                <RichTextEditor.Unlink disabled={isStarting} />
              </RichTextEditor.ControlsGroup>
              <RichTextEditor.ControlsGroup>
                <RichTextEditor.AlignLeft disabled={isStarting} />
                <RichTextEditor.AlignCenter disabled={isStarting} />
                <RichTextEditor.AlignJustify disabled={isStarting} />
                <RichTextEditor.AlignRight disabled={isStarting} />
              </RichTextEditor.ControlsGroup>
              <RichTextEditor.ControlsGroup>
                <RichTextEditor.Undo disabled={isStarting} />
                <RichTextEditor.Redo disabled={isStarting} />
              </RichTextEditor.ControlsGroup>
            </RichTextEditor.Toolbar>
            <RichTextEditor.Content />
          </RichTextEditor>
        </Box>
        <Divider w="100%" color="#E5E7EB" />
        <Flex align="center" justify="space-between" w="100%">
          <Pill c="#1F2A37" fw={500} fz="14px" radius="24px" bg="#E5E7EB">
            Tiêu đề
          </Pill>
          <Flex gap={12} align="center">
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
