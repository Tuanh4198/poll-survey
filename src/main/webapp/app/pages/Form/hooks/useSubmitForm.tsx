import { FileWithPath } from '@mantine/dropzone';
import { FormErrors, UseFormReturnType } from '@mantine/form';
import { TFormBody, TPage, TTargetDepartment, TTargetOther, TTargetSpecUser } from 'app/pages/Form/context/FormContext';
import { FormTypeEnum, handleConvertFileToBase64, hiddenFields } from 'app/pages/Form/helper';
import { prefixId } from 'app/pages/Form/hooks/useHandleUpsertComponents';
import { btnSubmitAndAssign, btnSubmitUpdate, btnSubmitUpdateAndAssign } from 'app/pages/Form/hooks/useValidateForm';
import { Routes } from 'app/pages/routes';
import { AssignStrategyMetafieldKey } from 'app/shared/model/assign-strategy.model';
import {
  ParticipantAnonymousType,
  ParticipantEmployeeTypeEnumWithFile,
} from 'app/shared/model/enumerations/participant-employee-type-enum';
import { TargetTypeEnum } from 'app/shared/model/enumerations/target-type-enum';
import { SurveyMetafieldKey } from 'app/shared/model/survey.model';
import { notiError, notiSuccess, notiWarning } from 'app/shared/notifications';
import axios from 'axios';
import _ from 'lodash';
import React from 'react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const handleUpdate = async body => {
  return await axios.put(`/api/surveys/${body.id}`, body);
};

const handleAdd = async body => {
  return await axios.post('/api/surveys', body);
};

const handleUpdateTemplate = async body => {
  return await axios.put(`/api/survey-templates/${body.id}`, body);
};

const handleAddTemplate = async body => {
  return await axios.post('/api/survey-templates', body);
};

const handleAssign = async (id: string | number) => {
  return await axios.post(`/api/surveys/${id}/assign`);
};

const handleUploadFile = async (apiFileUploadUrl: string, files?: FileWithPath[] | File[]) => {
  if (files && files.length > 0) {
    const file = files[0];
    const fileBlob = new Blob([file]);
    await axios.put(apiFileUploadUrl, fileBlob, {
      headers: {
        'Content-Type': file.type,
      },
    });
  }
};

