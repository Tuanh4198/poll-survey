import React, { createContext, ReactElement, useMemo, useState } from 'react';
import { FormErrors, isNotEmpty, useForm, UseFormReturnType } from '@mantine/form';
import { FileWithPath } from '@mantine/dropzone';
import { useChooseFile } from '../hooks/useChooseFile';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { IMetafield } from 'app/shared/model/metafields.model';
import { useHandleUpsertComponents } from 'app/pages/Form/hooks/useHandleUpsertComponents';
import { IBlockFields } from 'app/shared/model/block-fields.model';
import { IBlock } from 'app/shared/model/block.model';
import { AssignStrategyMetafieldKey } from 'app/shared/model/assign-strategy.model';
import { EventValue, EventValueOther, SurveyMetafieldKey } from 'app/shared/model/survey.model';
import { useValidateForm } from 'app/pages/Form/hooks/useValidateForm';
import { useHandlePageForm } from 'app/pages/Form/hooks/useHandlePageForm';
import { v4 as uuidv4 } from 'uuid';
import { TargetTypeEnum } from 'app/shared/model/enumerations/target-type-enum';
import { useSubmitForm } from 'app/pages/Form/hooks/useSubmitForm';
import { ParticipantDepartmentTypeEnum } from 'app/shared/model/enumerations/articipant-department-type-enum.model';
import {
  ParticipantEmployeeTypeEnumWithFile,
  ParticipantEmployeeTypeEnumWithoutFile,
} from 'app/shared/model/enumerations/participant-employee-type-enum';
import { compareTimeUtcWithLocalTime, FormTypeEnum } from 'app/pages/Form/helper';
import { useHandleChangeAnonymous } from 'app/pages/Form/hooks/useHandleChangeAnonymous';

export type TBlockField = IBlockFields;

export type TBlock = IBlock & {
  pageId: string;
  blockFields?: TBlockField[];
};

export type TAssignStrategy = {
  metafields: Array<IMetafield<AssignStrategyMetafieldKey>>;
};

export type TFormBody = {
  id?: number;
  title?: string;
  description?: string;
  thumbUrl?: string;
  applyTime?: Date;
  endTime?: Date;
  isRequired?: boolean;
  metafields?: Array<IMetafield<SurveyMetafieldKey, EventValue | EventValueOther>>;
  blocks?: Array<TBlock>;
  assignStrategies?: Array<TAssignStrategy>;
  // delete when submit
  presignThumbUrl?: string;
  thumbUrlUpload?: string;
  events?: EventValue | EventValueOther;
  targetType?: TargetTypeEnum;
};

export type TPage = { [key: string]: TBlock[] };

export type TTargetSpecUser = {
  value?: string; // Survey employees
  file?: string | FileWithPath;
  participants?: Array<
    | {
        id: string | number;
        type?: ParticipantEmployeeTypeEnumWithFile;
        file?: string | FileWithPath;
        value?: string;
      }
    | {
        id: string | number;
        type?: ParticipantEmployeeTypeEnumWithoutFile;
      }
  >;
};

export type TTargetDepartment = {
  value?: string; // Survey departments
  participants?: {
    [ParticipantDepartmentTypeEnum.EMPLOYEE_SAME_DEPARTMENT]: boolean;
    [ParticipantDepartmentTypeEnum.EMPLOYEE_OTHER_DEPARTMENT]: boolean;
  };
};

export type TTargetOther = {
  value?: string; // Survey subjects
  participants?: {
    file?: string | FileWithPath;
    value?: string;
  };
};

export type TTarget = {
  [TargetTypeEnum.SPEC_USERS]: TTargetSpecUser | undefined;
  [TargetTypeEnum.DEPARTMENT]: TTargetDepartment | undefined;
  [TargetTypeEnum.OTHER]: TTargetOther | undefined;
};

