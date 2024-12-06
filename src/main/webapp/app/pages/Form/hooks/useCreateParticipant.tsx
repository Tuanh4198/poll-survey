import { FormContext } from 'app/pages/Form/context/FormContext';
import { regEmployeeCode } from 'app/pages/Form/helper';
import { notiError, notiSuccess } from 'app/shared/notifications';
import axios from 'axios';
import { debounce } from 'lodash';
import React from 'react';
import { useContext, useRef, useState } from 'react';

interface IFormAddParticipant {
  code?: string | null;
  target?: string | null;
}

const formAddParticipantDefault = { code: undefined, target: undefined };

export const useCreateParticipant = ({ refetch }: { refetch: () => void }) => {
  const { form, targetType } = useContext(FormContext);

  const formAddParticipantRef = useRef<IFormAddParticipant>(structuredClone(formAddParticipantDefault));

  const [addParticipant, setAddParticipant] = useState(false);
  const [adding, setAdding] = useState(false);
  const [formAddParticipantError, setFormAddParticipantError] = useState<IFormAddParticipant>(formAddParticipantDefault);

  const validateCode = (code?: string | null) => {
    if (!code) {
      return 'Đối tượng nhận đánh giá bắt buộc nhập';
    } else if (code && !regEmployeeCode.test(code)) {
      return 'Đối tượng nhận đánh giá sai định dạng (Ví dụ: YD12345)';
    }
    return undefined;
  };

  const validateOnChangeCode = debounce((value: string) => {
    const _validateCode = validateCode(value);
    if (_validateCode) {
      setFormAddParticipantError(o => ({
        ...o,
        code: _validateCode,
      }));
    } else {
      setFormAddParticipantError(o => ({
        ...o,
        code: undefined,
      }));
    }
  }, 500);

  const validateOnChangeTarget = (value: string | null) => {
    formAddParticipantRef.current.target = value;
    if (value) {
      setFormAddParticipantError(o => ({
        ...o,
        target: undefined,
      }));
    }
  };

  const onSubmit = async () => {
    if (!form?.values?.id) return;
    let _formAddParticipantError: IFormAddParticipant | undefined = undefined;
    const _validateCode = validateCode(formAddParticipantRef.current.code);
    if (_validateCode) {
      _formAddParticipantError = {
        ...(_formAddParticipantError || {}),
        code: _validateCode,
      };
    }
    if (!formAddParticipantRef.current.target) {
      _formAddParticipantError = {
        ...(_formAddParticipantError || {}),
        target: 'Đối tượng được đánh giá bắt buộc chọn',
      };
    }
    if (_formAddParticipantError) {
      setFormAddParticipantError(_formAddParticipantError);
      return;
    }
    const body = {
      code: formAddParticipantRef.current.code?.toLocaleUpperCase(),
      surveyId: form?.values?.id,
      target: formAddParticipantRef.current.target,
      targetType,
    };
    setAdding(true);
    try {
      await axios.post(`/api/employee-surveys`, body);
      notiSuccess({ message: 'Thêm đối tượng nhận đánh giá thành công' });
      formAddParticipantRef.current = structuredClone(formAddParticipantDefault);
      setAddParticipant(false);
      refetch();
    } catch (error: any) {
      console.error('Create error: ', error);
      const errMsg = error?.response?.data?.errors?.message?.[0];
      notiError({
        message: (
          <>
            Tạo thất bại, vui lòng thử lại sau! <br />
            {errMsg}
          </>
        ),
      });
    }
    setAdding(false);
  };

  const onCacel = () => {
    formAddParticipantRef.current = structuredClone(formAddParticipantDefault);
    setAddParticipant(false);
    setFormAddParticipantError(formAddParticipantDefault);
  };

  return {
    formAddParticipantRef,
    addParticipant,
    adding,
    formAddParticipantError,
    validateOnChangeCode,
    setAddParticipant,
    validateOnChangeTarget,
    onSubmit,
    onCacel,
  };
};
