import React from 'react';
import { Box, Button, Flex, Text, MantineSpacing, Paper, StyleProp, CloseButton } from '@mantine/core';
import { Dropzone, FileWithPath, MS_EXCEL_MIME_TYPE } from '@mantine/dropzone';
import { notiError } from 'app/shared/notifications';
import { DownloadSimple, UploadSimple } from '@phosphor-icons/react';

interface UploadFileXlxsProps {
  p?: StyleProp<MantineSpacing>;
  file?: FileWithPath | string;
  error?: boolean;
  onDownload: () => void;
  onUpload: (files?: FileWithPath) => void;
  disabled?: boolean;
}
export const UploadFileXlxs = ({ p = '12px', file, error, onDownload, onUpload, disabled }: UploadFileXlxsProps) => {
  return (
    <Paper radius="8px" className="block-download-upload-file" bd={`1px dashed ${error ? '#fa5252' : '#E5E7EB'}`} p={p}>
      {file ? (
        <Flex gap="8px" p={12}>
          <Text color="#1F2A37" fw={600} flex={1}>
            {typeof file === 'string' ? file : file.name}
          </Text>
          <CloseButton disabled={disabled} onClick={() => onUpload(undefined)} />
        </Flex>
      ) : (
        <Flex gap="8px" align="center">
          <Box flex={1}>
            <Dropzone
              disabled={disabled}
              maxSize={5 * 1024 ** 2}
              maxFiles={1}
              p={0}
              bd="none"
              accept={MS_EXCEL_MIME_TYPE}
              onDrop={files => onUpload(files[0])}
              onReject={() => notiError({ message: 'Vui lòng chọn lại file khác!' })}
            >
              <Button w="100%" color={error ? '#fa5252' : '#4B5563'} variant="transparent" leftSection={<UploadSimple size={16} />}>
                Tải lên
              </Button>
            </Dropzone>
          </Box>
          <Box flex={1}>
            <Button
              disabled={disabled}
              onClick={onDownload}
              w="100%"
              color={error ? '#fa5252' : '#4B5563'}
              variant="subtle"
              leftSection={<DownloadSimple size={16} />}
            >
              File mẫu
            </Button>
          </Box>
        </Flex>
      )}
    </Paper>
  );
};