export type TFormContext = {
  isStarting: boolean;
  form?: UseFormReturnType<TFormBody, (values: TFormBody) => TFormBody>;
  errors?: FormErrors;
  submiting: boolean;
  onCustomValidate: (formErrors: FormErrors) => void;
  onSubmit: (body: TFormBody, event: any) => Promise<void>;
  onSubmitWithoutValidation: (id?: string) => Promise<void>;
  // page
  activePage: string;
  pages: TPage;
  setActivePage: React.Dispatch<React.SetStateAction<string>>;
  onAddPage: () => void;
  onRemovePage: (id: string) => void;
  // time
  applyTime?: Date;
  setApplyTime: React.Dispatch<React.SetStateAction<Date | undefined>>;
  endTime?: Date;
  setEndTime: React.Dispatch<React.SetStateAction<Date | undefined>>;
  // banner
  files: FileWithPath[] | undefined;
  setFiles: React.Dispatch<React.SetStateAction<FileWithPath[] | undefined>>;
  previewFiles?: React.JSX.Element[];
  setUrlFile: React.Dispatch<React.SetStateAction<string[] | undefined>>;
  // blocks
  onAddBlock: ({ num, pageNum, pageId, type }: { num: number; pageNum: number; pageId: string; type: ComponentTypeEnum }) => void;
  onDuplicateBlock: ({ id, pageId }: { id?: string | number; pageId: string }) => void;
  onReorderBlock: ({ destinationIndex, sourceIndex, pageId }: { destinationIndex: number; sourceIndex: number; pageId: string }) => void;
  onAddFieldImage: ({ id, pageId, file }: { id?: string | number; pageId: string; file?: File | null }) => Promise<void>;
  onChangeFieldImage: ({ id, pageId, file }: { id?: string | number; pageId: string; file?: File | null }) => Promise<void>;
  onRemoveFieldImage: ({ id, pageId, blockFieldId }: { id?: string | number; pageId: string; blockFieldId?: string }) => void;
  onChangePointScaleOption: ({ id, pageId, value }: { id?: string | number; pageId: string; value: number }) => void;
  onChangeEssayFieldType: ({ id, pageId, type }: { id?: string | number; pageId: string; type: FieldTypeEnum }) => void;
  onAddMultipleChoiceOption: ({ id, pageId }: { id?: string | number; pageId: string }) => void;
  onRemoveMultipleChoiceOption: ({ id, pageId, blockFieldId }: { id?: string | number; pageId: string; blockFieldId: string }) => void;
  onChangeFieldRequired: ({ id, pageId, required }: { id?: string | number; pageId: string; required: boolean }) => void;
  onChangeMultipleChoiceOptionValue: ({
    id,
    pageId,
    blockFieldId,
    value,
  }: {
    id?: string | number;
    pageId: string;
    blockFieldId: string;
    value: string;
  }) => void;
  onToggleOwnAnswerOnMultipleChoice: ({ id, pageId }: { id?: string | number; pageId: string }) => void;
  onChangeDescriptionStar: ({ id, pageId, value, desc }: { id?: string | number; pageId: string; value: string; desc: string }) => void;
  onChangeFieldTitle: ({ id, pageId, title }: { id?: string | number; pageId: string; title: string }) => void;
  onRemoveBlock: ({ id, pageId, type }: { id?: string | number; pageId: string; type: ComponentTypeEnum }) => void;
  // target
  targetType?: TargetTypeEnum;
  setTargetType: React.Dispatch<React.SetStateAction<TargetTypeEnum | undefined>>;
  targetSpecUser?: TTargetSpecUser;
  setTargetSpecUser: React.Dispatch<React.SetStateAction<TTargetSpecUser | undefined>>;
  targetDepartment?: TTargetDepartment;
  setTargetDepartment: React.Dispatch<React.SetStateAction<TTargetDepartment | undefined>>;
  targetOther?: TTargetOther;
  setTargetOther: React.Dispatch<React.SetStateAction<TTargetOther | undefined>>;
  // participant anonymous
  isAnonymous: boolean;
  onChangeIsAnonymous: (event: any) => void;
};

const defaultPage = uuidv4();

