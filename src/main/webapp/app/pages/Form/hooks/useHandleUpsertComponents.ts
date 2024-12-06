import { TBlockField, TFormBody, TPage } from 'app/pages/Form/context/FormContext';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { v4 as uuidv4 } from 'uuid';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { BlockMetafieldKey, BlockMetafieldRequiredValue } from 'app/shared/model/block.model';
import { IMetafield } from 'app/shared/model/metafields.model';
import { BlockFieldsMetafieldKey } from 'app/shared/model/block-fields.model';
import { UseFormReturnType } from '@mantine/form';
import { errorBlockLabel, errorEachBlockLabel, fieldValidate } from 'app/pages/Form/hooks/useValidateForm';
import { validateBlockOnPage } from 'app/pages/Form/helper';
import { useStagedFile } from 'app/pages/Form/hooks/useStagedFile';

export const prefixId = 'new-';

const defaultBlockMetafields: Array<IMetafield<BlockMetafieldKey, BlockMetafieldRequiredValue>> = [
  {
    key: BlockMetafieldKey.required,
    value: BlockMetafieldRequiredValue.false,
  },
];

const defaultBlockFieldsMetafields: Array<IMetafield<BlockFieldsMetafieldKey>> = [
  {
    key: BlockFieldsMetafieldKey.description,
    value: undefined,
  },
];

const defaultBlockFieldsTitle = {
  fieldName: ComponentTypeEnum.TITLE.toLowerCase(),
  fieldValue: '',
  type: FieldTypeEnum.TEXT,
};

const defaultValueMultipleChoiceOption = (i: number, id: string) => ({
  id,
  fieldName: `${ComponentTypeEnum.MULTIPLE_CHOICE.toLowerCase()}_${i + 1}`,
  fieldValue: undefined,
  type: FieldTypeEnum.MULTIPLE_CHOICE_OPTION,
});

const defaultValuePointScaleOption = (i: number) => ({
  fieldName: `${ComponentTypeEnum.POINT_SCALE.toLowerCase()}_${i + 1}`,
  fieldValue: (i + 1).toString(),
  metafields: defaultBlockFieldsMetafields,
  type: FieldTypeEnum.POINT_SCALE_OPTION,
});

const defaultValueStarOption = (i: number) => ({
  fieldName: `${ComponentTypeEnum.STAR.toLowerCase()}_${i + 1}`,
  fieldValue: (i + 1).toString(),
  metafields: defaultBlockFieldsMetafields,
  type: FieldTypeEnum.STAR_OPTION,
});

const defaultValueEssay = {
  fieldName: ComponentTypeEnum.ESSAY.toLowerCase(),
  fieldValue: '',
  metafields: defaultBlockFieldsMetafields,
  type: FieldTypeEnum.TEXT_INPUT,
};

const defaultBlockFields: Record<ComponentTypeEnum, TBlockField[]> = {
  [ComponentTypeEnum.MULTIPLE_CHOICE]: [
    defaultBlockFieldsTitle,
    ...Array.from({ length: 4 }).map((_, i) => defaultValueMultipleChoiceOption(i, `${prefixId}${uuidv4()}`)),
  ],
  [ComponentTypeEnum.POINT_SCALE]: [defaultBlockFieldsTitle, ...Array.from({ length: 10 }).map((_, i) => defaultValuePointScaleOption(i))],
  [ComponentTypeEnum.ESSAY]: [defaultBlockFieldsTitle, defaultValueEssay],
  [ComponentTypeEnum.STAR]: [defaultBlockFieldsTitle, ...Array.from({ length: 5 }).map((_, i) => defaultValueStarOption(i))],
  [ComponentTypeEnum.TITLE]: [defaultBlockFieldsTitle],
};

