import { hiddenField1, hiddenField2, hiddenField3 } from 'app/shared/model/survey.model';

export const replaceHiddenFields = ({
  fieldValue,
  field1,
  field2,
  field3,
}: {
  fieldValue: string | undefined;
  field1?: string | null;
  field2?: string | null;
  field3?: string | null;
}) => {
  if (!fieldValue) return '';

  return fieldValue
    .replaceAll(`{{${hiddenField1}}}`, field1 || `{{${hiddenField1}}}`)
    .replaceAll(`{{${hiddenField2}}}`, field2 || `{{${hiddenField2}}}`)
    .replaceAll(`{{${hiddenField3}}}`, field3 || `{{${hiddenField3}}}`);
};
