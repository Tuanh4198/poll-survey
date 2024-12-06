import React from 'react';
import { Navigate, Route } from 'react-router-dom';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import { Survey } from 'app/pages/Survey';
import { Template } from 'app/pages/Template';
import { TemplateDetail } from 'app/pages/TemplateDetail';
import { SurveyDetail } from 'app/pages/SurveyDetail';
import { Submit } from 'app/pages/Submit';

export const Routes = {
  DASHBOARD: '/',
  TEMPLATE: '/template',
  TEMPLATE_DETAIL: '/template/:id',
  SURVEY: '/survey',
  SURVEY_DETAIL: '/survey/:id',
  SURVEY_SUBMIT: '/survey-submit',
};

export default () => {
  return (
    <ErrorBoundaryRoutes>
      <Route path={Routes.DASHBOARD} element={<Navigate to={Routes.TEMPLATE} replace />} />
      <Route path={`${Routes.TEMPLATE}`} element={<Template />} />
      <Route path={`${Routes.TEMPLATE_DETAIL}`} element={<TemplateDetail />} />
      <Route path={`${Routes.SURVEY}`} element={<Survey />} />
      <Route path={`${Routes.SURVEY_DETAIL}`} element={<SurveyDetail />} />
      <Route path={`${Routes.SURVEY_SUBMIT}/:id`} element={<Submit />} />
    </ErrorBoundaryRoutes>
  );
};
