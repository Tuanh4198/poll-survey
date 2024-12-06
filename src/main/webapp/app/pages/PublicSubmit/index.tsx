/* eslint-disable @typescript-eslint/no-misused-promises */
import React, { Fragment, useCallback, useMemo } from 'react';
import './styles.scss';
import {
  Button,
  Flex,
  Text,
  Pill,
  Box,
  AspectRatio,
  Overlay,
  Image,
  ScrollArea,
  Skeleton,
  LoadingOverlay,
  Loader,
  Container,
  ThemeIcon,
} from '@mantine/core';
import { useParams, useSearchParams } from 'react-router-dom';
import { getBlocks, getIsAnonymous, getParticipants, getTartget } from 'app/shared/util/builder-form-from-data-server';
import { useFetchSurveyDetailByHash } from 'app/pages/PublicSubmit/hooks/useFetchSurveyDetail';
import { FieldSubmit, useHandleSubmitFormPublic } from 'app/pages/PublicSubmit/hooks/useHandleSubmitFormPublic';
import { useHandlePage } from 'app/pages/PublicSubmit/hooks/useHandlePage';
import { useHandleComponents } from 'app/pages/PublicSubmit/hooks/useHandleComponents';
import { hiddenField1, hiddenField2, hiddenField3, ISurvey } from 'app/shared/model/survey.model';
import { replaceHiddenFields } from 'app/shared/util/replace-all-hidden-field';
import { TBlock } from 'app/pages/Form/context/FormContext';
import { IBlock } from 'app/shared/model/block.model';
import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { SurveyNotFound } from 'app/shared/components/SurveyNotFound';
import { compareTimeUtcWithLocalTime } from 'app/pages/Form/helper';
import dayjs from 'dayjs';
import { SurveyNotStart } from 'app/shared/components/SurveyNotStart';
import { SubmitSurveySuccess } from 'app/shared/components/SubmitSurveySuccess';
import { ArrowLeft, ArrowRight, EyeSlash } from '@phosphor-icons/react';
import { SurveyExpired } from 'app/shared/components/SurveyExpired';
import _ from 'lodash';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';

export const PublicSubmit = () => {
  const { hash } = useParams();

  const { isLoading, data, refetch } = useFetchSurveyDetailByHash({ hash });

  const blocks = useMemo(() => {
    return getBlocks(data?.blocks);
  }, [data?.blocks]);

  const { fieldsFilled, submited, submitting, formValueRef, errors, onChangeFieldValue, onValidate, onSubmit } = useHandleSubmitFormPublic({
    blocks,
    hash,
  });

  const content = () => {
    if (isLoading) {
      return (
        <Container size="lg" mt={20}>
          <Flex direction="column" gap={20}>
            {Array.from({ length: 3 }).map((__, i) => (
              <Skeleton key={i} height={100} />
            ))}
          </Flex>
        </Container>
      );
    }
    if (!isLoading && !data) {
      return <SurveyNotFound refetch={refetch} />;
    }
    if (!isLoading && data) {
      return (
        <FormSurvey
          formValueRef={formValueRef}
          errors={errors}
          onChangeFieldValue={onChangeFieldValue}
          onValidate={onValidate}
          onSubmit={onSubmit}
          blocks={blocks}
          data={data}
          submitting={submitting}
          submited={submited}
          fieldsFilled={fieldsFilled}
        />
      );
    }
  };

  return (
    <Box className="submit-page" bg="#F9FAFB">
      <LoadingOverlay zIndex={10} visible={submitting} loaderProps={{ children: <Loader /> }} />
      <Flex bg="#fff" align="center" justify="space-between" px="20px" py="15px" style={{ borderBottom: '1px solid #E5E7EB' }}>
        <Image src={'../../../../content/images/logo.svg'} />
      </Flex>
      {content()}
    </Box>
  );
};

