/* eslint-disable @typescript-eslint/no-misused-promises */
import React, { useContext, useRef } from 'react';
import { ActionIcon, Button, Checkbox, CopyButton, Flex, Input, Select, Textarea } from '@mantine/core';
import { DateTimePicker } from '@mantine/dates';
import { CaretDown, Clock } from '@phosphor-icons/react';
import { TargetOther } from 'app/pages/Form/components/TargetOther';
import { TargetDepartment } from 'app/pages/Form/components/TargetDepartment';
import { TargetEmployee } from 'app/pages/Form/components/TargetEmployee';
import { FormContext } from 'app/pages/Form/context/FormContext';
import { EventLabel, EventValue } from 'app/shared/model/survey.model';
import {
  btnSubmit,
  btnSubmitUpdateAndAssign,
  btnSubmitAndAssign,
  btnSubmitTemplate,
  btnSubmitUpdate,
} from 'app/pages/Form/hooks/useValidateForm';
import { TargetTypeEnum, TargetTypeLabel } from 'app/shared/model/enumerations/target-type-enum';
import dayjs from 'dayjs';
import { useModalConfirm } from 'app/pages/Form/hooks/useModalConfirm';
import { FormTypeEnum, hiddenFields } from 'app/pages/Form/helper';
import { useDeleteTemplate } from 'app/pages/Form/hooks/useDeleteTemplate';
import { useDeleteSurvey } from 'app/pages/Form/hooks/useDeleteSurvey';

