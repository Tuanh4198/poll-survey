import { Button } from '@mantine/core';
import { Eye } from '@phosphor-icons/react';
import { FormContext } from 'app/pages/Form/context/FormContext';
import { FormTypeEnum } from 'app/pages/Form/helper';
import { useModalPrevewWithoutSubmit } from 'app/pages/Preview';
import React, { useContext } from 'react';
import { Fragment } from 'react';

export const SectionPreview = ({ type }: { type?: FormTypeEnum }) => {
  const { files, form, pages } = useContext(FormContext);

  const { onOpenModalPrevewWithoutSubmit, renderModalPrevewWithoutSubmit } = useModalPrevewWithoutSubmit();

  const data = form?.values;

  return (
    <Fragment>
      <Button
        onClick={onOpenModalPrevewWithoutSubmit}
        variant="outline"
        leftSection={<Eye size={16} />}
        color="#111928"
        bd="1px solid #E5E7EB"
      >
        Xem trước
      </Button>
      {renderModalPrevewWithoutSubmit({
        files,
        pages,
        type,
        data: {
          id: data?.id,
          presignThumbUrl: data?.presignThumbUrl,
          isRequired: data?.isRequired,
          title: data?.title,
          description: data?.description,
        },
      })}
    </Fragment>
  );
};
