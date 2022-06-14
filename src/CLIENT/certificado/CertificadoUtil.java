package CLIENT.certificado;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import br.com.esec.j128.J128;
import br.com.esec.license.License;
import br.com.esec.util.libs.DownloadLibsException;

public class CertificadoUtil {

	private static final Log LOGGER = LogFactory.getLog(CertificadoUtil.class);

	static {
	    Security.addProvider(new BouncyCastleProvider());
	}
	
	public CertificadoUtil(){
		try {
			License.addLicense("Conselho Nacional de Justica", "AAAAvyNlLVNlYyBQcm9kdWN0IExpY2Vuc2UNCiNNb24gQXVnIDE3IDE0OjA1OjQzIEdNVC0wMzowMCAyMDA5DQp2ZXJzaW9uPTIuMS4wDQp1c2VyPUNvbnNlbGhvIE5hY2lvbmFsIGRlIEp1c3RpY2ENCmRhdGU9dW5saW1pdGVkDQpuYW1lPVNESy1KYXZhIEdlcmFkb3IgZGUgTGljZW5jYXMNCmxpYi4xPVNES0phdmENCmxpYi4wPVNES1dlYg0KAAABADsJARmCkSVpjxEzYOjhnh1RX1mQuDA1hDtwxHW6LA2RQM8t961nKjBYbA/mPYkzf7SHhb67FPFu0MxrW2s+YhWqNS7ymF9jRDSWqsYly9gxbiVsQH76OSrblknrhB+guSR1NR6p9eMAzCSHmQx8N20EmdIdzAHnrh54DCyWIab+wYep/v2zaXx9OoE5NB417WSFeVARcOwA0hjINDAmjkyYBwTbRLDJV0IfjLBbzmdkPGG7ftct3Gze0VijIisIUZ40EfTIZsibPnlhNvjt+98B36OBhRXNEo0ysjyUQAAiYY6gUiD4mKqrxehCUCUfmmbVR3XniMcEsxAInsQogMc=");
			Security.addProvider(new J128());			
		} catch (DownloadLibsException e) {			
			LOGGER.error(e);
		}
	}
	

	public boolean isAssinado(byte[] arquivo) throws CMSException {
		CMSSignedData cmsSignedData;

		cmsSignedData = new CMSSignedData(arquivo);
		List<SignerInformation> siList = (List<SignerInformation>) cmsSignedData.getSignerInfos().getSigners();
		for (SignerInformation si : siList) {
			if (si.getSignedAttributes() != null) {
				return true;
			}
		}
		return false;
	}


	public byte[] getConteudoOriginal(byte[] arquivo) throws CMSException {
		CMSSignedData cmsSignedData;
		cmsSignedData = new CMSSignedData(arquivo);

		CMSProcessableByteArray cmsProcessableByteArray = (CMSProcessableByteArray) cmsSignedData.getSignedContent();
		return (byte[]) cmsProcessableByteArray.getContent();
	}

	/**
	 * M�todo que cria uma identidade digital, a ser utilizada por um usu�rio
	 * 
	 * @param ksRoot
	 * @param senhaRaiz
	 * @param keySize
	 * @param serial
	 * @param id
	 * @param pontoDeDistribuicao
	 * @param from
	 * @param to
	 * @return
	 * @throws Exception
	 */
	public KeyStore criarIdentidadeDigital(KeyStore ksRoot, String senhaRaiz, int keySize, BigInteger serial,
			Identidade id, String pontoDeDistribuicao, Date from, Date to) throws Exception {
		String dn = "C=" + id.getPais() + ", ST=" + id.getEstado() + ", L=" + id.getCidade() + ", CN=" + id.getNome();
		if (StringUtils.isNotBlank(id.getEmail())) {
			dn = dn + ", E=" + id.getEmail();
		}

		KeyPair idKeys = CertTools.geraChaves(keySize);

		String alias = (String) ksRoot.aliases().nextElement();
		X509Certificate caCert = (X509Certificate) ksRoot.getCertificate(alias);
		PrivateKey caPrivKey = (PrivateKey) ksRoot.getKey(alias, senhaRaiz.toCharArray());
		Certificate[] oldChain = ksRoot.getCertificateChain(alias);

		GeradorCertificados caGen = new GeradorCertificados(caCert, caPrivKey);
		X509Certificate idCert = caGen.generateCertificate(dn, "E=" + id.getEmail(), idKeys.getPublic(), from, to,
				serial, "SHA1WithRSA", "J128", new UsuarioFinal(id, pontoDeDistribuicao));

		return new CertTools().criaP12(id.getNome(), idKeys.getPrivate(), idCert, oldChain, id.getSenha());
	}

	public byte[] assinar(byte[] arquivo, String certificado, String senha) throws Exception {
		byte[] arquivoAssinado = null;
		FileInputStream fileInputStream = null;
		ByteArrayInputStream byteArrayInputStream = null;

		fileInputStream = new java.io.FileInputStream(certificado);
		byte[] certificadoProjudi = IOUtils.toByteArray(fileInputStream);

		byteArrayInputStream = new ByteArrayInputStream(certificadoProjudi);
		Chaveiro chaveiro = new Chaveiro(byteArrayInputStream, senha);

		CMSProcessableByteArray cmsProcessableByteArray = new CMSProcessableByteArray(arquivo);
		CMSSignedDataGenerator gerador = new CMSSignedDataGenerator();
		CMSSignedData cmsSignedDataRetorno = null;
		gerador.addSigner(chaveiro.getChavePrivada(), chaveiro.getCertificadoEmissor(),
				CMSSignedDataGenerator.DIGEST_SHA1);
		gerador.addCertificatesAndCRLs(chaveiro.getCertStore("BC"));
		cmsSignedDataRetorno = gerador.generate(cmsProcessableByteArray, true, "BC");
		arquivoAssinado = cmsSignedDataRetorno.getEncoded();

		return arquivoAssinado;
	}
}
