package com.ecer.kafka.connect.oracle;

public interface OracleConnectorSQL{
    String LOGMINER_SELECT_WITHSCHEMA="SELECT thread#, scn, start_scn, commit_scn,timestamp, operation_code, operation,status, SEG_TYPE_NAME ,info,seg_owner, table_name, username, sql_redo ,row_id, csf, TABLE_SPACE, SESSION_INFO, RS_ID, RBASQN, RBABLK, SEQUENCE#, TX_NAME, SEG_NAME, SRC_CON_ID FROM  v$logmnr_contents  WHERE OPERATION_CODE in (1,2,3) and commit_scn>=? and ";
    String START_LOGMINER_CMD="begin \nDBMS_LOGMNR.START_LOGMNR(STARTSCN => ?,";
    String LOGMINER_START_OPTIONS="OPTIONS =>  DBMS_LOGMNR.SKIP_CORRUPTION+DBMS_LOGMNR.NO_SQL_DELIMITER+DBMS_LOGMNR.NO_ROWID_IN_STMT+DBMS_LOGMNR.DICT_FROM_ONLINE_CATALOG + DBMS_LOGMNR.CONTINUOUS_MINE+DBMS_LOGMNR.COMMITTED_DATA_ONLY+dbms_logmnr.STRING_LITERALS_IN_STMT";
    String STOP_LOGMINER_CMD="begin \nSYS.DBMS_LOGMNR.END_LOGMNR; \nend;";
    String CURRENT_DB_SCN_SQL = "select min(current_scn) CURRENT_SCN from gv$database";    
    String LASTSCN_STARTPOS = "select min(FIRST_CHANGE#) FIRST_CHANGE# from (select FIRST_CHANGE# from v$log where ? between FIRST_CHANGE# and NEXT_CHANGE# union select FIRST_CHANGE# from v$archived_log where ? between FIRST_CHANGE# and NEXT_CHANGE# and standby_dest='NO')";
    String TABLE_WITH_COLS ="with dcc as (SELECT dcc.owner,dcc.table_name,dcc2.column_name,1 PK_COLUMN from dba_constraints dcc,dba_cons_columns dcc2 where dcc.owner=dcc2.owner and dcc.table_name=dcc2.table_name and dcc.constraint_name=dcc2.constraint_name and dcc.constraint_type='P'),duq as (select di2.TABLE_OWNER,di2.TABLE_NAME,di2.COLUMN_NAME , 1 UQ_COLUMN from dba_ind_columns di2 join dba_indexes di on di.table_owner=di2.TABLE_OWNER and di.table_name=di2.TABLE_NAME and di.uniqueness='UNIQUE' and di.owner=di2.INDEX_OWNER and di.index_name=di2.INDEX_NAME group by di2.TABLE_OWNER,di2.TABLE_NAME,di2.COLUMN_NAME) select dc.owner,dc.TABLE_NAME,dc.COLUMN_NAME,dc.NULLABLE,dc.DATA_TYPE,nvl(dc.DATA_PRECISION,dc.DATA_LENGTH) DATA_LENGTH,nvl(dc.DATA_SCALE,0) DATA_SCALE,nvl(dc.DATA_PRECISION,0) DATA_PRECISION,nvl(x.pk_column,0) pk_column,nvl(y.uq_column,0) uq_column from dba_tab_cols dc left outer join dcc x on x.owner=dc.owner and x.table_name=dc.TABLE_NAME and dc.COLUMN_NAME=x.column_name left outer join duq y on y.table_owner=dc.owner and y.table_name=dc.TABLE_NAME and y.column_name=dc.COLUMN_NAME where dC.Owner='$TABLE_OWNER$' and dc.TABLE_NAME='$TABLE_NAME$' and dc.HIDDEN_COLUMN='NO' and dc.VIRTUAL_COLUMN='NO' order by dc.TABLE_NAME,dc.COLUMN_ID";
    String TABLE_WITH_COLS_CDB = "\n" + 
    		"\n" + 
    		"WITH dcc AS\n" + 
    		"  (SELECT dcc.con_id,\n" + 
    		"    dcc.owner,\n" + 
    		"    dcc.table_name,\n" + 
    		"    dcc2.column_name,\n" + 
    		"    1 PK_COLUMN\n" + 
    		"  FROM cdb_constraints dcc,\n" + 
    		"    cdb_cons_columns dcc2\n" + 
    		"  WHERE dcc.con_id = dcc2.con_id\n" + 
    		"  AND dcc.owner        =dcc2.owner\n" + 
    		"  AND dcc.table_name     =dcc2.table_name\n" + 
    		"  AND dcc.constraint_name=dcc2.constraint_name\n" + 
    		"  AND dcc.constraint_type='P'\n" + 
    		"  ),\n" + 
    		"  duq AS\n" + 
    		"  (SELECT di2.CON_ID,\n" + 
    		"    di2.TABLE_OWNER,\n" + 
    		"    di2.TABLE_NAME,\n" + 
    		"    di2.COLUMN_NAME ,\n" + 
    		"    1 UQ_COLUMN\n" + 
    		"  FROM cdb_ind_columns di2\n" + 
    		"  JOIN cdb_indexes di\n" + 
    		"  ON di.con_id=di2.con_id\n" + 
    		"  AND di.table_owner=di2.TABLE_OWNER\n" + 
    		"  AND di.table_name=di2.TABLE_NAME\n" + 
    		"  AND di.uniqueness='UNIQUE'\n" + 
    		"  AND di.owner     =di2.INDEX_OWNER\n" + 
    		"  AND di.index_name=di2.INDEX_NAME\n" + 
    		"  GROUP BY di2.CON_ID,\n" + 
    		"    di2.TABLE_OWNER,\n" + 
    		"    di2.TABLE_NAME,\n" + 
    		"    di2.COLUMN_NAME\n" + 
    		"  )\n" + 
    		"SELECT dc.con_id,\n" + 
    		"  dc.owner,\n" + 
    		"  dc.TABLE_NAME,\n" + 
    		"  dc.COLUMN_NAME,\n" + 
    		"  dc.NULLABLE,\n" + 
    		"  dc.DATA_TYPE,\n" + 
    		"  NVL(dc.DATA_PRECISION,dc.DATA_LENGTH) DATA_LENGTH,\n" + 
    		"  NVL(dc.DATA_SCALE,0) DATA_SCALE,\n" + 
    		"  NVL(dc.DATA_PRECISION,0) DATA_PRECISION,\n" + 
    		"  NVL(x.pk_column,0) pk_column,\n" + 
    		"  NVL(y.uq_column,0) uq_column\n" + 
    		"FROM cdb_tab_cols dc\n" + 
    		"LEFT OUTER JOIN dcc x\n" + 
    		"ON x.con_id        =dc.con_id\n" + 
    		"AND x.owner        =dc.owner\n" + 
    		"AND x.table_name  =dc.TABLE_NAME\n" + 
    		"AND dc.COLUMN_NAME=x.column_name\n" + 
    		"LEFT OUTER JOIN duq y\n" + 
    		"ON y.con_id           =dc.con_id\n" + 
    		"AND y.table_owner     =dc.owner\n" + 
    		"AND y.table_name     =dc.TABLE_NAME\n" + 
    		"AND y.column_name    =dc.COLUMN_NAME\n" + 
    		"WHERE dC.Owner       ='SYSADM'\n" + 
    		"AND dc.TABLE_NAME    ='FACULTIES'\n" + 
    		"AND dc.HIDDEN_COLUMN ='NO'\n" + 
    		"AND dc.VIRTUAL_COLUMN='NO'\n" + 
    		"ORDER BY dc.TABLE_NAME,\n" + 
    		"  dc.COLUMN_ID";
}