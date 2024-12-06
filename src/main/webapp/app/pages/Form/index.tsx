/* eslint-disable @typescript-eslint/no-misused-promises */
import React, { useContext, useEffect, useRef, useState } from 'react';
import './styles.scss';
import { ActionIcon, Box, Button, Flex, Image, Loader, LoadingOverlay, Modal, ScrollArea, Tabs } from '@mantine/core';
import { CaretLeft, X } from '@phosphor-icons/react';
import { UploadBanner } from 'app/pages/Form/components/UploadBanner';
import { FormInfo } from 'app/pages/Form/components/FormInfo';
import { FormBuilder } from 'app/pages/Form/components/FormBuilder';
import { FormContext, FormContextView, TFormBody, TTarget } from 'app/pages/Form/context/FormContext';
import { useDisclosure, useViewportSize } from '@mantine/hooks';
import { useModalConfirm } from 'app/pages/Form/hooks/useModalConfirm';
import { FormTypeEnum } from 'app/pages/Form/helper';
import { SectionResult } from 'app/pages/Form/components/SectionResult';
import { SectionProgress } from 'app/pages/Form/components/SectionProgress';
import { SectionPreview } from 'app/pages/Form/components/SectionPreview';
import { useExportReport } from 'app/pages/Form/hooks/useExportReport';
import { useExportSubmit } from 'app/pages/Form/hooks/useExportSubmit';
import { CopyUrlSurvey } from 'app/shared/components/CopyUrlSurvey';

type FormContentPropsWithDetail = {
  isDetail: true;
  goBack: () => void;
  type: FormTypeEnum;
  onCloseModalUpsertForm?: () => void;
};

type FormContentPropsWithoutDetail = {
  isDetail?: false;
  onCloseModalUpsertForm?: () => void;
};

type FormContentProps = FormContentPropsWithDetail | FormContentPropsWithoutDetail;

const enum TabEnum {
  LIST = 'LIST',
  PROGRESS = 'PROGRESS',
  RESULT = 'RESULT',
}

const tabs = {
  [TabEnum.LIST]: 'Danh sách câu hỏi',
  [TabEnum.PROGRESS]: 'Tiến độ',
  [TabEnum.RESULT]: 'Kết quả',
};

