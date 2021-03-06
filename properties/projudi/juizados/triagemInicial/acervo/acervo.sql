	--ACERVO
	SELECT 
		DISTINCT 
		PROCESSO.NUMEROPROCESSOUNICO as numeroprocesso, 
		ARQUIVO_DOCUMENTO.CODDOCUMENTO AS idprocessodocumento,
		ARQUIVO_DOCUMENTO.DATAINSERCAO AS dtjuntada,
		DESCRICAO_TIPO_ARQUIVO.DESCRICAO AS dstipoprocessodocumento,  
		ARQUIVO_DOCUMENTO.CAMINHO AS nrdocumentostorage,
		'https://projudi.tjba.jus.br/projudi/listagens/DadosProcesso?numeroProcesso=' || to_char(PROCESSO.numeroprocesso) as linkProcesso
	FROM 
		ECNJ.MOVIMENTACAO MOV_DISTRIBUICAO,
		ECNJ.PROCESSO,
		ECNJ.ARQUIVO_DOCUMENTO,
		ECNJ.DESCRICAO_TIPO_ARQUIVO, 
		ECNJ.AREADEVARAS
	WHERE MOV_DISTRIBUICAO.NUMEROPROCESSO = PROCESSO.NUMEROPROCESSO
	AND MOV_DISTRIBUICAO.CODDOCUMENTO = ARQUIVO_DOCUMENTO.CODDOCUMENTO
	AND ARQUIVO_DOCUMENTO.CODIGOTIPOARQUIVO = DESCRICAO_TIPO_ARQUIVO.CODIGO
	AND PROCESSO.COD_AREA = AREADEVARAS.COD_AREA AND AREADEVARAS.COD_AREA = ?
	AND ARQUIVO_DOCUMENTO.CONTENTTYPE = 'application/pdf'
	AND DESCRICAO_TIPO_ARQUIVO.CODIGO = 30 -- PETICAO_INICIAL
	AND PROCESSO.DATAARQUIVAMENTO IS NULL
	AND PROCESSO.COD_VARA = ?
	AND MOV_DISTRIBUICAO.CODDESCREVEMOVIMENTACAO = 981 -- MOV RECEBIDO PELO DISTRIBUIDOR (ONDE FICA A PET INI)
	AND PROCESSO.SEGREDOJUSTICA = 0 -- (se for gerar dataset descomentar esta linha)
	AND NOT EXISTS
	(
		SELECT 1 FROM ECNJ.PROCESSO p2 
		INNER JOIN ECNJ.LOCALIZADOR_PROCESSO ON LOCALIZADOR_PROCESSO.NUMEROPROCESSO = p2.NUMEROPROCESSO
		INNER JOIN ECNJ.TIPO_LOCALIZADOR ON TIPO_LOCALIZADOR.CODLOCALIZADOR = LOCALIZADOR_PROCESSO.CODLOCALIZADOR
		AND TIPO_LOCALIZADOR.DESCRICAO  LIKE 'IA_%'
		WHERE  p2.NUMEROPROCESSO= PROCESSO.NUMEROPROCESSO 
		AND TIPO_LOCALIZADOR.codvara = 0
		AND LOCALIZADOR_PROCESSO.DATAFECHAMENTO IS NULL
	)
	--AND rownum < 50