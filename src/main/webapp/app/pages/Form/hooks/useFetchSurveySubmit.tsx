import { useQuery } from '@tanstack/react-query';
import { IBlockFields } from 'app/shared/model/block-fields.model';
import { IBlock } from 'app/shared/model/block.model';
import { ComponentTypeEnum } from 'app/shared/model/enumerations/component-type-enum.model';
import { ISurvey } from 'app/shared/model/survey.model';
import { DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import axios from 'axios';
import { useRef, useState } from 'react';

export interface SurveySubmit {
  id: number;
  code: string;
  name: string;
  surveyId: string;
  blockId: number;
  type: ComponentTypeEnum;
  fieldId: number;
  fieldName: string;
  fieldValue: string;
  blockField: IBlockFields;
  block: IBlock;
  survey: ISurvey;
}

export const FetchSurveySubmit = 'FetchSurveySubmit';
export const useFetchSurveySubmit = ({ target, surveyId, fieldId }: { target?: string; surveyId?: number; fieldId: string }) => {
  const targetRef = useRef<string | undefined>();
  const dataRef = useRef<Array<SurveySubmit>>([]);
  const viewportRef = useRef<HTMLDivElement | null>(null);
  const scrollPositionRef = useRef<number>(0);

  const [page, setPage] = useState<number>(0);
  const [blockLoadMoreResults, setBlockLoadMoreResults] = useState(false);

  const onScrollPositionChange = (position: { x: number; y: number }) => {
    scrollPositionRef.current = position.y;
    if (viewportRef?.current && !(isLoading || isFetching) && !blockLoadMoreResults) {
      if (position.y >= viewportRef?.current?.clientHeight - 315) {
        setPage(_page => _page + 1);
      }
    }
  };

  const fetchSurveySubmit = async () => {
    if (!target || !surveyId) throw new Error();
    let _page = 0;
    if (targetRef.current !== target) {
      setPage(0);
    } else {
      _page = page;
    }
    targetRef.current = target;
    const res = await axios.get<Array<SurveySubmit>>(`/api/survey-submits`, {
      params: {
        page: _page,
        target,
        surveyId,
        fieldId,
        size: ITEMS_PER_PAGE,
        sort: `id,${DESC}`,
      },
    });
    if (viewportRef.current && scrollPositionRef.current) {
      viewportRef.current.scrollTop = scrollPositionRef.current;
    }
    if (res.data.length <= 0) {
      setBlockLoadMoreResults(true);
    }
    let newData: Array<SurveySubmit> = [];
    if (page > 0) {
      newData = [...dataRef.current, ...res.data];
    } else {
      newData = res.data;
    }
    dataRef.current = newData;
    return newData;
  };

  const { isLoading, isFetching, data, refetch } = useQuery({
    queryKey: [FetchSurveySubmit, fieldId, page, target],
    queryFn: fetchSurveySubmit,
  });

  return {
    isLoading: isLoading || isFetching,
    data,
    refetch,
    viewportRef,
    page,
    onScrollPositionChange,
  };
};
