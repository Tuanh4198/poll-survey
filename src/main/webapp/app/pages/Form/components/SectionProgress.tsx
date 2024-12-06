/* eslint-disable @typescript-eslint/no-misused-promises */
import {
  ActionIcon,
  Box,
  Button,
  ComboboxData,
  Flex,
  Input,
  Loader,
  LoadingOverlay,
  Pagination,
  Pill,
  ScrollArea,
  Select,
  Table,
  TextInput,
} from '@mantine/core';
import { MagnifyingGlass, DownloadSimple, Plus, CaretDown, Trash } from '@phosphor-icons/react';
import { FormContext } from 'app/pages/Form/context/FormContext';
import { buildCondition, getTargetOption } from 'app/pages/Form/helper';
import { useCreateParticipant } from 'app/pages/Form/hooks/useCreateParticipant';
import { useDeleteProgress } from 'app/pages/Form/hooks/useDeleteProgress';
import { useFetchProgress } from 'app/pages/Form/hooks/useFetchProgress';
import { useModalConfirm } from 'app/pages/Form/hooks/useModalConfirm';
import { IEmployeeSurvey } from 'app/shared/model/employee-survey.model';
import {
  ParticipantEmployeeTypeEnumWithFile,
  ParticipantEmployeeTypeLabel,
  ParticipantOtherType,
} from 'app/shared/model/enumerations/participant-employee-type-enum';
import { SurveyStatusEnum, SurveyStatusLabel } from 'app/shared/model/enumerations/survey-status-enum.model';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import React, { useCallback, useContext, useMemo } from 'react';

export const SectionProgress = ({
  checksum,
  onExport,
  renderDialogPregressExport,
}: {
  checksum: string | undefined;
  onExport: (conditions: string) => Promise<void>;
  renderDialogPregressExport: () => React.JSX.Element;
}) => {
  const { form, targetType, targetSpecUser, targetDepartment, targetOther } = useContext(FormContext);

  const { isLoading, data, total, page, keysearch, status, handleNextPage, handleStatus, handleChangeKeySearch, refetch } =
    useFetchProgress({
      surveyId: form?.values?.id,
    });

  const {
    formAddParticipantRef,
    addParticipant,
    adding,
    formAddParticipantError,
    validateOnChangeCode,
    setAddParticipant,
    validateOnChangeTarget,
    onSubmit,
    onCacel,
  } = useCreateParticipant({ refetch });

  const targetOption = useMemo(
    (): ComboboxData =>
      getTargetOption({
        targetType,
        targetSpecUser,
        targetDepartment,
        targetOther,
      }),
    [targetType, targetSpecUser, targetDepartment, targetOther]
  );

  return (
    <Flex direction="column" m="auto" w="90%" h="100%">
      <Flex gap={20} mb={20}>
        <Input
          onChange={event => handleChangeKeySearch(event.currentTarget.value)}
          flex={1}
          placeholder="Tìm kiếm nhân sự"
          leftSection={<MagnifyingGlass color="#4B5563 " size={16} />}
        />
        <Select
          width={250}
          placeholder="Trạng thái"
          rightSection={<CaretDown size={16} />}
          rightSectionPointerEvents="none"
          value={status}
          data={Object.keys(SurveyStatusEnum).map(i => ({
            label: SurveyStatusLabel[i],
            value: i,
          }))}
          onChange={value => handleStatus(value as SurveyStatusEnum)}
        />
        <Button
          disabled={!!checksum}
          onClick={() =>
            onExport(
              buildCondition({
                search: keysearch,
                status,
              })
            )
          }
          variant="outline"
          ml={50}
          rightSection={checksum ? <Loader size={16} /> : <DownloadSimple size={16} />}
        >
          Xuất Excel
        </Button>
        {renderDialogPregressExport()}
        <Button disabled={adding} onClick={() => setAddParticipant(o => !o)} rightSection={<Plus size={16} />}>
          Thêm đối tượng nhận đánh giá
        </Button>
      </Flex>
      <Box pos="relative" pb={40} flex={1}>
        <LoadingOverlay style={{ borderRadius: '8px' }} zIndex={10} visible={isLoading} loaderProps={{ children: <Loader /> }} />
        <ScrollArea.Autosize scrollHideDelay={5000} type="scroll" mah="60vh" scrollbars="y">
          <Table withRowBorders highlightOnHover mah="100%">
            <Table.Thead style={{ position: 'sticky', top: 0, backgroundColor: '#f1f1f1', zIndex: 1 }}>
              <Table.Tr>
                <Table.Th bg="#E5E7EB" color="#1F2A37" fw={400} w={100}>
                  YD
                </Table.Th>
                <Table.Th bg="#E5E7EB" color="#1F2A37" fw={400}>
                  Đối tượng nhận đánh giá
                </Table.Th>
                <Table.Th bg="#E5E7EB" color="#1F2A37" fw={400}>
                  Đối tượng được đánh giá
                </Table.Th>
                <Table.Th bg="#E5E7EB" color="#1F2A37" fw={400} ta="center" w={160}>
                  Trạng thái
                </Table.Th>
                <Table.Th bg="#E5E7EB" color="#1F2A37" fw={400} ta="center" w={90}>
                  Thao tác
                </Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {addParticipant && (
                <Table.Tr>
                  <Table.Td fw={500} fz={16} />
                  <Table.Td fw={500} fz={16}>
                    <TextInput
                      error={formAddParticipantError.code}
                      onChange={event => {
                        formAddParticipantRef.current.code = event.currentTarget.value;
                        validateOnChangeCode(event.currentTarget.value);
                      }}
                      placeholder="Nhập YD"
                    />
                  </Table.Td>
                  <Table.Td fw={500} fz={16}>
                    <Select
                      data={targetOption}
                      error={formAddParticipantError.target}
                      onChange={validateOnChangeTarget}
                      placeholder="Chọn"
                      allowDeselect={false}
                      rightSectionPointerEvents="none"
                      rightSection={<CaretDown size={16} />}
                    />
                  </Table.Td>
                  <Table.Td fw={500} fz={16} align="center" colSpan={2}>
                    <Flex gap={12} justify="flex-end">
                      <Button disabled={adding} onClick={onCacel} variant="outline" c="#111928">
                        Hủy
                      </Button>
                      <Button loading={adding} onClick={onSubmit}>
                        Thêm
                      </Button>
                    </Flex>
                  </Table.Td>
                </Table.Tr>
              )}
              {data?.map(i => (
                <Row key={i.id} i={i} onDeteled={refetch} />
              ))}
            </Table.Tbody>
            {!data || (data?.length <= 0 && <Table.Caption>Chưa có dữ liệu</Table.Caption>)}
          </Table>
        </ScrollArea.Autosize>
        {Math.ceil(total / ITEMS_PER_PAGE) > 1 && (
          <Flex justify="flex-end" mt="20px">
            <Pagination
              value={page}
              disabled={isLoading}
              withControls={false}
              hideWithOnePage
              size="sm"
              total={Math.ceil(total / ITEMS_PER_PAGE)}
              onChange={handleNextPage}
            />
          </Flex>
        )}
      </Box>
    </Flex>
  );
};

