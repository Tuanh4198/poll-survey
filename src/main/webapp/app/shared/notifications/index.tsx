import React from 'react';
import { notifications } from '@mantine/notifications';
import { CheckCircle, WarningCircle } from '@phosphor-icons/react';

interface INoti {
  title?: string;
  message: React.ReactNode;
}

const baseConfig = {
  c: '#111928',
  fz: '14px',
  radius: 4,
  bg: 'white',
  withCloseButton: true,
  style: {
    boxShadow: '0px 7px 7px -5px rgba(0, 0, 0, 0.04), 0px 10px 15px -5px rgba(0, 0, 0, 0.10), 0px 1px 3px 0px rgba(0, 0, 0, 0.05',
  },
};

export const notiError = (props: INoti) => {
  notifications.show({
    ...props,
    ...baseConfig,
    color: '#FA5252',
    icon: <WarningCircle weight="fill" color="white" size={16} />,
  });
};

export const notiSuccess = (props: INoti) => {
  notifications.show({
    ...props,
    ...baseConfig,
    color: '#12B886',
    icon: <CheckCircle weight="fill" color="white" size={16} />,
  });
};

export const notiWarning = (props: INoti) => {
  notifications.show({
    ...props,
    ...baseConfig,
    color: '#FD7E14',
    icon: <CheckCircle weight="fill" color="white" size={16} />,
  });
};
