import React from 'react';
import { Card, Skeleton, Image, Text, Flex, Tooltip } from '@mantine/core';
import { ISurveyTemplate } from 'app/shared/model/survey-template.model';
import { Link } from 'react-router-dom';
import { Routes } from 'app/pages/routes';

interface SurvetTemplateCardProps {
  surveyTemplate?: ISurveyTemplate;
  loading?: boolean;
}
export const SurveyTemplateCard = ({ surveyTemplate, loading }: SurvetTemplateCardProps) => {
  if (loading) {
    return (
      <Card padding="12px" radius="16px" withBorder className="survey-template-card-item">
        <Flex direction="column" w="100%" h="100%">
          <Skeleton radius="8px" mb="16px" style={{ width: '100%', aspectRatio: 1 / 1 }} />
          <Skeleton height={45} />
        </Flex>
      </Card>
    );
  }

  return (
    <Link to={`${Routes.TEMPLATE}/${surveyTemplate?.id}`}>
      <Card radius="16px" className="survey-template-card-item">
        <div className="survey-banner">
          <Image
            src={surveyTemplate?.presignThumbUrl}
            fallbackSrc="../../../../../content/images/poll-survey.jpg"
            fit="cover"
            radius="8px"
          />
        </div>
        <Tooltip maw="100%" label={surveyTemplate?.title} multiline withinPortal={false}>
          <Text color="#1F2A37" mt="8px" lineClamp={2} ta="center">
            {surveyTemplate?.title}
          </Text>
        </Tooltip>
      </Card>
    </Link>
  );
};
