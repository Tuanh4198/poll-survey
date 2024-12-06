import React from 'react';
import { Flex, Image, Text } from '@mantine/core';

export const SubmitSurveySuccess = () => {
  return (
    <Flex direction="column" h="100%" pb="25vh" gap={15} align="center" justify="center" p="0 15px">
      <Image maw="250px" src={'../../../../content/images/success.svg'} />
      <Text c="#1F2A37" fz={24} fw={700} ta="center">
        Chúc mừng bạn đã <br /> hoàn thành khảo sát!
      </Text>
    </Flex>
  );
};
