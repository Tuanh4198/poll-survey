export enum ParticipantAnonymousType {
  ANONYMOUS = 'ANONYMOUS',
}

export enum ParticipantOtherType {
  OTHER = 'other',
}

export enum ParticipantEmployeeTypeEnumWithFile {
  SPEC_USERS = 'spec_users',
}

export enum ParticipantEmployeeTypeEnumWithoutFile {
  SELF = 'self',
  EMPLOYEE_SAME_DEPARTMENT = 'employee_same_department',
  DEPARTMENT_MANAGER = 'department_manager',
  EMPLOYEE_SAME_LEVEL_SAME_DEPARTMENT = 'employee_same_level_same_department',
  EMPLOYEE_LOWER_LEVEL_SAME_DEPARTMENT = 'employee_lower_level_same_department',
  ANY_USERS = 'any_users',
}

export const CombinedParticipantEmployeeTypeEnum = {
  ...ParticipantEmployeeTypeEnumWithFile,
  ...ParticipantEmployeeTypeEnumWithoutFile,
} as const;

export type ParticipantEmployeeTypeEnum = typeof CombinedParticipantEmployeeTypeEnum[keyof typeof CombinedParticipantEmployeeTypeEnum] &
  ParticipantOtherType &
  ParticipantAnonymousType;

export const ParticipantEmployeeTypeLabel = {
  [CombinedParticipantEmployeeTypeEnum.ANY_USERS]: 'Nhân sự toàn công ty',
  [CombinedParticipantEmployeeTypeEnum.EMPLOYEE_SAME_DEPARTMENT]: 'Nhân sự cùng phòng ban',
  [CombinedParticipantEmployeeTypeEnum.DEPARTMENT_MANAGER]: 'Quản lý trực tiếp theo phòng ban',
  [CombinedParticipantEmployeeTypeEnum.EMPLOYEE_SAME_LEVEL_SAME_DEPARTMENT]: 'Nhân sự cùng cấp cùng phòng ban',
  [CombinedParticipantEmployeeTypeEnum.EMPLOYEE_LOWER_LEVEL_SAME_DEPARTMENT]: 'Nhân sự cấp dưới cùng phòng ban',
  [CombinedParticipantEmployeeTypeEnum.SELF]: 'Nhân sự tự đánh giá',
  [CombinedParticipantEmployeeTypeEnum.SPEC_USERS]: 'Tải lên file nhân sự cụ thể',
};