export const useHandleUpsertComponents = ({
  pages,
  setPages,
  form,
}: {
  pages: TPage;
  setPages: React.Dispatch<React.SetStateAction<TPage>>;
  form: UseFormReturnType<TFormBody, (values: TFormBody) => TFormBody>;
}) => {
  const { onStagedFile } = useStagedFile();

  // global block
  const onAddBlock = ({ num, pageNum, pageId, type }: { num: number; pageNum: number; pageId: string; type: ComponentTypeEnum }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      let newBlocks = newPages?.[pageId] ? Array.from(newPages?.[pageId]) : [];
      newBlocks.splice(num, 0, {
        id: `${prefixId}${uuidv4()}`,
        num,
        pageNum,
        pageId,
        type,
        blockFields: defaultBlockFields[type],
        metafields: type !== ComponentTypeEnum.TITLE ? defaultBlockMetafields : undefined,
      });
      newBlocks = newBlocks.map((item, index) => ({
        ...item,
        num: index + 1,
      }));
      newPages = { ...newPages, [pageId]: newBlocks };
      validateBlockOnPage({
        pages,
        onOk() {
          form.clearFieldError(errorBlockLabel);
        },
      });
      return newPages;
    });
  };

  const onDuplicateBlock = ({ id, pageId }: { id?: string | number; pageId: string }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      let newBlocks = newPages?.[pageId] ? Array.from(newPages?.[pageId]) : [];
      const currentBlock = newBlocks.find(i => i.id === id);
      if (!currentBlock) return newPages;
      newBlocks.splice(currentBlock.num || 0, 0, {
        id: `${prefixId}${uuidv4()}`,
        num: 0,
        pageNum: currentBlock.pageNum,
        pageId: currentBlock.pageId,
        type: currentBlock.type,
        blockFields: defaultBlockFields[currentBlock.type || ComponentTypeEnum.TITLE],
        metafields: currentBlock.type !== ComponentTypeEnum.TITLE ? defaultBlockMetafields : undefined,
      });
      newBlocks = newBlocks.map((item, index) => ({
        ...item,
        num: index + 1,
      }));
      newPages = { ...newPages, [pageId]: newBlocks };
      validateBlockOnPage({
        pages,
        onOk() {
          form.clearFieldError(errorBlockLabel);
        },
      });
      return newPages;
    });
  };

  const onRemoveBlock = ({ id, pageId, type }: { id?: string | number; pageId: string; type: ComponentTypeEnum }) => {
    setPages(oldPages => {
      const newPages = {
        ...oldPages,
        [pageId]: oldPages?.[pageId]
          ?.filter(i => i.id?.toString() !== id?.toString())
          ?.map((_, i) => ({
            ..._,
            num: i + 1,
          })),
      };
      form.clearFieldError(
        errorEachBlockLabel({ id: id?.toString(), componentType: type || ComponentTypeEnum.TITLE, errType: fieldValidate.title })
      );
      if (type === ComponentTypeEnum.MULTIPLE_CHOICE) {
        form.clearFieldError(errorEachBlockLabel({ id: id?.toString(), componentType: type, errType: fieldValidate.description }));
      }
      return newPages;
    });
  };

  const onReorderBlock = ({ destinationIndex, sourceIndex, pageId }: { destinationIndex: number; sourceIndex: number; pageId: string }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      let newBlocks = newPages?.[pageId] ? Array.from(newPages?.[pageId]) : [];
      const [movedItem] = newBlocks.splice(sourceIndex, 1);
      newBlocks.splice(destinationIndex, 0, movedItem);
      newBlocks = newBlocks.map((item, index) => ({
        ...item,
        num: index + 1,
      }));
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  const onChangeFieldRequired = ({ id, pageId, required }: { id?: string | number; pageId: string; required: boolean }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      const newBlocks = Array.from(newPages[pageId]).map(i => {
        if (i.id === id) {
          return {
            ...i,
            metafields: i?.metafields?.map(mf => {
              if (mf.key === BlockMetafieldKey.required) {
                return {
                  ...mf,
                  value: required ? BlockMetafieldRequiredValue.true : BlockMetafieldRequiredValue.false,
                };
              }
              return mf;
            }),
          };
        }
        return i;
      });
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  const onChangeFieldTitle = ({ id, pageId, title }: { id?: string | number; pageId: string; title: string }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      const newBlocks = Array.from(newPages[pageId]).map(i => {
        if (i.id === id) {
          form.clearFieldError(
            errorEachBlockLabel({ id: id?.toString(), componentType: i.type || ComponentTypeEnum.TITLE, errType: fieldValidate.title })
          );
          return {
            ...i,
            blockFields: i.blockFields?.map(bf => {
              if (bf.type === FieldTypeEnum.TEXT && bf.fieldName === ComponentTypeEnum.TITLE.toLowerCase()) {
                return { ...bf, fieldValue: title };
              }
              return bf;
            }),
          };
        }
        return i;
      });
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  // image field
  const onAddFieldImage = async ({ id, pageId, file }: { id?: string | number; pageId: string; file?: File | null }) => {
    if (file) {
      const stagedRes = await onStagedFile({ files: [file] });
      setPages(oldPages => {
        let newPages = structuredClone(oldPages) as TPage;
        const newBlocks = Array.from(newPages[pageId]).map(i => {
          if (i.id === id) {
            return {
              ...i,
              blockFields: [
                ...(i.blockFields || []),
                {
                  id: `${prefixId}${uuidv4()}`,
                  fieldName: ComponentTypeEnum.TITLE.toLowerCase(),
                  fieldValue: stagedRes?.url,
                  type: FieldTypeEnum.IMAGE,
                  urlFileUpload: stagedRes?.resourceUrl,
                  file,
                },
              ],
            };
          }
          return i;
        });
        newPages = { ...newPages, [pageId]: newBlocks };
        return newPages;
      });
    }
  };

  const onChangeFieldImage = async ({ id, pageId, file }: { id?: string | number; pageId: string; file?: File | null }) => {
    if (file) {
      const stagedRes = await onStagedFile({ files: [file] });
      setPages(oldPages => {
        let newPages = structuredClone(oldPages) as TPage;
        const newBlocks = Array.from(newPages[pageId]).map(i => {
          if (i.id === id) {
            return {
              ...i,
              blockFields: i.blockFields?.map(bf => {
                if (bf.type === FieldTypeEnum.IMAGE) {
                  return { ...bf, file, fieldValue: stagedRes?.url, urlFileUpload: stagedRes?.resourceUrl };
                }
                return bf;
              }),
            };
          }
          return i;
        });
        newPages = { ...newPages, [pageId]: newBlocks };
        return newPages;
      });
    }
  };

  const onRemoveFieldImage = ({ id, pageId, blockFieldId }: { id?: string | number; pageId: string; blockFieldId?: string }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      const newBlocks = Array.from(newPages[pageId]).map(i => {
        if (i.id === id) {
          return {
            ...i,
            blockFields: i.blockFields?.filter(bf => {
              if (bf.type === FieldTypeEnum.IMAGE && blockFieldId === bf.id?.toString()) {
                return false;
              }
              return true;
            }),
          };
        }
        return i;
      });
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  // point scale block
  const onChangePointScaleOption = ({ id, pageId, value }: { id?: string | number; pageId: string; value: number }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      const newBlocks = Array.from(newPages[pageId]).map(i => {
        if (i.id === id) {
          return {
            ...i,
            blockFields: [
              ...(i.blockFields?.filter(mf => mf.type !== FieldTypeEnum.POINT_SCALE_OPTION) || []),
              ...Array.from({ length: value }).map((_, index) => defaultValuePointScaleOption(index)),
            ],
          };
        }
        return i;
      });
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  // essay block
  const onChangeEssayFieldType = ({ id, pageId, type }: { id?: string | number; pageId: string; type: FieldTypeEnum }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      const newBlocks = Array.from(newPages[pageId]).map(i => {
        if (i.id === id) {
          return {
            ...i,
            blockFields: i.blockFields?.map(bf => {
              if (bf.fieldName === ComponentTypeEnum.ESSAY.toLowerCase()) {
                return { ...bf, type };
              }
              return bf;
            }),
          };
        }
        return i;
      });
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  // star block
  const onChangeDescriptionStar = ({ id, pageId, value, desc }: { id?: string | number; pageId: string; value: string; desc: string }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      const newBlocks = Array.from(newPages[pageId]).map(i => {
        if (i.id === id) {
          return {
            ...i,
            blockFields: i.blockFields?.map(bf => {
              if (bf.type === FieldTypeEnum.STAR_OPTION && bf.fieldValue === value) {
                return {
                  ...bf,
                  metafields: bf.metafields?.map(mf => {
                    if (mf.key === BlockFieldsMetafieldKey.description) {
                      return { ...mf, value: desc };
                    }
                    return mf;
                  }),
                };
              }
              return bf;
            }),
          };
        }
        return i;
      });
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  // multiple choice block
  const onAddMultipleChoiceOption = ({ id, pageId }: { id?: string | number; pageId: string }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      const newBlocks = Array.from(newPages[pageId]).map(i => {
        if (i.id === id) {
          const newMultipleChoiceOption = i.blockFields?.filter(mf => mf.type === FieldTypeEnum.MULTIPLE_CHOICE_OPTION) || [];
          newMultipleChoiceOption.push(
            defaultValueMultipleChoiceOption(newMultipleChoiceOption?.length + 1 || 1, `${prefixId}${uuidv4()}`)
          );
          return {
            ...i,
            blockFields: [
              ...(i.blockFields?.filter(mf => mf.type !== FieldTypeEnum.MULTIPLE_CHOICE_OPTION) || []),
              ...newMultipleChoiceOption.map((bf, bfIndex) => {
                if (bf.type === FieldTypeEnum.MULTIPLE_CHOICE_OPTION) {
                  return { ...bf, fieldName: `${ComponentTypeEnum.MULTIPLE_CHOICE.toLowerCase()}_${bfIndex + 1}` };
                }
                return bf;
              }),
            ],
          };
        }
        return i;
      });
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  const onToggleOwnAnswerOnMultipleChoice = ({ id, pageId }: { id?: string | number; pageId: string }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      const newBlocks = Array.from(newPages[pageId]).map(i => {
        if (i.id === id) {
          return {
            ...i,
            blockFields: i.blockFields?.find(bf => bf.type === defaultValueEssay.type)
              ? i.blockFields.filter(bf => bf.type !== defaultValueEssay.type)
              : [...(i.blockFields || []), defaultValueEssay],
          };
        }
        return i;
      });
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  const onRemoveMultipleChoiceOption = ({ id, pageId, blockFieldId }: { id?: string | number; pageId: string; blockFieldId: string }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      const newBlocks = Array.from(newPages[pageId]).map(i => {
        if (i.id === id) {
          let newMultipleChoiceOption = i.blockFields?.filter(mf => mf.type === FieldTypeEnum.MULTIPLE_CHOICE_OPTION) || [];
          newMultipleChoiceOption = newMultipleChoiceOption
            ?.filter(bf => bf.id?.toString() !== blockFieldId?.toString())
            ?.map((bf, bfIndex) => {
              if (bf.type === FieldTypeEnum.MULTIPLE_CHOICE_OPTION) {
                return { ...bf, fieldName: `${ComponentTypeEnum.MULTIPLE_CHOICE.toLowerCase()}_${bfIndex + 1}` };
              }
              return bf;
            });
          return {
            ...i,
            blockFields: [
              ...(i.blockFields?.filter(mf => mf.type !== FieldTypeEnum.MULTIPLE_CHOICE_OPTION) || []),
              ...newMultipleChoiceOption,
            ],
          };
        }
        return i;
      });
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  const onChangeMultipleChoiceOptionValue = ({
    id,
    pageId,
    blockFieldId,
    value,
  }: {
    id?: string | number;
    pageId: string;
    blockFieldId: string;
    value: string;
  }) => {
    setPages(oldPages => {
      let newPages = structuredClone(oldPages) as TPage;
      const newBlocks = Array.from(newPages[pageId]).map(i => {
        if (i.id === id) {
          const newBlockFields = i.blockFields?.map(bf => {
            if (bf.id?.toString() === blockFieldId) {
              return { ...bf, fieldValue: value };
            }
            return bf;
          });
          if (
            !newBlockFields?.some(bf => {
              if (bf.type === FieldTypeEnum.MULTIPLE_CHOICE_OPTION) {
                return bf.fieldValue == null || bf.fieldValue.trim() === '';
              }
              return false;
            })
          ) {
            form.clearFieldError(
              errorEachBlockLabel({
                id: i.id?.toString(),
                componentType: i.type || ComponentTypeEnum.TITLE,
                errType: fieldValidate.description,
              })
            );
          }
          return {
            ...i,
            blockFields: newBlockFields,
          };
        }
        return i;
      });
      newPages = { ...newPages, [pageId]: newBlocks };
      return newPages;
    });
  };

  return {
    onAddBlock,
    onDuplicateBlock,
    onRemoveBlock,
    onReorderBlock,
    onAddFieldImage,
    onChangeFieldImage,
    onRemoveFieldImage,
    onChangePointScaleOption,
    onChangeEssayFieldType,
    onAddMultipleChoiceOption,
    onRemoveMultipleChoiceOption,
    onChangeMultipleChoiceOptionValue,
    onChangeFieldRequired,
    onChangeFieldTitle,
    onChangeDescriptionStar,
    onToggleOwnAnswerOnMultipleChoice,
  };
};
