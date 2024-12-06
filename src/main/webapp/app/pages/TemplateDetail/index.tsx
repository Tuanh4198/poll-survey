/* eslint-disable @typescript-eslint/no-misused-promises */
import React, { useCallback, useMemo } from 'react';
import { FormContextUpdate } from 'app/pages/Form';
import { Navigate, useLocation, useNavigate, useParams } from 'react-router-dom';
import { Routes } from 'app/pages/routes';
import { useFetchTemplateDetail } from 'app/pages/TemplateDetail/hooks/useFetchTemplateDetail';
import { Box, Loader, LoadingOverlay } from '@mantine/core';
import { TFormBody, TTarget } from 'app/pages/Form/context/FormContext';
import { FormTypeEnum } from 'app/pages/Form/helper';
import {
  getBlocks,
  getEvents,
  getIsAnonymous,
  getParticipants,
  getTargetValue,
  getTartget,
} from 'app/shared/util/builder-form-from-data-server';

export const TemplateDetail = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { id } = useParams();

  const goBack = () => {
    if (location.key) {
      navigate(-1);
    } else {
      navigate(Routes.TEMPLATE);
    }
  };

  const { isLoading, data, refetch } = useFetchTemplateDetail({ id });

  const target = useMemo(() => {
    return getTartget(data?.assignStrategies);
  }, [data?.assignStrategies]);

  const targetType = useMemo(() => {
    return target?.[0]?.type;
  }, [target]);

  const participants = useCallback(<T,>() => {
    return getParticipants<T>(data?.assignStrategies);
  }, [data?.assignStrategies]);

  const blocks = useMemo(() => {
    return getBlocks(data?.blocks);
  }, [data?.blocks]);

  const events = useMemo(() => {
    return getEvents(data?.metafields);
  }, [data?.metafields]);

  const initialValues = useMemo((): TFormBody => {
    return {
      id: data?.id,
      title: data?.title,
      description: data?.description,
      thumbUrl: data?.thumbUrl,
      applyTime: undefined,
      endTime: undefined,
      isRequired: false,
      metafields: data?.metafields,
      blocks,
      assignStrategies: [],
      // delete when submit
      events,
      presignThumbUrl: data?.presignThumbUrl,
      targetType,
    };
  }, [data, blocks, events, targetType]);

  const initialTarget = useMemo((): TTarget => {
    return getTargetValue({
      target,
      targetType,
      participants,
    });
  }, [data, targetType, participants]);

  const initialIsAnonymous = useMemo((): boolean => {
    return getIsAnonymous(participants);
  }, [participants]);

  return (
    <Box pos="relative" className="form-wrapper">
      <LoadingOverlay zIndex={10} visible={isLoading} loaderProps={{ children: <Loader /> }} />
      {isLoading ? null : data ? (
        <FormContextUpdate
          isDetail
          type={FormTypeEnum.TEMPLATE}
          goBack={goBack}
          onRefetch={refetch}
          initialTarget={initialTarget}
          initialValues={initialValues}
          initialIsAnonymous={initialIsAnonymous}
        />
      ) : (
        <Navigate to={Routes.TEMPLATE} replace />
      )}
    </Box>
  );
};
