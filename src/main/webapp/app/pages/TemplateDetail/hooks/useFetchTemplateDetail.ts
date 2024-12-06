import { useQuery } from '@tanstack/react-query';
import { ISurveyTemplate } from 'app/shared/model/survey-template.model';
import axios from 'axios';

export const FetchTemplateDetailName = 'FetchTemplateDetailName';
export const useFetchTemplateDetail = ({ id }: { id?: string }) => {
  const fetchTemplate = async () => {
    if (!id) throw new Error();
    return await axios.get<ISurveyTemplate>(`/api/survey-templates/${id}`);
  };

  const { isLoading, isFetching, data, refetch } = useQuery({
    queryKey: [FetchTemplateDetailName, id],
    queryFn: fetchTemplate,
  });

  return {
    isLoading: isLoading || isFetching,
    data: data?.data,
    refetch,
  };
};
