import React, { Fragment, useMemo, useRef, useState } from 'react';
import './styles.scss';
import { Button, Flex, Text, Pill, Box, AspectRatio, Overlay, Image, Modal, ScrollArea, Popover, Input, CopyButton } from '@mantine/core';
import { ISurvey } from 'app/shared/model/survey.model';
import { ISurveyTemplate } from 'app/shared/model/survey-template.model';
import { useDisclosure } from '@mantine/hooks';
import { SignOut } from '@phosphor-icons/react';
import { TPage } from 'app/pages/Form/context/FormContext';
import { useHandleComponents } from 'app/pages/Preview/hooks/useHandleComponents';
import { FileWithPath } from '@mantine/dropzone';
import { FormTypeEnum } from 'app/pages/Form/helper';
import { CopyUrlSurvey } from 'app/shared/components/CopyUrlSurvey';

type PreviewProps = {
  type?: FormTypeEnum;
  files?: FileWithPath[];
  data?: ISurveyTemplate | ISurvey;
  pages?: TPage;
};

type PreviewPropsBase = {
  onCloseModal: () => void;
};

const Preview = ({ type, files, data, pages, onCloseModal }: PreviewPropsBase & PreviewProps) => {
  const activePageIndexRef = useRef<number>(0);

  const [activePage, setActivePage] = useState(pages ? Object.keys(pages)[activePageIndexRef.current] : undefined);

  const totalPage = useMemo(() => {
    return pages ? Object.keys(pages).length : 0;
  }, [pages]);

  const activePageIndex = useMemo(() => {
    if (pages && Object.keys(pages).findIndex(i => i === activePage) >= 0) {
      return Object.keys(pages).findIndex(i => i === activePage) + 1;
    }
    return 1;
  }, [pages, activePage]);

  const blockPerPage = useMemo(() => {
    return pages && activePage && pages[activePage];
  }, [pages, activePage]);

  const { renderComponent } = useHandleComponents();

  return (
    <Box className="preview-page" bg="#F9FAFB">
      <Flex bg="#fff" align="center" justify="space-between" px="20px" py="15px" style={{ borderBottom: '1px solid #E5E7EB' }}>
        <Image src={'../../../../content/images/logo.svg'} />
        <Flex gap={20}>
          {data?.id && type === FormTypeEnum.SURVEY && <CopyUrlSurvey id={data.id} />}
          <Button
            onClick={() => onCloseModal && onCloseModal()}
            variant="outline"
            leftSection={<SignOut size={16} />}
            color="#111928"
            bd="1px solid #E5E7EB"
          >
            Thoát xem
          </Button>
        </Flex>
      </Flex>
      <Box pos="relative" mb={20} bg="white">
        <AspectRatio pos="absolute" top={0} left={0} bottom={0} w={`${(activePageIndex / totalPage) * 100}%`} style={{ zIndex: 10 }}>
          <Overlay color="#D0EBFF" w="100%" h="100%" backgroundOpacity={1} />
        </AspectRatio>
        <Flex align="center" justify="center" gap={10} p={10} pos="relative" style={{ zIndex: 10 }}>
          <Text c="#000000" fw={600}>
            Trang {activePageIndex}
          </Text>
          <Pill radius={24} c="#000000" bg="#FAB005">
            {activePageIndex}/{totalPage}
          </Pill>
        </Flex>
      </Box>
      <ScrollArea m="auto" p="0px 15px" w="950px" h="calc(100vh - 100px)" scrollbars="y">
        <Flex gap={20} direction="column" align="flex-start">
          {data?.presignThumbUrl ? (
            <AspectRatio ratio={2 / 1} w="100%">
              <Image src={data?.presignThumbUrl} fallbackSrc="../../../../../content/images/poll-survey.jpg" fit="cover" />
            </AspectRatio>
          ) : files && files.length > 0 ? (
            files.map((file, index) => {
              const imageUrl = URL.createObjectURL(file);
              return (
                <Image
                  key={index}
                  width="100%"
                  style={{ aspectRatio: '2 / 1' }}
                  radius={8}
                  fit="cover"
                  flex={1}
                  src={imageUrl}
                  onLoad={() => URL.revokeObjectURL(imageUrl)}
                />
              );
            })
          ) : null}
          {data && 'isRequired' in data && data.isRequired ? (
            <Pill c="#FFFFFF" fw={500} fz={14} bg="#FD7E14">
              Bắt buộc
            </Pill>
          ) : (
            <Pill c="#FFFFFF" fw={500} fz={14} bg="#12B886">
              Không bắt buộc
            </Pill>
          )}
          {data?.title && (
            <Text c="#1F2A37" fw={700} style={{ fontSize: '20px' }}>
              {data?.title}
            </Text>
          )}
          {data?.description && (
            <Text c="#1F2A37" fw={400} style={{ fontSize: '14px' }}>
              {data?.description}
            </Text>
          )}
          {blockPerPage &&
            blockPerPage.length > 0 &&
            blockPerPage.map(item => (
              <Fragment key={item.id}>
                {renderComponent({
                  block: item,
                  componentType: item.type,
                })}
              </Fragment>
            ))}
          <Flex gap={10} align="center" justify="center" mt={20} w="100%" mb={50}>
            {activePageIndex > 1 && (
              <Button
                onClick={() => {
                  if (pages) {
                    activePageIndexRef.current -= 1;
                    setActivePage(Object.keys(pages)[activePageIndexRef.current]);
                  }
                }}
              >
                Trước
              </Button>
            )}
            {activePageIndex < totalPage && (
              <Button
                onClick={() => {
                  if (pages) {
                    activePageIndexRef.current += 1;
                    setActivePage(Object.keys(pages)[activePageIndexRef.current]);
                  }
                }}
              >
                Tiếp theo
              </Button>
            )}
          </Flex>
        </Flex>
      </ScrollArea>
    </Box>
  );
};

export const useModalPrevewWithoutSubmit = () => {
  const [opened, { open, close }] = useDisclosure(false);

  return {
    openedModalPrevewWithoutSubmit: opened,
    onOpenModalPrevewWithoutSubmit: open,
    onCloseModalPrevewWithoutSubmit: close,
    renderModalPrevewWithoutSubmit: (props: PreviewProps) => (
      <Modal
        closeOnEscape={false}
        opened={opened}
        onClose={close}
        title={undefined}
        fullScreen
        lockScroll
        radius={0}
        closeButtonProps={{ hidden: true }}
        className="modal-form-wrapper"
      >
        {opened && <Preview onCloseModal={close} {...props} />}
      </Modal>
    ),
  };
};
