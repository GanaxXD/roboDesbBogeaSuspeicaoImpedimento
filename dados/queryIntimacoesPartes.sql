select 
	p.nr_processo as numeroProcesso,
	proc_parte.nm_pessoa_parte as nomeParte,
	proc_parte.ds_recibo_dje as reciboDiario
from core.tb_processo p 
inner join client.tb_processo_trf trf on trf.id_processo_trf = p.id_processo 
INNER JOIN client.tb_processo_expediente processo_expediente on trf.id_processo_trf = processo_expediente.id_processo_trf
INNER JOIN client.tb_proc_parte_expediente proc_parte on proc_parte.id_processo_expediente = processo_expediente.id_processo_expediente
where proc_parte.dt_ciencia_parte is null 
and proc_parte.in_ciencia_sistema = false
and proc_parte.in_fechado = false
and proc_parte.in_tipo_prazo = 'D'
and processo_expediente.in_meio_expedicao_expediente in ('P')
and p.id_processo in (

		SELECT p.id_processo
		FROM JBPM_TASKINSTANCE TASK
		INNER JOIN JBPM_VARIABLEINSTANCE var ON TASK.PROCINST_ = VAR.PROCESSINSTANCE_
						AND VAR.NAME_ = 'processo'
						AND VAR.CLASS_= 'L'
						and task.end_ is null
						and task.name_ = ?
		inner join core.tb_processo p on p.id_processo= var.longvalue_
		inner join client.tb_processo_trf trf on trf.id_processo_trf = p.id_processo
		inner join client.tb_orgao_julgador oj on oj.id_orgao_julgador = trf.id_orgao_julgador 
				and oj.id_orgao_julgador in (13, 9, 14)
		inner join client.tb_jurisdicao jur on jur.id_jurisdicao = trf.id_jurisdicao


)
and processo_expediente.dt_criacao_expediente >= to_date('01/12/2019','DD/MM/YYYY')
and processo_expediente.dt_criacao_expediente < to_date('20/07/2020','DD/MM/YYYY')