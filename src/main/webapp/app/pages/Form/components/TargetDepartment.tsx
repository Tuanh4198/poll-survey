import React, { Fragment, useContext, useMemo, useRef } from 'react';
import { Checkbox, Flex, Input, Paper, Pill, ScrollArea } from '@mantine/core';
import { FormContext, TTargetDepartment } from 'app/pages/Form/context/FormContext';
import { errorTargetLabel } from 'app/pages/Form/hooks/useValidateForm';
import { TargetTypeEnum } from 'app/shared/model/enumerations/target-type-enum';
import { ParticipantDepartmentTypeEnum } from 'app/shared/model/enumerations/articipant-department-type-enum.model';
import { SelectDepartment } from 'app/pages/Form/components/SelectDepartment';
import { separatorCharacter, validateTargetTypeDepartment } from 'app/pages/Form/helper';

export const TargetDepartment = ({ disabled }: { disabled: boolean }) => {
  const selectDepartmentRef = useRef<any>(null);

  const { isStarting, form, errors, targetDepartment, setTargetDepartment } = useContext(FormContext);

  const error = useMemo(() => errors?.[errorTargetLabel] === TargetTypeEnum.DEPARTMENT, [errors]);

  const targetDepartmentValue = useMemo(() => targetDepartment?.value, [targetDepartment?.value]);

  const employeeOtherDerpartment = useMemo(
    () => targetDepartment?.participants?.employee_other_department,
    [targetDepartment?.participants?.employee_other_department]
  );

  const employeeSameDerpartment = useMemo(
    () => targetDepartment?.participants?.employee_same_department,
    [targetDepartment?.participants?.employee_same_department]
  );

  const reValidate = (newValue: TTargetDepartment | undefined) =>
    validateTargetTypeDepartment({
      targetDepartment: newValue,
      onOk() {
        form?.clearFieldError(errorTargetLabel);
      },
    });

  const onDepartmentChange = (name?: string) => {
    setTargetDepartment(oldValue => {
      if (!name) return oldValue;
      let _newValue = oldValue?.value?.split(separatorCharacter) || [];
      if (_newValue?.includes(name)) {
        _newValue = _newValue?.filter(i => i !== name);
        selectDepartmentRef?.current?.onClear(name);
      } else {
        _newValue?.push(name);
      }
      const newValue = {
        ...(oldValue || {}),
        value: _newValue && _newValue?.length > 0 ? _newValue?.join(separatorCharacter) : '',
      };
      reValidate(newValue);
      return newValue;
    });
  };

  const onEmployeeSameDerpartmentChange = event => {
    setTargetDepartment(oldValue => {
      const newValue = {
        ...(oldValue || {}),
        participants: {
          [ParticipantDepartmentTypeEnum.EMPLOYEE_SAME_DEPARTMENT]: event.currentTarget.checked,
          [ParticipantDepartmentTypeEnum.EMPLOYEE_OTHER_DEPARTMENT]:
            !!oldValue?.participants?.[ParticipantDepartmentTypeEnum.EMPLOYEE_OTHER_DEPARTMENT],
        },
      };
      reValidate(newValue);
      return newValue;
    });
  };

  const onEmployeeOtherDerpartmentChange = event => {
    setTargetDepartment(oldValue => {
      const newValue = {
        ...(oldValue || {}),
        participants: {
          [ParticipantDepartmentTypeEnum.EMPLOYEE_OTHER_DEPARTMENT]: event.currentTarget.checked,
          [ParticipantDepartmentTypeEnum.EMPLOYEE_SAME_DEPARTMENT]:
            !!oldValue?.participants?.[ParticipantDepartmentTypeEnum.EMPLOYEE_SAME_DEPARTMENT],
        },
      };
      reValidate(newValue);
      return newValue;
    });
  };

  return (
    <Paper radius="8px" p="20px" bd={`1px solid ${error ? '#fa5252' : '#E5E7EB'}`}>
      <Flex gap="20px" direction="column">
        <Flex gap="10px" direction="column">
          {targetDepartment?.value && (
            <ScrollArea.Autosize mah={200} scrollbars="y" mt={10}>
              <Flex gap={8} wrap="wrap">
                {targetDepartment?.value?.split(separatorCharacter)?.map((item, index) => (
                  <Fragment key={index}>
                    {item ? (
                      <Pill
                        onRemove={() => !isStarting && onDepartmentChange(item)}
                        withRemoveButton={!isStarting}
                        c="#1E1E73"
                        fw={500}
                        fz="14px"
                        radius="24px"
                        bg="#D4D4F0"
                      >
                        {item}
                      </Pill>
                    ) : null}
                  </Fragment>
                ))}
              </Flex>
            </ScrollArea.Autosize>
          )}
          <SelectDepartment
            ref={selectDepartmentRef}
            error={!targetDepartmentValue && error}
            disabled={disabled}
            onSelectedItem={onDepartmentChange}
            label="Chọn phòng ban được khảo sát"
            placeholder="Chọn phòng ban"
          />
          {error && !targetDepartmentValue && <Input.Error>Phòng ban bắt buộc nhập</Input.Error>}
        </Flex>
        <Flex gap="10px" direction="column">
          <Input.Label>Đối tượng tham gia khảo sát</Input.Label>
          <Checkbox
            error={error && !employeeOtherDerpartment && !employeeSameDerpartment}
            disabled={disabled}
            onChange={onEmployeeOtherDerpartmentChange}
            checked={employeeOtherDerpartment}
            label="Tất cả nhân sự khác phòng ban"
          />
          <Checkbox
            error={error && !employeeOtherDerpartment && !employeeSameDerpartment}
            disabled={disabled}
            onChange={onEmployeeSameDerpartmentChange}
            checked={employeeSameDerpartment}
            label="Nhân sự phòng ban tự đánh giá"
          />
          {error && !employeeOtherDerpartment && !employeeSameDerpartment && (
            <Input.Error>Đối tượng tham gia khảo sắt bắt buộc chọn</Input.Error>
          )}
        </Flex>
      </Flex>
    </Paper>
  );
};
