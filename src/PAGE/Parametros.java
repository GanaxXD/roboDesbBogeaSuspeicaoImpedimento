package PAGE;

import java.util.List;

import javax.swing.JTextField;

import MODEL.FiltroEtiqueta;
import MODEL.TemaProcessualEtiqueta;
import PAGE.pje21.geral.DadosRedistribuicao;
import PAGE.pje21.justicaComum.CI.AtoJudicialEtiqueta;

/**
 * Classe que representa os parâmetros de configuração de cada robô. o Arquivo
 * json deve possuir o mesmo atributo aqui definido para que de forma automática
 * ele seja atribuído no momento de sua inicialização.
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @TJBA
 */
public class Parametros implements Cloneable{
	
	private String robo;
	
	private String execucaoJanela;

	private String url;
	private String usuario;
	private String senha;
	
	private String perfil;
	private String[] perfis;
	private String tarefa;
	private String[] tarefas;

	private String comunicacao;
	private String meioPoloAtivo;
	private String meioPoloPassivo;
	private String nomeArquivo;
	private String nomeArquivoAux;
	
	private String movimento;
	private String codMovimento;

	private String prazo;
	private String prazoAdvogado;
	private String prazoProcuradoria;
	private String ordem;
	private String urlTJBA;

	private String gerarRelatorios;

	private String validarPartes;

	private String qtdRobos;
	private String tipoDocumento;
	private String modeloDocumento;
	private String descricaoDocumento;
	private String documentoProcesso;
	private String[] documentosProcesso;
	
	private String fonteDeDados;
	private String intimarProcuradoriaAdvs;
	private String modeloAto;
	private String modeloAtoAdvogado;
	private String modeloAtoProcuradoria;
	private String tipoAto;
	private String assinar;
	private String textoValidacao;
	private String senhaToken;
	private String qtdLinhasGrid;
	
	private String filtrarEtiqueta;
	
	/**
	 * Nome para o qual a etiqueta a ser atribuída será renomeada.
	 * 
	 * A etiqueta que será atribuída aos processos pode já existir associada a
	 * processos. Portanto, esse parâmetro permite ao robô renomeá-la, de forma a
	 * mantê-la como histórico, e, dessa forma, o PJe criará a "nova" etiqueta
	 * contendo apenas os processos analisados por este procedimento.
	 * 
	 * @author William Sodre
	 */
	private String renomearEtiquetaAntesAtribuir;
	
	private String atribuirEtiqueta;
	private String manterEtiqueta;
	private String removerEtiqueta;
	private String motivoRemessa;
	private String queryProcessos;
	private String queryPartes;
	private String dbURL;
	private String dbUser;
	private String dbPass;
	private String movimentacaoDecursoDePrazo;
	private String navegador;
	private String timeout;
	private String movimentacaoBaixaDefinitiva;
	private String parametroQuery;

	private String queryIntimacoesPartes;

	private String deletarArquivo;


	private String arquivoDiario;
	private String urlDiario;
	private String secaoDiario;

	private String JCRLocation;

	private String[] movimentar;
	private String[] movimentarNoFinal;


	private String[] polos;
	private String[] tipoPolos;
	private String[] blackList;
	private String[] movimentosPeticaoIntermediaria;
	private String[] movimentosPermitidosBaixa;
	private String[] codigosTipoDocumentoED;
	private String[] movimentosJulgamento;
	private String[] acoes;
	private String[] parametrosQuery;

	private String sinapsesURL;
	private String sinapsesUser;
	private String sinapsesPassword;
	private float acuraciaIA;
	private String xpathDiario;
	private String movimentacaoIntimacao;

	private String caminhoDownload;

	private List<FiltroEtiqueta> filtrosEtiquetas;
	private AtoJudicialEtiqueta[] atosJudiciais;
	private DadosRedistribuicao dadosRedistribuicao;
	

	private List<TemaProcessualEtiqueta> temas;
	private List<AtoJudicialEtiqueta> atos;
	
	private int colunaProcesso;
	private int colunaIdProcesso;
	

	private int colunaEtiqueta;
	private String separador;
	
