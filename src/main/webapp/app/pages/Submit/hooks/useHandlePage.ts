import { TBlock } from 'app/pages/Form/context/FormContext';
import { IBlock } from 'app/shared/model/block.model';
import { notiWarning } from 'app/shared/notifications';
import { useEffect, useMemo, useRef, useState } from 'react';

export const useHandlePage = ({
  blocks,
  onValidate,
}: {
  blocks: TBlock[] | undefined;
  onValidate: (_blocks: IBlock[]) => (number | null)[];
}) => {
  const activePageIndexRef = useRef<number>(0);

  const [activePage, setActivePage] = useState<string | undefined>(undefined);

  const pages = useMemo(() => {
    if (blocks && blocks?.length > 0) {
      return blocks?.reduce((acc, item) => {
        const { pageId } = item;
        if (!acc[pageId]) {
          acc[pageId] = [];
        }
        acc[pageId].push(item);
        return acc;
      }, {});
    }
    return {};
  }, [blocks]);

  useEffect(() => {
    if (pages && Object.keys(pages)?.length > 0 && activePage == null) {
      setActivePage(Object.keys(pages)[activePageIndexRef.current]);
    }
  }, [pages]);

  const totalPage = useMemo(() => {
    return pages ? Object.keys(pages).length : 0;
  }, [pages]);

  const activePageIndex = useMemo(() => {
    if (pages && Object.keys(pages).findIndex(i => i === activePage) >= 0) {
      return Object.keys(pages).findIndex(i => i === activePage) + 1;
    }
    return 1;
  }, [pages, activePage]);

  const blockPerPage = useMemo(() => {
    return pages && activePage && pages[activePage];
  }, [pages, activePage]);

  const onPrevPage = () => {
    if (pages) {
      activePageIndexRef.current -= 1;
      const activePageId = Object.keys(pages)[activePageIndexRef.current];
      setActivePage(activePageId);
    }
  };

  const onNextPage = () => {
    let _errors: (number | null)[] = [];
    if (blockPerPage) {
      _errors = onValidate(blockPerPage);
    }
    if (_errors.length > 0) {
      notiWarning({ message: 'Kiểm tra lại các câu hỏi và điền đầy đủ câu trả lời' });
      return;
    }
    if (pages) {
      activePageIndexRef.current += 1;
      setActivePage(Object.keys(pages)[activePageIndexRef.current]);
    }
  };

  return {
    totalPage,
    activePageIndex,
    blockPerPage,
    onPrevPage,
    onNextPage,
  };
};
