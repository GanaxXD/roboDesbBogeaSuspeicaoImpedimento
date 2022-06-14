select 
				p.nr_processo as numeroProcesso,
				pe.dt_atualizacao as dataMovimentacao,
				e.cd_evento as codMovimentoCNJ,
				e.ds_evento as descMovimentoCNJ,
				pe.ds_texto_final_interno as textoFinalMovimento,
				pd.DS_PROCESSO_DOCUMENTO as descDocumento,
				pd.DS_PROCESSO_DOCUMENTO as tipoDocumento
		from core.tb_processo p 
		inner join client.tb_processo_trf trf on p.id_processo = trf.id_processo_trf
												and trf.id_processo_trf in 
												(
												
															SELECT trf2.id_processo_trf
															FROM JBPM_TASKINSTANCE TASK
															INNER JOIN JBPM_VARIABLEINSTANCE var ON TASK.PROCINST_ = VAR.PROCESSINSTANCE_
																			AND VAR.NAME_ = 'processo'	AND VAR.CLASS_= 'L'
																			and task.end_ is null
																			and task.name_ = ?
															inner join core.tb_processo p2 on p2.id_processo= var.longvalue_
															inner join client.tb_processo_trf trf2 on trf2.id_processo_trf = p2.id_processo
															inner join client.tb_orgao_julgador oj on oj.id_orgao_julgador = trf2.id_orgao_julgador 
																	and oj.id_orgao_julgador in (13, 9, 14)
												)
		inner join core.tb_processo_evento pe on trf.id_processo_trf = pe.id_processo
												and pe.dt_atualizacao >= (  
																			select max(pe2.dt_atualizacao) from core.tb_processo_evento pe2 
																			inner join core.tb_evento e on pe2.id_evento = e.id_evento
																			where pe2.id_processo = p.id_processo
																			and e.cd_evento in ('237','238','239','240','241','242','901')		
												)
												
		inner join core.tb_evento e on pe.id_evento = e.id_evento
		left join core.tb_processo_documento pd on pe.id_processo_documento = pd.id_processo_documento
		left join core.tb_tipo_processo_documento TPD on TPD.id_tipo_processo_documento = pd.id_tipo_processo_documento