	private String mongoHost;
	private int mongoPort;
	private String mongoDB;
	private String mongoCollection;
	
	
	/* Parâmetros para o robô de protocolar processo */
	private String qtdProcessos;
	private String secao;
	private String classeJudicial;
	private String poloAtivoTipo;
	private String poloAtivoCpf;
	private String poloPassivoTipo;
	private String poloPassivoCpf;
	private String competencia;	
	
	/*
	 * Parâmetros para o robô que etiqueta processos pelo endereço da parte autora
	 * (São José de Ribamar)
	 */
	private String atribuirEtiquetaJuizado1;
	private String atribuirEtiquetaJuizado2;
	private String arquivoBairrosJuizado1;
	private String arquivoBairrosJuizado2;
	
	public boolean isJanelaOculta() {
		return ("OCULTA".equals(getExecucaoJanela()));
	}
	
	public String getMongoHost() {
		return mongoHost;
	}

	public void setMongoHost(String mongoHost) {
		this.mongoHost = mongoHost;
	}

	public int getMongoPort() {
		return mongoPort;
	}

	public void setMongoPort(int mongoPort) {
		this.mongoPort = mongoPort;
	}

	public String getMongoDB() {
		return mongoDB;
	}

	public void setMongoDB(String mongoDB) {
		this.mongoDB = mongoDB;
	}

	public String getMongoCollection() {
		return mongoCollection;
	}

	public void setMongoCollection(String mongoCollection) {
		this.mongoCollection = mongoCollection;
	}

	public String getManterEtiqueta() {
		return manterEtiqueta;
	}

	public String[] getDocumentosProcesso() {
		return documentosProcesso;
	}


	public void setDocumentosProcesso(String[] documentosProcesso) {
		this.documentosProcesso = documentosProcesso;
	}

	public void setManterEtiqueta(String manterEtiqueta) {
		this.manterEtiqueta = manterEtiqueta;
	}
	public int getColunaIdProcesso() {
		return colunaIdProcesso;
	}


	public void setColunaIdProcesso(int colunaIdProcesso) {
		this.colunaIdProcesso = colunaIdProcesso;
	}
	
	
	public String getNomeArquivoAux() {
		return nomeArquivoAux;
	}


	public void setNomeArquivoAux(String nomeArquivoAux) {
		this.nomeArquivoAux = nomeArquivoAux;
	}

	public String getSeparador() {
		return separador;
	}


	public void setSeparador(String separador) {
		this.separador = separador;
	}


	public int getColunaProcesso() {
		return colunaProcesso;
	}


	public void setColunaProcesso(int colunaProcesso) {
		this.colunaProcesso = colunaProcesso;
	}


	public int getColunaEtiqueta() {
		return colunaEtiqueta;
	}


	public void setColunaEtiqueta(int colunaEtiqueta) {
		this.colunaEtiqueta = colunaEtiqueta;
	}

	private String filtrarClasse;

	public DadosRedistribuicao getDadosRedistribuicao() {
		return dadosRedistribuicao;
	}


	public void setDadosRedistribuicao(DadosRedistribuicao dadosRedistribuicao) {
		this.dadosRedistribuicao = dadosRedistribuicao;
	}
	
	public String getFiltrarClasse() {
		return filtrarClasse;
	}


	public void setFiltrarClasse(String filtrarClasse) {
		this.filtrarClasse = filtrarClasse;
	}


	public String getDeletarArquivo() {
		return deletarArquivo;
	}


	public void setDeletarArquivo(String deletarArquivo) {
		this.deletarArquivo = deletarArquivo;
	}
	public String[] getPerfis() {
		return perfis;
	}

	
	public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
	
	public void setPerfis(String[] perfis) {
		this.perfis = perfis;
	}

	
	public String[] getTarefas() {
		return tarefas;
	}

	public void setTarefas(String[] tarefas) {
		this.tarefas = tarefas;
	}
	
	public List<AtoJudicialEtiqueta> getAtos() {
		return atos;
	}

	public void setAtos(List<AtoJudicialEtiqueta> atos) {
		this.atos = atos;
	}

