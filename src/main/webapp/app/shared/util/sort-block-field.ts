import { IBlockFields } from 'app/shared/model/block-fields.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';

export const sortBlockField = ({
  bfA,
  bfB,
  componentType,
}: {
  bfA: IBlockFields | undefined;
  bfB: IBlockFields | undefined;
  componentType: ComponentTypeEnum;
}) => {
  const isMultipleChoiceA = bfA?.fieldName?.startsWith(`${componentType.toLowerCase()}_`);
  const isMultipleChoiceB = bfB?.fieldName?.startsWith(`${componentType.toLowerCase()}_`);
  if (isMultipleChoiceA && isMultipleChoiceB) {
    const numA = parseInt(bfA?.fieldName?.split('_').pop() || '0', 10);
    const numB = parseInt(bfB?.fieldName?.split('_').pop() || '0', 10);
    return numA - numB;
  }
  if (isMultipleChoiceA) return 1;
  if (isMultipleChoiceB) return -1;
  return 0;
};