export const FormInfo = ({ isDetail, type }: { isDetail: boolean; type?: FormTypeEnum }) => {
  const { isAnonymous, onChangeIsAnonymous, isStarting, applyTime, endTime, form, targetType, onSubmitWithoutValidation } =
    useContext(FormContext);

  const startDateTime = useRef<HTMLInputElement>(null);

  const endDateTime = useRef<HTMLInputElement>(null);

  const targetBlock = () => {
    switch (targetType) {
      case TargetTypeEnum.SPEC_USERS:
        return <TargetEmployee disabled={isStarting} />;
      case TargetTypeEnum.DEPARTMENT:
        return <TargetDepartment disabled={isStarting} />;
      case TargetTypeEnum.OTHER:
        return <TargetOther disabled={isStarting} />;
      default:
        return null;
    }
  };

  const { deleting: deletingSurvey, deleteEntity: deleteSurvey } = useDeleteSurvey();

  const { deleting: deletingTemplate, deleteEntity: deleteTemplate } = useDeleteTemplate();

  const { openModal, modalConfirm } = useModalConfirm({
    title: type === FormTypeEnum.SURVEY ? 'Xoá bài khảo sát' : 'Xoá bài khảo sát mẫu',
    content:
      type === FormTypeEnum.SURVEY
        ? 'Việc xoá bài khảo sát sẽ xoá cả kết quả khảo sát. Bạn xác nhận xoá?'
        : 'Bạn xác nhận xoá bài khảo sát mẫu?',
    onOk() {
      if (type === FormTypeEnum.SURVEY) deleteSurvey(form?.values.id);
      else if (type === FormTypeEnum.TEMPLATE) deleteTemplate(form?.values.id);
    },
  });

  return (
    <Flex mt="24px" direction="column" gap="20px">
      <Input display="none" {...form?.getInputProps('id')} />
      <Textarea
        withAsterisk
        placeholder="Tiêu đề"
        className="input-title"
        autosize
        minRows={1}
        maxRows={3}
        {...form?.getInputProps('title')}
      />
      <Textarea placeholder="Mô tả" className="textarea-desc" autosize minRows={1} maxRows={10} {...form?.getInputProps('description')} />
      <Flex gap="8px" align="flex-start">
        <DateTimePicker
          flex={1}
          label="Bắt đầu"
          placeholder="Chọn"
          c="#4B5563"
          withAsterisk
          minDate={dayjs().toDate()}
          maxDate={endTime ? dayjs(endTime).toDate() : undefined}
          {...form?.getInputProps('applyTime')}
          highlightToday
          disabled={isStarting}
          withSeconds={false}
          timeInputProps={{
            ref: startDateTime,
            rightSection: (
              <ActionIcon variant="subtle" onClick={() => startDateTime.current?.showPicker()}>
                <Clock size={16} />
              </ActionIcon>
            ),
          }}
        />
        <DateTimePicker
          flex={1}
          label="Kết thúc"
          placeholder="Chọn"
          c="#4B5563"
          minDate={applyTime && !isStarting ? dayjs(applyTime).toDate() : dayjs().toDate()}
          {...form?.getInputProps('endTime')}
          highlightToday
          withSeconds={false}
          timeInputProps={{
            ref: endDateTime,
            rightSection: (
              <ActionIcon variant="subtle" onClick={() => endDateTime.current?.showPicker()}>
                <Clock size={16} />
              </ActionIcon>
            ),
          }}
        />
      </Flex>
      <Checkbox
        disabled={isStarting || isAnonymous}
        label="Bắt buộc hoàn thành"
        checked={form?.values?.isRequired}
        {...form?.getInputProps('isRequired')}
      />
      <Checkbox disabled={isStarting} label="Khảo sát ẩn danh" checked={isAnonymous} onChange={onChangeIsAnonymous} />
      {isAnonymous && (
        <Flex gap={8}>
          {hiddenFields.map((item, index) => (
            <CopyButton key={item.key} value={`{{${item.key}}}`}>
              {({ copied, copy }) => (
                <Button variant="outline" size="compact-xs" color={copied ? 'teal' : 'blue'} onClick={copy}>
                  {copied ? `Copied biến thứ ${index + 1}` : `Copy biến thứ ${index + 1}`}
                </Button>
              )}
            </CopyButton>
          ))}
        </Flex>
      )}
      {isAnonymous ? (
        <Select
          disabled={isAnonymous}
          label="Trường hợp khảo sát"
          placeholder="Chọn"
          rightSection={<CaretDown size={16} />}
          {...form?.getInputProps('events')}
        />
      ) : (
        <Select
          disabled={isStarting}
          withAsterisk
          label="Trường hợp khảo sát"
          placeholder="Chọn"
          rightSection={<CaretDown size={16} />}
          clearable={false}
          {...form?.getInputProps('events')}
          data={Object.values(EventValue).map(value => ({
            label: EventLabel[value],
            value,
          }))}
        />
      )}
      <Select
        disabled={isStarting || isAnonymous}
        withAsterisk
        label="Đối tượng khảo sát"
        placeholder="Chọn"
        rightSection={<CaretDown size={16} />}
        clearable={false}
        {...form?.getInputProps('targetType')}
        data={Object.values(TargetTypeEnum).map(value => ({
          label: TargetTypeLabel[value],
          value,
        }))}
      />
      {targetBlock()}
      {isDetail ? (
        <Flex justify="flex-end" align="center" gap="20px" direction="column">
          {modalConfirm}
          {type === FormTypeEnum.SURVEY && (
            <>
              <Button fullWidth radius={5} type="submit" id={btnSubmitUpdateAndAssign}>
                Lưu chỉnh sửa và giao
              </Button>
              <Button fullWidth radius={5} type="submit" id={btnSubmitUpdate} color="#111928" variant="outline">
                Lưu chỉnh sửa
              </Button>
            </>
          )}
          {type === FormTypeEnum.TEMPLATE && (
            <>
              <Button fullWidth radius={5} type="submit" id={btnSubmitAndAssign}>
                Tạo khảo sát và giao
              </Button>
              <Button fullWidth radius={5} type="submit" id={btnSubmit} color="#111928" variant="outline">
                Lưu khảo sát nháp
              </Button>
              <Button
                fullWidth
                radius={5}
                type="button"
                id={btnSubmitUpdate}
                onClick={() => onSubmitWithoutValidation(btnSubmitUpdate)}
                color="#111928"
                variant="outline"
              >
                Lưu chỉnh sửa
              </Button>
            </>
          )}
          <Button
            fullWidth
            radius={5}
            loading={deletingSurvey || deletingTemplate}
            onClick={openModal}
            type="button"
            color="#FA5252"
            variant="outline"
          >
            Xóa
          </Button>
        </Flex>
      ) : (
        <Flex justify="flex-end" align="center" gap="20px" direction="column">
          <Button fullWidth radius={5} type="submit" id={btnSubmitAndAssign}>
            Tạo khảo sát và giao
          </Button>
          <Button fullWidth radius={5} type="submit" id={btnSubmit} color="#111928" variant="outline">
            Lưu khảo sát nháp
          </Button>
          <Button
            fullWidth
            radius={5}
            type="button"
            id={btnSubmitTemplate}
            onClick={() => onSubmitWithoutValidation()}
            color="#111928"
            variant="outline"
          >
            Lưu thành mẫu
          </Button>
        </Flex>
      )}
    </Flex>
  );
};
