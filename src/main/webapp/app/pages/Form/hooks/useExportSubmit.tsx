import { Dialog, Flex, Progress, Text } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { FormContext } from 'app/pages/Form/context/FormContext';
import { notiError } from 'app/shared/notifications';
import axios from 'axios';
import React, { useContext, useEffect, useMemo } from 'react';
import { useState } from 'react';
import { v4 as uuidv4 } from 'uuid';

export enum ExportStatusEnum {
  FINISH = 'FINISH',
  PROCESSING = 'PROCESSING',
  PENDING = 'PENDING',
  ERROR = 'ERROR',
}

interface ChecksumResult {
  id: number;
  checksum: string;
  userCode: string;
  component: string;
  conditions: string;
  status: ExportStatusEnum;
  url?: string;
  total?: number;
  current?: number;
  reason?: string;
}

export const useExportSubmit = () => {
  const { form, pages } = useContext(FormContext);

  const [checksum, setChecksum] = useState<string | undefined>();

  const [progress, setProgress] = useState<number>(0);

  const [opened, { open, close }] = useDisclosure(false);

  useEffect(() => {
    let intervalId;
    const fetchData = async () => {
      await onChecksum();
    };
    if (checksum) {
      fetchData();
      intervalId = setInterval(fetchData, 5000);
    } else {
      close();
    }
    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [checksum]);

  const onExport = async () => {
    if (!form?.values?.id) return;
    const body = {
      checksum: uuidv4(),
      conditions: `survey_id=${form?.values?.id}`,
    };
    setChecksum(body.checksum);
    try {
      await axios.post('/api/survey-submits/export', body);
      open();
    } catch (error: any) {
      console.error('Export error: ', error);
      const errMsg = error?.response?.data?.errors?.message?.[0];
      notiError({
        message: (
          <>
            Tạo thất bại, vui lòng thử lại sau! <br />
            {errMsg}
          </>
        ),
      });
      setChecksum(undefined);
    }
  };

  const downloadFile = (downloadUrl: string) => {
    window.open(downloadUrl, '_blank');
    setTimeout(() => {
      window.URL.revokeObjectURL(downloadUrl);
    }, 100);
  };

  const onChecksum = async () => {
    if (!checksum) return;
    try {
      const res = await axios.get<ChecksumResult>(`/api/exports/${checksum}`);
      if (res.data) {
        switch (res.data.status) {
          case ExportStatusEnum.FINISH: {
            if (res.data.url) {
              setChecksum(undefined);
              downloadFile(res.data.url);
            }
            break;
          }
          case ExportStatusEnum.ERROR: {
            setChecksum(undefined);
            notiError({ message: res.data.reason ? res.data.reason : 'Quá trình chuẩn bị file export thất bại, vui lòng thử lại sau!' });
            console.error('Export faild: ', res.data);
            break;
          }
          case ExportStatusEnum.PENDING: {
            if (res.data.total != null && res.data.current != null) {
              setProgress((res.data.current / (res.data.total || 1)) * 100);
            }
            break;
          }
          case ExportStatusEnum.PROCESSING: {
            if (res.data.total != null && res.data.current != null) {
              setProgress((res.data.current / (res.data.total || 1)) * 100);
            }
            break;
          }
          default: {
            setChecksum(undefined);
            break;
          }
        }
      }
    } catch (error: any) {
      setChecksum(undefined);
      console.error('Export error: ', error);
      const errMsg = error?.response?.data?.errors?.message?.[0];
      notiError({
        message: (
          <>
            Tạo thất bại, vui lòng thử lại sau! <br />
            {errMsg}
          </>
        ),
      });
    }
  };

  const renderDialogPregressExport = () => {
    return (
      <Dialog
        opened={opened}
        onClose={() => {
          close();
          setChecksum(undefined);
        }}
        withCloseButton={false}
        size="lg"
        radius="md"
      >
        <Flex mb="xs" align="center" justify="space-between">
          <Text size="sm" fw={500}>
            Đang chuẩn bị file export...
          </Text>
          <Text size="sm" fw={700}>
            {Math.floor(progress)}%
          </Text>
        </Flex>
        <Progress size="md" value={progress} striped animated />
      </Dialog>
    );
  };

  return {
    progress,
    checksum,
    onExport,
    onChecksum,
    renderDialogPregressExport,
  };
};
