import React from 'react';
import { AdminAppFrame } from './shared/layout/admin';
import axios from 'axios';
import { useQuery } from '@tanstack/react-query';
import { isPublicUrl } from 'app/bootstrap';

export const App = () => {
  const fetchAccount = async () => {
    return await axios.get(`api/account`);
  };

  useQuery({
    queryKey: ['FetchAccount', isPublicUrl],
    queryFn: fetchAccount,
    enabled: !isPublicUrl,
    refetchInterval: 45000,
    refetchIntervalInBackground: true,
  });

  return <AdminAppFrame title={''} />;
};

export default App;
