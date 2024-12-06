import React, { useCallback } from 'react';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { TBlock } from 'app/pages/Form/context/FormContext';
import { ComponentTypeMultipleChoice } from 'app/shared/components/ComponentType/ComponentTypeMultipleChoice';
import { ComponentTypeEssay } from 'app/shared/components/ComponentType/ComponentTypeEssay';
import { ComponentTypeStar } from 'app/shared/components/ComponentType/ComponentTypeStar';
import { ComponentTypePointScale } from 'app/shared/components/ComponentType/ComponentTypePointScale';
import { ComponentTypeTitle } from 'app/shared/components/ComponentType/ComponentTypeTitle';

export const useHandleComponents = () => {
  const renderComponent = useCallback(({ block, componentType }: { block?: TBlock; componentType?: ComponentTypeEnum }) => {
    if (!block || !componentType) return null;
    switch (componentType) {
      case ComponentTypeEnum.MULTIPLE_CHOICE:
        return <ComponentTypeMultipleChoice block={block} />;
      case ComponentTypeEnum.ESSAY:
        return <ComponentTypeEssay block={block} />;
      case ComponentTypeEnum.STAR:
        return <ComponentTypeStar block={block} />;
      case ComponentTypeEnum.POINT_SCALE:
        return <ComponentTypePointScale block={block} />;
      case ComponentTypeEnum.TITLE:
        return <ComponentTypeTitle block={block} />;
      default:
        return null;
    }
  }, []);

  return {
    renderComponent,
  };
};
