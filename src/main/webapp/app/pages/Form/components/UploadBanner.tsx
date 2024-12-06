import { Box, Flex, FileButton, Text, TextInput, AspectRatio, Overlay } from '@mantine/core';
import { IMAGE_MIME_TYPE, Dropzone } from '@mantine/dropzone';
import { Image, Trash, PlusCircle } from '@phosphor-icons/react';
import { FormContext } from 'app/pages/Form/context/FormContext';
import { useStagedFile } from 'app/pages/Form/hooks/useStagedFile';
import { notiError } from 'app/shared/notifications';
import React, { useContext, useEffect } from 'react';

export const UploadBanner = () => {
  const { form, previewFiles, files, setFiles, setUrlFile } = useContext(FormContext);

  const { onStagedFile } = useStagedFile();

  const onRemoveFile = () => {
    setFiles(undefined);
    setUrlFile(undefined);
    form?.setFieldValue('thumbUrl', undefined);
    form?.setFieldValue('thumbUrlUpload', undefined);
  };

  useEffect(() => {
    if (files && files?.length > 0) {
      onStagedFile({
        files,
        onSuccess({ resourceUrl, url }) {
          form?.setFieldValue('thumbUrl', url);
          form?.setFieldValue('thumbUrlUpload', resourceUrl);
        },
        onError() {
          setFiles(undefined);
        },
      });
    }
  }, [files]);

  return (
    <>
      <TextInput display="none" {...form?.getInputProps('thumbUrl')} />
      <TextInput display="none" {...form?.getInputProps('thumbUrlUpload')} />
      {previewFiles && previewFiles?.length > 0 ? (
        <Box pos="relative" className="banner-wrapper">
          <AspectRatio ratio={2 / 1} maw={400} mx="auto" pos="relative" className="banner-overlay">
            {previewFiles}
            <Overlay color="#000" radius={8} backgroundOpacity={0.5} />
          </AspectRatio>
          <Box pos="absolute" top={0} left={0} right={0} bottom={0} className="banner-action">
            <Flex gap={50} align="center" justify="center" w="100%" h="100%">
              <FileButton onChange={setFiles} multiple accept={IMAGE_MIME_TYPE.join(',')}>
                {props => (
                  <Flex direction="column" align="center" gap={8} variant="transparent" color="#ffffff" {...props} className="btn-change">
                    <Image size={44} color="#ffffff" />
                    <Text fw={600} color="white">
                      Thay ảnh
                    </Text>
                  </Flex>
                )}
              </FileButton>
              <Flex
                direction="column"
                align="center"
                gap={8}
                variant="transparent"
                color="#ffffff"
                onClick={onRemoveFile}
                className="btn-remove"
              >
                <Trash size={44} color="#ffffff" />
                <Text fw={600} color="white">
                  Xoá ảnh
                </Text>
              </Flex>
            </Flex>
          </Box>
        </Box>
      ) : (
        <Dropzone
          bg="white"
          radius={8}
          p={previewFiles && previewFiles?.length > 0 ? 0 : 20}
          maxSize={5 * 1024 ** 2}
          maxFiles={1}
          accept={IMAGE_MIME_TYPE}
          onDrop={setFiles}
          onReject={() => notiError({ message: 'Vui lòng chọn lại file khác!' })}
        >
          <Dropzone.Accept>
            <Flex w="100%" direction="column" align="center" justify="center">
              <Text fz={12} c="#2A2A86" mt={8} ta="center">
                Thả file vào đây để chọn!
              </Text>
            </Flex>
          </Dropzone.Accept>
          <Dropzone.Reject>
            <Flex w="100%" direction="column" align="center" justify="center">
              <Text fz={12} c="#FA5252" mt={8} ta="center">
                File không đúng định dạng hoặc dung lượng lớn hơn 5MB, <br />
                vui lòng chọn lại file khác!
              </Text>
            </Flex>
          </Dropzone.Reject>
          <Dropzone.Idle>
            <Flex w="100%" direction="column" align="center" justify="center">
              <PlusCircle color="#1F2A37" size={44} />
              <Text fz={14} c="#000000" mt={8} ta="center">
                Thêm ảnh bìa
              </Text>
              <Text fz={12} c="#6B7280" mt={5} ta="center">
                Kích thước tiêu chuẩn: 1000x500 (2:1). dưới 5Mb
              </Text>
            </Flex>
          </Dropzone.Idle>
        </Dropzone>
      )}
    </>
  );
};
