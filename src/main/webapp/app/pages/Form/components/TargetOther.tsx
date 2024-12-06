import React, { useContext, useMemo } from 'react';
import { Flex, Input, Paper, Pill, Text, TextInput } from '@mantine/core';
import { UploadFileXlxs } from 'app/pages/Form/components/UploadFileXlxs';
import { useExportFileXlxsTemplate } from 'app/pages/Form/hooks/useExportFileXlxsTemplate';
import { FormContext, TTargetOther } from 'app/pages/Form/context/FormContext';
import { FileWithPath } from '@mantine/dropzone';
import { debounce } from 'lodash';
import { validateTargetTypeOther } from 'app/pages/Form/helper';
import { errorTargetLabel } from 'app/pages/Form/hooks/useValidateForm';
import { TargetTypeEnum } from 'app/shared/model/enumerations/target-type-enum';
import { CheckCircle } from '@phosphor-icons/react';

export const TargetOther = ({ disabled }: { disabled: boolean }) => {
  const { handleExportTemplateTarget } = useExportFileXlxsTemplate();

  const { isAnonymous, form, errors, targetOther, setTargetOther } = useContext(FormContext);

  const error = useMemo(() => errors?.[errorTargetLabel] === TargetTypeEnum.OTHER, [errors]);

  const reValidate = (newValue: TTargetOther | undefined) =>
    validateTargetTypeOther({
      isAnonymous,
      targetOther: newValue,
      onOk() {
        form?.clearFieldError(errorTargetLabel);
      },
    });

  const onChangeValue = debounce((value: string) => {
    setTargetOther(oldValue => {
      const newValue = { ...oldValue, value };
      reValidate(newValue);
      return newValue;
    });
  }, 500);

  const onChosseFile = (file?: FileWithPath) => {
    setTargetOther(oldValue => {
      const newValue = {
        ...oldValue,
        participants: {
          file,
        },
      };
      reValidate(newValue);
      return newValue;
    });
  };

  return (
    <Paper radius="8px" p="20px" bd={`1px solid ${error ? '#fa5252' : '#E5E7EB'}`}>
      <Flex gap="20px" direction="column">
        <TextInput
          disabled={disabled}
          onChange={event => onChangeValue(event.currentTarget.value)}
          withAsterisk
          defaultValue={targetOther?.value}
          error={error && !targetOther?.value && 'Tên bắt buộc nhập'}
          label="Đối tượng được khảo sát"
          placeholder="Nhập tên đối tượng"
        />
        <Flex gap="5px" direction="column">
          <Input.Label required>Đối tượng tham gia khảo sát</Input.Label>
          {isAnonymous ? (
            <Text>Ẩn danh người tham gia khảo sát</Text>
          ) : (
            <>
              <UploadFileXlxs
                disabled={disabled}
                error={!!(error && !targetOther?.participants?.file)}
                file={targetOther?.participants?.file}
                onUpload={onChosseFile}
                onDownload={handleExportTemplateTarget}
              />
              {error && (!targetOther?.participants?.file || !targetOther?.participants?.value) && (
                <Input.Error>Đối tượng tham gia khảo sắt bắt buộc nhập</Input.Error>
              )}
              {!targetOther?.participants?.file && targetOther?.participants?.value && (
                // <ScrollArea.Autosize mah={200} scrollbars="y" mt={10}>
                //   <Flex gap={8} wrap="wrap">
                //     {targetOther?.participants?.value?.split('\\|\\|')?.map((item, index) => (
                //       <Fragment key={index}>
                //         {item ? (
                //           <Pill c="#1E1E73" fw={500} fz="14px" radius="24px" bg="#D4D4F0">
                //             {item}
                //           </Pill>
                //         ) : null}
                //       </Fragment>
                //     ))}
                //   </Flex>
                // </ScrollArea.Autosize>
                <>
                  <Pill radius={24} bg="#D4D4F0" c="#1E1E73" w="100%" ta="left" fz={14} fw={500} mt={10}>
                    <Flex align="center" gap={10}>
                      <CheckCircle color="#1E1E73" size={16} weight="fill" /> File đã tải lên
                    </Flex>
                  </Pill>
                  <Text c="#1F2A37" fw={500} mt={10}>
                    Để thay thế vui lòng tải lên 1 file khác
                  </Text>
                </>
              )}
            </>
          )}
        </Flex>
      </Flex>
    </Paper>
  );
};
