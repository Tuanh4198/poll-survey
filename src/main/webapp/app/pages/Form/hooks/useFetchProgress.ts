import { useQuery } from '@tanstack/react-query';
import { IEmployeeSurvey } from 'app/shared/model/employee-survey.model';
import { SurveyStatusEnum } from 'app/shared/model/enumerations/survey-status-enum.model';
import { DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import axios from 'axios';
import { debounce } from 'lodash';
import { useCallback, useState } from 'react';

export const FetchProgressName = 'FetchProgressName';
export const useFetchProgress = ({ surveyId, defaultStatus }: { surveyId?: string | number; defaultStatus?: SurveyStatusEnum }) => {
  const [params, setParams] = useState<{
    page: number;
    size: number;
    keysearch?: string;
    status?: SurveyStatusEnum;
  }>({
    page: 1,
    size: ITEMS_PER_PAGE,
    keysearch: undefined,
    status: defaultStatus,
  });

  const handleNextPage = useCallback((value: number) => {
    setParams(old => ({
      ...old,
      page: value,
    }));
  }, []);

  const handleStatus = useCallback((value: SurveyStatusEnum) => {
    setParams(old => ({
      ...old,
      page: 1,
      status: value,
    }));
  }, []);

  const handleChangeKeySearch = debounce((value?: string) => {
    setParams(old => ({
      ...old,
      page: 1,
      keysearch: value,
    }));
  }, 500);

  const fetchSurveyList = async () => {
    if (!surveyId) return undefined;
    return await axios.get<IEmployeeSurvey[]>(
      `/api/admin-employee-surveys${params?.keysearch ? `?search=${params.keysearch.trim()}` : ''}`,
      {
        params: {
          surveyId,
          page: params.page - 1,
          size: params.size,
          status: params.status,
          sort: `id,${DESC}`,
        },
      }
    );
  };

  const { isLoading, isFetching, data, refetch } = useQuery({
    queryKey: [FetchProgressName, params, surveyId],
    queryFn: fetchSurveyList,
  });

  return {
    isLoading: isLoading || isFetching,
    data: data?.data,
    total: data?.headers?.xTotalCount,
    page: params.page,
    status: params?.status,
    keysearch: params.keysearch,
    handleNextPage,
    handleStatus,
    handleChangeKeySearch,
    refetch,
  };
};
