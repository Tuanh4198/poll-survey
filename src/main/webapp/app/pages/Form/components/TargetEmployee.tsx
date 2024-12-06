import React, { Fragment, useContext, useMemo, useRef, useState } from 'react';
import { Box, Button, CloseButton, Flex, Input, Modal, Paper, Pill, Select, Tabs, TagsInput, Text, Tooltip } from '@mantine/core';
import { UploadFileXlxs } from 'app/pages/Form/components/UploadFileXlxs';
import { UploadSimple, Plus, CaretDown, XCircle, CheckCircle } from '@phosphor-icons/react';
import { v4 as uuidv4 } from 'uuid';
import { useDisclosure } from '@mantine/hooks';
import { useExportFileXlxsTemplate } from 'app/pages/Form/hooks/useExportFileXlxsTemplate';
import {
  CombinedParticipantEmployeeTypeEnum,
  ParticipantEmployeeTypeEnum,
  ParticipantEmployeeTypeEnumWithFile,
  ParticipantEmployeeTypeEnumWithoutFile,
  ParticipantEmployeeTypeLabel,
} from 'app/shared/model/enumerations/participant-employee-type-enum';
import { regEmployeeCode, separatorCharacter, validateTargetSpecUser } from 'app/pages/Form/helper';
import { FormContext, TTargetSpecUser } from 'app/pages/Form/context/FormContext';
import { notiWarning } from 'app/shared/notifications';
import { FileWithPath } from '@mantine/dropzone';
import { errorTargetLabel } from 'app/pages/Form/hooks/useValidateForm';
import { TargetTypeEnum } from 'app/shared/model/enumerations/target-type-enum';

const onDplicateEmployeeCodesTag = (value: string) => {
  notiWarning({ message: `Mã ${value.toUpperCase()} đã tồn tại` });
};

const tabs = {
  advanced: 'Nâng cao',
  basic: 'Cơ bản',
};

