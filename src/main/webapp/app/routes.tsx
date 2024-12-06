import React from 'react';
import { Route } from 'react-router-dom';
import LoginRedirect from 'app/modules/login/login-redirect';
import Admin from 'app/modules/administration';
import Logout from 'app/modules/login/logout';
import Routes from 'app/pages/routes';
import PrivateRoute from 'app/shared/auth/private-route';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PageNotFound from 'app/shared/error/page-not-found';
import { adminPathName, AUTHORITIES, loginUrl, logoutUrl, publicSubmit, userPathName } from 'app/config/constants';
import { PublicSubmit } from 'app/pages/PublicSubmit';

const AppRoutes = () => {
  return (
    <ErrorBoundaryRoutes>
      <Route path={logoutUrl} element={<Logout />} />
      <Route path={loginUrl} element={<LoginRedirect />} />
      <Route path="docs" element={<Admin />} />
      {/* Admin routes */}
      <Route
        path={`${adminPathName}*`}
        element={
          // <PrivateRoute hasAnyAuthorities={[AUTHORITIES.manage]}>
          <PrivateRoute hasAnyAuthorities={[]}>
            <Routes />
          </PrivateRoute>
        }
      />
      <Route path={`${publicSubmit}/:hash`} element={<PublicSubmit />} />
      <Route path="*" element={<PageNotFound />} />
    </ErrorBoundaryRoutes>
  );
};

export default AppRoutes;
