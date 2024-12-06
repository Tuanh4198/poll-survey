import { useQuery } from '@tanstack/react-query';
import { ISurvey } from 'app/shared/model/survey.model';
import { DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import axios from 'axios';
import { debounce } from 'lodash';
import { useCallback, useState } from 'react';

export const FetchSurveyListName = 'FetchSurveyListName';
export const useFetchSurvey = () => {
  const [params, setParams] = useState<{
    page: number;
    size: number;
    title?: string;
  }>({
    page: 1,
    size: ITEMS_PER_PAGE,
    title: undefined,
  });

  const handleNextPage = useCallback((value: number) => {
    setParams(old => ({
      ...old,
      page: value,
    }));
  }, []);

  const handleChangeKeySearch = debounce((value?: string) => {
    setParams(old => ({
      ...old,
      page: 1,
      title: value,
    }));
  }, 500);

  const fetchSurveyList = async () => {
    return await axios.get<ISurvey[]>(`/api/surveys${params?.title ? `?title.contains=${params.title.trim()}` : ''}`, {
      params: {
        page: params.page - 1,
        size: params.size,
        sort: `id,${DESC}`,
      },
    });
  };

  const { isLoading, isFetching, data, refetch } = useQuery({
    queryKey: [FetchSurveyListName, params],
    queryFn: fetchSurveyList,
  });

  return {
    isLoading: isLoading || isFetching,
    data: data?.data,
    total: data?.headers?.xTotalCount,
    page: params.page,
    keysearch: params.title,
    handleNextPage,
    handleChangeKeySearch,
    refetch,
  };
};