export const TargetEmployee = ({ disabled }: { disabled: boolean }) => {
  const targetSpecUserRef = useRef<TTargetSpecUser | undefined>();

  const { handleExportTemplateTarget, handleExportTemplateParticipant, handleExportTemplateSurveyEmployee } = useExportFileXlxsTemplate();

  const { form, errors, targetSpecUser, setTargetSpecUser } = useContext(FormContext);

  const [opened, { open, close }] = useDisclosure(false);

  const error = useMemo(() => errors?.[errorTargetLabel] === TargetTypeEnum.SPEC_USERS, [errors]);

  const errorTarget = useMemo(
    () => error && !targetSpecUser?.file && (targetSpecUser?.value == null || targetSpecUser?.value === ''),
    [error, targetSpecUser?.file, targetSpecUser?.value]
  );

  const errorParticipantSpecUser = useMemo(
    () =>
      error &&
      targetSpecUser?.participants &&
      targetSpecUser?.participants?.some(i => i.type === ParticipantEmployeeTypeEnumWithFile.SPEC_USERS && !i.file),
    [error, targetSpecUser?.participants]
  );

  const employeeCodes = useMemo(() => (targetSpecUser?.value ? targetSpecUser?.value?.split(separatorCharacter) : []), [targetSpecUser]);

  const reValidate = (newValue: TTargetSpecUser | undefined) =>
    validateTargetSpecUser({
      targetSpecUser: newValue,
      onOk() {
        form?.clearFieldError(errorTargetLabel);
      },
    });

  const onChangeEmployeeCodesTag = (value: string[]) => {
    setTargetSpecUser(oldValue => {
      if (value?.some(tag => !regEmployeeCode.test(tag))) {
        notiWarning({ message: 'Cần nhập đúng định dạng của mã nhân viên: "yd123456 | YD123456"' });
      }
      const newValue = {
        ...oldValue,
        value: value
          ?.filter(i => regEmployeeCode.test(i))
          ?.map(i => i.toUpperCase().trim())
          ?.join(separatorCharacter),
      };
      reValidate(newValue);
      return newValue;
    });
  };

  const onOpenModalChooseFileTarget = () => {
    targetSpecUserRef.current = targetSpecUser;
    open();
  };

  const onChooseFileTarget = (file?: FileWithPath) => {
    setTargetSpecUser(oldValue => {
      const newValue = {
        ...oldValue,
        value: undefined,
        file,
      };
      reValidate(newValue);
      return newValue;
    });
  };

  const onChooseFileSpecUser = (id: string | number, file?: FileWithPath) => {
    setTargetSpecUser(oldValue => {
      const newValue: TTargetSpecUser = {
        ...oldValue,
        participants: oldValue?.participants?.map(i => {
          if (i.id.toString() === id.toString() && i.type === ParticipantEmployeeTypeEnumWithFile.SPEC_USERS) {
            return {
              ...i,
              file,
            };
          }
          return i;
        }),
      };
      reValidate(newValue);
      return newValue;
    });
  };

  const onCancelFileSpecUser = () => {
    setTargetSpecUser(targetSpecUserRef.current);
    close();
  };

  const onConfirmFileSpecUser = () => {
    close();
  };

  const onAddSurveySubjectType = () => {
    setTargetSpecUser(oldValue => {
      const newValue = {
        ...oldValue,
        participants: [
          ...(oldValue?.participants || []),
          {
            id: uuidv4(),
            type: CombinedParticipantEmployeeTypeEnum.SELF,
          },
        ],
      };
      reValidate(newValue);
      return newValue;
    });
  };

  const onUpdateParticipantType = (value: ParticipantEmployeeTypeEnum, id: number | string) => {
    setTargetSpecUser(oldValue => {
      const newValue = {
        ...oldValue,
        participants: oldValue?.participants?.map(i => {
          if (i.id === id) {
            return { ...i, type: value };
          }
          return i;
        }),
      };
      reValidate(newValue);
      return newValue;
    });
  };

  const onRemoveSurveySubjectType = id => {
    if (!targetSpecUser?.participants || targetSpecUser?.participants?.length <= 1) return;
    setTargetSpecUser(oldValue => {
      const newValue = {
        ...oldValue,
        participants: [...(oldValue?.participants?.filter(i => i.id !== id) || [])],
      };
      reValidate(newValue);
      return newValue;
    });
  };

  const targetSpecUserAdvancedRef = useRef<TTargetSpecUser | undefined>(undefined);
  const targetSpecUserBasicRef = useRef<TTargetSpecUser | undefined>(undefined);
  const [tab, setTab] = useState<string | null>(tabs.basic);

  const onTabChange = (value: string | null) => {
    setTab(value || tabs.basic);
    if (value === tabs.advanced) {
      targetSpecUserBasicRef.current = targetSpecUser;
      setTargetSpecUser(
        targetSpecUserAdvancedRef.current
          ? targetSpecUserAdvancedRef.current
          : {
              value: undefined,
              file: undefined,
              participants: [
                {
                  id: uuidv4(),
                  type: ParticipantEmployeeTypeEnumWithFile.SPEC_USERS,
                  file: undefined,
                },
              ],
            }
      );
    } else {
      targetSpecUserAdvancedRef.current = targetSpecUser;
      setTargetSpecUser(
        targetSpecUserBasicRef.current
          ? targetSpecUserBasicRef.current
          : {
              value: undefined,
              participants: [
                {
                  id: uuidv4(),
                  type: ParticipantEmployeeTypeEnumWithoutFile.SELF,
                },
              ],
            }
      );
    }
  };

  return (
    <Paper radius="8px" p="20px" bd={`1px solid ${error ? '#fa5252' : '#E5E7EB'}`}>
      <Tabs onChange={onTabChange} value={tab}>
        <Tabs.List mb={20} className="tab-list-target-employee" display="none">
          <Tabs.Tab value={tabs.basic}>{tabs.basic}</Tabs.Tab>
          <Tabs.Tab disabled value={tabs.advanced}>
            {tabs.advanced}
          </Tabs.Tab>
        </Tabs.List>
        {/* tabs basic */}
        <Tabs.Panel value={tabs.basic}>
          <Flex gap="20px" direction="column">
            <Flex gap="5px" direction="column">
              <Input.Label>Nhân sự được khảo sát</Input.Label>
              <Flex gap="8px">
                <TagsInput
                  styles={{
                    pill: {
                      borderRadius: '24px',
                      background: '#D4D4F0',
                      color: '#1E1E73',
                      fontSize: '12px',
                      fontWeight: 500,
                      lineHeight: '14px',
                    },
                  }}
                  disabled={!!targetSpecUser?.file || disabled}
                  value={employeeCodes}
                  onChange={onChangeEmployeeCodesTag}
                  onDuplicate={onDplicateEmployeeCodesTag}
                  placeholder="Nhập YD để thêm"
                  error={errorTarget}
                  flex={1}
                />
                <Tooltip label="Nếu chọn file, dữ liệu YD được nhập sẽ không được ghi nhận">
                  <Button
                    disabled={disabled}
                    onClick={onOpenModalChooseFileTarget}
                    color={errorTarget ? '#fa5252' : '#111928'}
                    variant="outline"
                    px="8px"
                    size="sm"
                  >
                    <UploadSimple size={16} />
                  </Button>
                </Tooltip>
                <Modal
                  radius={8}
                  opened={opened}
                  onClose={onCancelFileSpecUser}
                  title={
                    <Text color="#111928" fw={600} fz={18}>
                      Tải lên
                    </Text>
                  }
                  size="555px"
                >
                  <UploadFileXlxs
                    disabled={disabled}
                    p="50px 12px"
                    file={targetSpecUser?.file}
                    onUpload={onChooseFileTarget}
                    onDownload={handleExportTemplateTarget}
                  />
                  <Flex mt="24px" gap="24px" justify="flex-end">
                    <Button onClick={onCancelFileSpecUser} variant="outline" color="#2A2A86" size="sm" radius={4}>
                      Hủy
                    </Button>
                    <Button onClick={onConfirmFileSpecUser} variant="filled" color="#2A2A86" size="sm" radius={4}>
                      Xác nhận
                    </Button>
                  </Flex>
                </Modal>
              </Flex>
              {targetSpecUser?.file && (
                <Flex gap="8px" mt={5}>
                  <Text color="#1F2A37" fw={600} flex={1}>
                    {typeof targetSpecUser?.file === 'string' ? targetSpecUser?.file : targetSpecUser?.file?.name}
                  </Text>
                  <CloseButton disabled={disabled} onClick={() => onChooseFileTarget(undefined)} />
                </Flex>
              )}
              {errorTarget && <Input.Error>Đối tượng được khảo sát bắt buộc nhập</Input.Error>}
            </Flex>
            <Flex gap="5px" direction="column" align="flex-start">
              <Input.Label>Đối tượng tham gia khảo sát</Input.Label>
              <Flex gap="8px" direction="column" w="100%">
                {targetSpecUser?.participants?.map(i => (
                  <Flex key={i.id} direction="column" justify="flex-start" gap="8px" align="center" w="100%">
                    <Flex gap="12px" align="center" w="100%">
                      <Select
                        disabled={disabled}
                        flex={1}
                        placeholder="Chọn"
                        rightSection={<CaretDown size={16} />}
                        value={i.type}
                        data={Object.values(CombinedParticipantEmployeeTypeEnum).map(value => ({
                          label: ParticipantEmployeeTypeLabel[value],
                          value,
                        }))}
                        onChange={value => onUpdateParticipantType(value as ParticipantEmployeeTypeEnum, i.id)}
                      />
                      <Button
                        disabled={disabled}
                        onClick={() => onRemoveSurveySubjectType(i.id)}
                        color="#111928"
                        variant="transparent"
                        px="0"
                        size="compact-xs"
                      >
                        <XCircle size={20} />
                      </Button>
                    </Flex>
                    {i.type === ParticipantEmployeeTypeEnumWithFile.SPEC_USERS && (
                      <Box w="100%" key={i.id}>
                        <UploadFileXlxs
                          disabled={disabled}
                          error={errorParticipantSpecUser && !i.file}
                          file={i.file}
                          onUpload={file => onChooseFileSpecUser(i.id, file)}
                          onDownload={handleExportTemplateParticipant}
                        />
                        {!i?.file && i?.value && (
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
                      </Box>
                    )}
                  </Flex>
                ))}
              </Flex>
              <Button
                disabled={disabled}
                onClick={onAddSurveySubjectType}
                mt="5px"
                color="#111928"
                variant="outline"
                leftSection={<Plus size={16} />}
              >
                Thêm đối tượng
              </Button>
            </Flex>
          </Flex>
        </Tabs.Panel>
        {/* tabs advanced */}
        <Tabs.Panel value={tabs.advanced}>
          {targetSpecUser?.participants?.map(i => (
            <Fragment key={i.id}>
              {i.type === ParticipantEmployeeTypeEnumWithFile.SPEC_USERS ? (
                <Box w="100%" key={i.id}>
                  <UploadFileXlxs
                    disabled={disabled}
                    error={errorParticipantSpecUser && !i.file}
                    file={i.file}
                    onUpload={file => onChooseFileSpecUser(i.id, file)}
                    onDownload={handleExportTemplateSurveyEmployee}
                  />
                  {!i?.file && i?.value && (
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
                </Box>
              ) : null}
            </Fragment>
          ))}
        </Tabs.Panel>
      </Tabs>
    </Paper>
  );
};
