import { useQuery } from '@tanstack/react-query';
import { ISurvey } from 'app/shared/model/survey.model';
import axios from 'axios';

export const FetchSurveyDetaillByHashName = 'FetchSurveyDetaillByHashName';
export const useFetchSurveyDetailByHash = ({ hash }: { hash?: string }) => {
  const fetchTemplate = async () => {
    if (!hash) throw new Error();
    return await axios.get<ISurvey>(`/api/public/survey-by-hash/${hash}`);
  };

  const { isLoading, isFetching, data, refetch } = useQuery({
    queryKey: [FetchSurveyDetaillByHashName, hash],
    queryFn: fetchTemplate,
  });

  return {
    isLoading: isLoading || isFetching,
    data: data?.data,
    refetch,
  };
};
