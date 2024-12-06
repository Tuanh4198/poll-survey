import { UseFormReturnType } from '@mantine/form';
import { TFormBody, TTargetDepartment, TTargetOther, TTargetSpecUser } from 'app/pages/Form/context/FormContext';
import { ParticipantAnonymousType } from 'app/shared/model/enumerations/participant-employee-type-enum';
import { TargetTypeEnum } from 'app/shared/model/enumerations/target-type-enum';
import { EventValueOther } from 'app/shared/model/survey.model';
import { useState } from 'react';

interface UseHandleChangeAnonymousProps {
  form: UseFormReturnType<TFormBody, (values: TFormBody) => TFormBody>;
  setTargetType: React.Dispatch<React.SetStateAction<TargetTypeEnum | undefined>>;
  setTargetSpecUser: React.Dispatch<React.SetStateAction<TTargetSpecUser | undefined>>;
  setTargetDepartment: React.Dispatch<React.SetStateAction<TTargetDepartment | undefined>>;
  setTargetOther: React.Dispatch<React.SetStateAction<TTargetOther | undefined>>;
  initialIsAnonymous?: boolean;
}
export const useHandleChangeAnonymous = ({
  form,
  setTargetType,
  setTargetSpecUser,
  setTargetDepartment,
  setTargetOther,
  initialIsAnonymous,
}: UseHandleChangeAnonymousProps) => {
  const [isAnonymous, setIsAnonymous] = useState<boolean>(!!initialIsAnonymous);

  const onChangeIsAnonymous = event => {
    setIsAnonymous(event.target.checked);
    if (event.target.checked) {
      form.setFieldValue('isRequired', false);
      form.setFieldValue('events', EventValueOther.OTHER);
      form.setFieldValue('targetType', TargetTypeEnum.OTHER);
      setTargetType(TargetTypeEnum.OTHER);
      setTargetSpecUser(undefined);
      setTargetDepartment(undefined);
      setTargetOther(oldValue => {
        const newValue = {
          ...oldValue,
          participants: {
            value: ParticipantAnonymousType.ANONYMOUS,
          },
        };
        return newValue;
      });
    }
  };

  return {
    isAnonymous,
    onChangeIsAnonymous,
  };
};
