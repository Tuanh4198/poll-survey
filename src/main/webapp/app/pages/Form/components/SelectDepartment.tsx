import { Combobox, InputBaseProps, ScrollArea } from '@mantine/core';
import { useHandleSelectWithLoadmore } from 'app/shared/hooks/useHandleSelectWithLoadmore';
import { IDepartment } from 'app/shared/model/department.model';
import { ASC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import axios from 'axios';
import React, { forwardRef, useImperativeHandle } from 'react';

const apiDepartmentUrl = 'api/departments';

const formatOption = (value: IDepartment) => {
  return {
    label: value?.name || '',
    value: value?.name || '',
  };
};

export const SelectDepartment = forwardRef(
  (
    {
      placeholder,
      defaultSelected,
      searchPlaceholder,
      onSelectedItem,
      ...rest
    }: InputBaseProps & {
      placeholder?: string;
      searchPlaceholder?: string;
      defaultSelected?: IDepartment;
      onSelectedItem?: (value?: string, data?: IDepartment) => void;
    },
    ref
  ) => {
    const getAllDepartments = async (isFirst?: boolean, query?: string) => {
      const requestUrl = `${apiDepartmentUrl}${`?page=${isFirst ? 0 : pageRef.current}&size=${ITEMS_PER_PAGE}&sort=id,${ASC}`}${
        query != null && query.trim() !== '' ? `&name.contains=${query.trim()}` : ''
      }`;
      const res = await axios.get<IDepartment[]>(requestUrl);
      return res;
    };

    const {
      pageRef,
      viewportRef,
      onScrollPositionChange,
      options,
      empty,
      combobox,
      onOptionSubmit,
      comboboxTarget,
      comboboxSearch,
      setSelectedValues,
    } = useHandleSelectWithLoadmore<IDepartment>({
      ...rest,
      showValue: false,
      multiple: true,
      defaultSelected,
      onSelectedItem,
      formatOption,
      fetchFunc: getAllDepartments,
      placeholder: placeholder ? placeholder : 'Chọn phòng ban',
      searchPlaceholder: searchPlaceholder ? searchPlaceholder : 'Tìm kiếm theo tên hoặc mã phòng ban',
      processData(idMap, acc, currentItem) {
        if (currentItem?.code && !idMap[currentItem?.code]) {
          idMap[currentItem.code] = true;
          acc.push(formatOption(currentItem));
        }
        return acc;
      },
      findData(item, value) {
        return item.code === value;
      },
    });

    useImperativeHandle(ref, () => ({
      onClear(item) {
        setSelectedValues(oldValues => oldValues?.filter(i => i.value !== item));
      },
    }));

    return (
      <Combobox store={combobox} withinPortal={false} onOptionSubmit={onOptionSubmit}>
        {comboboxTarget}
        <Combobox.Dropdown>
          {comboboxSearch}
          <Combobox.Options>
            <ScrollArea.Autosize type="scroll" mah={200} viewportRef={viewportRef} onScrollPositionChange={onScrollPositionChange}>
              {options}
              {empty}
            </ScrollArea.Autosize>
          </Combobox.Options>
        </Combobox.Dropdown>
      </Combobox>
    );
  }
);
