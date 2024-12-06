export enum TargetTypeEnum {
  SPEC_USERS = 'spec_users',
  DEPARTMENT = 'department',
  OTHER = 'other',
}

export const TargetTypeLabel = {
  [TargetTypeEnum.SPEC_USERS]: 'Khảo sát nhân sự',
  [TargetTypeEnum.DEPARTMENT]: 'Khảo sát phòng ban',
  [TargetTypeEnum.OTHER]: 'Khảo sát khác',
};
