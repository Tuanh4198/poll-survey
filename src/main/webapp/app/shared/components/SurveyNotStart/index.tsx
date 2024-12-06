import React from 'react';
import { Flex, Image, Text } from '@mantine/core';

export const SurveyNotStart = () => {
  return (
    <Flex direction="column" h="100%" pb="25vh" gap={15} align="center" justify="center" p="0 15px">
      <Image maw="250px" src={'../../../../content/images/not-start.png'} />
      <Text c="#1F2A37" fw={400}>
        Khảo sát chưa diễn ra. Vui lòng trở lại sau
      </Text>
    </Flex>
  );
};
