SELECT DISTINCT	
	p.nr_processo as numeroprocesso,
	pd.id_processo_documento as idprocessodocumento, 
	pd.dt_juntada as dtjuntada,
	tpd.id_tipo_processo_documento as idtipoprocessodocumento,
	tpd.ds_tipo_processo_documento as dstipoprocessodocumento,
	pdb.nr_documento_storage as nrdocumentostorage,
	pdb.ds_extensao as dsextensao,
	pdb.ds_modelo_documento as dsmodelodocumento,
	pdb.nr_tamanho as nrtamanho
FROM JBPM_TASKINSTANCE TASK
INNER JOIN JBPM_VARIABLEINSTANCE var ON TASK.PROCINST_ = VAR.PROCESSINSTANCE_ AND VAR.NAME_ = 'processo' AND VAR.CLASS_= 'L' 
		and (task.end_ is null 	and task.name_ = (?))
inner join core.tb_processo p on p.id_processo= var.longvalue_
inner join core.tb_processo_instance pin on pin.id_processo=p.id_processo
inner join client.tb_processo_trf trf on trf.id_processo_trf = p.id_processo
inner join core.tb_processo_documento pd on p.id_processo = pd.id_processo and pd.id_tipo_processo_documento in (115, 12, 168, 126, 19, 122, 62, 310)
inner join core.tb_processo_documento_bin pdb on pd.id_processo_documento_bin = pdb.id_processo_documento_bin
inner join core.tb_tipo_processo_documento tpd on pd.id_tipo_processo_documento = tpd.id_tipo_processo_documento
inner join client.tb_orgao_julgador oj on oj.id_orgao_julgador = trf.id_orgao_julgador  and oj.id_orgao_julgador in (?)
inner join client.tb_processo_parte pp on pp.id_processo_trf = trf.id_processo_trf 
									and pp.id_pessoa in (select id_usuario from acl.tb_usuario_login
																	where (ds_login like '151396290%' or ds_login like '135046750%')
														)
