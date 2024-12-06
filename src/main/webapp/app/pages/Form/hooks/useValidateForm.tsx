import { FormErrors, UseFormReturnType } from '@mantine/form';
import { TFormBody, TPage, TTargetDepartment, TTargetOther, TTargetSpecUser } from 'app/pages/Form/context/FormContext';
import {
  validateBlockOnPage,
  validateBlockTitle,
  validateBlockTypeMultipleChoice,
  validateTargetSpecUser,
  validateTargetTypeDepartment,
  validateTargetTypeOther,
} from 'app/pages/Form/helper';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { TargetTypeEnum } from 'app/shared/model/enumerations/target-type-enum';
import _ from 'lodash';
import React, { useEffect, useRef } from 'react';
import { useState } from 'react';

export const btnSubmitTemplate = 'btnSubmitTemplate';
export const btnSubmitAndAssign = 'btnSubmitAndAssign';
export const btnSubmitUpdateAndAssign = 'btnSubmitUpdateAndAssign';
export const btnSubmitUpdate = 'btnSubmitUpdate';
export const btnSubmit = 'btnSubmit';
export const errorBlockLabel = 'BLOCK';
export const errorTargetLabel = 'TARGET';
export const fieldValidate = {
  title: 'title',
  description: 'description',
};

export const errorEachBlockLabel = ({
  id,
  componentType,
  errType,
}: {
  id?: string;
  componentType?: ComponentTypeEnum;
  errType?: string;
}) => {
  if (!id || !componentType) '';
  return `${errorBlockLabel}_${componentType}_${id}${errType ? `_${errType}` : ''}`;
};

interface UseValidateFormProps {
  form: UseFormReturnType<TFormBody, (values: TFormBody) => TFormBody>;
  pages: TPage;
  setApplyTime: React.Dispatch<React.SetStateAction<Date | undefined>>;
  setEndTime: React.Dispatch<React.SetStateAction<Date | undefined>>;
  targetSpecUser?: TTargetSpecUser;
  targetDepartment?: TTargetDepartment;
  targetOther?: TTargetOther;
  setTargetType: React.Dispatch<React.SetStateAction<TargetTypeEnum | undefined>>;
  isAnonymous: boolean;
}
export const useValidateForm = ({
  form,
  pages,
  setApplyTime,
  setEndTime,
  targetSpecUser,
  targetDepartment,
  targetOther,
  setTargetType,
  isAnonymous,
}: UseValidateFormProps) => {
  const pagesRef = useRef<TPage>(pages);

  useEffect(() => {
    pagesRef.current = pages;
  }, [pages]);

  form.watch('targetType', ({ value }) => {
    setTargetType(value as TargetTypeEnum);
  });

  form.watch('applyTime', ({ value }) => {
    setApplyTime(value);
  });

  form.watch('endTime', ({ value }) => {
    setEndTime(value);
  });

  const onCustomValidate = (formErrors?: FormErrors) => {
    let errors: FormErrors | undefined = undefined;
    if (formErrors) errors = structuredClone(formErrors);
    if (form.errors && Object.keys(form.errors)?.length > 0) errors = { ...errors, ...form.errors };
    // validate block exist on per page
    validateBlockOnPage({
      pages: pagesRef.current,
      onError() {
        console.error('pagesRef.current: ', pagesRef.current);
        console.error('pages: ', pages);
        errors = {
          ...(errors ? errors : {}),
          [errorBlockLabel]: 'Form chưa tồn tại block.',
        };
      },
    });
    // validate target
    switch (form.getValues().targetType) {
      case TargetTypeEnum.OTHER:
        validateTargetTypeOther({
          isAnonymous,
          targetOther,
          onError() {
            errors = {
              ...(errors ? errors : {}),
              [errorTargetLabel]: TargetTypeEnum.OTHER,
            };
          },
        });
        break;
      case TargetTypeEnum.DEPARTMENT:
        validateTargetTypeDepartment({
          targetDepartment,
          onError() {
            errors = {
              ...(errors ? errors : {}),
              [errorTargetLabel]: TargetTypeEnum.DEPARTMENT,
            };
          },
        });
        break;
      case TargetTypeEnum.SPEC_USERS:
        validateTargetSpecUser({
          targetSpecUser,
          onError() {
            errors = {
              ...(errors ? errors : {}),
              [errorTargetLabel]: TargetTypeEnum.SPEC_USERS,
            };
          },
        });
        break;
      default:
        return;
    }
    // vaidate each block
    Object.values(pagesRef.current)
      .flat()
      .forEach(b => {
        // validate block title
        validateBlockTitle({
          blockFields: b.blockFields,
          onError() {
            errors = {
              ...(errors ? errors : {}),
              [errorEachBlockLabel({ id: b?.id?.toString(), componentType: b.type, errType: fieldValidate.title })]: 'Chưa nhập tiêu đề',
            };
          },
        });
        // validate block description
        switch (b.type) {
          case ComponentTypeEnum.MULTIPLE_CHOICE:
            validateBlockTypeMultipleChoice({
              blockFields: b.blockFields,
              onError() {
                errors = {
                  ...(errors ? errors : {}),
                  [errorEachBlockLabel({ id: b?.id?.toString(), componentType: b.type, errType: fieldValidate.description })]:
                    'Chưa nhập mô tả cho lựa chọn',
                };
              },
            });
            break;
          case ComponentTypeEnum.POINT_SCALE:
            // TODO
            break;
          case ComponentTypeEnum.ESSAY:
            // TODO
            break;
          case ComponentTypeEnum.TITLE:
            // TODO
            break;
          case ComponentTypeEnum.STAR:
            // TODO
            break;
          default:
            return;
        }
      });
    if (errors) form.setErrors(errors);
    console.error('errors: ', errors);
    return errors;
  };

  return {
    form,
    onCustomValidate,
  };
};
