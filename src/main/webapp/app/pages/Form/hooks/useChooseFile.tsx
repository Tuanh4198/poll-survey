import React, { useMemo, useState } from 'react';
import { FileWithPath } from '@mantine/dropzone';
import { Image } from '@mantine/core';

export const useChooseFile = ({ defaultUrlFiles, height }: { defaultUrlFiles?: string[]; height?: string | number }) => {
  const [files, setFiles] = useState<FileWithPath[]>();
  const [urlFile, setUrlFile] = useState<string[] | undefined>(defaultUrlFiles);

  const previewFiles = useMemo(() => {
    if (files && files?.length > 0) {
      return files.map((file, index) => {
        const imageUrl = URL.createObjectURL(file);
        return (
          <Image
            key={index}
            width="100%"
            style={{ aspectRatio: '2 / 1' }}
            height={height}
            radius={8}
            fit="cover"
            flex={1}
            src={imageUrl}
            onLoad={() => URL.revokeObjectURL(imageUrl)}
          />
        );
      });
    }
    if (urlFile && urlFile?.length > 0) {
      return urlFile.map((file, index) => (
        <Image
          key={index}
          width="100%"
          style={{ aspectRatio: '2 / 1' }}
          height={height}
          radius={8}
          fit="cover"
          flex={1}
          src={file}
          onLoad={() => URL.revokeObjectURL(file)}
          fallbackSrc="../../../../../content/images/poll-survey.jpg"
        />
      ));
    }
    return undefined;
  }, [files, urlFile]);

  return {
    files,
    setFiles,
    setUrlFile,
    previewFiles,
  };
};
