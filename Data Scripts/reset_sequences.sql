--
-- SEQUENCE: ASSAY_CONTEXT_ID_SEQ
--

CREATE SEQUENCE ASSAY_CONTEXT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: ASSAY_CONTEXT_ITEM_ID_SEQ
--

CREATE SEQUENCE ASSAY_CONTEXT_ITEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: ASSAY_CONTEXT_MEASURE_ID_SEQ
--

CREATE SEQUENCE ASSAY_CONTEXT_MEASURE_ID_SEQ
    START WITH 111
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 200
    NOORDER
;

--
-- SEQUENCE: ASSAY_DOCUMENT_ID_SEQ
--

CREATE SEQUENCE ASSAY_DOCUMENT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: ASSAY_ID_SEQ
--

CREATE SEQUENCE ASSAY_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: BARD_ONTOLOGY_SEQ
--

--CREATE SEQUENCE BARD_ONTOLOGY_SEQ
--    START WITH 873
--    INCREMENT BY 1
--    NOMINVALUE
--    NOMAXVALUE
--    CACHE 200
--    NOORDER
--;
GRANT SELECT ON BARD_ONTOLOGY_SEQ TO ONTOLOGY_HELPER
;
--
-- SEQUENCE: ELEMENT_HIERARCHY_ID_SEQ
--

CREATE SEQUENCE ELEMENT_HIERARCHY_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: ELEMENT_ID_SEQ
--

CREATE SEQUENCE ELEMENT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: ERROR_LOG_ID_SEQ
--

CREATE SEQUENCE ERROR_LOG_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: EXPERIMENT_ID_SEQ
--

CREATE SEQUENCE EXPERIMENT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: EXPRMT_CONTEXT_ID_SEQ
--

CREATE SEQUENCE EXPRMT_CONTEXT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: EXPRMT_CONTEXT_ITEM_ID_SEQ
--

CREATE SEQUENCE EXPRMT_CONTEXT_ITEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: EXPRMT_MEASURE_ID_SEQ
--

CREATE SEQUENCE EXPRMT_MEASURE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: EXTERNAL_REFERENCE_ID_SEQ
--

CREATE SEQUENCE EXTERNAL_REFERENCE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: EXTERNAL_SYSTEM_ID_SEQ
--

CREATE SEQUENCE EXTERNAL_SYSTEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    NOCACHE
    NOORDER
;

--
-- SEQUENCE: FAVORITE_ID_SEQ
--

CREATE SEQUENCE FAVORITE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: MEASURE_ID_SEQ
--

CREATE SEQUENCE MEASURE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: ONTOLOGY_ID_SEQ
--

CREATE SEQUENCE ONTOLOGY_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: ONTOLOGY_ITEM_ID_SEQ
--

CREATE SEQUENCE ONTOLOGY_ITEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: PERSON_ID_SEQ
--

CREATE SEQUENCE PERSON_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: PERSON_ROLE_ID_SEQ
--

CREATE SEQUENCE PERSON_ROLE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: PROJECT_CONTEXT_ID_SEQ
--

CREATE SEQUENCE PROJECT_CONTEXT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: PROJECT_CONTEXT_ITEM_ID_SEQ
--

CREATE SEQUENCE PROJECT_CONTEXT_ITEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 200
    NOORDER
;

--
-- SEQUENCE: PROJECT_EXPERIMENT_ID_SEQ
--

CREATE SEQUENCE PROJECT_EXPERIMENT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: PROJECT_ID_SEQ
--

CREATE SEQUENCE PROJECT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: PROJECT_STEP_ID_SEQ
--

CREATE SEQUENCE PROJECT_STEP_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 200
    NOORDER
;

--
-- SEQUENCE: RESULT_HIERARCHY_ID_SEQ
--

CREATE SEQUENCE RESULT_HIERARCHY_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: RESULT_ID_SEQ
--

CREATE SEQUENCE RESULT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 1000
    NOORDER
;

--
-- SEQUENCE: ROLE_ID_SEQ
--

CREATE SEQUENCE ROLE_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: RSLT_CONTEXT_ITEM_ID_SEQ
--

CREATE SEQUENCE RSLT_CONTEXT_ITEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: STEP_CONTEXT_ID_SEQ
--

CREATE SEQUENCE STEP_CONTEXT_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: STEP_CONTEXT_ITEM_ID_SEQ
--

CREATE SEQUENCE STEP_CONTEXT_ITEM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: TEAM_ID_SEQ
--

CREATE SEQUENCE TEAM_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: TEAM_MEMBER_ID_SEQ
--

CREATE SEQUENCE TEAM_MEMBER_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;

--
-- SEQUENCE: UNIT_CONVERSION_ID_SEQ
--

CREATE SEQUENCE UNIT_CONVERSION_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOMINVALUE
    MAXVALUE 2147483648
    NOCYCLE
    CACHE 20
    NOORDER
;



DECLARE
    cursor cur_sequence
    is
    select sequence_name
    from user_sequences
    WHERE sequence_name LIKE '%_ID_SEQ';

    lv_max_sql  varchar2(1000);
    lv_drop_sql varchar2(1000);
    lv_create_sql   varchar2(1000);
    lv_grant_sql    varchar2(1000);
    lv_table_name   varchar2(50);
    lv_primary_key  varchar2(50);
    ln_max_id   number;

begin
    for rec_sequence in cur_sequence
    loop
--        IF rec_sequence.sequence_name = 'PRJCT_EXPRMT_CNTXT_ITEM_ID_SEQ'
--        THEN
--            lv_table_name := 'PRJCT_EXPRMT_CONTEXT_ITEM';
--            lv_primary_key :=  'PRJCT_EXPRMT_CONTEXT_ITEM_ID';
--        ELSE
            lv_table_name := replace(rec_sequence.sequence_name, '_ID_SEQ', null);
            lv_primary_key := replace(rec_sequence.sequence_name, '_SEQ', null);
--        END IF;

        lv_max_sql := 'select nvl(max(' || lv_primary_key || '), 0) from ' || lv_table_name;
        begin
            --dbms_output.put_line(lv_max_sql);
            EXECUTE IMMEDIATE lv_max_sql INTO ln_max_ID;

            lv_drop_sql := 'drop sequence ' || rec_sequence.sequence_name;
            --dbms_output.put_line(lv_drop_sql);
            EXECUTE IMMEDIATE lv_drop_sql;

            lv_create_sql := 'create sequence ' || rec_sequence.sequence_name
                    || ' start with ' || to_char(ln_max_id + 1)
                    || ' increment by 1 nominvalue maxvalue 2147483648 nocycle ';
            IF rec_sequence.sequence_name = 'RESULT_ID_SEQ'
            THEN
                lv_create_sql := lv_create_sql || 'cache 10000 noorder';
            ELSE
                lv_create_sql := lv_create_sql || 'cache 20 noorder';
            END IF;
            --dbms_output.put_line(lv_create_sql);

            lv_grant_sql := 'grant select on ' || rec_sequence.sequence_name
                    || ' to schatwin';
            --dbms_output.put_line(lv_grant_sql);
            EXECUTE IMMEDIATE lv_create_sql;
            EXECUTE IMMEDIATE lv_grant_sql;

        exception
            when others
            then
                null;   --dbms_output.put_line (to_char(sqlcode) || ', ' || sqlerrm);

        end;

    end loop;

    if cur_sequence%isopen
    then
        close cur_sequence;
    end if;

end;
/

SELECT * FROM user_sequences;