export const FormContext = createContext<TFormContext>({
  isStarting: false,
  form: undefined,
  errors: undefined,
  submiting: false,
  async onSubmit() {},
  async onSubmitWithoutValidation() {},
  onCustomValidate() {},
  // page
  activePage: '',
  pages: { [defaultPage]: [] },
  setActivePage() {},
  onAddPage() {},
  onRemovePage() {},
  // time
  applyTime: undefined,
  setApplyTime() {},
  endTime: undefined,
  setEndTime() {},
  // banner
  files: undefined,
  setFiles() {},
  setUrlFile() {},
  previewFiles: undefined,
  // blocks
  onAddBlock() {},
  onDuplicateBlock() {},
  onReorderBlock() {},
  async onAddFieldImage() {},
  async onChangeFieldImage() {},
  onRemoveFieldImage() {},
  onChangePointScaleOption() {},
  onChangeEssayFieldType() {},
  onAddMultipleChoiceOption() {},
  onRemoveMultipleChoiceOption() {},
  onChangeMultipleChoiceOptionValue() {},
  onToggleOwnAnswerOnMultipleChoice() {},
  onChangeFieldRequired() {},
  onChangeDescriptionStar() {},
  onChangeFieldTitle() {},
  onRemoveBlock() {},
  // target
  targetType: undefined,
  setTargetType() {},
  targetSpecUser: undefined,
  setTargetSpecUser() {},
  targetDepartment: undefined,
  setTargetDepartment() {},
  targetOther: undefined,
  setTargetOther() {},
  // participant anonymous
  isAnonymous: false,
  onChangeIsAnonymous() {},
});

type TFormUpdateContextProps = {
  initialIsAnonymous: boolean;
  initialValues: TFormBody;
  initialTarget: TTarget;
  onRefetch: () => void;
  children: ReactElement;
  type: FormTypeEnum | undefined;
};

type TFormCreateContextProps = {
  initialValues: undefined;
  children: ReactElement;
};

type TFormContextProps = TFormCreateContextProps | TFormUpdateContextProps;

