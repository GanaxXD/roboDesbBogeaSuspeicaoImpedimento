package PAGE.pje.geral;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.openqa.selenium.By;

import MODEL.Processo;
import PAGE.AutomacaoException;
import PAGE.Parametros;
import PAGE.pje.PaginaBasePJE;

/**
 * Rob� utilizado para inativar servidor ou remover todas as visibilidades
 * @autor Leonardo Ribeiro de Oliveira 
 * @COJE @TJBA
 */
public class InativarRemoverVisibilidadeServidor_Page extends PaginaBasePJE {

	public InativarRemoverVisibilidadeServidor_Page(Parametros parametro) throws AutomacaoException {
		setParametros(parametro);
	}

	@Override
	protected void realizarTarefa(Processo processo)
			throws InterruptedException, AutomacaoException {

		try {

			Thread.sleep(1000);

			limpar();

			digitar("//*[@id=\"pesquisarServidorForm:idNomeDecoration:idNome\"]", processo.getNumeroProcesso().trim());

			Thread.sleep(2000);

			clicar("//input[@id = 'pesquisarServidorForm:searchButton']");

			Thread.sleep(8000);
			String cpf = "";

			try {
				cpf = obterTexto(By.xpath("//tbody[@id='pessoaServidorList:tb']/tr/td[2]/div/span"));

			} catch (Exception e) {
				System.out.println("Erro ao obter CPF");
			}

			int qtdElementosGrid = obterQuantidadeElementos("//*[@id=\"pessoaServidorList:tb\"]/tr");
			Thread.sleep(2000);

			if (qtdElementosGrid > 1) {
				criarLog("O servidor " + processo.getNumeroProcesso()
						+ " n�o foi desativado. Consulta retornou mais de um registro!", obterArquivoLog());
				throw new AutomacaoException("O servidor " + processo.getNumeroProcesso() + " n�o foi desativado");

			} else if (qtdElementosGrid == 1) {
				getDriver().switchTo().defaultContent();
				Thread.sleep(2000);

				switch (getParametros().getTarefa()) {
				case "inativar":
					inativar();
					break;
				case "removerVisibilidade":
					removerVisibilidade();
					break;

				}

			} else {
				criarLog("O servidor " + processo.getNumeroProcesso()
						+ " n�o foi desativado. Consulta n�o retornou registro!", obterArquivoLog());
				throw new AutomacaoException("O servidor " + processo.getNumeroProcesso() + " n�o foi desativado");
			}

			criarLog("\nO servidor (" + processo.getNumeroProcesso() + ") - CPF: " + cpf
					+ " foi desativado com sucesso!!", obterArquivoLog());

			Thread.sleep(4000);

		} catch (Exception e) {
			criarLog("\nO servidor (" + processo.getNumeroProcesso()
					+ ") n�o foi desativado ou n�o foi localizado no cadastro", obterArquivoLog());
			throw new AutomacaoException("O servidor " + processo.getNumeroProcesso() + " n�o foi desativado");
		} finally {

			try {
				getDriver().switchTo().defaultContent();
				clicar("//*[@id=\"search_lbl\"]");
				Thread.sleep(5000);
			} catch (Exception e) {

			}

			limpar();

			Thread.sleep(2000);

		}

	}

	private void removerVisibilidade() throws AutomacaoException, InterruptedException {

		int qtdElementosGrid;

		// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// String dataFinal = sdf.format(GregorianCalendar.getInstance().getTime());
		int diaAtual = GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH);

		Thread.sleep(2000);

		clicar("//a[@title='Editar']");
		Thread.sleep(4000);

		clicar("//*[@id=\"visibilidadeUsuario_lbl\"]");
		Thread.sleep(8000);