const FormContent = (props: FormContentProps) => {
  const { isDetail, onCloseModalUpsertForm } = props;

  let goBack: (() => void) | undefined = undefined;
  let type: FormTypeEnum | undefined = undefined;
  if (isDetail) {
    goBack = props.goBack;
    type = props.type;
  }

  const [tab, setTab] = useState<TabEnum>(TabEnum.LIST);

  const {
    checksum: checksumReport,
    onExport: onExportReport,
    renderDialogPregressExport: renderDialogPregressExportReport,
  } = useExportReport();

  const {
    checksum: checksumSubmit,
    onExport: onExportSubmit,
    renderDialogPregressExport: renderDialogPregressExportSubmit,
  } = useExportSubmit();

  const tabPanel = () => {
    switch (tab) {
      case TabEnum.LIST:
        return <FormBuilder />;
      case TabEnum.PROGRESS:
        return (
          <SectionProgress
            checksum={checksumReport}
            onExport={onExportReport}
            renderDialogPregressExport={renderDialogPregressExportReport}
          />
        );
      case TabEnum.RESULT:
        return (
          <SectionResult
            checksum={checksumSubmit}
            onExport={onExportSubmit}
            renderDialogPregressExport={renderDialogPregressExportSubmit}
          />
        );
      default:
        return null;
    }
  };

  const headerRef = useRef<HTMLDivElement | null>(null);

  const { height } = useViewportSize();

  const { form, submiting, onCustomValidate, onSubmit } = useContext(FormContext);

  const { openModal, modalConfirm } = useModalConfirm({
    title: 'Trở lại',
    content: 'Bài khảo sát đánh giá sẽ không được lưu. Bạn xác nhận thoát?',
    onOk() {
      if (isDetail) {
        goBack && goBack();
      } else {
        onCloseModalUpsertForm && onCloseModalUpsertForm();
      }
    },
  });

  const handleKeyDown = event => {
    if (event.key === 'Enter') {
      event.preventDefault();
    }
  };

  useEffect(() => {
    const handleBeforeUnload = event => {
      if (!isDetail) {
        event.preventDefault();
        event.returnValue = '';
      }
    };
    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, []);

  return (
    <Box pos="relative" className="form-wrapper">
      <LoadingOverlay zIndex={10} visible={submiting} loaderProps={{ children: <Loader /> }} />
      <form onSubmit={form?.onSubmit(onSubmit, onCustomValidate)} onKeyDown={handleKeyDown} className="modal-body">
        <Flex
          ref={headerRef}
          bg="#fff"
          align="center"
          justify="space-between"
          px="20px"
          py="15px"
          style={{ borderBottom: '1px solid #E5E7EB' }}
        >
          <Flex gap="20px" align="center">
            {isDetail && (
              <ActionIcon onClick={openModal} variant="outline" size="lg" color="#111928" bd="1px solid #E5E7EB">
                <CaretLeft size={16} />
              </ActionIcon>
            )}
            <Image src={'../../../../content/images/logo.svg'} />
          </Flex>
          <Flex gap="20px">
            {form?.values?.id && type === FormTypeEnum.SURVEY && <CopyUrlSurvey id={form.values.id} />}
            <SectionPreview type={type} />
            {!isDetail && (
              <Button onClick={openModal} variant="outline" leftSection={<X size={16} />} color="#FA5252">
                Hủy
              </Button>
            )}
          </Flex>
          {modalConfirm}
        </Flex>
        <Flex style={{ height: `${height - (headerRef.current?.scrollHeight || 0)}px` }}>
          <ScrollArea h="100%" scrollbars="y" style={{ background: '#ffffff', borderRight: '1px solid #E5E7EB' }}>
            <Box style={{ padding: '20px', width: '360px' }}>
              <UploadBanner />
              <FormInfo isDetail={!!isDetail} type={type} />
            </Box>
          </ScrollArea>
          <Box
            flex={1}
            pos="relative"
            bg={tab === TabEnum.PROGRESS ? '#fff' : '#F9FAFB'}
            style={{ padding: isDetail && type === FormTypeEnum.SURVEY ? '60px 20px 20px' : '20px' }}
          >
            {isDetail && type === FormTypeEnum.SURVEY ? (
              <>
                <Tabs
                  pos="absolute"
                  left={0}
                  right={0}
                  top={0}
                  className="tab-wrapper"
                  color="#2A2A86"
                  value={tab}
                  onChange={value => setTab(value as TabEnum)}
                >
                  <Tabs.List justify="center">
                    {Object.entries(tabs).map(([key, value]) => (
                      <Tabs.Tab key={key} value={key}>
                        {value}
                      </Tabs.Tab>
                    ))}
                  </Tabs.List>
                </Tabs>
                {tabPanel()}
              </>
            ) : (
              <FormBuilder />
            )}
          </Box>
        </Flex>
      </form>
    </Box>
  );
};

// Section create
const withFormContentCreate = (WrappedComponent: (props: FormContentPropsWithoutDetail) => React.JSX.Element) => {
  return (props: FormContentPropsWithoutDetail) => (
    <FormContextView initialValues={undefined}>
      <WrappedComponent {...props} />
    </FormContextView>
  );
};

const FormContextCreate = withFormContentCreate(FormContent);

export const useModalCreateForm = () => {
  const [opened, { open, close }] = useDisclosure(false);

  return {
    openedModalUpsertForm: opened,
    onOpenModalUpsertForm: open,
    onCloseModalUpsertForm: close,
    renderModalUpsertForm: () => (
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
        {opened && <FormContextCreate onCloseModalUpsertForm={close} />}
      </Modal>
    ),
  };
};

// Section update
const withFormContentUpdate = (WrappedComponent: (props: FormContentPropsWithDetail) => React.JSX.Element) => {
  return ({
    initialIsAnonymous,
    initialValues,
    initialTarget,
    onRefetch,
    ...props
  }: FormContentPropsWithDetail & {
    initialIsAnonymous: boolean;
    initialTarget: TTarget;
    initialValues: TFormBody;
    onRefetch: () => void;
  }) => (
    <FormContextView
      type={props.type}
      initialIsAnonymous={initialIsAnonymous}
      initialTarget={initialTarget}
      initialValues={initialValues}
      onRefetch={onRefetch}
    >
      <WrappedComponent {...props} />
    </FormContextView>
  );
};

export const FormContextUpdate = withFormContentUpdate(FormContent);
