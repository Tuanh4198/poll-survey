import React from 'react';
import { Box } from '@mantine/core';
import { TBlock } from 'app/pages/Form/context/FormContext';
import { useSearchParams } from 'react-router-dom';
import { hiddenField1, hiddenField2, hiddenField3 } from 'app/shared/model/survey.model';
import { replaceHiddenFields } from 'app/shared/util/replace-all-hidden-field';

interface ComponentTypeTitleProps {
  block: TBlock;
}

export const ComponentTypeTitle = ({ block }: ComponentTypeTitleProps) => {
  const [searchParams] = useSearchParams();

  const field1 = searchParams.get(hiddenField1);
  const field2 = searchParams.get(hiddenField2);
  const field3 = searchParams.get(hiddenField3);

  return (
    <Box pos="relative" className="component-type-section">
      {block.blockFields?.map(bf => (
        <div
          key={bf.id}
          dangerouslySetInnerHTML={{
            __html: replaceHiddenFields({
              fieldValue: bf.fieldValue,
              field1,
              field2,
              field3,
            }),
          }}
        />
      ))}
    </Box>
  );
};