interface UseSubmitFormProps {
  initialValues?: TFormBody;
  onCustomValidate: (formErrors?: FormErrors) => FormErrors | undefined;
  targetSpecUser?: TTargetSpecUser;
  targetDepartment?: TTargetDepartment;
  targetOther?: TTargetOther;
  files: FileWithPath[] | undefined;
  form: UseFormReturnType<TFormBody, (values: TFormBody) => TFormBody>;
  pages: TPage;
  onRefetch?: () => void;
  type?: FormTypeEnum;
}
export const useSubmitForm = ({
  initialValues,
  onCustomValidate,
  targetSpecUser,
  targetDepartment,
  targetOther,
  files,
  form,
  pages,
  onRefetch,
  type,
}: UseSubmitFormProps) => {
  const navigate = useNavigate();

  const [submiting, setSubmiting] = useState(false);

  const targetsBuilder = async (target?: TargetTypeEnum) => {
    switch (target) {
      case TargetTypeEnum.SPEC_USERS: {
        const newPs: any = [];
        if (targetSpecUser?.participants && targetSpecUser?.participants?.length > 0) {
          for (const p of targetSpecUser.participants) {
            const newP: any = {
              fileParticipant: {
                base64:
                  p.type === ParticipantEmployeeTypeEnumWithFile.SPEC_USERS && p.file ? await handleConvertFileToBase64(p.file) : null,
              },
              fileTargets: {
                base64: targetSpecUser?.file ? await handleConvertFileToBase64(targetSpecUser?.file) : null,
              },
              metafields: [
                {
                  key: AssignStrategyMetafieldKey.participants,
                  value: p.type === ParticipantEmployeeTypeEnumWithFile.SPEC_USERS ? p.value : '',
                  type: p.type,
                },
                {
                  key: AssignStrategyMetafieldKey.targets,
                  value: targetSpecUser?.value,
                  type: TargetTypeEnum.SPEC_USERS,
                },
              ],
            };
            if (!newP.fileParticipant.base64) delete newP.fileParticipant;
            if (!newP.fileTargets.base64) delete newP.fileTargets;
            newPs.push(newP);
          }
        }
        return newPs;
      }
      case TargetTypeEnum.DEPARTMENT:
        return targetDepartment?.participants
          ? Object.entries(targetDepartment.participants)
              .filter(([__, value]) => value)
              .map(([key, __]) => ({
                metafields: [
                  {
                    key: AssignStrategyMetafieldKey.participants,
                    value: '',
                    type: key,
                  },
                  {
                    key: AssignStrategyMetafieldKey.targets,
                    value: targetDepartment.value,
                    type: TargetTypeEnum.DEPARTMENT,
                  },
                ],
              }))
          : [];
      case TargetTypeEnum.OTHER: {
        const newP: any = {
          fileParticipant: {
            base64: await handleConvertFileToBase64(targetOther?.participants?.file),
          },
          metafields: [
            {
              key: AssignStrategyMetafieldKey.participants,
              value: targetOther?.participants?.value,
              type:
                targetOther?.participants?.value === ParticipantAnonymousType.ANONYMOUS
                  ? ParticipantAnonymousType.ANONYMOUS
                  : ParticipantEmployeeTypeEnumWithFile.SPEC_USERS,
            },
            {
              key: AssignStrategyMetafieldKey.targets,
              value: targetOther?.value,
              type: TargetTypeEnum.OTHER,
            },
          ],
        };
        if (targetOther?.participants?.value && !targetOther?.participants?.file && !newP.fileParticipant.base64) {
          delete newP.fileParticipant;
        }
        return [newP];
      }
      default:
        return [];
    }
  };

  const blockBuilder = () => {
    return Object.values(pages)
      .flat()
      .map(({ id, pageId, ...rest }) => {
        rest.blockFields = rest.blockFields?.map(bf => {
          if (bf.file && bf.urlFileUpload) {
            handleUploadFile(bf.urlFileUpload, [bf.file]);
          }
          if (bf.id?.toString()?.startsWith(prefixId)) {
            return _.omit(bf, ['id', 'file', 'urlFileUpload']);
          }
          return bf;
        });
        if (id?.toString()?.startsWith(prefixId)) {
          return rest;
        }
        return { id, ...rest };
      });
  };

  const bodyBuilder = async (value: TFormBody, isDeletedId?: boolean) => {
    const body: any = {
      ...value,
      isRequired: !!value.isRequired,
      thumbUrl: value?.thumbUrl || '',
      metafields: [
        ...hiddenFields,
        {
          key: SurveyMetafieldKey.events,
          value: value.events,
        },
      ],
      blocks: blockBuilder(),
      assignStrategies: await targetsBuilder(value.targetType),
    };
    if (isDeletedId) {
      body.tempId = value.id;
      delete body.id;
    }
    // delete when submit
    delete body.presignThumbUrl;
    delete body.thumbUrlUpload;
    delete body.events;
    delete body.targetType;
    return body;
  };

  // save draff and asign
  const onSubmit = async (value: TFormBody, event) => {
    const respomCustomValidate = onCustomValidate();
    if (respomCustomValidate) {
      notiWarning({ message: 'Vui lòng kiểm tra lại thông tin các block trong form, và điền đủ các thông tin của khảo sát' });
      return;
    }
    if (value.thumbUrlUpload) {
      handleUploadFile(value.thumbUrlUpload, files);
    }
    const body = await bodyBuilder(value, !!(type === FormTypeEnum.TEMPLATE && initialValues));
    setSubmiting(true);
    try {
      let res;
      if (
        initialValues &&
        (event.nativeEvent.submitter.id === btnSubmitUpdate || event.nativeEvent.submitter.id === btnSubmitUpdateAndAssign)
      ) {
        res = await handleUpdate(body);
        onRefetch && onRefetch();
      } else {
        res = await handleAdd(body);
      }
      const newId = res?.data?.id;
      if ((event.nativeEvent.submitter.id === btnSubmitAndAssign || event.nativeEvent.submitter.id === btnSubmitUpdateAndAssign) && newId) {
        await handleAssign(newId);
        notiSuccess({ message: `${initialValues ? 'Cập nhật' : 'Tạo'} khảo sát và giao thành công.` });
      } else {
        notiSuccess({ message: `${initialValues ? 'Cập nhật' : 'Tạo'} khảo sát thành công.` });
      }
      if (!initialValues || body.tempId) navigate(`${Routes.SURVEY}/${newId}`);
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
    setSubmiting(false);
  };

  // save without validate
  const onSubmitWithoutValidation = async (id?: string) => {
    form.clearErrors();
    if (form.values?.thumbUrlUpload) {
      handleUploadFile(form.values?.thumbUrlUpload, files);
    }
    const body = await bodyBuilder(form.values);
    setSubmiting(true);
    try {
      let res;
      if (initialValues && id === btnSubmitUpdate) {
        res = await handleUpdateTemplate(body);
        onRefetch && onRefetch();
      } else {
        res = await handleAddTemplate(body);
      }
      notiSuccess({ message: `${initialValues ? 'Cập nhật' : 'Tạo'} khảo sát mẫu thành công.` });
      const newId = res?.data?.id;
      if (!initialValues) navigate(`${Routes.TEMPLATE}/${newId}`);
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
    setSubmiting(false);
  };

  return {
    form,
    submiting,
    onCustomValidate,
    onSubmit,
    onSubmitWithoutValidation,
  };
};
