ALTER TABLE ELEMENT MODIFY ABBREVIATION VARCHAR2(30);
ALTER TABLE ASSAY_DESCRIPTOR_TREE MODIFY ABBREVIATION VARCHAR2(30);
ALTER TABLE BARD_TREE MODIFY ABBREVIATION VARCHAR2(30);
ALTER TABLE BIOLOGY_DESCRIPTOR_TREE MODIFY ABBREVIATION VARCHAR2(30);
ALTER TABLE DICTIONARY_TREE MODIFY ABBREVIATION VARCHAR2(30);
ALTER TABLE EXT_ONTOLOGY_TREE MODIFY ABBREVIATION VARCHAR2(30);
ALTER TABLE INSTANCE_DESCRIPTOR_TREE MODIFY ABBREVIATION VARCHAR2(30);
ALTER TABLE ONTOLOGY MODIFY ABBREVIATION VARCHAR2(30);
ALTER TABLE RESULT_TYPE_TREE MODIFY ABBREVIATION VARCHAR2(30);
ALTER TABLE STATS_MODIFIER_TREE MODIFY ABBREVIATION VARCHAR2(30);
ALTER TABLE UNIT_TREE MODIFY ABBREVIATION VARCHAR2(30);
update element set abbreviation ='physicochemical assay' where element_id = 45;
update element set abbreviation ='conformation determination' where element_id = 68;
update element set abbreviation ='enzyme activity meas method' where element_id = 70;
update element set abbreviation ='gene-expr detection' where element_id = 71;
update element set abbreviation ='membrane potential meas.' where element_id = 72;
update element set abbreviation ='molecular interaction assess.' where element_id = 73;
update element set abbreviation ='redistribution determination' where element_id = 76;
update element set abbreviation ='post-translational mod.' where element_id = 130;
update element set abbreviation ='nucleic acid amp. Method' where element_id = 184;
update element set abbreviation ='2nd messenger redistrib.' where element_id = 202;
update element set abbreviation ='FRET' where element_id = 277;
update element set abbreviation ='enzyme-linked immunosorbent' where element_id = 391;
update element set abbreviation ='homogeneous time-res. fluor.' where element_id = 396;
update element set abbreviation ='gene knockdown by RNAi' where element_id = 520;
update element set abbreviation ='SM collection source' where element_id = 637;
update element set abbreviation ='SM collection source' where element_id = 637;
update element set abbreviation ='screen. conc. (molar)' where element_id = 971;
update element set abbreviation ='growth inhibitory conc.' where element_id = 1727;
update element set abbreviation ='amp. luminescence prox. assay' where element_id = 1747;
update element set abbreviation ='component conc. (molar)' where element_id = 1944;
update element set abbreviation ='component conc. (%/mass)' where element_id = 1945;
update element set abbreviation ='component conc. (%/vol)' where element_id = 1946;
update element set abbreviation ='component conc. (W/V)' where element_id = 1947;
update element set abbreviation ='screen. conc. (%/mass)' where element_id = 1948;
update element set abbreviation ='screen. conc. (%/vol)' where element_id = 1949;
update element set abbreviation ='component conc. (cells/well)' where element_id = 2302;
update element set abbreviation ='component conc. (cells/vol)' where element_id = 2304;
update element set abbreviation ='component conc. (OD600)' where element_id = 2368;
update element set abbreviation ='alt confirmatory screen' where element_id = 633;
update element set abbreviation ='overexp stable transfect', expected_value_type = 'free text' where element_id = 521;
update element set abbreviation ='overexp transient transfect', expected_value_type = 'free text' where element_id = 522;
ALTER TABLE ELEMENT ADD CONSTRAINT ck_element_abbreviation CHECK ((LENGTH(label)>30 AND expected_value_type <> 'none' AND abbreviation IS NOT NULL) OR LENGTH(label) <= 30 OR expected_value_type = 'none');