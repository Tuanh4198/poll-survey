import { IAssignStrategy } from 'app/shared/model/assign-strategy.model';
import { IEmployeeSurvey } from 'app/shared/model/employee-survey.model';
import { IBlock } from 'app/shared/model/block.model';
import { ISurveySubmit } from 'app/shared/model/survey-submit.model';
import { IMetafield } from 'app/shared/model/metafields.model';

export enum EventValueOther {
  OTHER = 'OTHER',
}

export enum EventValue {
  CHECK_IN = 'CHECK_IN',
  APPLICATION_CREATE = 'APPLICATION_CREATE',
  ORDER_CREATE = 'ORDER_CREATE',
}

export const EventLabel = {
  [EventValue.CHECK_IN]: 'Sau khi chấm công xong',
  [EventValue.APPLICATION_CREATE]: 'Sau khi tạo đơn từ thành công',
  [EventValue.ORDER_CREATE]: 'Sau khi tạo đơn hàng',
};

export enum SurveyMetafieldKey {
  events = 'events',
  hidden_field = 'hidden_field',
}

export interface ISurvey {
  id?: number;
  title?: string;
  description?: string;
  thumbUrl?: string;
  presignThumbUrl?: string;
  applyTime?: string;
  endTime?: string;
  isRequired?: boolean;
  assignStrategies?: IAssignStrategy[];
  surveys?: IEmployeeSurvey[];
  blocks?: IBlock[];
  surveySubmits?: ISurveySubmit[];
  metafields?: Array<IMetafield<SurveyMetafieldKey, EventValue | EventValueOther>>;
}

export const hiddenField1 = `${SurveyMetafieldKey.hidden_field}1`;

export const hiddenField2 = `${SurveyMetafieldKey.hidden_field}2`;

export const hiddenField3 = `${SurveyMetafieldKey.hidden_field}3`;
