import { IBlockFields } from 'app/shared/model/block-fields.model';
import { IBlock } from 'app/shared/model/block.model';
import { ISurvey } from 'app/shared/model/survey.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';

export interface ISurveySubmit {
  id?: number;
  code?: string;
  name?: string;
  surveyId?: number;
  blockId?: number;
  type?: ComponentTypeEnum;
  fieldId?: number;
  fieldName?: string;
  fieldValue?: string;
  blockField?: IBlockFields;
  block?: IBlock;
  survey?: ISurvey;
}
