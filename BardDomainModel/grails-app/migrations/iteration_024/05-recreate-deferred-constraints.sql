ALTER TABLE ASSAY_CONTEXT DROP CONSTRAINT UK_ASSAY_CONTEXT
;
ALTER TABLE ASSAY_CONTEXT ADD CONSTRAINT UK_ASSAY_CONTEXT UNIQUE(ASSAY_ID, CONTEXT_GROUP, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE ASSAY_CONTEXT_ITEM DROP CONSTRAINT UK_ASSAY_CONTEXT_ITEM
;
ALTER TABLE ASSAY_CONTEXT_ITEM ADD CONSTRAINT UK_ASSAY_CONTEXT_ITEM UNIQUE(ASSAY_CONTEXT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE EXPRMT_CONTEXT DROP CONSTRAINT UK_EXPRMT_CONTEXT
;
ALTER TABLE EXPRMT_CONTEXT ADD CONSTRAINT UK_EXPRMT_CONTEXT UNIQUE(EXPERIMENT_ID, CONTEXT_GROUP, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE EXPRMT_CONTEXT_ITEM DROP CONSTRAINT UK_EXPRMT_CONTEXT_ITEM
;
ALTER TABLE EXPRMT_CONTEXT_ITEM ADD CONSTRAINT UK_EXPRMT_CONTEXT_ITEM UNIQUE(EXPRMT_CONTEXT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE PRJCT_EXPRMT_CNTXT_ITEM DROP CONSTRAINT UK_PRJCT_EXPRMT_CNTXT_ITEM
;
ALTER TABLE PRJCT_EXPRMT_CNTXT_ITEM ADD CONSTRAINT UK_PRJCT_EXPRMT_CNTXT_ITEM UNIQUE(PRJCT_EXPRMT_CONTEXT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT DROP CONSTRAINT UK_PRJCT_EXPRMT_CNTXT
;
ALTER TABLE PRJCT_EXPRMT_CONTEXT ADD CONSTRAINT UK_PRJCT_EXPRMT_CNTXT UNIQUE(PROJECT_EXPERIMENT_ID, CONTEXT_GROUP, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE PROJECT_CONTEXT DROP CONSTRAINT UK_PROJECT_CONTEXT
;
ALTER TABLE PROJECT_CONTEXT ADD CONSTRAINT UK_PROJECT_CONTEXT UNIQUE(PROJECT_ID, CONTEXT_GROUP, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE PROJECT_CONTEXT_ITEM DROP CONSTRAINT UK_PROJECT_CONTEXT_ITEM
;
ALTER TABLE PROJECT_CONTEXT_ITEM ADD CONSTRAINT UK_PROJECT_CONTEXT_ITEM UNIQUE(PROJECT_CONTEXT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE STEP_CONTEXT DROP CONSTRAINT UK_STEP_CONTEXT
;
ALTER TABLE STEP_CONTEXT ADD CONSTRAINT UK_STEP_CONTEXT UNIQUE(PROJECT_STEP_ID, CONTEXT_GROUP, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;
ALTER TABLE STEP_CONTEXT_ITEM DROP CONSTRAINT UK_STEP_CONTEXT_ITEM
;
ALTER TABLE STEP_CONTEXT_ITEM ADD CONSTRAINT UK_STEP_CONTEXT_ITEM UNIQUE(STEP_CONTEXT_ID, DISPLAY_ORDER) INITIALLY DEFERRED DEFERRABLE
;