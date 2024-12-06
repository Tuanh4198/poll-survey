export enum SurveyStatusEnum {
  NOT_ATTENDED = 'NOT_ATTENDED',
  COMPLETED = 'COMPLETED',
  PENDING = 'PENDING',
}

export const SurveyStatusLabel = {
  [SurveyStatusEnum.COMPLETED]: 'Đã tham gia',
  [SurveyStatusEnum.NOT_ATTENDED]: 'Chưa tham gia',
  [SurveyStatusEnum.PENDING]: 'Chờ bắt đầu',
};