	public void atribuirCredenciais(Parametros p) {
		
		setUsuario(p.getUsuario());
		setSenha(p.getSenha());
		setDbURL(p.getDbURL());
		setDbUser(p.getDbUser());
		setDbPass(p.getDbPass());
		setJCRLocation(p.getJCRLocation());
		setSinapsesURL(p.getSinapsesURL());
		setSinapsesUser(p.getSinapsesUser());
		setSinapsesPassword(p.getSinapsesPassword());
		setMongoCollection(p.getMongoCollection());
		setMongoHost(p.getMongoHost());
		setMongoPort(p.getMongoPort());
		setMongoDB(p.getMongoDB());
	}
	
	public String[] getMovimentarNoFinal() {
		return movimentarNoFinal;
	}

	public void setMovimentarNoFinal(String[] movimentarNoFinal) {
		this.movimentarNoFinal = movimentarNoFinal;
	}
	
	
	public String getGerarRelatorios() {
		return gerarRelatorios;
	}

	public void setGerarRelatorios(String gerarRelatorios) {
		this.gerarRelatorios = gerarRelatorios;
	}

	public float getAcuraciaIA() {
		return acuraciaIA;
	}

	public void setAcuraciaIA(float acuraciaIA) {
		this.acuraciaIA = acuraciaIA;
	}

	public List<TemaProcessualEtiqueta> getTemas() {
		return temas;
	}

	public void setTemas(List<TemaProcessualEtiqueta> temas) {
		this.temas = temas;
	}

	public String getCaminhoDownload() {
		return caminhoDownload;
	}

	public void setCaminhoDownload(String caminhoDownload) {
		this.caminhoDownload = caminhoDownload;
	}

	public AtoJudicialEtiqueta[] getAtosJudiciais() {
		return atosJudiciais;
	}

	public void setAtosJudiciais(AtoJudicialEtiqueta[] atosJudiciais) {
		this.atosJudiciais = atosJudiciais;
	}

	@Override
	public String toString() {
		return "\nParametros [\nrobo=" + robo + "\nurl=" + url + "\nperfil=" + perfil + "\ntarefa=" + tarefa + "]";
	}

	public String getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(String codMovimento) {
		this.codMovimento = codMovimento;
	}

	public List<FiltroEtiqueta> getFiltrosEtiquetas() {
		return filtrosEtiquetas;
	}

	public void setFiltrosEtiquetas(List<FiltroEtiqueta> filtrosEtiquetas) {
		this.filtrosEtiquetas = filtrosEtiquetas;
	}

	public String getJCRLocation() {
		return JCRLocation;
	}

	public void setJCRLocation(String jCRLocation) {
		JCRLocation = jCRLocation;
	}

	public String[] getParametrosQuery() {
		return parametrosQuery;
	}

	public void setParametrosQuery(String[] parametrosQuery) {
		this.parametrosQuery = parametrosQuery;
	}

	public String getValidarPartes() {
		return validarPartes;
	}

	public void setValidarPartes(String validarPartes) {
		this.validarPartes = validarPartes;
	}

	public String getUrlTJBA() {
		return urlTJBA;
	}

	public void setUrlTJBA(String urlTJBA) {
		this.urlTJBA = urlTJBA;
	}

	public String getMovimentacaoIntimacao() {
		return movimentacaoIntimacao;
	}

	public void setMovimentacaoIntimacao(String movimentacaoIntimacao) {
		this.movimentacaoIntimacao = movimentacaoIntimacao;
	}

	public String getXpathDiario() {
		return xpathDiario;
	}

	public void setXpathDiario(String xpathDiario) {
		this.xpathDiario = xpathDiario;
	}

	public String getSecaoDiario() {
		return secaoDiario;
	}

	public void setSecaoDiario(String secaoDiario) {
		this.secaoDiario = secaoDiario;
	}

	public String getUrlDiario() {
		return urlDiario;
	}

	public void setUrlDiario(String urlDiario) {
		this.urlDiario = urlDiario;
	}

	public String getQueryIntimacoesPartes() {
		return queryIntimacoesPartes;
	}

	public void setQueryIntimacoesPartes(String queryIntimacoesPartes) {
		this.queryIntimacoesPartes = queryIntimacoesPartes;
	}

	public String getArquivoDiario() {
		return arquivoDiario;
	}

	public void setArquivoDiario(String arquivoDiario) {
		this.arquivoDiario = arquivoDiario;
	}

