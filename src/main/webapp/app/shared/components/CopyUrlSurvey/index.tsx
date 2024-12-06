import { Button, CopyButton, Flex, Input, Popover, Tooltip } from '@mantine/core';
import { CheckCircle, Copy, CopySimple } from '@phosphor-icons/react';
import { publicSubmit } from 'app/config/constants';
import { hiddenFields } from 'app/pages/Form/helper';
import axios from 'axios';
import React, { useEffect, useMemo, useState } from 'react';

export const CopyUrlSurvey = ({ id }: { id: number | string }) => {
  const origin = window.location.origin;

  const [opened, setOpened] = useState(false);

  const [inputHiddenFields, setInputHiddenFields] = useState(hiddenFields);

  const [hash, setHash] = useState(undefined);

  const onFetchRealUrl = async () => {
    try {
      const res = await axios.get(`/api/survey-hash/${id}`);
      if (res.data) setHash(res.data);
    } catch (error: any) {
      console.error('Get hash error: ', error);
    }
  };

  useEffect(() => {
    onFetchRealUrl();
  }, []);

  const copyUrl = useMemo(() => {
    const queryParams = inputHiddenFields.map(item => `${encodeURIComponent(item.key)}=${encodeURIComponent(item.value)}`).join('&');
    return `${origin}${publicSubmit}/${hash}${queryParams ? `?${queryParams}` : ''}`;
  }, [hash, origin, inputHiddenFields]);

  const onChangeInputHiddenField = (value: string, key: string) => {
    setInputHiddenFields(fields => {
      return fields.map(field => {
        if (field.key === key) {
          return {
            ...field,
            value,
          };
        }
        return field;
      });
    });
  };

  return (
    <Popover opened={opened} onChange={setOpened} position="bottom-end">
      <Popover.Target>
        <Button
          disabled={!hash}
          onClick={() => hash && setOpened(o => !o)}
          variant="outline"
          leftSection={<Copy size={16} />}
          color="#111928"
          bd="1px solid #E5E7EB"
        >
          Sao chép liên kết
        </Button>
      </Popover.Target>
      <Popover.Dropdown>
        <Flex direction="column" gap={10}>
          <Flex align="center" gap={8}>
            <Tooltip label={copyUrl} multiline maw={330} style={{ wordWrap: 'break-word' }}>
              <Input value={copyUrl} disabled flex={1} />
            </Tooltip>
            <CopyButton value={copyUrl}>
              {({ copied, copy }) => (
                <Button size="compact-md" color={copied ? 'teal' : 'blue'} onClick={copy}>
                  {copied ? <CheckCircle size={16} weight="fill" /> : <CopySimple size={16} />}
                </Button>
              )}
            </CopyButton>
          </Flex>
          {inputHiddenFields.map((item, index) => (
            <Flex key={index} align="center" gap={8}>
              <Input.Label>Biến thứ {index + 1}</Input.Label>
              <Input
                defaultValue={item.value}
                onChange={event => onChangeInputHiddenField(event.target.value, item.key)}
                placeholder="Nhập..."
              />
            </Flex>
          ))}
        </Flex>
      </Popover.Dropdown>
    </Popover>
  );
};
