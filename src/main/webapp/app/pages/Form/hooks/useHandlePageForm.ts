import { UseFormReturnType } from '@mantine/form';
import { TFormBody, TPage } from 'app/pages/Form/context/FormContext';
import { validateBlockOnPage } from 'app/pages/Form/helper';
import { errorBlockLabel } from 'app/pages/Form/hooks/useValidateForm';
import { useState } from 'react';
import { v4 as uuidv4 } from 'uuid';

export const useHandlePageForm = ({
  defaultPage,
  setPages,
  form,
}: {
  defaultPage: string;
  setPages: React.Dispatch<React.SetStateAction<TPage>>;
  form?: UseFormReturnType<TFormBody, (values: TFormBody) => TFormBody>;
}) => {
  const [activePage, setActivePage] = useState<string>(defaultPage);

  const onAddPage = () => {
    const newId = uuidv4();
    setPages(oldValue => ({
      ...oldValue,
      [newId]: [],
    }));
    setActivePage(newId);
  };

  const onRemovePage = (id: string) => {
    setPages(({ [id]: _, ...rest }) => {
      validateBlockOnPage({
        pages: rest,
        onOk() {
          form && form.clearFieldError(errorBlockLabel);
        },
      });
      if (id === activePage) setActivePage(Object.keys(rest)?.[0]);
      return rest;
    });
  };

  return {
    activePage,
    setActivePage,
    onAddPage,
    onRemovePage,
  };
};
