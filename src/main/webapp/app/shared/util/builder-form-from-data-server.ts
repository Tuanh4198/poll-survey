import { TBlock, TTarget } from 'app/pages/Form/context/FormContext';
import { AssignStrategyMetafieldKey, IAssignStrategy } from 'app/shared/model/assign-strategy.model';
import { IBlock } from 'app/shared/model/block.model';
import { ParticipantDepartmentTypeEnum } from 'app/shared/model/enumerations/articipant-department-type-enum.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import {
  ParticipantAnonymousType,
  ParticipantEmployeeTypeEnum,
  ParticipantOtherType,
} from 'app/shared/model/enumerations/participant-employee-type-enum';
import { TargetTypeEnum } from 'app/shared/model/enumerations/target-type-enum';
import { IMetafield } from 'app/shared/model/metafields.model';
import { EventValue, EventValueOther, SurveyMetafieldKey } from 'app/shared/model/survey.model';
import { sortBlockField } from 'app/shared/util/sort-block-field';
import { v4 as uuidv4 } from 'uuid';

export const getTartget = (assignStrategies: IAssignStrategy[] | undefined) => {
  return assignStrategies
    ?.map(a => a.metafields)
    ?.flat()
    ?.filter(mf => mf?.key === AssignStrategyMetafieldKey.targets) as IMetafield<
    AssignStrategyMetafieldKey.targets,
    string,
    TargetTypeEnum
  >[];
};

export const getParticipants = <T>(assignStrategies: IAssignStrategy[] | undefined) => {
  return assignStrategies
    ?.map(a => a.metafields)
    ?.flat()
    ?.filter(mf => mf?.key === AssignStrategyMetafieldKey.participants) as IMetafield<AssignStrategyMetafieldKey.participants, string, T>[];
};

export const getBlocks = (blocks: IBlock[] | undefined): TBlock[] | undefined => {
  const pageNumToIdMap: { [key: number]: string } = {};
  return blocks
    ?.map(b => {
      if (!pageNumToIdMap[b.pageNum || 0]) {
        pageNumToIdMap[b.pageNum || 0] = uuidv4();
      }
      let blockFields = b.blockFields;
      if (b.type === ComponentTypeEnum.MULTIPLE_CHOICE) {
        blockFields = b.blockFields?.sort((bfA, bfB) => sortBlockField({ bfA, bfB, componentType: ComponentTypeEnum.MULTIPLE_CHOICE }));
      }
      if (b.type === ComponentTypeEnum.STAR) {
        blockFields = b.blockFields?.sort((bfA, bfB) => sortBlockField({ bfA, bfB, componentType: ComponentTypeEnum.STAR }));
      }
      return {
        ...b,
        blockFields,
        pageId: pageNumToIdMap[b.pageNum || 0],
      };
    })
    .sort((a, b) => (a.num || 0) - (b.num || 0));
};

export const getEvents = (metafields: IMetafield<SurveyMetafieldKey, EventValue | EventValueOther, string>[] | undefined) => {
  return metafields?.find(mf => mf.key === SurveyMetafieldKey.events)?.value;
};

export const getTargetValue = ({
  target,
  targetType,
  participants,
}: {
  target: IMetafield<AssignStrategyMetafieldKey.targets, string, TargetTypeEnum>[];
  targetType: TargetTypeEnum | undefined;
  participants: <T>() => IMetafield<AssignStrategyMetafieldKey.participants, string, T>[];
}) => {
  let res: TTarget = {
    [TargetTypeEnum.SPEC_USERS]: undefined,
    [TargetTypeEnum.DEPARTMENT]: undefined,
    [TargetTypeEnum.OTHER]: undefined,
  };
  switch (targetType) {
    case TargetTypeEnum.SPEC_USERS: {
      res = {
        ...res,
        [TargetTypeEnum.SPEC_USERS]: {
          file: undefined,
          value: target?.[0]?.value,
          participants: participants<ParticipantEmployeeTypeEnum>()?.map(p => {
            if (p?.type) {
              return {
                id: p?.id || uuidv4(),
                type: p.type,
                file: undefined,
                value: p.value,
              };
            } else {
              return {
                id: p?.id || uuidv4(),
                type: p?.type,
              };
            }
          }),
        },
      };
      break;
    }
    case TargetTypeEnum.DEPARTMENT: {
      res = {
        ...res,
        [TargetTypeEnum.DEPARTMENT]: {
          value: target?.[0]?.value,
          participants: {
            [ParticipantDepartmentTypeEnum.EMPLOYEE_SAME_DEPARTMENT]: !!participants<ParticipantDepartmentTypeEnum>()?.find(
              i => i.type === ParticipantDepartmentTypeEnum.EMPLOYEE_SAME_DEPARTMENT
            ),
            [ParticipantDepartmentTypeEnum.EMPLOYEE_OTHER_DEPARTMENT]: !!participants<ParticipantDepartmentTypeEnum>()?.find(
              i => i.type === ParticipantDepartmentTypeEnum.EMPLOYEE_OTHER_DEPARTMENT
            ),
          },
        },
      };
      break;
    }
    case TargetTypeEnum.OTHER: {
      res = {
        ...res,
        [TargetTypeEnum.OTHER]: {
          value: target?.[0]?.value,
          participants: {
            file: undefined,
            value: participants<ParticipantOtherType>()?.[0]?.value,
          },
        },
      };
      break;
    }
    default:
      break;
  }
  return res;
};

export const getIsAnonymous = (participants: <T>() => IMetafield<AssignStrategyMetafieldKey.participants, string, T>[]): boolean => {
  return participants()?.some(i => i.type === ParticipantAnonymousType.ANONYMOUS);
};
