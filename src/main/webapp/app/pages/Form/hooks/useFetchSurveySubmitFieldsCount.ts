import { useQuery } from '@tanstack/react-query';
import axios from 'axios';
import qs from 'qs';

export interface FieldCount {
  [key: number]: number;
}

export const FetchSurveySubmitFieldsCount = 'FetchSurveySubmitFieldsCount';
export const useFetchSurveySubmitFieldsCount = ({
  target,
  surveyId,
  fieldIds,
}: {
  target?: string;
  surveyId?: number;
  fieldIds: string[];
}) => {
  const fetchSurveySubmitFieldsCount = async () => {
    if (!target || !surveyId) throw new Error();
    const params = {
      target,
      fieldIds,
    };
    return await axios.get<{ fieldsCount: FieldCount }>(`/api/survey-submits/${surveyId}/fields-count`, {
      params,
      paramsSerializer: _params => qs.stringify(_params, { arrayFormat: 'repeat' }),
    });
  };

  const { isLoading, isFetching, data, refetch } = useQuery({
    queryKey: [FetchSurveySubmitFieldsCount, fieldIds, target],
    queryFn: fetchSurveySubmitFieldsCount,
  });

  return {
    isLoading: isLoading || isFetching,
    data: data?.data?.fieldsCount,
    refetch,
  };
};
