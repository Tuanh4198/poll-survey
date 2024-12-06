import { TBlock } from 'app/pages/Form/context/FormContext';
import { BlockMetafieldKey, BlockMetafieldRequiredValue, IBlock } from 'app/shared/model/block.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { notiError, notiSuccess, notiWarning } from 'app/shared/notifications';
import axios from 'axios';
import React from 'react';
import { useEffect, useRef, useState } from 'react';

export interface FieldSubmit {
  blockId?: number;
  type?: ComponentTypeEnum;
  fieldId: number | null;
  fieldName: string | null;
  fieldValue: string | null;
  fieldType: FieldTypeEnum | null;
}

export const useHandleSubmitFormPublic = ({ hash, blocks }: { hash: string | undefined; blocks: TBlock[] | undefined }) => {
  const formValueRef = useRef<{ [key: number]: FieldSubmit }>();

  const [fieldsFilled, setFieldsFilled] = useState<Array<string | number>>([]);

  const [errors, setErrors] = useState<(number | null)[]>([]);

  const [submitting, setSubmitting] = useState<boolean>(false);

  const [submited, setSubmited] = useState<boolean>(false);

  useEffect(() => {
    if (blocks && formValueRef.current == null) {
      formValueRef.current = blocks
        ?.filter(b => b.type !== ComponentTypeEnum.TITLE)
        ?.reduce((acc, item) => {
          const { id: blockId } = item;
          if (!acc[blockId as number]) {
            acc[blockId as number] = [];
          }
          acc[blockId as number] = {
            blockId: Number(item.id),
            type: item.type,
            fieldId: null,
            fieldName: null,
            fieldValue: null,
            fieldType: null,
          };
          return acc;
        }, {});
    }
  }, [blocks]);

  const onChangeFieldValue = ({
    blockId,
    fieldId,
    fieldName,
    fieldValue,
    fieldType,
  }: {
    blockId?: number;
    fieldId: number | null;
    fieldName: string | null;
    fieldValue: string | null;
    fieldType: FieldTypeEnum | null;
  }) => {
    console.log(
      'blockId: ',
      blockId,
      ' fieldId: ',
      fieldId,
      ' fieldName: ',
      fieldName,
      ' fieldValue: ',
      fieldValue,
      ' fieldType: ',
      fieldType
    );
    if (formValueRef.current && blockId && formValueRef.current[blockId]) {
      formValueRef.current = {
        ...formValueRef.current,
        [blockId]: {
          ...formValueRef.current[blockId],
          fieldId,
          fieldName,
          fieldValue,
          fieldType,
        },
      };
      const _fieldsFilled = Object.entries(formValueRef.current)
        .filter(([_, value]) => value.fieldValue != null && value.fieldValue !== '')
        .map(([key, _]) => key);
      setFieldsFilled(_fieldsFilled);
    }
  };

  const onValidate = (_blocks: IBlock[]) => {
    const _errors: (number | null)[] = [];
    _blocks
      .filter(b => b.type !== ComponentTypeEnum.TITLE)
      .forEach(b => {
        const required = b.metafields?.find(mf => mf.key === BlockMetafieldKey.required)?.value;
        if (required === BlockMetafieldRequiredValue.true && formValueRef.current && b.id && formValueRef.current[b.id]) {
          const _block = formValueRef.current[Number(b.id)];
          if (_block.fieldValue === '' || _block.fieldValue == null) {
            _errors.push(_block.blockId as number);
          }
        }
      });
    setErrors(_errors);
    return _errors;
  };

  const onSubmit = async () => {
    if (!blocks || !hash || !formValueRef.current) return;
    const _errors = onValidate(Object.values(blocks));
    console.log('_errors: ', _errors);
    console.log('formValueRef.current: ', formValueRef.current);
    if (_errors.length > 0) {
      notiWarning({ message: 'Kiểm tra lại các câu hỏi và điền đầy đủ câu trả lời' });
      return;
    }
    const fieldSubmits = Object.values(formValueRef.current).filter(i => i.fieldId != null && i.fieldValue != null);
    console.log('fieldSubmits: ', fieldSubmits);
    setSubmitting(true);
    try {
      await axios.post(
        `/api/public/surveys/${hash}/submit`,
        {
          fieldSubmits,
        },
        {
          headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json, text/plain, */*',
          },
        }
      );
      notiSuccess({
        message: 'Gửi bài khảo sát thành công',
      });
      setSubmited(true);
    } catch (error: any) {
      console.error('Create error: ', error);
      const errMsg = error?.response?.data?.errors?.message?.[0];
      notiError({
        message: (
          <>
            Gửi khảo sát thất bại, vui lòng thử lại sau! <br />
            {errMsg}
          </>
        ),
      });
    }
    setSubmitting(false);
  };

  return {
    submited,
    submitting,
    formValueRef,
    errors,
    fieldsFilled,
    onChangeFieldValue,
    onValidate,
    onSubmit,
  };
};
