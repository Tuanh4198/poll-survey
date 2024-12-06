import { useQuery } from '@tanstack/react-query';
import { ISurveyTemplate } from 'app/shared/model/survey-template.model';
import { DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import axios from 'axios';
import { useCallback, useState } from 'react';

export const FetchSurveyTemplateListName = 'FetchSurveyTemplateListName';
export const useFetchSurveyTemplate = () => {
  const [params, setParams] = useState<{
    page: number;
    size: number;
  }>({
    page: 1,
    size: ITEMS_PER_PAGE,
  });

  const handleNextPage = useCallback((value: number) => {
    setParams(old => ({
      ...old,
      page: value,
    }));
  }, []);

  const fetchSurveyTemplateList = async () => {
    return await axios.get<ISurveyTemplate[]>(`/api/survey-templates`, {
      params: {
        page: params.page - 1,
        size: params.size,
        sort: `id,${DESC}`,
      },
    });
  };

  const { isLoading, isFetching, data, refetch } = useQuery({
    queryKey: [FetchSurveyTemplateListName, params],
    queryFn: fetchSurveyTemplateList,
  });

  return {
    isLoading: isLoading || isFetching,
    data: data?.data,
    total: data?.headers?.xTotalCount,
    page: params.page,
    handleNextPage,
    refetch,
  };
};
