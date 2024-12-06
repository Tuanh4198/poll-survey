export interface IMetafield<K, V = string, T = string> {
  id?: number;
  ownerResource?: string;
  ownerId?: number;
  namespace?: string;
  key?: K;
  value?: V;
  type?: T;
  description?: string;
}
