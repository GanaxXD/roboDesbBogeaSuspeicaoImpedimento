package PAGE.pje21.geral;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import CLIENT.util.ArquivoUtil;
import CLIENT.util.PDFUtil;
import MODEL.Processo;
import MODEL.TemaProcessualEtiqueta;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje21.PainelTarefasPJE;

/**
 * Robô que realiza citações e intimações de acordo com a configuração passada.
 * Apenas partes devidamente qualificadas são intimadas (Com Procuradorias ou
 * Advogados)
 * 
 * @autor Leonardo Ribeiro de Oliveira
 * @COJE @TJBA
 */
public class TriagemInicial_Filtro_Page extends TriagemSimples_Page {

	
	
	public TriagemInicial_Filtro_Page(Parametros parametro) throws AutomacaoException {
		super(parametro);

	}

	protected List<Processo> carregarProcessosCSV() throws AutomacaoException {
		System.out.println();
		List<Processo> listaRetorno = new ArrayList<Processo>();
		List<Processo> nProcessos = super.carregarProcessosCSV();
		
		Set<String> hashset = carregarProcessosMigrados();
		if(hashset.size()>0) {
			
			for (Processo processo : nProcessos) {
				if(hashset.contains(processo.getNumeroProcesso())) {
					listaRetorno.add(processo);
				}
				
			}
		
		}
		
		return listaRetorno;
	}

	protected Set<String> carregarProcessosMigrados() throws AutomacaoException {
		
		Set<String> hashset = new HashSet<String>();
		
		try {
			String arquivo = new File("").getAbsolutePath() + "\\" + getParametros().getNomeArquivoAux();
			
			
			
			InputStreamReader inputStream = new InputStreamReader(new FileInputStream(arquivo), "UTF-8");
			BufferedReader reader = new BufferedReader(inputStream);

			String linha = "";
			int count = 0;
			while ((linha = reader.readLine()) != null) {
				if (count == 0) {
					count++;
					continue;
				}
				
				hashset.add(linha.trim());
				
			}
			reader.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Não foi possível CARREGAR PROCESSOS DO ARQUIVO");

		}

		return hashset;
	}

	
}
