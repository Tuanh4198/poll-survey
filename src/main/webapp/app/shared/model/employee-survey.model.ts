import { ISurvey } from 'app/shared/model/survey.model';
import { SurveyStatusEnum } from 'app/shared/model/enumerations/survey-status-enum.model';
import { ParticipantEmployeeTypeEnum } from 'app/shared/model/enumerations/participant-employee-type-enum';
import { IBlock } from 'app/shared/model/block.model';

export interface IEmployeeSurvey {
  id?: number;
  code?: string;
  name?: string;
  surveyId?: number;
  status?: SurveyStatusEnum;
  rootId?: ISurvey;
  participantType?: ParticipantEmployeeTypeEnum;
  target?: string;
  targetType: string;
  targetName: string;
  title?: string | null;
  description?: string | null;
  thumbUrl?: string | null;
  applyTime?: string | null;
  endTime?: string | null;
  isRequired?: boolean | null;
  assignStrategies?: string | null;
  blocks?: IBlock | null;
  metafields?: null;
}
