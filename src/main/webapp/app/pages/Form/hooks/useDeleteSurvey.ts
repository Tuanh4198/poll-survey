import { Routes } from 'app/pages/routes';
import { notiError, notiSuccess } from 'app/shared/notifications';
import axios from 'axios';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export const useDeleteSurvey = () => {
  const navigate = useNavigate();
  const [deleting, setDeleting] = useState(false);

  const deleteEntity = async (id?: number | string) => {
    if (!id) return;
    setDeleting(true);
    try {
      await axios.delete(`/api/surveys/${id}`);
      notiSuccess({ message: 'Đã xoá bài khảo sát!' });
      navigate(Routes.SURVEY);
    } catch (error: any) {
      console.error(error);
      notiError({ message: error?.response?.data ?? 'Xóa thất bại, vui lòng thử lại sau!' });
    }
    setDeleting(false);
  };

  return {
    deleting,
    deleteEntity,
  };
};