export const FormContextView = (props: TFormContextProps) => {
  const { initialValues, children } = props;

  let onRefetch: (() => void) | undefined = undefined;
  let initialIsAnonymous: boolean | undefined = undefined;
  let initialTarget: TTarget | undefined = undefined;
  let type: FormTypeEnum | undefined = undefined;
  if (initialValues) {
    onRefetch = props.onRefetch;
    type = props.type;
    initialTarget = props.initialTarget;
    initialIsAnonymous = props.initialIsAnonymous;
  }

  const initialBanner = useMemo(() => {
    if (initialValues?.presignThumbUrl) {
      return [initialValues?.presignThumbUrl];
    }
    return undefined;
  }, [initialValues?.presignThumbUrl]);

  const initialDefaultPage = useMemo(() => {
    if (initialValues?.blocks) {
      return initialValues?.blocks?.find(b => b.pageNum === 1)?.pageId;
    }
    return undefined;
  }, [initialValues?.blocks]);

  const initialPage = useMemo(() => {
    if (initialValues?.blocks && initialValues?.blocks?.length > 0) {
      return initialValues?.blocks?.reduce((acc, item) => {
        const { pageId } = item;
        if (!acc[pageId]) {
          acc[pageId] = [];
        }
        acc[pageId].push(item);
        return acc;
      }, {});
    }
    return { [defaultPage]: [] };
  }, [initialValues?.blocks]);

  const initialTargetType = useMemo(() => {
    return initialValues?.targetType;
  }, [initialValues]);

  const initialTargetSpecUser = useMemo(() => {
    if (initialTarget?.[TargetTypeEnum.SPEC_USERS]) return initialTarget?.[TargetTypeEnum.SPEC_USERS];
    return {
      value: undefined,
      participants: [
        {
          id: uuidv4(),
          type: ParticipantEmployeeTypeEnumWithoutFile.SELF,
        },
      ],
    };
  }, [initialTarget]);

  const initialTargetDepartment = useMemo(() => {
    if (initialTarget?.[TargetTypeEnum.DEPARTMENT]) return initialTarget?.[TargetTypeEnum.DEPARTMENT];
    return {
      value: undefined,
      participants: {
        [ParticipantDepartmentTypeEnum.EMPLOYEE_SAME_DEPARTMENT]: false,
        [ParticipantDepartmentTypeEnum.EMPLOYEE_OTHER_DEPARTMENT]: false,
      },
    };
  }, [initialTarget]);

  const initialTargetOther = useMemo(() => {
    if (initialTarget?.[TargetTypeEnum.OTHER]) return initialTarget?.[TargetTypeEnum.OTHER];
    return {
      value: undefined,
      participants: {
        file: undefined,
      },
    };
  }, [initialTarget]);

  const form = useForm<TFormBody>({
    mode: 'controlled',
    initialValues,
    validate: {
      title: isNotEmpty('Tiêu đề bắt buộc nhập.'),
      applyTime: isNotEmpty('Thời gian bắt đầu bắt buộc chọn.'),
      events: isNotEmpty('Trường hợp khảo sát bắt buộc chọn.'),
      targetType: isNotEmpty('Đối tượng khảo sát bắt buộc chọn.'),
    },
  });

  const [pages, setPages] = useState<TPage>(initialPage);

  const [applyTime, setApplyTime] = useState<Date | undefined>(initialValues?.applyTime);

  const [endTime, setEndTime] = useState<Date | undefined>(initialValues?.endTime);

  const [targetType, setTargetType] = useState<TargetTypeEnum | undefined>(initialTargetType);

  const [targetSpecUser, setTargetSpecUser] = useState<TTargetSpecUser | undefined>(initialTargetSpecUser);

  const [targetDepartment, setTargetDepartment] = useState<TTargetDepartment | undefined>(initialTargetDepartment);

  const [targetOther, setTargetOther] = useState<TTargetOther | undefined>(initialTargetOther);

  const { files, setFiles, previewFiles, setUrlFile } = useChooseFile({ defaultUrlFiles: initialBanner });

  const { activePage, setActivePage, onAddPage, onRemovePage } = useHandlePageForm({
    defaultPage: initialDefaultPage ? initialDefaultPage : defaultPage,
    setPages,
    form,
  });

  const {
    onAddBlock,
    onDuplicateBlock,
    onReorderBlock,
    onAddFieldImage,
    onChangeFieldImage,
    onRemoveFieldImage,
    onChangePointScaleOption,
    onChangeEssayFieldType,
    onChangeFieldRequired,
    onChangeMultipleChoiceOptionValue,
    onAddMultipleChoiceOption,
    onRemoveMultipleChoiceOption,
    onToggleOwnAnswerOnMultipleChoice,
    onChangeDescriptionStar,
    onChangeFieldTitle,
    onRemoveBlock,
  } = useHandleUpsertComponents({ pages, setPages, form });

  const { isAnonymous, onChangeIsAnonymous } = useHandleChangeAnonymous({
    form,
    setTargetType,
    setTargetSpecUser,
    setTargetDepartment,
    setTargetOther,
    initialIsAnonymous,
  });

  const { onCustomValidate } = useValidateForm({
    isAnonymous,
    pages,
    form,
    targetSpecUser,
    targetDepartment,
    targetOther,
    setApplyTime,
    setEndTime,
    setTargetType,
  });

  const { onSubmit, onSubmitWithoutValidation, submiting } = useSubmitForm({
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
  });

  const isStarting = useMemo(
    () => (initialValues?.applyTime ? compareTimeUtcWithLocalTime(initialValues?.applyTime) : false),
    [initialValues?.applyTime]
  );

  return (
    <FormContext.Provider
      value={{
        isStarting,
        form,
        errors: form.errors,
        onCustomValidate,
        onSubmit,
        onSubmitWithoutValidation,
        submiting,
        // page
        activePage,
        pages,
        setActivePage,
        onAddPage,
        onRemovePage,
        // time
        applyTime,
        setApplyTime,
        endTime,
        setEndTime,
        // banner
        files,
        setFiles,
        setUrlFile,
        previewFiles,
        // blocks
        onAddBlock,
        onDuplicateBlock,
        onReorderBlock,
        onAddFieldImage,
        onChangeFieldImage,
        onRemoveFieldImage,
        onChangePointScaleOption,
        onChangeEssayFieldType,
        onAddMultipleChoiceOption,
        onRemoveMultipleChoiceOption,
        onChangeFieldRequired,
        onChangeMultipleChoiceOptionValue,
        onToggleOwnAnswerOnMultipleChoice,
        onChangeDescriptionStar,
        onChangeFieldTitle,
        onRemoveBlock,
        // target
        targetType,
        setTargetType,
        targetSpecUser,
        setTargetSpecUser,
        targetDepartment,
        setTargetDepartment,
        targetOther,
        setTargetOther,
        // participant anonymous
        isAnonymous,
        onChangeIsAnonymous,
      }}
    >
      {children}
    </FormContext.Provider>
  );
};
