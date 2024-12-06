import { TBlockField, TPage, TTargetDepartment, TTargetOther, TTargetSpecUser } from 'app/pages/Form/context/FormContext';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { ParticipantEmployeeTypeEnumWithFile } from 'app/shared/model/enumerations/participant-employee-type-enum';
import { ParticipantDepartmentTypeEnum } from 'app/shared/model/enumerations/articipant-department-type-enum.model';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';
import { TargetTypeEnum } from 'app/shared/model/enumerations/target-type-enum';
import { ComboboxData } from '@mantine/core';
import { hiddenField1, hiddenField2, hiddenField3, SurveyMetafieldKey } from 'app/shared/model/survey.model';

dayjs.extend(utc);
dayjs.extend(timezone);

export const hiddenFields = [
  {
    key: hiddenField1,
    value: '',
  },
  {
    key: hiddenField2,
    value: '',
  },
  {
    key: hiddenField3,
    value: '',
  },
];

export const separatorCharacter = '||';

export const regEmployeeCode = /^(yd|fgg|dl|ht|mt)\d{4,}$/i;

export enum FormTypeEnum {
  SURVEY = 'SURVEY',
  TEMPLATE = 'TEMPLATE',
}

export const getTargetOption = ({
  targetType,
  targetSpecUser,
  targetDepartment,
  targetOther,
}: {
  targetType: TargetTypeEnum | undefined;
  targetSpecUser: TTargetSpecUser | undefined;
  targetDepartment: TTargetDepartment | undefined;
  targetOther: TTargetOther | undefined;
}): ComboboxData => {
  switch (targetType) {
    case TargetTypeEnum.SPEC_USERS: {
      if (targetSpecUser?.value) {
        return targetSpecUser.value.split(separatorCharacter).map(i => ({
          label: i,
          value: i,
        }));
      }
      return [];
    }
    case TargetTypeEnum.OTHER: {
      if (targetOther?.value) {
        return [
          {
            label: targetOther?.value,
            value: targetOther?.value,
          },
        ];
      }
      return [];
    }
    case TargetTypeEnum.DEPARTMENT: {
      if (targetDepartment?.value) {
        return targetDepartment.value.split(separatorCharacter).map(i => ({
          label: i,
          value: i,
        }));
      }
      return [];
    }
    default: {
      return [];
    }
  }
};

export const buildCondition = (condition: { [key: string]: string | number | undefined | null }) => {
  const queryString = Object.entries(condition)
    .filter(([_, value]) => value != null && value !== '')
    .map(([key, value]) => value && `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    .join('&');
  return queryString;
};

export const getRandomDarkColor = () => {
  const r = Math.floor(Math.random() * 256);
  const g = Math.floor(Math.random() * 256);
  const b = Math.floor(Math.random() * 256);
  const color = `rgb(${r}, ${g}, ${b})`;
  return color;
};

export const compareTimeUtcWithLocalTime = (utcTime?: Date) => {
  if (!utcTime) return false;
  const utcDate = dayjs.utc(utcTime);
  const localTime = utcDate.tz('Asia/Bangkok');
  const currentLocalTime = dayjs().tz('Asia/Bangkok');
  return localTime.isBefore(currentLocalTime);
};

const convertFileToBase64 = (file): Promise<string | null> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onloadend = () => {
      const result = reader.result as string;
      const base64String = result.split(',')[1];
      resolve(base64String);
    };
    reader.onerror = () => {
      reject(new Error('Error reading file'));
    };
    reader.readAsDataURL(file);
  });
};

export const handleConvertFileToBase64 = async file => {
  try {
    const base64String = await convertFileToBase64(file);
    return base64String;
  } catch (error) {
    console.error('Error converting file to base64:', error);
  }
};

export const validateBlockOnPage = ({ pages, onError, onOk }: { pages: TPage; onError?: () => void; onOk?: () => void }) => {
  if (Object.values(pages).length <= 0 || Object.values(pages).some(values => !values || values.length <= 0)) {
    onError && onError();
  } else {
    onOk && onOk();
  }
};

export const validateBlockTitle = ({ blockFields, onError }: { blockFields?: TBlockField[]; onError: () => void }) => {
  blockFields?.forEach(bf => {
    if (
      bf.type === FieldTypeEnum.TEXT &&
      bf.fieldName === ComponentTypeEnum.TITLE.toLowerCase() &&
      (bf.fieldValue == null || bf.fieldValue === '')
    ) {
      onError();
      return;
    }
  });
};

export const validateBlockTypeMultipleChoice = ({ blockFields, onError }: { blockFields?: TBlockField[]; onError: () => void }) => {
  blockFields?.forEach(bf => {
    if (bf.type === FieldTypeEnum.MULTIPLE_CHOICE_OPTION && (bf.fieldValue == null || bf.fieldValue === '')) {
      onError();
      return;
    }
  });
};

export const validateTargetTypeOther = ({
  isAnonymous,
  targetOther,
  onError,
  onOk,
}: {
  isAnonymous: boolean;
  targetOther?: TTargetOther;
  onError?: () => void;
  onOk?: () => void;
}) => {
  if (
    targetOther?.value == null ||
    targetOther?.value === '' ||
    (!isAnonymous && !targetOther?.participants?.file && !targetOther?.participants?.value)
  ) {
    onError && onError();
  } else {
    onOk && onOk();
  }
};

export const validateTargetTypeDepartment = ({
  targetDepartment,
  onError,
  onOk,
}: {
  targetDepartment?: TTargetDepartment;
  onError?: () => void;
  onOk?: () => void;
}) => {
  if (
    (!targetDepartment?.participants?.[ParticipantDepartmentTypeEnum.EMPLOYEE_SAME_DEPARTMENT] &&
      !targetDepartment?.participants?.[ParticipantDepartmentTypeEnum.EMPLOYEE_OTHER_DEPARTMENT]) ||
    targetDepartment.value == null ||
    targetDepartment.value === ''
  ) {
    onError && onError();
  } else {
    onOk && onOk();
  }
};

export const validateTargetSpecUser = ({
  targetSpecUser,
  onError,
  onOk,
}: {
  targetSpecUser?: TTargetSpecUser;
  onError?: () => void;
  onOk?: () => void;
}) => {
  let isErr = false;
  if (!targetSpecUser?.file && (targetSpecUser?.value == null || targetSpecUser?.value === '')) {
    isErr = true;
  } else if (!targetSpecUser?.participants || targetSpecUser?.participants?.length <= 0) {
    isErr = true;
  } else if (targetSpecUser?.participants?.some(i => i.type === ParticipantEmployeeTypeEnumWithFile.SPEC_USERS && !i.file && !i.value)) {
    isErr = true;
  }
  // callback
  if (isErr) {
    onError && onError();
  } else {
    onOk && onOk();
  }
};
