import { ReducersMapObject } from '@reduxjs/toolkit';
import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';
import administration from 'app/modules/administration/administration.reducer';
import userManagement from './user-management';
import entitiesReducers from 'app/pages/reducers';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer: ReducersMapObject = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  ...entitiesReducers,
};

export default rootReducer;
