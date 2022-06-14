package CLIENT.certificado;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import br.com.esec.pkix.x509.icpbrasil.ICPBrasil_2_16_76_1_3_1_OtherName;
import br.com.esec.pkix.x509.icpbrasil.ICPBrasil_2_16_76_1_3_5_OtherName;
import br.com.esec.pkix.x509.icpbrasil.ICPBrasil_2_16_76_1_3_6_OtherName;

/**
 * 
 * Esta classe tem a função de auxiliar na padronização
 * dos certificados utilizaos, utilizando regras definidas pela ICP-Brasil
 *
 */
public class UsuarioFinal extends CertificadoConfig {

	Identidade id;

	public UsuarioFinal(Identidade id, String pontoDistribuicao) {
		
		this.id = id;

		setValidity(730);

		setUseBasicConstraints(true);
		setBasicConstraintsCritical(true);

		setUseSubjectKeyIdentifier(true);
		setSubjectKeyIdentifierCritical(false);

		setUseAuthorityKeyIdentifier(true);
		setAuthorityKeyIdentifierCritical(false);

		setUseSubjectAlternativeName(true);
		setSubjectAlternativeNameCritical(false);

		setUseCRLDistributionPoint(true);
		setCRLDistributionPointCritical(false);
		setCRLDistributionPointURI(pontoDistribuicao);

		setUseCertificatePolicies(false);
		setCertificatePoliciesCritical(false);
		setCertificatePolicyId("2.5.29.32.0");

		setType(TYPE_ENDENTITY);

		int[] bitlengths = { 512, 1024, 2048, 4096 };
		setAvailableBitLengths(bitlengths);

		setUseKeyUsage(true);
		setKeyUsage(new boolean[9]);
		setKeyUsage(DIGITALSIGNATURE, true);
		setKeyUsage(KEYENCIPHERMENT, true);
		setKeyUsage(NONREPUDIATION,true);
		setKeyUsageCritical(true);

		setUseExtendedKeyUsage(false);
		ArrayList eku = new ArrayList();
		eku.add(new Integer(SERVERAUTH));
		eku.add(new Integer(CLIENTAUTH));
		eku.add(new Integer(EMAILPROTECTION));
		eku.add(new Integer(IPSECENDSYSTEM));
		eku.add(new Integer(IPSECUSER));
		setExtendedKeyUsage(eku);
		setExtendedKeyUsageCritical(false);


	}
	
