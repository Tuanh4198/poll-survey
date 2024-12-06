import React, { useContext, useMemo } from 'react';
import { Button, Flex, Pill, ScrollArea, Text, Image, Box } from '@mantine/core';
import { Plus } from '@phosphor-icons/react/dist/ssr';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import { components, ComponentsListName, FormAreaName, useHandleComponents } from 'app/pages/Form/hooks/useHandleShowComponents';
import { FormContext } from 'app/pages/Form/context/FormContext';
import { errorBlockLabel, fieldValidate } from 'app/pages/Form/hooks/useValidateForm';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import _ from 'lodash';
import { useModalConfirm } from 'app/pages/Form/hooks/useModalConfirm';

export const FormBuilder = () => {
  const { isStarting, activePage, pages, onAddPage, errors } = useContext(FormContext);

  const { viewport, handleDragEnd, handleClickAddNode, renderComponent } = useHandleComponents();

  const blockPerPage = useMemo(() => {
    return pages[activePage];
  }, [pages, activePage]);

  const blockError = useMemo(() => {
    return errors?.[errorBlockLabel];
  }, [errors]);

  const pageError = useMemo(() => {
    if (!errors || Object.entries(errors).length <= 0) return [];
    const errBlocks = Object.keys(errors || {})
      .filter(key => key.startsWith(errorBlockLabel))
      .map(key =>
        key
          .replace(`${errorBlockLabel}_${ComponentTypeEnum.ESSAY}_`, '')
          .replace(`${errorBlockLabel}_${ComponentTypeEnum.MULTIPLE_CHOICE}_`, '')
          .replace(`${errorBlockLabel}_${ComponentTypeEnum.POINT_SCALE}_`, '')
          .replace(`${errorBlockLabel}_${ComponentTypeEnum.STAR}_`, '')
          .replace(`${errorBlockLabel}_${ComponentTypeEnum.TITLE}_`, '')
          .replace(`_${fieldValidate.title}`, '')
          .replace(`_${fieldValidate.description}`, '')
      );
    const errBlocksUnique = [...new Set(errBlocks)];
    const errPages: string[] = [];
    Object.entries(pages).forEach(([key, values]) => {
      if (
        !values ||
        values.length <= 0 ||
        _.intersection(
          values.map(b => b.id),
          errBlocksUnique
        ).length > 0
      ) {
        errPages.push(key);
      }
    });
    return [...new Set(errPages)];
  }, [errors, pages]);

  return (
    <DragDropContext onDragEnd={handleDragEnd}>
      <Flex direction="column" m="auto" w="90%" h="100%">
        <Flex gap="12px">
          {Object.keys(pages).map((item, index) => (
            <PageNode key={item} index={index} pageId={item} pageError={pageError} />
          ))}
          <Button
            disabled={isStarting}
            onClick={onAddPage}
            bd="1px dashed #111928"
            radius={4}
            variant="outline"
            bg="#ffffff"
            color="#111928"
            leftSection={<Plus size={16} />}
          >
            Thêm 1 trang mới
          </Button>
        </Flex>
        <ScrollArea viewportRef={viewport} flex={1} scrollbars="y" mt="20px" mb="30px" className="form-area-wrapper">
          {blockError && (!blockPerPage || blockPerPage.length <= 0) && (
            <Text ta="center" color="#fa5252" size="18px" mt="5px">
              {blockError}
            </Text>
          )}
          <Droppable isDropDisabled={isStarting} droppableId={FormAreaName}>
            {(droppableProvided, snapshot) => (
              <div
                ref={droppableProvided.innerRef}
                {...droppableProvided.droppableProps}
                style={{
                  width: '100%',
                  height: '100%',
                  transition: 'ease-in',
                  display: 'flex',
                  gap: '20px',
                  flexDirection: 'column',
                  backgroundColor: snapshot.isDraggingOver ? '#2a2a861a' : 'transparent',
                }}
              >
                {blockPerPage &&
                  blockPerPage.length > 0 &&
                  blockPerPage.map((item, index) => (
                    <Draggable isDragDisabled={isStarting} key={item.id} draggableId={item?.id?.toString() || ''} index={index}>
                      {draggableProvided => (
                        <div ref={draggableProvided.innerRef} {...draggableProvided.draggableProps} {...draggableProvided.dragHandleProps}>
                          {renderComponent(item, item.type)}
                        </div>
                      )}
                    </Draggable>
                  ))}
              </div>
            )}
          </Droppable>
        </ScrollArea>
        {(!blockPerPage || blockPerPage.length <= 0) && (
          <Flex className="list-component-instruct-wrapper" align="center" justify="center" direction="column" gap="10px">
            <Text ta="center" c="#1F2A37" fw={600} size="14px">
              Chọn loại câu hỏi <br /> muốn thêm
            </Text>
            <Image w={65} src="../../../../../../content/images/Tap.svg" />
          </Flex>
        )}
        <Droppable droppableId={ComponentsListName} isDropDisabled>
          {droppableProvided => (
            <div className="list-component-wrapper" ref={droppableProvided.innerRef} {...droppableProvided.droppableProps}>
              <Flex>
                {components.map((item, index) => (
                  <Box key={item.id} pos="relative" className="item-component-wrapper">
                    <Box pos="absolute" top={0} left={0} right={0} bottom={0} style={{ zIndex: 10 }}>
                      <Draggable isDragDisabled={isStarting} draggableId={item.id.toString()} index={index}>
                        {draggableProvided => (
                          <div
                            ref={draggableProvided.innerRef}
                            {...draggableProvided.draggableProps}
                            {...draggableProvided.dragHandleProps}
                            style={{
                              userSelect: 'none',
                              background: 'white',
                              borderRadius: '24px',
                              ...draggableProvided.draggableProps.style,
                            }}
                            onClick={() => !isStarting && handleClickAddNode(item.componentType)}
                          >
                            {item.node}
                          </div>
                        )}
                      </Draggable>
                    </Box>
                    {item.node}
                  </Box>
                ))}
              </Flex>
            </div>
          )}
        </Droppable>
      </Flex>
    </DragDropContext>
  );
};

