package CLIENT.certificado;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import br.com.esec.pkix.x509.icpbrasil.ICPBrasil_2_16_76_1_3_1_OtherName;
import br.com.esec.pkix.x509.icpbrasil.ICPBrasil_2_16_76_1_3_5_OtherName;
import br.com.esec.pkix.x509.icpbrasil.ICPBrasil_2_16_76_1_3_6_OtherName;

/**
 * 
 * Esta classe tem a fun??o de auxiliar na padroniza??o
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
		 * A ICP-Brasil tamb?m define como obrigat?ria a extens?o "Subject Alternative Name", n?o cr?tica e com os seguintes formatos:
		 * Para certificado de pessoa f?sica, 3 (tr?s)campos otherName, contendo:
		 */
		if(id.ehPessoaFisica()) {
			/*
			 * -OID =2.16.76.1.3.1 e conte?do =
			 * nas primeiras 8 (oito)posi??es,a data de nascimento do titular, no formato ddmmaaaa;
			 * nas 11 (onze) posi??es subseq?entes,o Cadastro de Pessoa F?sica (CPF) do titular;
			 * nas 11 (onze) posi??es subseq?entes, o N?mero de Identifica??o Social-NIS (PIS, PASEP ou CI);
			 * nas 11 (onze) posi??es subseq?entes, o n?mero do Registro Geral (RG) do titular;
			 * nas 6 (seis) posi??es subseq?entes, as siglas do ?rg?o expedidor do RG e respectiva UF.		 * 
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
			 * -OID =2.16.76.1.3.6 e conte?do =nas 12 (doze)posi??es o n?mero do Cadastro Espec?fico do INSS (CEI)da pessoa f?sica titular do certificado.
			 */
			ICPBrasil_2_16_76_1_3_6_OtherName pfExt2 = new ICPBrasil_2_16_76_1_3_6_OtherName();
			pfExt2.setCEI(id.getINSS());
			vec.add(pfExt2);
			
			/* 
			 * -OID =2.16.76.1.3.5 e conte?do = 
			 * nas primeiras 12 (doze) posi??es, o n?mero de inscri??o do T?tulo de Eleitor; 
			 * nas 3 (tr?s) posi??es subseq?entes, a Zona Eleitoral; 
			 * nas 4 (quatro) posi??es seguintes, a Se??o; 
			 * nas 22 (vinte e duas) posi??es subseq?entes, o munic?pio e a UF do T?tulo de Eleitor.				
			*/
			ICPBrasil_2_16_76_1_3_5_OtherName pfExt3 = new ICPBrasil_2_16_76_1_3_5_OtherName();
			vec.add(pfExt3);			
		} else {
			/* 
			 * 
			 * Para certificado de pessoa jur?dica,4 (quatro)campos otherName,contendo,nesta ordem:
			 * -OID =2.16.76.1.3.4 e conte?do = nas primeiras 8 (oito) posi??es, a data de nascimento do respons?vel pelo certificado, no formato ddmmaaaa; nas (onze) posi??es subseq?entes,o Cadastro de Pessoa F?sica (CPF) do respons?vel; nas 11 (onze) posi??es subseq?entes, o N?mero de Identifica??o Social-NIS (PIS,PASEP ou CI); nas (onze) posi??es subseq?entes, o n?mero do RG do respons?vel;nas 6 (seis) posi??es subseq?entes, as siglas do ?rg?o expedidor do RG e respectiva UF;
			 * -OID =2.16.76.1.3.2 e conte?do =nome do respons?vel pelo certificado;
			 * -OID =2.16.76.1.3.3 e conte?do = Cadastro Nacional de Pessoa Jur?dica (CNPJ) da pessoa jur?dica titular do certificado;
			 * -OID =2.16.76.1.3.7 e conte?do =nas 12 (doze)posi??es o n?mero do Cadastro Espec?fico do INSS (CEI)da pessoa jur?dica titular do certificado.
			 * 
			 * Os campos otherName definidos como obrigat?rios pela ICP-Brasil devem estar de acordo com as seguintes especifica??es:
			 * -O conjunto de informa??es definido em cada campo otherName deve ser armazenado como uma cadeia de caracteres do tipo ASN.1 OCTET STRING;
			 * -Quando os n?meros de CPF,NIS (PIS,PASEP ou CI),RG,CNPJ,CEI ou T?tulo de Eleitor n?o estiverem dispon?veis,os campos correspondentes devem ser integralmente preenchidos com caracteres "zero";
			 * -Se o n?mero do RG n?o estiver dispon?vel,n?o se deve preencher o campo de ?rg?o emissor e UF. O mesmo ocorre para o campo de munic?pio e UF,se n?o houver n?mero de inscri??o do T?tulo de Eleitor;
			 * -Todas informa??es de tamanho vari?vel referentes a n?meros,tais como RG,devem ser preenchidas com caracteres "zero"a sua esquerda para que seja completado seu m?ximo tamanho poss?vel;
			 * 
			 * -As 6 (seis)posi??es das informa??es sobre ?rg?o emissor do RG e UF referem-se ao tamanho m?ximo,devendo ser utilizadas apenas as posi??es necess?rias ao seu armazenamento,da esquerda para a direita.O mesmo se aplica ?s 22 (vinte e duas)posi??es das informa??es sobre munic?pio e UF do T?tulo de Eleitor.
			 * -Apenas os caracteres de A a Z e de 0 a 9 poder?o ser utilizados,n?o sendo permitidos caracteres especiais,s?mbolos,espa?os ou quaisquer outros. Campos otherName adicionais,contendo informa??es espec?ficas e forma de preenchimento e armazenamento definidas pela AC,poder?o ser utilizados com OID atribu?dos ou aprovados pela AC- Raiz. 
			 * 
			 */
				throw new RuntimeException("Certificado para pessoa jur?dica n?o suportado.");
		}
	
	}

}
