import { exportToExcel } from 'app/shared/util/export-xlxs-file';

export const useExportFileXlxsTemplate = () => {
  const template = [{ USERNAME: 'YD12345' }];

  const mau_danh_gia_nhan_su = [{ 'USERNAME ĐỐI TƯỢNG BỊ ĐÁNH GIÁ': 'YD12345', 'USERNAME ĐỐI TƯỢNG NHẬN ĐÁNH GIÁ': 'YD12345' }];

  const handleExportTemplateTarget = () => {
    exportToExcel(template, 'mau_nhan_su_duoc_khao_sat');
  };

  const handleExportTemplateParticipant = () => {
    exportToExcel(template, 'mau_nhan_su_nhan_khao_sat');
  };

  const handleExportTemplateSurveyEmployee = () => {
    exportToExcel(mau_danh_gia_nhan_su, 'mau_danh_gia_nhan_su');
  };

  return {
    handleExportTemplateTarget,
    handleExportTemplateParticipant,
    handleExportTemplateSurveyEmployee,
  };
};
