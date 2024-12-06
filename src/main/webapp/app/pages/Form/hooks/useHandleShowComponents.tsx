import React, { useRef, useEffect, useCallback, useContext } from 'react';
import { ComponentTypeEssay } from 'app/pages/Form/components/ComponentTypeEssay';
import { ComponentTypeMultipleChoice } from 'app/pages/Form/components/ComponentTypeMultipleChoice';
import { ComponentTypePointScale } from 'app/pages/Form/components/ComponentTypePointScale';
import { ComponentTypeStar } from 'app/pages/Form/components/ComponentTypeStar';
import { ComponentTypeTitle } from 'app/pages/Form/components/ComponentTypeTitle';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { Flex, Image, Text } from '@mantine/core';
import { FormContext, TBlock } from 'app/pages/Form/context/FormContext';

export const components = [
  {
    id: 1,
    componentType: ComponentTypeEnum.MULTIPLE_CHOICE,
    node: (
      <Flex w={135} direction="column" align="center" justify="center" gap="8px" p="20px">
        <Image w={35} src="../../../../content/images/Choice.svg" />
        <Text className="component-title" c="#1E1E73">
          Trắc nghiệm
        </Text>
      </Flex>
    ),
  },
  {
    id: 2,
    componentType: ComponentTypeEnum.ESSAY,
    node: (
      <Flex w={135} direction="column" align="center" justify="center" gap="8px" p="20px">
        <Image w={35} src="../../../../content/images/Wirte.svg" />
        <Text className="component-title" c="#12B886">
          Tự luận
        </Text>
      </Flex>
    ),
  },
  {
    id: 3,
    componentType: ComponentTypeEnum.STAR,
    node: (
      <Flex w={135} direction="column" align="center" justify="center" gap="8px" p="20px">
        <Image w={35} src="../../../../content/images/Stars.svg" />
        <Text className="component-title" c="#FAB005">
          Vote sao
        </Text>
      </Flex>
    ),
  },
  {
    id: 4,
    componentType: ComponentTypeEnum.POINT_SCALE,
    node: (
      <Flex w={135} direction="column" align="center" justify="center" gap="8px" p="20px">
        <Image w={35} src="../../../../content/images/Point.svg" />
        <Text className="component-title" c="#228BE6">
          Thang điểm
        </Text>
      </Flex>
    ),
  },
  {
    id: 5,
    componentType: ComponentTypeEnum.TITLE,
    node: (
      <Flex w={135} direction="column" align="center" justify="center" gap="8px" p="20px">
        <Image w={35} src="../../../../content/images/Text.svg" />
        <Text className="component-title" c="#1F2A37">
          Tiêu đề
        </Text>
      </Flex>
    ),
  },
];

export const FormAreaName = 'FormArea';
export const ComponentsListName = 'ComponentsList';

export const useHandleComponents = () => {
  const viewport = useRef<HTMLDivElement>(null);
  const eventActiveRef = useRef<'click' | 'drag' | null>(null);

  const { pages, activePage, onAddBlock, onReorderBlock, onRemoveBlock } = useContext(FormContext);

  const handleDragEnd = result => {
    const { source, destination } = result;
    if (!destination) return;
    if (source.droppableId === ComponentsListName && destination.droppableId === FormAreaName) {
      const draggedItem = components[source.index];
      onAddBlock({
        num: destination.index,
        pageNum: Object.keys(pages).findIndex(p => p === activePage) + 1,
        pageId: activePage,
        type: draggedItem.componentType,
      });
      if (pages[activePage] && destination.index === pages[activePage].length - 1) eventActiveRef.current = 'drag';
    }
    if (source.droppableId === FormAreaName && destination.droppableId === FormAreaName) {
      onReorderBlock({ destinationIndex: destination.index, sourceIndex: source.index, pageId: activePage });
    }
  };

  const handleClickAddNode = (componentType: ComponentTypeEnum) => {
    eventActiveRef.current = 'click';
    const _pageNum = Object.keys(pages).findIndex(p => p === activePage);
    onAddBlock({
      num: pages[activePage]?.length || 1,
      pageNum: _pageNum < 0 ? 1 : _pageNum + 1,
      pageId: activePage,
      type: componentType,
    });
  };

  useEffect(() => {
    if (eventActiveRef.current != null) {
      viewport.current?.scrollTo({
        top: (viewport.current?.scrollHeight || 0) + (viewport.current?.clientHeight || 0),
        behavior: 'smooth',
      });
      eventActiveRef.current = null;
    }
  }, [pages]);

  const renderComponent = useCallback((block?: TBlock, componentType?: ComponentTypeEnum) => {
    if (!block || !componentType) return null;
    switch (componentType) {
      case ComponentTypeEnum.MULTIPLE_CHOICE:
        return (
          <ComponentTypeMultipleChoice
            block={block}
            onRemove={() =>
              onRemoveBlock({ id: block?.id?.toString() || '', pageId: block.pageId, type: ComponentTypeEnum.MULTIPLE_CHOICE })
            }
          />
        );
      case ComponentTypeEnum.ESSAY:
        return (
          <ComponentTypeEssay
            block={block}
            onRemove={() => onRemoveBlock({ id: block?.id?.toString() || '', pageId: block.pageId, type: ComponentTypeEnum.ESSAY })}
          />
        );
      case ComponentTypeEnum.STAR:
        return (
          <ComponentTypeStar
            block={block}
            onRemove={() => onRemoveBlock({ id: block?.id?.toString() || '', pageId: block.pageId, type: ComponentTypeEnum.STAR })}
          />
        );
      case ComponentTypeEnum.POINT_SCALE:
        return (
          <ComponentTypePointScale
            block={block}
            onRemove={() => onRemoveBlock({ id: block?.id?.toString() || '', pageId: block.pageId, type: ComponentTypeEnum.POINT_SCALE })}
          />
        );
      case ComponentTypeEnum.TITLE:
        return (
          <ComponentTypeTitle
            block={block}
            onRemove={() => onRemoveBlock({ id: block?.id?.toString() || '', pageId: block.pageId, type: ComponentTypeEnum.TITLE })}
          />
        );
      default:
        return null;
    }
  }, []);

  return {
    viewport,
    handleDragEnd,
    handleClickAddNode,
    renderComponent,
  };
};
