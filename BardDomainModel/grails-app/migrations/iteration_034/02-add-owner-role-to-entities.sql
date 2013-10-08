ALTER TABLE ASSAY ADD ( OWNER_ROLE_ID NUMBER(19) );
ALTER TABLE PANEL ADD ( OWNER_ROLE_ID NUMBER(19) );
ALTER TABLE PROJECT ADD ( OWNER_ROLE_ID NUMBER(19) );
ALTER TABLE EXPERIMENT ADD ( OWNER_ROLE_ID NUMBER(19) );


-- ADD foreign keys to Role table
ALTER TABLE ASSAY ADD CONSTRAINT FK_ASSAY_OWNER_ROLE_ID FOREIGN KEY (OWNER_ROLE_ID) REFERENCES ROLE (ROLE_ID);
ALTER TABLE PANEL ADD CONSTRAINT FK_PANEL_OWNER_ROLE_ID FOREIGN KEY (OWNER_ROLE_ID) REFERENCES ROLE (ROLE_ID);
ALTER TABLE PROJECT ADD CONSTRAINT FK_PROJECT_OWNER_ROLE_ID FOREIGN KEY (OWNER_ROLE_ID) REFERENCES ROLE (ROLE_ID);
ALTER TABLE EXPERIMENT ADD CONSTRAINT FK_EXP_OWNER_ROLE_ID FOREIGN KEY (OWNER_ROLE_ID) REFERENCES ROLE (ROLE_ID);