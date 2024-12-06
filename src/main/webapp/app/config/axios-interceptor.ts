import axios from 'axios';
import applyCaseMiddleware from 'axios-case-converter';

const TIMEOUT = 1 * 60 * 1000;
axios.defaults.timeout = TIMEOUT;
axios.defaults.baseURL = SERVER_API_URL;
applyCaseMiddleware(axios);

const setupAxiosInterceptors = onUnauthenticated => {
  const onRequestSuccess = config => {
    return config;
  };
  const onResponseSuccess = response => response;
  const onResponseError = err => {
    const status = err.status || (err.response ? err.response.status : 0);
    const response = err.response;
    const data = response.data;
    if (status === 403 || status === 401 && (err.message === ''
        || (data && data.path && (data.path.includes('/api/account') || data.path.includes('/api/authenticate'))))) {
      onUnauthenticated();
    }
    return Promise.reject(err);
  };
  axios.interceptors.request.use(onRequestSuccess);
  axios.interceptors.response.use(onResponseSuccess, onResponseError);
};

export default setupAxiosInterceptors;
