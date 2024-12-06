import React, { useMemo } from 'react';
import { Button, Flex, Modal, Text } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';

export const useModalConfirm = ({ title, content, onOk }: { title: string; content: string; onOk: () => void }) => {
  const [opened, { open, close }] = useDisclosure(false);

  const modalConfirm = useMemo(
    () => (
      <Modal
        closeOnEscape={false}
        opened={opened}
        onClose={close}
        title={
          <Text color="#111928" fw={600} fz={18}>
            {title}
          </Text>
        }
        size="sm"
        radius={8}
      >
        <Text color="#4B5563" mb={24}>
          {content}
        </Text>
        <Flex justify="flex-end" gap={20}>
          <Button variant="outline" onClick={close}>
            Xem lại
          </Button>
          <Button
            variant="filled"
            onClick={() => {
              close();
              onOk();
            }}
          >
            Xác nhận
          </Button>
        </Flex>
      </Modal>
    ),
    [opened, title, content, onOk, close]
  );

  return {
    openModal: open,
    closeModal: close,
    modalConfirm,
  };
};
