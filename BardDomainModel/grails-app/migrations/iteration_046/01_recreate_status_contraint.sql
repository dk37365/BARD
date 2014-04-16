ALTER TABLE ASSAY DROP CONSTRAINT CK_ASSAY_STATUS;

ALTER TABLE EXPERIMENT DROP CONSTRAINT CK_EXPERIMENT_STATUS;

ALTER TABLE PROJECT DROP CONSTRAINT CK_PROJECT_STATUS;

alter table PROJECT modify(project_Status  VARCHAR2(20));

ALTER TABLE ASSAY ADD CONSTRAINT CK_ASSAY_STATUS CHECK (Assay_Status IN ('Draft', 'Approved', 'Retired', 'Provisional')) ENABLE;

ALTER TABLE PROJECT ADD CONSTRAINT CK_PROJECT_STATUS CHECK (Project_Status IN ('Draft', 'Approved', 'Retired', 'Provisional')) ENABLE;

ALTER TABLE EXPERIMENT ADD CONSTRAINT CK_EXPERIMENT_STATUS CHECK (EXPERIMENT_STATUS IN ('Draft', 'Approved', 'Retired', 'Provisional')) ENABLE;