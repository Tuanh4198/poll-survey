import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import debounce from 'lodash/debounce';
import { CheckIcon, CloseButton, Combobox, Group, Input, InputBase, InputBaseProps, Loader, Pill, useCombobox } from '@mantine/core';
import { CaretDown } from '@phosphor-icons/react';
import { AxiosResponse } from 'axios';
import { DebouncedFunc } from 'lodash';

export const defaultSelectedValue = {
  label: '',
  value: '',
};
export interface IOption {
  label: string;
  value: string;
}
export const useHandleSelectWithLoadmore = <T, O = unknown>({
  defaultSelected,
  defaultSelecteds,
  fetchFunc,
  onSelectedItem,
  processData,
  findData,
  formatOption,
  allowClear = true,
  placeholder,
  searchPlaceholder,
  rootId,
  showValue = true,
  multiple = false,
  ...rest
}: InputBaseProps & {
  fetchFunc: (isFirst?: boolean, query?: string) => Promise<AxiosResponse<T[], any>>;
  defaultSelected?: T;
  defaultSelecteds?: T[];
  onSelectedItem?: (value?: string, selectedData?: T) => void;
  processData: (idMap: { [key: string]: boolean }, acc: (IOption & O)[], currentItem: T) => (IOption & O)[];
  findData?: (value: T, val: string) => void;
  formatOption: (value: T) => IOption & O;
  allowClear?: boolean;
  placeholder?: string;
  searchPlaceholder?: string;
  rootId?: string;
  showValue?: boolean;
  multiple?: boolean;
}) => {
  const openedRef = useRef(false);
  const scrollPositionRef = useRef(0);
  const prevSearchValueRef = useRef<string | undefined>(undefined);
  const viewportRef = useRef<HTMLDivElement | null>(null);
  const handleFetchFuncRef = useRef<DebouncedFunc<(isFirst?: boolean, query?: string) => void> | null>(null);
  const pageRef = useRef<number>(0);
  const dataRef = useRef<T[]>([]);
  const [opened, setOpened] = useState(false);
  const [data, setData] = useState<T[]>([]);
  const [searchValue, setSearchValue] = useState<string | undefined>(undefined);
  const [selectedValue, setSelectedValue] = useState<(IOption & O) | undefined>(defaultSelectedValue as IOption & O);
  const [selectedValues, setSelectedValues] = useState<(IOption & O)[] | undefined>([]);
  const [loading, setLoading] = useState(false);
  const [blockLoadMoreResults, setBlockLoadMoreResults] = useState(false);

  const handleFetchFunc = async (isFirst?: boolean, query?: string) => {
    let listEmployeesCurrent = [...data];
    if (!isFirst) pageRef.current += 1;
    if (isFirst) {
      listEmployeesCurrent = query || !defaultSelected ? [] : [defaultSelected];
      setBlockLoadMoreResults(false);
      pageRef.current = 0;
    }
    setLoading(true);
    const res = await fetchFunc(isFirst, query);
    setData([...listEmployeesCurrent, ...res.data]);
    if (viewportRef.current && scrollPositionRef.current) {
      viewportRef.current.scrollTop = scrollPositionRef.current;
    }
    setLoading(false);
    if (res.data.length <= 0) setBlockLoadMoreResults(true);
  };

  const onSearch = debounce((isFirst?: boolean, query?: string) => {
    handleFetchFunc(isFirst, query);
  }, 500);

  useEffect(() => {
    openedRef.current = opened;
  }, [opened]);

  useEffect(() => {
    handleFetchFuncRef.current = onSearch;
  }, [onSearch]);

  useEffect(() => {
    dataRef.current = data;
  }, [data]);

  useEffect(() => {
    if (searchValue !== prevSearchValueRef.current && openedRef.current && handleFetchFuncRef.current) {
      handleFetchFuncRef.current(true, searchValue);
      prevSearchValueRef.current = searchValue;
    }

    return () => {
      handleFetchFuncRef?.current?.cancel();
    };
  }, [searchValue]);

  useEffect(() => {
    if (dataRef.current?.length <= 0 && opened) {
      handleFetchFunc(true);
    }
  }, [opened]);

  useEffect(() => {
    setData([]);
  }, [rootId]);

  useEffect(() => {
    if (defaultSelected) {
      if (multiple) {
        setSelectedValues(defaultSelecteds?.map(i => formatOption(i)));
      } else {
        setSelectedValue(formatOption(defaultSelected));
      }
    }
  }, []);

  const onScrollPositionChange = (position: { x: number; y: number }) => {
    scrollPositionRef.current = position.y;
    if (viewportRef?.current && !loading && !blockLoadMoreResults) {
      if (position.y >= viewportRef?.current?.scrollHeight - 250) {
        handleFetchFunc(false, searchValue);
      }
    }
  };

  const onClear = useCallback(() => {
    if (multiple) {
      setSelectedValues([]);
    } else {
      setSelectedValue(defaultSelectedValue as IOption & O);
    }
    onSelectedItem && onSelectedItem(undefined);
  }, [defaultSelectedValue, multiple, onSelectedItem, setSelectedValues]);

  const onOptionSubmit = (val: string) => {
    const selectedItem = optionsData.find(item => item.value === val);
    const selectedData = findData ? dataRef.current?.find(value => findData(value, val)) : undefined;
    if (multiple) {
      setSelectedValues(oldValue => {
        if (!selectedItem) return [];
        const existValue = oldValue?.filter(i => i.value === selectedItem?.value);
        if (existValue && existValue?.length > 0) {
          return oldValue?.filter(i => i.value !== selectedItem?.value);
        } else {
          return [...(oldValue || []), selectedItem];
        }
      });
    } else {
      setSelectedValue(selectedItem);
      combobox.closeDropdown();
    }
    onSelectedItem && onSelectedItem(selectedItem?.value, selectedData);
  };

  const toggleOpened = useCallback((value: boolean) => {
    setOpened(value);
  }, []);

  const combobox = useCombobox({
    onOpenedChange: toggleOpened,
    onDropdownClose() {
      combobox.resetSelectedOption();
      combobox.focusTarget();
      setSearchValue('');
    },
    onDropdownOpen() {
      combobox.focusSearchInput();
    },
  });

  const optionsData = useMemo((): (IOption & O)[] => {
    const idMap = {};
    return data.reduce((acc: (IOption & O)[], currentItem: T) => {
      return processData(idMap, acc, currentItem);
    }, []);
  }, [data]);

  const options = useMemo(
    () =>
      optionsData.map(item => {
        const active = multiple ? selectedValues?.map(i => i.value)?.includes(item.value) : item.value === selectedValue?.value;
        return (
          <Combobox.Option value={item.value} key={item.value} active={active}>
            <Group justify="space-between" align="center" bg={active ? '#f1f1f8' : undefined}>
              <span>{item.label}</span>
              {active && <CheckIcon size={12} />}
            </Group>
          </Combobox.Option>
        );
      }),
    [optionsData, selectedValue, selectedValues, multiple]
  );

  const empty = useMemo(
    () =>
      loading ? (
        <Combobox.Empty>Loading....</Combobox.Empty>
      ) : optionsData.length <= 0 ? (
        <Combobox.Empty>Không có dữ liệu</Combobox.Empty>
      ) : null,
    [optionsData, loading]
  );

  const rightSection = useMemo(() => {
    if (!allowClear) return loading ? <Loader size={16} /> : <CaretDown size={16} />;

    return loading ? (
      <Loader size={16} />
    ) : selectedValue?.label !== '' ? (
      <CloseButton size="sm" onMouseDown={event => event.preventDefault()} onClick={onClear} aria-label="Clear value" />
    ) : (
      <CaretDown size={16} />
    );
  }, [loading, selectedValue]);

  const rightSectionSearch = useMemo(
    () =>
      searchValue != null && searchValue !== '' ? (
        <CloseButton size="sm" onMouseDown={event => event.preventDefault()} onClick={() => setSearchValue('')} aria-label="Clear value" />
      ) : undefined,
    [searchValue]
  );

  const comboboxTarget = useMemo(
    () => (
      <Combobox.Target>
        <InputBase
          {...rest}
          component="button"
          type="button"
          pointer
          onClick={() => combobox.toggleDropdown()}
          rightSectionPointerEvents={selectedValue?.label !== '' && showValue && !{ ...rest }.disabled ? 'all' : 'none'}
          rightSection={showValue && rightSection}
        >
          {multiple ? (
            selectedValues && selectedValues?.length > 0 && showValue ? (
              selectedValues?.map(i => (
                <Pill key={i.value} c="#1E1E73" fw={500} fz="14px" radius="24px" bg="#D4D4F0">
                  {i.label}
                </Pill>
              ))
            ) : (
              <Input.Placeholder>{placeholder}</Input.Placeholder>
            )
          ) : selectedValue?.label !== '' && showValue ? (
            selectedValue?.label
          ) : (
            <Input.Placeholder>{placeholder}</Input.Placeholder>
          )}
        </InputBase>
      </Combobox.Target>
    ),
    [combobox, selectedValue, selectedValues, rightSection, placeholder, multiple, showValue, { ...rest }]
  );

  const comboboxSearch = useMemo(
    () => (
      <Combobox.Search
        autoFocus
        value={searchValue}
        onChange={event => setSearchValue(event.currentTarget.value)}
        placeholder={searchPlaceholder}
        rightSectionPointerEvents={searchValue != null || searchValue !== '' ? 'all' : 'none'}
        rightSection={rightSectionSearch}
      />
    ),
    [searchValue, searchPlaceholder, rightSectionSearch]
  );

  return {
    pageRef,
    viewportRef,
    data,
    setData,
    loading,
    setLoading,
    searchValue,
    setSearchValue,
    blockLoadMoreResults,
    setBlockLoadMoreResults,
    selectedValue,
    setSelectedValue,
    selectedValues,
    setSelectedValues,
    defaultSelectedValue,
    onScrollPositionChange,
    onClear,
    onOptionSubmit,
    combobox,
    optionsData,
    options,
    empty,
    rightSection,
    rightSectionSearch,
    comboboxTarget,
    comboboxSearch,
  };
};