	public String[] getAcoes() {
		return acoes;
	}

	public void setAcoes(String[] acoes) {
		this.acoes = acoes;
	}

	public String getParametroQuery() {
		return parametroQuery;
	}

	public void setParametroQuery(String parametroQuery) {
		this.parametroQuery = parametroQuery;
	}

	public String getSinapsesURL() {
		return sinapsesURL;
	}

	public void setSinapsesURL(String sinapsesURL) {
		this.sinapsesURL = sinapsesURL;
	}

	public String getSinapsesUser() {
		return sinapsesUser;
	}

	public void setSinapsesUser(String sinapsesUser) {
		this.sinapsesUser = sinapsesUser;
	}

	public String getSinapsesPassword() {
		return sinapsesPassword;
	}

	public void setSinapsesPassword(String sinapsesPassword) {
		this.sinapsesPassword = sinapsesPassword;
	}

	public String getAssinar() {
		return assinar;
	}

	public void setAssinar(String assinar) {
		this.assinar = assinar;
	}

	public String getTipoAto() {
		return tipoAto;
	}

	public void setTipoAto(String tipoAto) {
		this.tipoAto = tipoAto;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getFonteDeDados() {
		return fonteDeDados;
	}

	public void setFonteDeDados(String fonteDeDados) {
		this.fonteDeDados = fonteDeDados;
	}

	public String[] getMovimentar() {
		return movimentar;
	}

	public void setMovimentar(String[] movimentar) {
		this.movimentar = movimentar;
	}

	public String getDocumentoProcesso() {
		return documentoProcesso;
	}

	public void setDocumentoProcesso(String documentoProcesso) {
		this.documentoProcesso = documentoProcesso;
	}

	public String getComunicacao() {
		return comunicacao;
	}

	public void setComunicacao(String comunicacao) {
		this.comunicacao = comunicacao;
	}

	public String[] getPolos() {
		return polos;
	}

	public void setPolos(String[] polos) {
		this.polos = polos;
	}

	public String[] getTipoPolos() {
		return tipoPolos;
	}

	public void setTipoPolos(String[] tipoPolos) {
		this.tipoPolos = tipoPolos;
	}

	public String getMovimentacaoBaixaDefinitiva() {
		return movimentacaoBaixaDefinitiva;
	}

	public void setMovimentacaoBaixaDefinitiva(String movimentacaoBaixaDefinitiva) {
		this.movimentacaoBaixaDefinitiva = movimentacaoBaixaDefinitiva;
	}

	public String[] getMovimentosJulgamento() {
		return movimentosJulgamento;
	}

	public void setMovimentosJulgamento(String[] movimentosJulgamento) {
		this.movimentosJulgamento = movimentosJulgamento;
	}

	public String getRobo() {
		return robo;
	}

	public void setRobo(String robo) {
		this.robo = robo;
	}

	public String getNavegador() {
		return navegador;
	}

	public void setNavegador(String navegador) {
		this.navegador = navegador;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getMovimentacaoDecursoDePrazo() {
		return movimentacaoDecursoDePrazo;
	}

	public void setMovimentacaoDecursoDePrazo(String movimentacaoDecursoDePrazo) {
		this.movimentacaoDecursoDePrazo = movimentacaoDecursoDePrazo;
	}

	public String[] getMovimentosPeticaoIntermediaria() {
		return movimentosPeticaoIntermediaria;
	}

	public void setMovimentosPeticaoIntermediaria(String[] movimentosPeticaoIntermediaria) {
		this.movimentosPeticaoIntermediaria = movimentosPeticaoIntermediaria;
	}

	public String[] getCodigosTipoDocumentoED() {
		return codigosTipoDocumentoED;
	}

	public void setCodigosTipoDocumentoED(String[] codigosTipoDocumentoED) {
		this.codigosTipoDocumentoED = codigosTipoDocumentoED;
	}

	public String[] getMovimentosPermitidosBaixa() {
		return movimentosPermitidosBaixa;
	}

	public void setMovimentosPermitidosBaixa(String[] movimentosPermitidosBaixa) {
		this.movimentosPermitidosBaixa = movimentosPermitidosBaixa;
	}

	public String getQueryProcessos() {
		return queryProcessos;
	}

	public void setQueryProcessos(String queryProcessos) {
		this.queryProcessos = queryProcessos;
	}

	public String getQueryPartes() {
		return queryPartes;
	}

	public void setQueryPartes(String queryPartes) {
		this.queryPartes = queryPartes;
	}

	public String getDbURL() {
		return dbURL;
	}

	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	public String getRemoverEtiqueta() {
		return removerEtiqueta;
	}

	public void setRemoverEtiqueta(String removerEtiqueta) {
		this.removerEtiqueta = removerEtiqueta;
	}

	public String getMotivoRemessa() {
		return motivoRemessa;
	}

	public void setMotivoRemessa(String motivoRemessa) {
		this.motivoRemessa = motivoRemessa;
	}

	public String getAtribuirEtiqueta() {
		return atribuirEtiqueta;
	}

	public void setAtribuirEtiqueta(String atribuirEtiqueta) {
		this.atribuirEtiqueta = atribuirEtiqueta;
	}

	public boolean isFiltrarEtiqueta() {
		if (getFiltrarEtiqueta() != null && !getFiltrarEtiqueta().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean atribuirEtiqueta() {
		if (getAtribuirEtiqueta() != null && !getAtribuirEtiqueta().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean removerEtiqueta() {
		if (getRemoverEtiqueta() != null && getRemoverEtiqueta().equals("sim")) {
			return true;
		} else {
			return false;
		}
	}

	public String getFiltrarEtiqueta() {
		return filtrarEtiqueta;
	}

	public void setFiltrarEtiqueta(String filtrarEtiqueta) {
		this.filtrarEtiqueta = filtrarEtiqueta;
	}

	public String getRenomearEtiquetaAntesAtribuir() {
		return renomearEtiquetaAntesAtribuir;
	}

	public void setRenomearEtiquetaAntesAtribuir(String renomearEtiquetaAntesAtribuir) {
		this.renomearEtiquetaAntesAtribuir = renomearEtiquetaAntesAtribuir;
	}

	public String getQtdLinhasGrid() {
		return qtdLinhasGrid;
	}

	public void setQtdLinhasGrid(String qtdLinhasGrid) {
		this.qtdLinhasGrid = qtdLinhasGrid;
	}

	public String getSenhaToken() {
		return senhaToken;
	}

	public void setSenhaToken(String senhaToken) {
		this.senhaToken = senhaToken;
	}

	public String getTextoValidacao() {
		return textoValidacao;
	}

	public void setTextoValidacao(String textoValidacao) {
		this.textoValidacao = textoValidacao;
	}

	public String getIntimarProcuradoriaAdvs() {
		return intimarProcuradoriaAdvs;
	}

	public void setIntimarProcuradoriaAdvs(String intimarProcuradoriaAdvs) {
		this.intimarProcuradoriaAdvs = intimarProcuradoriaAdvs;
	}

	public String getModeloAto() {
		return modeloAto;
	}

	public void setModeloAto(String modeloAto) {
		this.modeloAto = modeloAto;
	}

	public String getModeloAtoAdvogado() {
		return modeloAtoAdvogado;
	}

	public void setModeloAtoAdvogado(String modeloAtoAdvogado) {
		this.modeloAtoAdvogado = modeloAtoAdvogado;
	}

	public String getModeloAtoProcuradoria() {
		return modeloAtoProcuradoria;
	}

	public void setModeloAtoProcuradoria(String modeloAtoProcuradoria) {
		this.modeloAtoProcuradoria = modeloAtoProcuradoria;
	}

	public String getDescricaoDocumento() {
		return descricaoDocumento;
	}

	public void setDescricaoDocumento(String descricaoDocumento) {
		this.descricaoDocumento = descricaoDocumento;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getModeloDocumento() {
		return modeloDocumento;
	}

	public void setModeloDocumento(String modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}

	public String getQtdRobos() {
		return qtdRobos;
	}

	public void setQtdRobos(String qtdRobos) {
		this.qtdRobos = qtdRobos;
	}

	public String[] getBlackList() {
		return blackList;
	}

	public void setBlackList(String[] blackList) {
		this.blackList = blackList;
	}

	public String getOrdem() {
		return ordem;
	}

	public void setOrdem(String ordem) {
		this.ordem = ordem;
	}

	public Parametros() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public String getTarefa() {
		return tarefa;
	}

	public void setTarefa(String tarefa) {
		this.tarefa = tarefa;
	}

	public String getMeioPoloAtivo() {
		return meioPoloAtivo;
	}

	public void setMeioPoloAtivo(String meioPoloAtivo) {
		this.meioPoloAtivo = meioPoloAtivo;
	}

	public String getMeioPoloPassivo() {
		return meioPoloPassivo;
	}

	public void setMeioPoloPassivo(String meioPoloPassivo) {
		this.meioPoloPassivo = meioPoloPassivo;
	}

	public String getMovimento() {
		return movimento;
	}

	public void setMovimento(String movimento) {
		this.movimento = movimento;
	}

	public String getPrazo() {
		return prazo;
	}

	public String getPrazoAdvogado() {
		return prazoAdvogado;
	}

	public void setPrazoAdvogado(String prazoAdvogado) {
		this.prazoAdvogado = prazoAdvogado;
	}

	public String getPrazoProcuradoria() {
		return prazoProcuradoria;
	}

	public void setPrazoProcuradoria(String prazoProcuradoria) {
		this.prazoProcuradoria = prazoProcuradoria;
	}

	public void setPrazo(String prazo) {
		this.prazo = prazo;
	}

	public String getQtdProcessos() {
		return qtdProcessos;
	}

	public void setQtdProcessos(String qtdProcessos) {
		this.qtdProcessos = qtdProcessos;
	}

	public String getSecao() {
		return secao;
	}

	public void setSecao(String secao) {
		this.secao = secao;
	}

	public String getClasseJudicial() {
		return classeJudicial;
	}

	public void setClasseJudicial(String classeJudicial) {
		this.classeJudicial = classeJudicial;
	}

	public String getPoloAtivoCpf() {
		return poloAtivoCpf;
	}

	public void setPoloAtivoCpf(String poloAtivoCpf) {
		this.poloAtivoCpf = poloAtivoCpf;
	}

	public String getPoloAtivoTipo() {
		return poloAtivoTipo;
	}

	public void setPoloAtivoTipo(String poloAtivoTipo) {
		this.poloAtivoTipo = poloAtivoTipo;
	}

	public String getPoloPassivoTipo() {
		return poloPassivoTipo;
	}

	public void setPoloPassivoTipo(String poloPassivoTipo) {
		this.poloPassivoTipo = poloPassivoTipo;
	}

	public String getPoloPassivoCpf() {
		return poloPassivoCpf;
	}

	public void setPoloPassivoCpf(String poloPassivoCpf) {
		this.poloPassivoCpf = poloPassivoCpf;
	}

	public String getCompetencia() {
		return competencia;
	}

	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}

	public String getAtribuirEtiquetaJuizado1() {
		return atribuirEtiquetaJuizado1;
	}

	public void setAtribuirEtiquetaJuizado1(String atribuirEtiquetaJuizado1) {
		this.atribuirEtiquetaJuizado1 = atribuirEtiquetaJuizado1;
	}

	public String getAtribuirEtiquetaJuizado2() {
		return atribuirEtiquetaJuizado2;
	}

	public void setAtribuirEtiquetaJuizado2(String atribuirEtiquetaJuizado2) {
		this.atribuirEtiquetaJuizado2 = atribuirEtiquetaJuizado2;
	}

	public String getArquivoBairrosJuizado1() {
		return arquivoBairrosJuizado1;
	}

	public void setArquivoBairrosJuizado1(String arquivoBairros) {
		this.arquivoBairrosJuizado1 = arquivoBairros;
	}

	public String getArquivoBairrosJuizado2() {
		return arquivoBairrosJuizado2;
	}

	public void setArquivoBairrosJuizado2(String arquivoBairrosJuizado2) {
		this.arquivoBairrosJuizado2 = arquivoBairrosJuizado2;
	}

	public String getExecucaoJanela() {
		return execucaoJanela;
	}

	public void setExecucaoJanela(String janela) {
		this.execucaoJanela = janela;
	}
}