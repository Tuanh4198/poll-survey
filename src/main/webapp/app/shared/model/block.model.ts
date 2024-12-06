import { IBlockFields } from 'app/shared/model/block-fields.model';
import { ISurveySubmit } from 'app/shared/model/survey-submit.model';
import { ISurvey } from 'app/shared/model/survey.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { IMetafield } from 'app/shared/model/metafields.model';

export enum BlockMetafieldKey {
  required = 'required',
}

export enum BlockMetafieldRequiredValue {
  false = 'false',
  true = 'true',
}

export interface IBlock {
  id?: string | number;
  type?: ComponentTypeEnum;
  surveyId?: number;
  pageNum?: number;
  num?: number;
  blockFields?: IBlockFields[];
  surveySubmits?: ISurveySubmit[];
  survey?: ISurvey;
  metafields?: Array<IMetafield<BlockMetafieldKey, BlockMetafieldRequiredValue>>;
}