const FormSurvey = ({
  fieldsFilled,
  formValueRef,
  errors,
  onValidate,
  onSubmit,
  blocks,
  data,
  submitting,
  submited,
  onChangeFieldValue,
}: {
  fieldsFilled: (string | number)[];
  formValueRef: React.MutableRefObject<{ [key: number]: FieldSubmit } | undefined>;
  errors: (number | null)[];
  onValidate: (_blocks: IBlock[]) => (number | null)[];
  onSubmit: () => Promise<void>;
  blocks?: TBlock[];
  data?: ISurvey;
  submitting: boolean;
  submited: boolean;
  onChangeFieldValue: ({
    blockId,
    fieldId,
    fieldName,
    fieldValue,
    fieldType,
  }: {
    blockId?: number;
    fieldId: number | null;
    fieldName: string | null;
    fieldValue: string | null;
    fieldType: FieldTypeEnum | null;
  }) => void;
}) => {
  const [searchParams] = useSearchParams();

  const field1 = searchParams.get(hiddenField1);
  const field2 = searchParams.get(hiddenField2);
  const field3 = searchParams.get(hiddenField3);

  const { totalPage, activePageIndex, blockPerPage, onPrevPage, onNextPage } = useHandlePage({
    blocks,
    onValidate,
  });

  const { renderComponent } = useHandleComponents();

  const isStarting = useMemo(
    () => (data?.applyTime ? compareTimeUtcWithLocalTime(dayjs(data?.applyTime).toDate()) : false),
    [data?.applyTime]
  );

  const isExpired = useMemo(() => (data?.endTime ? compareTimeUtcWithLocalTime(dayjs(data?.endTime).toDate()) : false), [data?.endTime]);

  const target = useMemo(() => {
    return getTartget(data?.assignStrategies);
  }, [data?.assignStrategies]);

  const participants = useCallback(<T,>() => {
    return getParticipants<T>(data?.assignStrategies);
  }, [data?.assignStrategies]);

  const initialIsAnonymous = useMemo((): boolean => {
    return getIsAnonymous(participants);
  }, [data?.assignStrategies]);

  const blockIdPerPage = useMemo(() => {
    return Array.isArray(blockPerPage) ? blockPerPage?.map(i => i.id?.toString()) : [];
  }, [blockPerPage]);

  const totalBlockPerPage = useMemo(() => {
    return Array.isArray(blockPerPage) ? blockPerPage?.filter(i => i.type !== ComponentTypeEnum.TITLE)?.length : 0;
  }, [blockPerPage]);

  const totalFieldsFilled = useMemo(() => {
    return _.intersection(fieldsFilled, blockIdPerPage).length;
  }, [fieldsFilled, blockIdPerPage]);

  if (!isStarting) {
    return <SurveyNotStart />;
  }

  if (isExpired) {
    return <SurveyExpired />;
  }

  if (submited) {
    return <SubmitSurveySuccess />;
  }

  return (
    <Fragment>
      <Box pos="relative" mb={20} bg="white">
        <AspectRatio pos="absolute" top={0} left={0} bottom={0} w={`${(activePageIndex / totalPage) * 100}%`} style={{ zIndex: 10 }}>
          <Overlay color="#D0EBFF" w="100%" h="100%" backgroundOpacity={1} />
        </AspectRatio>
        <Flex align="center" justify="center" gap={10} p={10} pos="relative" style={{ zIndex: 10 }}>
          <Text c="#000000" fw={600}>
            Trang {activePageIndex}
          </Text>
          <Pill radius={24} c="#000000" bg="#FAB005">
            {totalFieldsFilled}/{totalBlockPerPage}
          </Pill>
        </Flex>
      </Box>
      <Container size="lg" h="calc(100vh - 100px)">
        <ScrollArea m="auto" p="0px 15px" h="100%" scrollbars="y">
          <Flex gap={20} direction="column" align="flex-start">
            {data?.presignThumbUrl ? (
              <AspectRatio ratio={2 / 1} w="100%">
                <Image src={data?.presignThumbUrl} fallbackSrc="../../../../../content/images/poll-survey.jpg" fit="cover" />
              </AspectRatio>
            ) : null}
            {data && 'isRequired' in data && data.isRequired ? (
              <Pill c="#FFFFFF" fw={500} fz={14} bg="#FD7E14">
                Bắt buộc
              </Pill>
            ) : (
              <Pill c="#FFFFFF" fw={500} fz={14} bg="#12B886">
                Không bắt buộc
              </Pill>
            )}
            {initialIsAnonymous && (
              <Flex
                w="100%"
                p="12px 14px"
                bg="white"
                align="center"
                gap={16}
                style={{ boxShadow: '0px 1px 2px 0px rgba(0, 0, 0, 0.10), 0px 1px 3px 0px rgba(0, 0, 0, 0.05)', borderRadius: '4px' }}
              >
                <ThemeIcon radius="50%" bg="#228BE6">
                  <EyeSlash size={16} weight="fill" color="#E7F5FF" />
                </ThemeIcon>
                <Flex direction="column">
                  <Text fw={600} fz={14} c="#111928">
                    Khảo sát này là ẩn danh.
                  </Text>
                  <Text fw={400} fz={14} c="#4B5563">
                    Thông tin của bạn được giữ bí mật.
                  </Text>
                </Flex>
              </Flex>
            )}
            {data?.title && (
              <Text c="#1F2A37" fw={700} style={{ fontSize: '20px' }}>
                {replaceHiddenFields({
                  fieldValue: data?.title,
                  field1,
                  field2,
                  field3,
                })}
              </Text>
            )}
            {data?.description && (
              <Text c="#1F2A37" fw={400} style={{ fontSize: '14px' }}>
                {replaceHiddenFields({
                  fieldValue: data?.description,
                  field1,
                  field2,
                  field3,
                })}
              </Text>
            )}
            {target?.[0]?.value && (
              <Text c="#1F2A37" fw={700} style={{ fontSize: '16px' }}>
                Đối tượng đánh giá:{' '}
                {replaceHiddenFields({
                  fieldValue: target?.[0]?.value,
                  field1,
                  field2,
                  field3,
                })}
              </Text>
            )}
            {blockPerPage &&
              blockPerPage.length > 0 &&
              blockPerPage.map(item => (
                <Fragment key={item.id}>
                  {renderComponent({
                    block: item,
                    componentType: item.type,
                    onChangeFieldValue,
                    defaultValue: item.id ? formValueRef.current?.[item.id] : undefined,
                    isError: item.id ? errors.includes(Number(item.id)) : undefined,
                  })}
                </Fragment>
              ))}
            <Flex gap={10} align="center" justify="space-between" mt={20} w="100%" mb={50}>
              {activePageIndex > 1 ? (
                <Button
                  size="md"
                  radius={4}
                  disabled={submitting}
                  onClick={onPrevPage}
                  leftSection={<ArrowLeft size={16} />}
                  variant="outline"
                >
                  {activePageIndex === totalPage ? 'Trở lại' : 'Trang trước'}
                </Button>
              ) : (
                <div />
              )}
              {activePageIndex < totalPage && (
                <Button size="md" radius={4} disabled={submitting} onClick={onNextPage} rightSection={<ArrowRight size={16} />}>
                  Tiếp theo
                </Button>
              )}
              {activePageIndex === totalPage && (
                <Button size="md" radius={4} loading={submitting} onClick={onSubmit} rightSection={<ArrowRight size={16} />}>
                  Gửi
                </Button>
              )}
            </Flex>
          </Flex>
        </ScrollArea>
      </Container>
    </Fragment>
  );
};
