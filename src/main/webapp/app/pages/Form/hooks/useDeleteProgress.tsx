import { notiError, notiSuccess } from 'app/shared/notifications';
import axios from 'axios';
import { useState } from 'react';

export const useDeleteProgress = () => {
  const [deleting, setDeleting] = useState(false);

  const deleteEntity = async (id?: number | string) => {
    if (!id) return;
    setDeleting(true);
    try {
      await axios.delete(`/api/employee-surveys/${id}`);
      notiSuccess({ message: 'Đã xoá khảo sát!' });
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
