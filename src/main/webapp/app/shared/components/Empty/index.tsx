import React, { ReactNode } from 'react';
import { Button, Flex, Image } from '@mantine/core';

export const Empty = ({
  withIcon,
  withBtn,
  btnText,
  onBtnClick,
  icon,
  description,
}: {
  withIcon?: boolean;
  withBtn?: boolean;
  btnText?: string;
  onBtnClick?: () => void;
  icon?: ReactNode;
  description: string;
}) => {
  return (
    <Flex gap={'15px'} align={'center'} justify={'center'} direction={'column'} className={`empty-wrapper`}>
      {withIcon && (icon ? icon : <Image w={100} src="../../../content/images/empty-box.svg" />)}
      <p>{description}</p>
      {withBtn && <Button onClick={() => onBtnClick && onBtnClick()}>{btnText ? btnText : 'Về trang chủ'}</Button>}
    </Flex>
  );
};
