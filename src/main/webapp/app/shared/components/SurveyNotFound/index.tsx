import { Button, Flex, Image, Text } from '@mantine/core';
import React from 'react';

export const SurveyNotFound = ({ refetch }: { refetch: () => void }) => {
  return (
    <Flex direction="column" h="100%" pb="25vh" gap={15} align="center" justify="center" p="0 15px">
      <Image maw="250px" src={'../../../../content/images/not-found.png'} />
      <Text ta="center" c="#1F2A37" fw={400}>
        Bài khảo sát không tồn tại hoặc có lỗi. <br />
        Bạn vui lòng tải lại trang hoặc liên hệ với admin để xử lý nhé.
      </Text>
      <Flex gap="12px" align="center" justify="center">
        <Button target="_blank" component="a" href="https://www.gapowork.vn/collab/6988492569666437120/chat" variant="outline">
          Hỗ trợ trên nhóm 247
        </Button>
        <Button onClick={refetch}>Tải lại</Button>
      </Flex>
    </Flex>
  );
};
