import { TBlock } from 'app/pages/Form/context/FormContext';
import { useFetchProgress } from 'app/pages/Form/hooks/useFetchProgress';
import { BlockMetafieldKey, BlockMetafieldRequiredValue, IBlock } from 'app/shared/model/block.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { SurveyStatusEnum } from 'app/shared/model/enumerations/survey-status-enum.model';
import { notiError, notiSuccess, notiWarning } from 'app/shared/notifications';
import axios from 'axios';
import React from 'react';
import { useEffect, useRef, useState } from 'react';

interface FieldSubmit {
  blockId?: number;
  type?: ComponentTypeEnum;
  fieldId: number | null;
  fieldName: string | null;
  fieldValue: string | null;
  fieldType: FieldTypeEnum | null;
}

export const useHandleSubmitForm = ({ id, blocks }: { id: string | undefined; blocks: TBlock[] | undefined }) => {
  const { isLoading, data } = useFetchProgress({
    surveyId: id,
    defaultStatus: SurveyStatusEnum.NOT_ATTENDED,
  });

  const formValueRef = useRef<{ [key: number]: FieldSubmit }>();

  const [errors, setErrors] = useState<(number | null)[]>([]);

  const [submitting, setSubmitting] = useState<boolean>(false);

  useEffect(() => {
    if (blocks && formValueRef.current == null) {
      formValueRef.current = blocks?.reduce((acc, item) => {
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
    if (!data || data?.length <= 0) {
      notiError({ message: `Cái danh sách giao hết rồi, vào DB đổi trạng thái sang NOT_ATTENDED để test tiếp nhé, survey_id=${id}` });
      return;
    }
    if (!blocks || !id || !formValueRef.current) return;
    const _errors = onValidate(Object.values(blocks));
    if (_errors.length > 0) {
      notiWarning({ message: 'Kiểm tra lại các câu hỏi và điền đầy đủ câu trả lời' });
      return;
    }
    setSubmitting(true);
    try {
      await axios.post(`/api/employee-surveys/${data?.[0]?.id}/submit`, {
        fieldSubmits: Object.values(formValueRef.current),
      });
      notiSuccess({
        message: 'Gửi bài khảo sát thành công',
      });
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
    submitting: submitting || isLoading,
    formValueRef,
    errors,
    onChangeFieldValue,
    onValidate,
    onSubmit,
  };
};
