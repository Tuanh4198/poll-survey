import { ParticipantEmployeeTypeEnum } from 'app/shared/model/enumerations/participant-employee-type-enum';
import { TargetTypeEnum } from 'app/shared/model/enumerations/target-type-enum';
import { IMetafield } from 'app/shared/model/metafields.model';
import { ISurvey } from 'app/shared/model/survey.model';

export enum AssignStrategyMetafieldKey {
  participants = 'participants',
  targets = 'targets',
}

export interface IAssignStrategy {
  id?: number;
  surveyId?: number;
  survey?: ISurvey;
  metafields?:
    | Array<IMetafield<AssignStrategyMetafieldKey.targets, string, TargetTypeEnum>>
    | Array<IMetafield<AssignStrategyMetafieldKey.participants, string, ParticipantEmployeeTypeEnum>>;
}
