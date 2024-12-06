import { useQuery } from '@tanstack/react-query';
import { ISurvey } from 'app/shared/model/survey.model';
import axios from 'axios';

export const FetchSurveyDetaillName = 'FetchSurveyDetaillName';
export const useFetchSurveyDetail = ({ id }: { id?: string }) => {
  const fetchTemplate = async () => {
    if (!id) throw new Error();
    return await axios.get<ISurvey>(`/api/surveys/${id}`);
  };

  const { isLoading, isFetching, data, refetch } = useQuery({
    queryKey: [FetchSurveyDetaillName, id],
    queryFn: fetchTemplate,
  });

  return {
    isLoading: isLoading || isFetching,
    data: data?.data,
    refetch,
  };
};
