import React, { useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { DotOutline, GridFour, ListDashes } from '@phosphor-icons/react';
import { Routes } from 'app/pages/routes';
import { Button, Collapse, Flex, Text } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';

interface NavigationItems {
  url: string;
  label: string;
  icon: React.JSX.Element;
  subMenu?: {
    url: string;
    label: string;
  }[];
}

const navigationItems: NavigationItems[] = [
  {
    url: `${Routes.TEMPLATE}`,
    label: 'Tạo mới khảo sát',
    icon: <GridFour size="16px" />,
  },
  {
    url: `${Routes.SURVEY}`,
    label: 'Khảo sát của bạn',
    icon: <ListDashes size="16px" />,
  },
];

const MainNavigation = () => {
  return (
    <Flex direction="column" gap="12px" w="100%">
      {navigationItems?.map(item => (
        <ItemNavigation key={item.url} item={item} />
      ))}
    </Flex>
  );
};

const ItemNavigation = ({ item }: { item: NavigationItems }) => {
  const location = useLocation();
  const navigate = useNavigate();

  const [opened, { toggle }] = useDisclosure(false);

  const handleComparePath = useCallback(
    (path: string) => {
      let comparePath = '';
      const adminDashboard = Routes.TEMPLATE;
      if (location.pathname === adminDashboard) {
        comparePath = adminDashboard;
      } else {
        Object.values(Routes).forEach(i => {
          const adminRoute = i;
          if (adminRoute !== adminDashboard) {
            if (location.pathname.startsWith(adminRoute)) comparePath = adminRoute;
          }
        });
      }
      return comparePath === path;
    },
    [location]
  );

  return (
    <>
      <Button
        justify="flex-start"
        variant="subtle"
        radius="8px"
        p="12px"
        color={handleComparePath(item.url) ? '#2A2A86' : '#1F2A37'}
        bg={handleComparePath(item.url) ? '#F9F9FF' : undefined}
        key={item.url}
        fullWidth
        leftSection={item.icon}
        size="md"
        onClick={() => {
          if (item.subMenu) {
            toggle();
          } else {
            navigate(item.url);
          }
        }}
      >
        <Text fw={handleComparePath(item.url) ? '600' : 'normal'}>{item.label}</Text>
      </Button>
      {item.subMenu && (
        <Collapse in={opened}>
          <Flex direction="column" gap="12px" w="100%" pl="8px">
            {item.subMenu.map(subItem => (
              <Button
                key={subItem.url}
                justify="flex-start"
                variant="subtle"
                radius="8px"
                p="12px"
                color={handleComparePath(subItem.url) ? '#2A2A86' : '#1F2A37'}
                bg={handleComparePath(subItem.url) ? '#F9F9FF' : undefined}
                fullWidth
                leftSection={<DotOutline size="16px" style={{ opacity: handleComparePath(subItem.url) ? 1 : 0 }} />}
                size="md"
                onClick={() => {
                  navigate(subItem.url);
                }}
              >
                <Text fw={handleComparePath(subItem.url) ? '600' : 'normal'}>{subItem.label}</Text>
              </Button>
            ))}
          </Flex>
        </Collapse>
      )}
    </>
  );
};

export default MainNavigation;
