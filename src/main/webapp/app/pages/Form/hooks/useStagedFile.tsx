import { notiError } from 'app/shared/notifications';
import axios from 'axios';

export interface StagedUploadsRespon {
  parameters: Array<string>;
  resourceUrl: '';
  url: '';
}

export const useStagedFile = () => {
  const onStagedFile = async ({
    files,
    onSuccess,
    onError,
  }: {
    files: File[];
    onSuccess?: ({ resourceUrl, url }: { resourceUrl?: string; url?: string }) => void;
    onError?: () => void;
  }) => {
    try {
      const body = files?.map(f => ({
        filename: f.name,
        mime_type: f.type,
        resource: 'IMAGE',
      }));
      const res = await axios.post<Array<StagedUploadsRespon>>(`/api/staged-uploads`, body);
      if (res?.data?.[0].url && res?.data?.[0].resourceUrl) {
        onSuccess &&
          onSuccess({
            resourceUrl: res?.data?.[0].resourceUrl,
            url: res?.data?.[0].url,
          });
      }
      return {
        resourceUrl: res?.data?.[0].resourceUrl,
        url: res?.data?.[0].url,
      };
    } catch (error) {
      console.error('Staged uploads error: ', error);
      notiError({ message: 'Tải file lỗi, vui lòng chọn file khác.' });
      onError && onError();
    }
  };

  return { onStagedFile };
};
