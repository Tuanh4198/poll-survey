import React, { useMemo } from 'react';
import { useAppSelector } from 'app/config/store';
import { Avatar, Burger, Button, Divider, Flex, Group, Popover, Text, UnstyledButton } from '@mantine/core';
import { CaretDown } from '@phosphor-icons/react';
import { generateAvatar } from 'app/shared/util';
import { logoutUrl, userPathName } from 'app/config/constants';
import { useNavigate } from 'react-router-dom';
import { getLoginUrl } from 'app/shared/util/url-utils';

const Header = ({
  mobileNavigationActive,
  toggleMobileNavigationActive,
  desktopNavigationActive,
  toggleDesktopNavigationActive,
}: {
  mobileNavigationActive: boolean;
  toggleMobileNavigationActive: () => void;
  desktopNavigationActive: boolean;
  toggleDesktopNavigationActive: () => void;
}) => {
  const navigate = useNavigate();

  const account = useAppSelector(state => state.authentication.account);

  const fullname = useMemo(() => `${account?.lastName} ${account?.firstName}`, [account?.lastName, account?.firstName]);

  const userMenuMarkup = useMemo(
    () => (
      <Popover trapFocus position="bottom-end" withArrow shadow="md">
        <Popover.Target>
          <Button
            bg="#F9FAFB"
            p="4px"
            color="#111928"
            style={{ border: '1px solid #E5E7EB' }}
            rightSection={<CaretDown color="#111928" size={14} />}
          >
            <Avatar color="#2A2A86" size="24px" radius="100%">
              {account && account.login ? generateAvatar(fullname) : 'U'}
            </Avatar>
          </Button>
        </Popover.Target>
        <Popover.Dropdown>
          {account && account.login ? (
            <>
              <Text fw="bold">
                {account?.lastName} {account?.firstName}
              </Text>
              <Divider my="xs" />
              <Flex gap="8px" direction="column">
                <UnstyledButton w="100%" onClick={() => navigate(userPathName)}>
                  Giao diện người dùng
                </UnstyledButton>
                <UnstyledButton w="100%" onClick={() => navigate(logoutUrl)}>
                  Đăng xuất
                </UnstyledButton>
              </Flex>
            </>
          ) : (
            <UnstyledButton w="100%" onClick={() => navigate(getLoginUrl())}>
              Đăng Nhập
            </UnstyledButton>
          )}
        </Popover.Dropdown>
      </Popover>
    ),
    [account]
  );

  return (
    <Group h="100%" px="md" justify="space-between">
      <Group h="100%" px="md">
        <img className="support-icon" src={'../../../../../content/images/logo.svg'} alt="empty" />
        <Burger opened={mobileNavigationActive} onClick={toggleMobileNavigationActive} hiddenFrom="sm" size="sm" />
        <Burger opened={desktopNavigationActive} onClick={toggleDesktopNavigationActive} visibleFrom="sm" size="sm" />
      </Group>
      <Group ml="xl" gap="md" visibleFrom="sm">
        {userMenuMarkup}
      </Group>
    </Group>
  );
};

export default Header;
