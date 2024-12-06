import { AspectRatio, Box, FileButton, Flex, Overlay, Text, Image, Loader } from '@mantine/core';
import { IMAGE_MIME_TYPE } from '@mantine/dropzone';
import { Image as ImageIco, Trash } from '@phosphor-icons/react';
import { FormContext } from 'app/pages/Form/context/FormContext';
import { useFetchRealUrlFile } from 'app/pages/Form/hooks/useFetchRealUrlFile';
import React, { useContext, useMemo } from 'react';

export const BlockFieldImage = ({
  file,
  urlFile,
  onChangeFile,
  onRemoveFile,
}: {
  file?: File | null;
  urlFile?: string;
  onChangeFile: (file: File | null) => void;
  onRemoveFile: () => void;
}) => {
  const { isStarting } = useContext(FormContext);

  const { urlImg } = useFetchRealUrlFile(urlFile);

  const showFileAction = useMemo(() => {
    if (isStarting) return null;
    return (
      <Box pos="absolute" top={0} left={0} right={0} bottom={0} className="field-image-action">
        <AspectRatio ratio={2 / 1} w="100%" mx="auto" pos="relative" className="field-image-overlay">
          <Overlay color="#000" radius={8} backgroundOpacity={0.5} />
        </AspectRatio>
        <Flex gap={50} align="center" justify="center" w="100%" h="100%" className="field-image-btn">
          <FileButton onChange={onChangeFile} multiple={false} accept={IMAGE_MIME_TYPE.join(',')}>
            {props => (
              <Flex direction="column" align="center" gap={8} variant="transparent" color="#ffffff" {...props} className="btn-change">
                <ImageIco size={44} color="#ffffff" />
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
    );
  }, [isStarting, onChangeFile, onRemoveFile]);

  if (file) {
    const localFileUrl = URL.createObjectURL(file);
    return (
      <Box w="40%" pos="relative" className="field-image">
        <Image
          flex={1}
          radius={8}
          fit="cover"
          width="40%"
          src={localFileUrl}
          style={{ aspectRatio: '2 / 1' }}
          onLoad={() => URL.revokeObjectURL(localFileUrl)}
        />
        {showFileAction}
      </Box>
    );
  } else if (urlFile) {
    return (
      <Box w="40%" pos="relative" className="field-image">
        <Image
          flex={1}
          radius={8}
          fit="cover"
          width="40%"
          src={urlImg}
          style={{ aspectRatio: '2 / 1' }}
          onLoad={() => <Loader />}
          fallbackSrc="../../../../content/images/poll-survey.jpg"
        />
        {showFileAction}
      </Box>
    );
  } else return null;
};