		try {

			qtdElementosGrid = obterQuantidadeElementos("//*[@id=\"idUsuarioLocalizacaoVisibilidade:tb\"]/tr");

			for (int i = 0; i < qtdElementosGrid; i++) {

				clicar("//a[contains(@name, 'idUsuarioLocalizacaoVisibilidade:" + i + "') and @title='Editar']");

				Thread.sleep(2000);

				limparDigitacao("//*[@id=\"usuarioLocalizacaoVisibilidade:idDtFinalDecoration:idDtFinalInputDate\"]");

				Thread.sleep(2000);

				// digitar("//*[@id=\"usuarioLocalizacaoVisibilidade:idDtFinalDecoration:idDtFinalInputDate\"]",
				// dataFinal);

				clicar("//*[@id=\"usuarioLocalizacaoVisibilidade:idDtFinalDecoration:idDtFinalPopupButton\"]");

				Thread.sleep(2000);

				clicar("//*[@id=\"usuarioLocalizacaoVisibilidade:idDtFinalDecoration:idDtFinalDayCell" + diaAtual
						+ "\"]");

				Thread.sleep(2000);

				clicar("//input[@value='Salvar']");

				Thread.sleep(2000);

			}

			clicar("//*[@id=\"search_lbl\"]");
			Thread.sleep(5000);

		} catch (Exception e) {
			criarLog("Erro ao tentar setar a data fim na visibilidade do servidor", obterArquivoLog());
			throw new AutomacaoException("Erro ao tentar setar a data fim na visibilidade do servidor");
		}

	}

	private void inativar() throws AutomacaoException, InterruptedException {
		int qtdElementosGrid;
		clicar("//a[@title='Inativar perfil']");
		Thread.sleep(2000);
		getDriver().switchTo().alert().accept();

		Thread.sleep(10000);

		String perfilAtivo;

		try {
			perfilAtivo = obterTexto(By.xpath("//tbody[@id='pessoaServidorList:tb']/tr/td[5]/div/span"));

			if (perfilAtivo != null && perfilAtivo.equalsIgnoreCase("sim")) {

				clicar("//a[@title='Editar']");
				Thread.sleep(4000);

				clicar("//*[@id=\"visibilidadeUsuario_lbl\"]");
				Thread.sleep(8000);
				try {

					qtdElementosGrid = obterQuantidadeElementos("//*[@id=\"idUsuarioLocalizacaoVisibilidade:tb\"]/tr");
					for (int i = 0; i < qtdElementosGrid; i++) {
						clicar("//a[contains(@name, 'idUsuarioLocalizacaoVisibilidade:0') and @title='Remover']");
						Thread.sleep(2000);
						getDriver().switchTo().alert().accept();
						Thread.sleep(8000);
					}
				} catch (Exception e) {

				}

				clicar("//*[@id=\"localizacaoUsuario_lbl\"]");
				Thread.sleep(8000);

				try {
					qtdElementosGrid = obterQuantidadeElementos(
							"//*[@id=\"idUsuarioLocalizacaoMagistradoServidor:tb\"]/tr");
					for (int i = 0; i < qtdElementosGrid; i++) {

						clicar("//a[contains(@name, 'idUsuarioLocalizacaoMagistradoServidor:0') and @title='Remover']");
						Thread.sleep(2000);
						getDriver().switchTo().alert().accept();
						Thread.sleep(8000);
					}

				} catch (Exception e) {

				}

				try {

					clicar("//*[@id=\"search_lbl\"]");
					Thread.sleep(5000);

					clicar("//a[@title='Inativar perfil']");
					Thread.sleep(2000);
					getDriver().switchTo().alert().accept();

					Thread.sleep(5000);

				} catch (Exception e) {

				}

			}

		} catch (Exception e) {

		}
	}

	private void limpar() throws AutomacaoException, InterruptedException {
		getDriver().switchTo().defaultContent();

		// Thread.sleep(2000);
		// clicar("//*[@id=\"pesquisarServidorForm:clearButton\"]");
		Thread.sleep(1000);

		limparDigitacao("//*[@id=\"pesquisarServidorForm:idNomeDecoration:idNome\"]");
		Thread.sleep(1000);
		// limparDigitacao("//*[@id=\"pesquisarServidorForm:idCpfDecoration:idCpf\"]");
		// Thread.sleep(1000);

	}

	protected void selecionarPerfil() throws AutomacaoException {
		super.selecionarPerfil();
		
		try {

			clicar("//a[@title='Abrir menu']");

			Thread.sleep(2000);

			clicar("//a[text()=' Configura��o ']");

			Thread.sleep(2000);

			clicar("//a[text()=' Pessoa ']");

			Thread.sleep(2000);

			clicar("//a[text()=' Servidor ']");

		} catch (Exception e) {
			throw new AutomacaoException("Erro ao selecionar a consulta de servidor: "+ e.getMessage());
		}

	}

	@Override
	protected void selecionarTarefa(String tarefa, boolean isFiltrarEtiqueta, String etiqueta)
			throws AutomacaoException {
		// TODO Auto-generated method stub
		
	}

}
