import React from 'react';
import { Flex, Text } from '@mantine/core';
import { PlusCircle } from '@phosphor-icons/react';
import { useModalCreateForm } from 'app/pages/Form';

export const SurveyBlockCreate = () => {
  const { onOpenModalUpsertForm, renderModalUpsertForm } = useModalCreateForm();

  return (
    <Flex direction="column" gap="8px" align="center" w="100%">
      <Flex onClick={onOpenModalUpsertForm} w="100%" align="center" justify="center" className="survey-block-btn-create">
        <PlusCircle color="#1F2A37" size={44} />
      </Flex>
      <Text color="#1F2A37" mt="8px" lineClamp={2} ta="center">
        Khảo sát trống
      </Text>
      {renderModalUpsertForm()}
    </Flex>
  );
};
