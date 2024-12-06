import React from 'react';
import { Card, Skeleton, Image, Text, Flex, Pill, Tooltip } from '@mantine/core';
import { ISurvey } from 'app/shared/model/survey.model';
import { formatDateTime } from 'app/shared/util/date-utils';
import { Clock, UsersThree } from '@phosphor-icons/react';
import { Routes } from 'app/pages/routes';
import { Link } from 'react-router-dom';

interface SurveyCardProps {
  survey?: ISurvey;
  loading?: boolean;
}
export const SurveyCard = ({ survey, loading }: SurveyCardProps) => {
  if (loading) {
    return (
      <Card padding="12px" radius="16px" withBorder className="survey-card-item">
        <Flex direction="column" w="100%" h="100%">
          <Skeleton radius="8px" mb="16px" style={{ width: '100%', aspectRatio: 2 / 1 }} />
          <Skeleton height={45} />
        </Flex>
      </Card>
    );
  }

  return (
    <Link to={`${Routes.SURVEY}/${survey?.id}`}>
      <Card radius="16px" className="survey-card-item">
        <div className="survey-banner">
          <Image src={survey?.presignThumbUrl} fallbackSrc="../../../../../content/images/poll-survey.jpg" fit="cover" radius="8px" />
        </div>
        <Flex direction="column" align="flex-start" mt="10px" gap="10px">
          {survey?.isRequired ? (
            <Pill c="#FFFFFF" fw={500} fz={14} bg="#FD7E14">
              Bắt buộc
            </Pill>
          ) : (
            <Pill c="#FFFFFF" fw={500} fz={14} bg="#12B886">
              Không bắt buộc
            </Pill>
          )}
          <Tooltip maw="100%" label={survey?.title} multiline withinPortal={false}>
            <Text fz={16} color="#1F2A37" lineClamp={2} fw={500} w="100%" h={45}>
              {survey?.title}
            </Text>
          </Tooltip>
          <Flex gap={8}>
            <Clock size={16} color="#4B5563" />
            <Text c="#4B5563" fz={14}>
              Bắt đầu: {formatDateTime(survey?.applyTime)}
            </Text>
          </Flex>
          <Flex gap={8}>
            <Clock size={16} color="#4B5563" />
            <Text c="#4B5563" fz={14}>
              Kết thúc: {survey?.endTime ? formatDateTime(survey?.endTime) : '-'}
            </Text>
          </Flex>
          <Flex gap={8}>
            <UsersThree size={16} color="#4B5563" />
            <Text c="#4B5563" fz={14}>
              0 tham gia
            </Text>
          </Flex>
        </Flex>
      </Card>
    </Link>
  );
};
