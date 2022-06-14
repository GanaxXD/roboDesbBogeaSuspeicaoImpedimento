package CLIENT.certificado;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import br.com.esec.asn1.ASN1ObjectIdentifier;
import br.com.esec.pkix.x509.X509CertificateImpl;
import br.com.esec.pkix.x509.X509Exception;
import br.com.esec.pkix.x509.X509Extensions;
import br.com.esec.pkix.x509.X509Principal;
import br.com.esec.pkix.x509.extns.X509AuthorityKeyIdentifierExtension;
import br.com.esec.pkix.x509.extns.X509BasicConstraintsExtension;
import br.com.esec.pkix.x509.extns.X509CRLDistributionPointsExtension;
import br.com.esec.pkix.x509.extns.X509CertificatePoliciesExtension;
import br.com.esec.pkix.x509.extns.X509ExtensionObject;
import br.com.esec.pkix.x509.extns.X509KeyUsageExtension;
import br.com.esec.pkix.x509.extns.X509SubjectKeyIdentifierExtension;
import br.com.esec.pkix.x509.imp.X509DistributionPoint;
import br.com.esec.pkix.x509.imp.X509GeneralName;
import br.com.esec.pkix.x509.imp.X509GeneralNameList;
import br.com.esec.pkix.x509.imp.X509PolicyInformation;
import br.com.esec.pkix.x509.imp.X509StringGeneralName;
import br.com.esec.pkix.x509.imp.asn1.PolicyInformation;
import br.com.esec.sdk.crl.CRLPointInfo;

public class CertTools {

	private static Logger log = Logger.getLogger(CertTools.class);

	public static final String EMAIL = "rfc822name";

	public static final String EMAIL1 = "email";

	public static final String EMAIL2 = "EmailAddress";

	public static final String EMAIL3 = "E";

	public static final String DNS = "dNSName";

	public static final String URI = "uniformResourceIdentifier";

	public static final String URI1 = "uri";

	public static final String IPADDR = "iPAddress";
	
	/**
	 * N�o deve ser instanciado
	 */
	public CertTools() {
	}

	/**
	 * Retorna o DN do dono de um certificado
	 * 
	 * @param cert
	 * @return a String DN
	 */
	public static String getSubjectDN(X509Certificate cert) {
		return getDN(cert, 1);
	}

	/**
	 * Retorna o DN do emissor de um certificado
	 * 
	 * @param cert
	 * @return a String DN
	 */
	public static String getIssuerDN(X509Certificate cert) {
		return getDN(cert, 2);
	}

	private static String getDN(X509Certificate cert, int which) {
		String dn = null;
		if (cert == null) {
			return dn;
		}
		if (which == 1) {
			dn = cert.getSubjectDN().toString();
		} else {
			dn = cert.getIssuerDN().toString();
		}
		return dn;
	}

	/**
	 * Retorna o DN do emissor de um LRC
	 * 
	 * @param crl
	 * @return a String DN
	 */
	public static String getIssuerDN(X509CRL crl) {
		return crl.getIssuerDN().toString();
	}

	public static CertificateFactory getCertificateFactory() {
		try {
			return CertificateFactory.getInstance("X.509", "J128");
		} catch (NoSuchProviderException nspe) {
			log.error("NoSuchProvider: ", nspe);
		} catch (CertificateException ce) {
			log.error("CertificateException: ", ce);
		}
		return null;
	}

	/**
	 * Checks if a certificate is self signed by verifying if subject and issuer
	 * are the same.
	 * 
	 * @param cert
	 *            the certificate that skall be checked.
	 * 
	 * @return boolean true if the certificate has the same issuer and subject,
	 *         false otherwise.
	 */
	public static boolean ehAutoAssinado(X509Certificate cert) {

		boolean ret = CertTools.getSubjectDN(cert).equals(CertTools.getIssuerDN(cert));

		return ret;
	} // isSelfSigned

