import { IAssignStrategy } from 'app/shared/model/assign-strategy.model';
import { IBlock } from 'app/shared/model/block.model';
import { IMetafield } from 'app/shared/model/metafields.model';
import { EventValue, SurveyMetafieldKey } from 'app/shared/model/survey.model';

export interface ISurveyTemplate {
  id?: number;
  title?: string;
  description?: string;
  thumbUrl?: string;
  presignThumbUrl?: string;
  usedTime?: number;
  blocks?: IBlock[];
  assignStrategies?: IAssignStrategy[];
  metafields?: Array<IMetafield<SurveyMetafieldKey, EventValue>>;
}
