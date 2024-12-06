export interface IDepartment {
  id?: number;
  code?: string;
  name?: string;
  managerCode?: string | null;
  representCode?: string | null;
  phone?: string | null;
  parentId?: number | null;
  genealogy?: string | null;
  status?: boolean;
}