	public void nomeAlternativoICPBrasil(ArrayList vec) {
		/*
		 * A ICP-Brasil também define como obrigatória a extensão "Subject Alternative Name", não crítica e com os seguintes formatos:
		 * Para certificado de pessoa física, 3 (três)campos otherName, contendo:
		 */
		if(id.ehPessoaFisica()) {
			/*
			 * -OID =2.16.76.1.3.1 e conteúdo =
			 * nas primeiras 8 (oito)posições,a data de nascimento do titular, no formato ddmmaaaa;
			 * nas 11 (onze) posições subseqüentes,o Cadastro de Pessoa Física (CPF) do titular;
			 * nas 11 (onze) posições subseqüentes, o Número de Identificação Social-NIS (PIS, PASEP ou CI);
			 * nas 11 (onze) posições subseqüentes, o número do Registro Geral (RG) do titular;
			 * nas 6 (seis) posições subseqüentes, as siglas do órgão expedidor do RG e respectiva UF.		 * 
			  */
	
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyy");
			ICPBrasil_2_16_76_1_3_1_OtherName pfExt1 = new ICPBrasil_2_16_76_1_3_1_OtherName();
			try {
				pfExt1.setBirthDate(sdf.parse(id.getDataNascimento()));
			} catch (ParseException e) {
			}
			pfExt1.setCPF(id.getCPF());
			pfExt1.setPisPasep(id.getNIS());
			pfExt1.setRG(id.getRGOrgaoExpedidorUF());
			pfExt1.setOrgaoEmissor(id.getRGOrgaoExpedidorUF());
			vec.add(pfExt1);
			/*
			 * -OID =2.16.76.1.3.6 e conteúdo =nas 12 (doze)posições o número do Cadastro Específico do INSS (CEI)da pessoa física titular do certificado.
			 */
			ICPBrasil_2_16_76_1_3_6_OtherName pfExt2 = new ICPBrasil_2_16_76_1_3_6_OtherName();
			pfExt2.setCEI(id.getINSS());
			vec.add(pfExt2);
			
			/* 
			 * -OID =2.16.76.1.3.5 e conteúdo = 
			 * nas primeiras 12 (doze) posições, o número de inscrição do Título de Eleitor; 
			 * nas 3 (três) posições subseqüentes, a Zona Eleitoral; 
			 * nas 4 (quatro) posições seguintes, a Seção; 
			 * nas 22 (vinte e duas) posições subseqüentes, o município e a UF do Título de Eleitor.				
			*/
			ICPBrasil_2_16_76_1_3_5_OtherName pfExt3 = new ICPBrasil_2_16_76_1_3_5_OtherName();
			vec.add(pfExt3);			
		} else {
			/* 
			 * 
			 * Para certificado de pessoa jurídica,4 (quatro)campos otherName,contendo,nesta ordem:
			 * -OID =2.16.76.1.3.4 e conteúdo = nas primeiras 8 (oito) posições, a data de nascimento do responsável pelo certificado, no formato ddmmaaaa; nas (onze) posições subseqüentes,o Cadastro de Pessoa Física (CPF) do responsável; nas 11 (onze) posições subseqüentes, o Número de Identificação Social-NIS (PIS,PASEP ou CI); nas (onze) posições subseqüentes, o número do RG do responsável;nas 6 (seis) posições subseqüentes, as siglas do órgão expedidor do RG e respectiva UF;
			 * -OID =2.16.76.1.3.2 e conteúdo =nome do responsável pelo certificado;
			 * -OID =2.16.76.1.3.3 e conteúdo = Cadastro Nacional de Pessoa Jurídica (CNPJ) da pessoa jurídica titular do certificado;
			 * -OID =2.16.76.1.3.7 e conteúdo =nas 12 (doze)posições o número do Cadastro Específico do INSS (CEI)da pessoa jurídica titular do certificado.
			 * 
			 * Os campos otherName definidos como obrigatórios pela ICP-Brasil devem estar de acordo com as seguintes especificações:
			 * -O conjunto de informações definido em cada campo otherName deve ser armazenado como uma cadeia de caracteres do tipo ASN.1 OCTET STRING;
			 * -Quando os números de CPF,NIS (PIS,PASEP ou CI),RG,CNPJ,CEI ou Título de Eleitor não estiverem disponíveis,os campos correspondentes devem ser integralmente preenchidos com caracteres "zero";
			 * -Se o número do RG não estiver disponível,não se deve preencher o campo de órgão emissor e UF. O mesmo ocorre para o campo de município e UF,se não houver número de inscrição do Título de Eleitor;
			 * -Todas informações de tamanho variável referentes a números,tais como RG,devem ser preenchidas com caracteres "zero"a sua esquerda para que seja completado seu máximo tamanho possível;
			 * 
			 * -As 6 (seis)posições das informações sobre órgão emissor do RG e UF referem-se ao tamanho máximo,devendo ser utilizadas apenas as posições necessárias ao seu armazenamento,da esquerda para a direita.O mesmo se aplica às 22 (vinte e duas)posições das informações sobre município e UF do Título de Eleitor.
			 * -Apenas os caracteres de A a Z e de 0 a 9 poderão ser utilizados,não sendo permitidos caracteres especiais,símbolos,espaços ou quaisquer outros. Campos otherName adicionais,contendo informações específicas e forma de preenchimento e armazenamento definidas pela AC,poderão ser utilizados com OID atribuídos ou aprovados pela AC- Raiz. 
			 * 
			 */
				throw new RuntimeException("Certificado para pessoa jurídica não suportado.");
		}
	
	}

}
