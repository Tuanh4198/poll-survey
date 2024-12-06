import { ISurveySubmit } from 'app/shared/model/survey-submit.model';
import { IBlock } from 'app/shared/model/block.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { IMetafield } from 'app/shared/model/metafields.model';

export enum BlockFieldsMetafieldKey {
  description = 'description',
}

export interface IBlockFields {
  id?: number | string;
  blockId?: number;
  surveyId?: number;
  fieldName?: string;
  fieldValue?: string;
  type?: FieldTypeEnum;
  metafields?: Array<IMetafield<BlockFieldsMetafieldKey>>;
  ids?: ISurveySubmit[];
  block?: IBlock;
  // only create and update, delete when submit
  file?: File | null;
  urlFileUpload?: string;
}