const Row = ({ i, onDeteled }: { i: IEmployeeSurvey; onDeteled: () => void }) => {
  const { deleting, deleteEntity } = useDeleteProgress();

  const { openModal, modalConfirm } = useModalConfirm({
    title: 'Xoá đánh giá',
    content: 'Bạn xác nhận xoá đánh giá này?',
    async onOk() {
      await deleteEntity(i.id);
      onDeteled();
    },
  });

  const tagStatus = useCallback((_status?: SurveyStatusEnum) => {
    switch (_status) {
      case SurveyStatusEnum.COMPLETED:
        return (
          <Pill c="#099268" bg="#C3FAE8">
            {SurveyStatusLabel[SurveyStatusEnum.COMPLETED]}
          </Pill>
        );
      case SurveyStatusEnum.NOT_ATTENDED:
        return (
          <Pill c="#6B7280" bg="#E5E7EB">
            {SurveyStatusLabel[SurveyStatusEnum.NOT_ATTENDED]}
          </Pill>
        );
      case SurveyStatusEnum.PENDING:
        return (
          <Pill c="#FD7E14" bg="#FFEECD">
            {SurveyStatusLabel[SurveyStatusEnum.PENDING]}
          </Pill>
        );
      default:
        return null;
    }
  }, []);

  const participantName = useMemo(() => {
    if (i.name) {
      return i.name;
    } else if (
      i.participantType &&
      i.participantType !== ParticipantOtherType.OTHER &&
      i.participantType !== ParticipantEmployeeTypeEnumWithFile.SPEC_USERS
    ) {
      return ParticipantEmployeeTypeLabel[i.participantType];
    } else {
      return '-';
    }
  }, [i.name, i.participantType]);

  return (
    <Table.Tr key={i.id}>
      <Table.Td fw={500} fz={16}>
        {i.code}
      </Table.Td>
      <Table.Td fw={500} fz={16}>
        {participantName}
      </Table.Td>
      <Table.Td fw={500} fz={16}>
        {i.targetName}
      </Table.Td>
      <Table.Td fw={500} fz={16} align="center">
        {tagStatus(i.status)}
      </Table.Td>
      <Table.Td fw={500} fz={16} align="center">
        {deleting ? (
          <Loader size={20} />
        ) : (
          <ActionIcon onClick={openModal} variant="transparent">
            <Trash size={20} color="#343330" />
          </ActionIcon>
        )}
        {modalConfirm}
      </Table.Td>
    </Table.Tr>
  );
};
