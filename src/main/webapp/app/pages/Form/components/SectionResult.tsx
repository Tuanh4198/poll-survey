/* eslint-disable @typescript-eslint/no-misused-promises */
import { Button, Flex, Select, Image, Text, ComboboxData, Pill, ScrollArea, Loader } from '@mantine/core';
import { CaretDown, Download } from '@phosphor-icons/react';
import { BlockResultTypeEssay } from 'app/pages/Form/components/BlockResultTypeEssay';
import { BlockResultTypeMultipleChoice } from 'app/pages/Form/components/BlockResultTypeMultipleChoice';
import { BlockResultTypePointScale } from 'app/pages/Form/components/BlockResultTypePointScale';
import { BlockResultTypeStar } from 'app/pages/Form/components/BlockResultTypeStar';
import { FormContext } from 'app/pages/Form/context/FormContext';
import { getTargetOption } from 'app/pages/Form/helper';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import React, { useContext, useMemo, useState } from 'react';

export const SectionResult = ({
  checksum,
  onExport,
  renderDialogPregressExport,
}: {
  checksum: string | undefined;
  onExport: () => Promise<void>;
  renderDialogPregressExport: () => React.JSX.Element;
}) => {
  const { form, pages, targetType, targetOther, targetDepartment, targetSpecUser } = useContext(FormContext);

  const [activePageResult, setActivePageResult] = useState(Object.keys(pages)?.[0]);

  const [selectedTarget, setSelectedTarget] = useState<string | null>();

  const blockPerPage = useMemo(() => {
    return pages[activePageResult];
  }, [pages, activePageResult]);

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
      <Flex gap={20}>
        <Flex align="center" justify="center" flex={1} direction="column" gap={20}>
          <Select
            w="100%"
            onChange={value => setSelectedTarget(value)}
            data={targetOption}
            placeholder="Chọn đối tượng được đánh giá"
            allowDeselect={false}
            rightSectionPointerEvents="none"
            rightSection={<CaretDown size={16} />}
          />
          {!selectedTarget && (
            <Flex align="center" justify="center" direction="column" gap="20px">
              <div style={{ transform: 'rotate(180deg)' }}>
                <Image w={65} src="../../../../../../content/images/Tap.svg" className="point-icon" />
              </div>
              <Text ta="center" c="#1F2A37" fw={600} size="14px">
                Chọn đối tượng <br /> muốn xem
              </Text>
            </Flex>
          )}
        </Flex>
        <Button
          disabled={!!checksum}
          onClick={onExport}
          color="#111928"
          variant="outline"
          bg="white"
          leftSection={checksum ? <Loader size={16} /> : <Download size={16} />}
        >
          Tải xuống dữ liệu đánh giá
        </Button>
        {renderDialogPregressExport()}
      </Flex>
      {selectedTarget && Object.keys(pages).length > 1 && (
        <Flex gap={12} mt={20}>
          {Object.keys(pages).map((pageId, index) => (
            <Pill
              onClick={() => setActivePageResult(pageId)}
              variant={activePageResult === pageId ? 'default' : 'contrast'}
              bg={activePageResult === pageId ? '#2A2A86' : '#ffffff'}
              bd={activePageResult === pageId ? '1px solid #2A2A86' : '1px solid #111928'}
              c={activePageResult === pageId ? '#ffffff' : '#111928'}
              styles={{ label: { alignItems: 'center', display: 'flex' } }}
              size="md"
              display="flex"
              h="auto"
              radius={4}
              key={pageId}
            >
              Trang {index + 1}
            </Pill>
          ))}
        </Flex>
      )}
      {selectedTarget && Object.keys(pages).length > 0 && (
        <ScrollArea flex={1} mt={20} scrollbars="y">
          <Flex gap={20} direction="column">
            {blockPerPage?.map(b => {
              switch (b.type) {
                case ComponentTypeEnum.ESSAY:
                  return <BlockResultTypeEssay key={b.id} block={b} target={selectedTarget} surveyId={form?.values.id} />;
                case ComponentTypeEnum.MULTIPLE_CHOICE:
                  return <BlockResultTypeMultipleChoice key={b.id} block={b} target={selectedTarget} surveyId={form?.values.id} />;
                case ComponentTypeEnum.POINT_SCALE:
                  return <BlockResultTypePointScale key={b.id} block={b} target={selectedTarget} surveyId={form?.values.id} />;
                case ComponentTypeEnum.STAR:
                  return <BlockResultTypeStar key={b.id} block={b} target={selectedTarget} surveyId={form?.values.id} />;
                default:
                  return null;
              }
            })}
          </Flex>
        </ScrollArea>
      )}
    </Flex>
  );
};
