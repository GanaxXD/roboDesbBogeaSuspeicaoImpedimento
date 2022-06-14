select 
			distinct p.nr_processo as numeroProcesso,
			ul.DS_NOME as nomeParte
	from core.tb_processo p 
	join client.tb_processo_trf trf on p.id_processo = trf.id_processo_trf
									and trf.id_processo_trf in 
									(
											SELECT trf2.id_processo_trf
											FROM JBPM_TASKINSTANCE TASK
											INNER JOIN JBPM_VARIABLEINSTANCE var ON TASK.PROCINST_ = VAR.PROCESSINSTANCE_
															AND VAR.NAME_ = 'processo'
																AND VAR.CLASS_= 'L'
																and task.end_ is null
																and task.name_ = ?
											inner join core.tb_processo p2 on p2.id_processo= var.longvalue_
											inner join client.tb_processo_trf trf2 on trf2.id_processo_trf = p2.id_processo
											inner join client.tb_orgao_julgador oj on oj.id_orgao_julgador = trf2.id_orgao_julgador 
													and oj.id_orgao_julgador in (13, 9, 14 )
									
									)
	join client.tb_processo_parte pp on pp.id_processo_trf = trf.id_processo_trf and pp.in_situacao = 'A'
	join acl.tb_usuario_login ul on ul.id_usuario = pp.id_pessoa and pp.id_tipo_parte in (25,26, 233)
	join client.tb_tipo_parte tp on pp.id_tipo_parte = tp.id_tipo_parte