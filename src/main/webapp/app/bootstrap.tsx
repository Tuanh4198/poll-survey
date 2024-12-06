import 'app/config/dayjs';
import '@mantine/core/styles.css';
import '@mantine/dates/styles.css';
import '@mantine/tiptap/styles.css';
import '@mantine/dropzone/styles.css';
import '@mantine/notifications/styles.css';
import 'app/content/styles/globalStyle.scss';
import React from 'react';
import { createRoot } from 'react-dom/client';
import { bindActionCreators } from 'redux';
import { Provider } from 'react-redux';
import { keepPreviousData, QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter as Router } from 'react-router-dom';
import { createTheme, MantineProvider } from '@mantine/core';
import { DatesProvider } from '@mantine/dates';
import { Notifications } from '@mantine/notifications';
import getStore from 'app/config/store';
import { registerLocale } from 'app/config/translation';
import { clearAuthentication } from 'app/shared/reducers/authentication';
import setupAxiosInterceptors from 'app/config/axios-interceptor';
import ErrorBoundary from 'app/shared/error/error-boundary';
import AppComponent from 'app/app';
import { publicSubmit } from 'app/config/constants';

const theme = createTheme({
  defaultRadius: 8,
  colors: {
    blue: ['#E6E6F8', '#D1D1F1', '#BCBCEA', '#A7A7E3', '#9292DC', '#7D7DD5', '#2A2A86', '#5353C7', '#3E3EC0', '#6868CE'],
  },
});

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      placeholderData: keepPreviousData,
      refetchOnMount: true,
      refetchOnWindowFocus: false,
    },
  },
});

const store = getStore();
registerLocale(store);

export const isPublicUrl = window.location.pathname.startsWith(publicSubmit);

if (!isPublicUrl) {
  const actions = bindActionCreators({ clearAuthentication }, store.dispatch);
  setupAxiosInterceptors(() => {
    actions.clearAuthentication('login.error.unauthorized');
  });
}

// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
const rootEl = document.getElementById('root')!;
const root = createRoot(rootEl);

const baseHref = document.querySelector('base')?.getAttribute('href')?.replace(/\/$/, '');

const render = Component =>
  root.render(
    <ErrorBoundary>
      <Provider store={store}>
        <QueryClientProvider client={queryClient}>
          <MantineProvider theme={theme}>
            <DatesProvider settings={{ locale: 'vi' }}>
              <Router basename={baseHref}>
                <Component />
                <Notifications position="top-right" limit={5} autoClose={5000} containerWidth={400} zIndex={10000} />
              </Router>
            </DatesProvider>
          </MantineProvider>
        </QueryClientProvider>
      </Provider>
    </ErrorBoundary>
  );

render(AppComponent);
