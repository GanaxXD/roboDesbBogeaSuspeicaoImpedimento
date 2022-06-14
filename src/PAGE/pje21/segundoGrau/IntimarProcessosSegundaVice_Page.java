package PAGE.pje21.segundoGrau;


import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.geral.CitacaoIntimacao_Page;

public class IntimarProcessosSegundaVice_Page extends CitacaoIntimacao_Page {

	public IntimarProcessosSegundaVice_Page(Parametros parametro) throws AutomacaoException {
		super(parametro);
	}
	
	
	protected void executar(Processo processo)
			throws InterruptedException, AutomacaoException {

		super.executar(processo);
		getParametros().setRemoverEtiqueta("sim");
		getParametros().setAtribuirEtiqueta(getParametros().getFiltrarEtiqueta());
		atribuirEtiqueta(getParametros().getFiltrarEtiqueta().replace("Robo", "OK"));
		
	}
	
	
	protected boolean partesQualificadas(Processo processo, String[] polos)
			throws InterruptedException, AutomacaoException {

		if (getParametros().getValidarPartes() != null && getParametros().getValidarPartes().equalsIgnoreCase("sim")) {
			return super.partesQualificadas(processo, polos);
		} else {

			clicar("//button[@title='Abrir autos']", 20, 2000);

			Thread.sleep(8000);

			alternarParaDetalhes();

			clicar("//a[@title='Mais detalhes']", 60, 2000);

			Thread.sleep(1000);

			carregarPartes(processo);
			
			if(processo.getListaAdvogadosPoloAtivo().size()>=1 && processo.getListaPartePoloPassivo().size()>=1) {
				return true;	
			}else {
				return false;
			}
			
		}

	}

}