	/**
	 * Cria um arquivo de guarda de chave privada e certificado padr�o PKCS#12
	 * 
	 * @param alias
	 *            o nome da chave (alias)
	 * @param privKey
	 *            a chave privada
	 * @param cert
	 *            o certificado a ser guardado
	 * @param cachain
	 *            o caminho de certifica��o
	 * @return um keystore com os valores fornecidos
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws X509Exception
	 */
	public KeyStore criaP12(String alias, PrivateKey privKey,
			X509Certificate cert, Certificate[] cachain, String password) throws IOException,
			KeyStoreException, CertificateException, NoSuchProviderException,
			NoSuchAlgorithmException, InvalidKeySpecException, X509Exception {
		// Certificate chain
		if (cert == null) {
			throw new IllegalArgumentException("Par�metro cert n�o pode ser null.");
		}
		int len = 1;
		if (cachain != null) {
			len += cachain.length;
		}
		Certificate[] chain = new Certificate[len];
		CertificateFactory cf = CertTools.getCertificateFactory();
		chain[0] = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
		if (cachain != null) {
			for (int i = 0; i < cachain.length; i++) {
				X509Certificate tmpcert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cachain[i].getEncoded()));
				chain[i + 1] = tmpcert;
			}
		}
		// store the key and the certificate chain
		Certificate[] userCert = new Certificate[1];
		userCert[0] = chain[0];
		KeyStore store = KeyStore.getInstance("PKCS12", "J128");
		store.load(null, null);
		store.setKeyEntry(alias, privKey, password.toCharArray(), userCert);
		if (chain.length > 1) {
			for (int i = 1; i < chain.length; i++) {
				X509CertificateImpl cacert = new X509CertificateImpl(chain[i]);
				// We constuct a friendly name for the CA, and try with some
				// parts from the DN if they exist.
				String cafriendly = cacert.getSubject().getFirst(X509Principal.COMMON_NAME);
				// On the ones below we +i to make it unique, O might not be
				// otherwise
				if (cafriendly == null){
					cafriendly = cacert.getSubject().getFirst(X509Principal.ORGANIZATION_NAME) + i;
				}
				
				if (cafriendly == null){
					cafriendly = cacert.getSubject().getFirst(X509Principal.ORGANIZATIONAL_UNIT_NAME) + i;
				}
				
				if (cafriendly == null){
					cafriendly = "CA_unknown" + i;
				}
				
				store.setCertificateEntry(cafriendly, chain[i]);
			}
		}

		return store;
	}

	/**
	 * Gera um par de chaves p�blica e privada
	 * 
	 * @param keysize
	 *            o tamanho da chave
	 * @return o par de chaves
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public static KeyPair geraChaves(int keysize)
			throws NoSuchAlgorithmException, NoSuchProviderException {

		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA", "J128");
		keygen.initialize(keysize);

		KeyPair rsaKeys = keygen.generateKeyPair();

		log.debug("Generated "
				+ rsaKeys.getPublic().getAlgorithm()
				+ " keys with length "
				+ ((RSAPrivateKey) rsaKeys.getPrivate()).getPrivateExponent().bitLength());

		return rsaKeys;
	}
	public static void installBCProvider() {
		if (Security.addProvider(new BouncyCastleProvider()) < 0) {
			// Se j� est� instalado, tentamos remover pra refazer a instala��o
			Security.removeProvider("BC");
			if (Security.addProvider(new BouncyCastleProvider()) < 0) {
				log.error("N�o foi poss�vel sequer instalar novamente!");
			}
		}
	}
	
	
	
	/**
	 * Gera um certificado raiz
	 * 
	 * @param dn
	 * @param serial
	 * @param pontoDeDistribuicao
	 * @param privKey
	 * @param pubKey
	 * @param firstDate
	 * @param lastDate
	 * @param isCA
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SecurityException
	 * @throws SignatureException
	 * @throws X509Exception 
	 */
	public static X509Certificate geraCertAutoAssinado(String dn,
			BigInteger serial, String pontoDeDistribuicao, PrivateKey privKey,
			PublicKey pubKey, Date firstDate, Date lastDate, boolean isCA)
			throws NoSuchAlgorithmException, InvalidKeyException,
			NoSuchProviderException, SecurityException, SignatureException, X509Exception {

		// Create self signed certificate
		String sigAlg = "SHA1WithRSA";

		X509CertificateImpl certgen = new X509CertificateImpl();
		X509Principal principal = new X509Principal(dn);
		certgen.setSerialNumber(serial);
		certgen.setNotBefore(firstDate);
		certgen.setNotAfter(lastDate);
		certgen.setSignatureAlgorithm(sigAlg);
		certgen.setSubject(principal);
		certgen.setIssuer(principal);
		certgen.setPublicKey(pubKey);
		X509BasicConstraintsExtension bc = new X509BasicConstraintsExtension(isCA, new Integer(0), true);
		X509Extensions extensions = new X509Extensions();
		extensions.addExtension(bc);

		// Put critical KeyUsage in CA-certificates
		if (isCA == true) {
			X509KeyUsageExtension ku = new X509KeyUsageExtension(true);
			ku.setKeyCertSign(true);
			ku.setCrlSign(true);
			ku.setDataEncipherment(true);
			extensions.addExtension(bc);
		}

		// Subject and Authority key identifier is always non-critical and MUST
		// be present for certificates to verify in Mozilla.
		if (isCA == true) {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] kIdS = md.digest(pubKey.getEncoded());
			X509ExtensionObject subjectKeyIdentifier = new X509SubjectKeyIdentifierExtension(kIdS, false);
			X509ExtensionObject authKeyIdentifier = new X509AuthorityKeyIdentifierExtension(kIdS, false);
			extensions.addExtension(subjectKeyIdentifier);
			extensions.addExtension(authKeyIdentifier);
		}
		// CertificatePolicies extension if supplied policy ID, always
		// non-critical
		X509CertificatePoliciesExtension policy = new X509CertificatePoliciesExtension(false);
		PolicyInformation pInfo = new PolicyInformation();
		pInfo.setPolicyIdentifier(new ASN1ObjectIdentifier("2.5.29.32.0"));
		X509PolicyInformation policyInfo = new X509PolicyInformation(pInfo);
		policy.addPolicy(policyInfo);
		extensions.addExtension(policy);

		StringTokenizer tokenizer = new StringTokenizer(pontoDeDistribuicao, ";", false);
		X509CRLDistributionPointsExtension crlDist = new X509CRLDistributionPointsExtension(false);
		int lenPoints = 0;
		while (tokenizer.hasMoreTokens()) {
			lenPoints++;
			// 6 is URI
			String uri = tokenizer.nextToken();

			X509GeneralNameList gNames = new X509GeneralNameList();
			gNames.add(new X509StringGeneralName(X509StringGeneralName.URI_NAME, uri));
			X509DistributionPoint point = new X509DistributionPoint();
			point.setDistributionPoint(gNames);
			crlDist.addDistributionPoint(point);
		}
		if (lenPoints > 0) {

			extensions.addExtension(crlDist);
		}
		certgen.setExtensions(extensions);
		
		certgen.sign(privKey);

		return certgen;
	}

	/**
	 * Recupera a authority key identifier de um dado certificado com a extens�o
	 * 
	 * @param cert
	 *            o certificado que cont�m a extens�o
	 * @return byte[] contendo a authority key identifier
	 * @throws IOException
	 * @throws X509Exception
	 */
	public static byte[] getAuthorityKeyId(X509Certificate x509)
			throws IOException, X509Exception {
		X509CertificateImpl cert = new X509CertificateImpl(x509);
		if (!cert.getExtensions().hasExtension(X509AuthorityKeyIdentifierExtension.OID)) {
			return null;
		}
		X509AuthorityKeyIdentifierExtension keyId = (X509AuthorityKeyIdentifierExtension) cert.getExtensions().getExtension(X509AuthorityKeyIdentifierExtension.OID);
		return keyId.getKeyId();
	}
	
	

	/**
	 * Recupera a subject key identifier de um dado certificado com a extens�o
	 * 
	 * @param cert
	 *            o certificado que cont�m a extens�o
	 * @return byte[] contendo a subject key identifier
	 * @throws IOException
	 * @throws X509Exception
	 */
	public static byte[] getSubjectKeyId(X509Certificate x509)
			throws IOException, X509Exception {
		X509CertificateImpl cert = new X509CertificateImpl(x509);
		if (!cert.getExtensions().hasExtension(X509SubjectKeyIdentifierExtension.OID)) {
			return null;
		}
		X509SubjectKeyIdentifierExtension keyId = (X509SubjectKeyIdentifierExtension) cert.getExtensions().getExtension(X509SubjectKeyIdentifierExtension.OID);
		return keyId.getKeyId();
	}

	/**
	 * Recupera a pol�tica de certifica��o de um dado certificado com a extens�o
	 * de pol�tica de certifica��o
	 * 
	 * @param cert
	 *            o certificado que cont�m a extens�o
	 * @param pos
	 *            a posi��o da pol�tica
	 * @return uma String com o OID da pol�tica
	 * @throws IOException
	 * @throws X509Exception
	 */
	public static String getCertificatePolicyId(X509Certificate x509, int pos)
			throws IOException, X509Exception {
		X509CertificateImpl cert = new X509CertificateImpl(x509);
		if (!cert.getExtensions().hasExtension(
				X509CertificatePoliciesExtension.OID)) {
			return null;
		}

		X509CertificatePoliciesExtension policies = (X509CertificatePoliciesExtension) cert.getExtensions().getExtension(X509CertificatePoliciesExtension.OID);
		X509PolicyInformation pInfo = policies.getPolicy(pos);
		return pInfo.getOid();
	}

	/**
	 * Pega o ponto de distribui��o de um certificado
	 * 
	 * @param certificate
	 * @return
	 * @throws CertificateParsingException
	 * @throws X509Exception
	 * @throws URISyntaxException
	 * @throws NoSuchAlgorithmException
	 */
	public static String getPontoDeDistribuicao(X509Certificate certificate)
			throws CertificateParsingException, X509Exception,
			NoSuchAlgorithmException, URISyntaxException {
		X509CertificateImpl cert = new X509CertificateImpl(certificate);
		if (!cert.getExtensions().hasExtension(X509CRLDistributionPointsExtension.OID)) {
			return null;
		}
		X509Extensions extns = cert.getExtensions();
		X509CRLDistributionPointsExtension cdp = (X509CRLDistributionPointsExtension) extns.getExtension(X509CRLDistributionPointsExtension.OID);
		X509DistributionPoint[] points = cdp.getDistributionPoints();
		for (int i = 0; i < points.length; i++) {
			X509GeneralNameList dpLocationList = points[i].getFullName();
			X509GeneralName[] dpLocationsArray = dpLocationList.getNames();

			CRLPointInfo crlPointInfo = new CRLPointInfo();
			crlPointInfo.setPointNames(dpLocationsArray, cert.getIssuer());
			
			return crlPointInfo.getUris()[0].toString();
		}
		return null;
	}
	
	public static String getEmailFromDN(String dn){
		try {
			X509Principal principal = new X509Principal(dn);
			return principal.getFirst(X509Principal.EMAIL_ADDRESS);
		} catch (X509Exception e) {
			return null;
		}
		
	}
	
}
