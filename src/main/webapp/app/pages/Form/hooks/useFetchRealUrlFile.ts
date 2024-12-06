import axios from 'axios';
import { useEffect, useState } from 'react';

export const useFetchRealUrlFile = (url?: string) => {
  const [loadingImg, setLoadingImg] = useState(false);
  const [urlImg, setUrlImg] = useState<string>();

  const onFetchRealUrl = async () => {
    setLoadingImg(true);
    try {
      const res = await axios.get(`/api/staged-uploads?url=${url}`);
      if (res.data) setUrlImg(res.data);
    } catch (error: any) {
      console.error('Create error: ', error);
    }
    setLoadingImg(false);
  };

  useEffect(() => {
    if (url) {
      onFetchRealUrl();
    }
  }, [url]);

  return { onFetchRealUrl, loadingImg, urlImg };
};
