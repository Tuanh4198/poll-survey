import React, { useEffect, useMemo } from 'react';
import { Helmet } from 'react-helmet';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getProfile } from 'app/shared/reducers/application-profile';
import ErrorBoundary from 'app/shared/error/error-boundary';
import AppRoutes from 'app/routes';
import Header from 'app/shared/layout/admin/header/header';
import MainNavigation from './menus/main';
import { AppShell, Box, LoadingOverlay, ScrollArea } from '@mantine/core';
import { useDisclosure, useHeadroom } from '@mantine/hooks';
import { Routes } from 'app/pages/routes';
import { useLocation } from 'react-router-dom';
import { isPublicUrl } from 'app/bootstrap';
import { publicSubmit } from 'app/config/constants';

const pageWithoutLayout = [`${Routes.SURVEY}/`, `${Routes.TEMPLATE}/`, publicSubmit];

export const AdminAppFrame = ({ title }: { title: string }) => {
  const location = useLocation();

  const dispatch = useAppDispatch();

  useEffect(() => {
    if (!isPublicUrl) {
      dispatch(getSession() as any);
      dispatch(getProfile() as any);
    }
  }, []);

  const pinned = useHeadroom({ fixedAt: 120 });

  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  const [mobileNavigationActive, { toggle: toggleMobileNavigationActive }] = useDisclosure(false);

  const [desktopNavigationActive, { toggle: toggleDesktopNavigationActive }] = useDisclosure(true);

  const navigationMarkup = useMemo(() => <MainNavigation />, []);

  const actualPageMarkup = useMemo(
    () => (
      <ErrorBoundary>
        <AppRoutes />
      </ErrorBoundary>
    ),
    []
  );

  const helmet = useMemo(
    () => (
      <Helmet>
        <title>{title}</title>
      </Helmet>
    ),
    [title]
  );

  return (
    <Box pos="relative">
      {!isPublicUrl && <LoadingOverlay visible={!isAuthenticated} zIndex={1000} overlayProps={{ radius: 'sm', blur: 2 }} />}
      {helmet}
      {!pageWithoutLayout.some(i => location.pathname.startsWith(i)) ? (
        <AppShell
          padding="20px"
          header={{ height: 60, collapsed: !pinned }}
          navbar={{
            width: 300,
            breakpoint: 'sm',
            collapsed: { mobile: !mobileNavigationActive, desktop: !desktopNavigationActive },
          }}
        >
          <AppShell.Header>
            <Header
              mobileNavigationActive={mobileNavigationActive}
              toggleMobileNavigationActive={toggleMobileNavigationActive}
              desktopNavigationActive={desktopNavigationActive}
              toggleDesktopNavigationActive={toggleDesktopNavigationActive}
            />
          </AppShell.Header>
          <AppShell.Navbar p="md">
            <AppShell.Section grow my="md" component={ScrollArea}>
              {navigationMarkup}
            </AppShell.Section>
          </AppShell.Navbar>
          <AppShell.Main bg="#F9FAFB" pb="70px" h="100vh">
            {actualPageMarkup}
          </AppShell.Main>
        </AppShell>
      ) : (
        actualPageMarkup
      )}
    </Box>
  );
};
