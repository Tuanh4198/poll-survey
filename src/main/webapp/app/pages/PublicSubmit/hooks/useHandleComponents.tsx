import React, { useCallback } from 'react';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { TBlock } from 'app/pages/Form/context/FormContext';
import { ComponentTypeMultipleChoice } from 'app/shared/components/ComponentType/ComponentTypeMultipleChoice';
import { ComponentTypeEssay } from 'app/shared/components/ComponentType/ComponentTypeEssay';
import { ComponentTypeStar } from 'app/shared/components/ComponentType/ComponentTypeStar';
import { ComponentTypePointScale } from 'app/shared/components/ComponentType/ComponentTypePointScale';
import { ComponentTypeTitle } from 'app/shared/components/ComponentType/ComponentTypeTitle';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';

export const useHandleComponents = () => {
  const renderComponent = useCallback(
    ({
      block,
      componentType,
      onChangeFieldValue,
      isError,
      defaultValue,
    }: {
      block?: TBlock;
      componentType?: ComponentTypeEnum;
      isError?: boolean;
      defaultValue?: {
        blockId?: number;
        fieldId: number | null;
        fieldName: string | null;
        fieldValue: string | null;
        fieldType: FieldTypeEnum | null;
      };
      onChangeFieldValue?: ({
        blockId,
        fieldId,
        fieldName,
        fieldValue,
        fieldType,
      }: {
        blockId?: number;
        fieldId: number | null;
        fieldName: string | null;
        fieldValue: string | null;
        fieldType: FieldTypeEnum | null;
      }) => void;
    }) => {
      if (!block || !componentType) return null;
      switch (componentType) {
        case ComponentTypeEnum.ESSAY:
          return <ComponentTypeEssay defaultValue={defaultValue} block={block} isError={isError} onChangeFieldValue={onChangeFieldValue} />;
        case ComponentTypeEnum.STAR:
          return <ComponentTypeStar defaultValue={defaultValue} block={block} isError={isError} onChangeFieldValue={onChangeFieldValue} />;
        case ComponentTypeEnum.POINT_SCALE:
          return (
            <ComponentTypePointScale defaultValue={defaultValue} block={block} isError={isError} onChangeFieldValue={onChangeFieldValue} />
          );
        case ComponentTypeEnum.MULTIPLE_CHOICE:
          return (
            <ComponentTypeMultipleChoice
              defaultValue={defaultValue}
              block={block}
              isError={isError}
              onChangeFieldValue={onChangeFieldValue}
            />
          );
        case ComponentTypeEnum.TITLE:
          return <ComponentTypeTitle block={block} />;
        default:
          return null;
      }
    },
    []
  );

  return {
    renderComponent,
  };
};