const PageNode = ({ index, pageId, pageError }: { index: number; pageId: string; pageError: string[] }) => {
  const { isStarting, activePage, pages, setActivePage, onRemovePage } = useContext(FormContext);

  const blockPerPage = useMemo(() => {
    return pages[pageId];
  }, [pages, pageId]);

  const { openModal, modalConfirm } = useModalConfirm({
    title: 'Xoá trang',
    content: 'Xoá trang sẽ xoá tất cả các câu hỏi được tạo trong trang này. Bạn xác nhận xoá?',
    onOk() {
      onRemovePage(pageId);
    },
  });

  return (
    <>
      <Pill
        className={pageError.includes(pageId) ? 'error' : ''}
        variant={activePage === pageId ? 'default' : 'contrast'}
        bg={activePage === pageId ? (pageError.includes(pageId) ? '#fa5252' : '#2A2A86') : '#ffffff'}
        bd={pageError.includes(pageId) ? '1px solid #fa5252' : activePage === pageId ? '1px solid #2A2A86' : '1px solid #111928'}
        c={activePage === pageId ? '#ffffff' : pageError.includes(pageId) ? '#fa5252' : '#111928'}
        size="md"
        display="flex"
        h="auto"
        styles={{ label: { alignItems: 'center', display: 'flex' } }}
        radius={4}
        key={pageId}
        withRemoveButton={Object.keys(pages).length > 1 && !isStarting}
        onRemove={() => (blockPerPage.length > 0 ? openModal() : onRemovePage(pageId))}
        onClick={() => setActivePage(pageId)}
      >
        Trang {index + 1}
      </Pill>
      {modalConfirm}
    </>
  );
};
