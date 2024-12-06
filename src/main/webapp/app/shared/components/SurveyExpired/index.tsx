import React from 'react';
import { Flex, Image, Text } from '@mantine/core';

export const SurveyExpired = () => {
  return (
    <Flex direction="column" h="100%" pb="25vh" gap={15} align="center" justify="center" p="0 15px">
      <Image maw="250px" src={'../../../../content/images/expired.svg'} />
      <Text c="#1F2A37" fw={400}>
        Khảo sát đã hết hạn!
      </Text>
    </Flex>
  );
};
