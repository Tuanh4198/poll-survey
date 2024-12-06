import dayjs, { ConfigType } from 'dayjs';
import 'dayjs/plugin/utc';
import { APP_DATE_FORMAT, APP_DATE_SERVER_FORMAT, APP_LOCAL_DATETIME_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export const convertDateFromServer = date => (date ? dayjs(date).format(APP_LOCAL_DATE_FORMAT) : null);

export const convertDateToServer = date => (date ? dayjs(date, APP_LOCAL_DATE_FORMAT).format(APP_DATE_SERVER_FORMAT) : null);

export const convertDateTimeFromServer = date => (date ? dayjs(date).format(APP_LOCAL_DATETIME_FORMAT) : null);

export const convertDateTimeToServer = date => (date ? dayjs(date).toDate() : null);

export const displayDefaultDateTime = () => dayjs().startOf('day').format(APP_LOCAL_DATETIME_FORMAT);

export const formatDateTime = (date: ConfigType, format?: string) => dayjs(date).format(format || APP_DATE_FORMAT);

export const formatDateTimeUtc = (date: ConfigType, format?: string) => dayjs.utc(date).format(format || APP_DATE_FORMAT);

export const convertStringToDate = (date: string) => dayjs(date, APP_LOCAL_DATE_FORMAT).toDate();
