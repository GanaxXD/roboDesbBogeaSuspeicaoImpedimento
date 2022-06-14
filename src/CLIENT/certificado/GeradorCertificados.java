package CLIENT.certificado;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import br.com.esec.asn1.ASN1ObjectIdentifier;
import br.com.esec.pkix.x509.X509CertificateImpl;
import br.com.esec.pkix.x509.X509Exception;
import br.com.esec.pkix.x509.X509Extensions;
import br.com.esec.pkix.x509.X509Principal;
import br.com.esec.pkix.x509.extns.X509AuthorityInfoAccessSyntaxExtension;
import br.com.esec.pkix.x509.extns.X509AuthorityKeyIdentifierExtension;
import br.com.esec.pkix.x509.extns.X509BasicConstraintsExtension;
import br.com.esec.pkix.x509.extns.X509CRLDistributionPointsExtension;
import br.com.esec.pkix.x509.extns.X509CertificatePoliciesExtension;
import br.com.esec.pkix.x509.extns.X509ExtendedKeyUsageExtension;
import br.com.esec.pkix.x509.extns.X509ExtensionObject;
import br.com.esec.pkix.x509.extns.X509KeyUsageExtension;
import br.com.esec.pkix.x509.extns.X509SubjectAltNameExtension;
import br.com.esec.pkix.x509.extns.X509SubjectKeyIdentifierExtension;
import br.com.esec.pkix.x509.imp.X509DistributionPoint;
import br.com.esec.pkix.x509.imp.X509GeneralName;
import br.com.esec.pkix.x509.imp.X509GeneralNameList;
import br.com.esec.pkix.x509.imp.X509PolicyInformation;
import br.com.esec.pkix.x509.imp.X509RFC822Name;
import br.com.esec.pkix.x509.imp.X509StringGeneralName;
import br.com.esec.pkix.x509.imp.X509URIName;
import br.com.esec.pkix.x509.imp.asn1.PolicyInformation;

/**
 * 
 * Esta classe tem a função de auxiliar na
 * criação/geração prorpiamente dita dos arquivos de certificado
 * 
 */
public class GeradorCertificados implements Serializable {

	X509CertificateImpl esteCert;

	PrivateKey keyPrivate;

	boolean useAuthorityKeyIdentifier;

	boolean authorityKeyIdentifierCritical;

	/**
	 * Método construtor
	 * 
	 * @param ca
	 * @param privateKey
	 * @param useAuthorityKeyIdentifier
	 * @param authorityKeyIdentifierCritical
	 */
	public GeradorCertificados(X509Certificate ca, PrivateKey privateKey,
			boolean useAuthorityKeyIdentifier,
			boolean authorityKeyIdentifierCritical) {
		try {
			esteCert = new X509CertificateImpl(ca);
		} catch (X509Exception e) {
		}
		this.keyPrivate = privateKey;
		this.useAuthorityKeyIdentifier = useAuthorityKeyIdentifier;
		this.authorityKeyIdentifierCritical = authorityKeyIdentifierCritical;
	}

	public GeradorCertificados(X509Certificate ca, PrivateKey privateKey) {
		this(ca, privateKey, true, false);
	}

	PublicKey getPublicKey() {
		return esteCert.getPublicKey();
	}

	PrivateKey getPrivateKey() {
		return keyPrivate;
	}

	String getSubjectDN() {
		return esteCert.getSubjectDN().getName();
	}

	/**
	 * Método que gera o certificado
	 * 
	 * @param subjectDN
	 * @param subjectAltName
	 * @param publicKey
	 * @param firstDate
	 * @param lastDate
	 * @param serial
	 * @param algoritmo
	 * @param provider
	 * @param certProfile
	 * @return
	 * @throws Exception
	 */
	public X509Certificate generateCertificate(String subjectDN,
			String subjectAltName, PublicKey publicKey, Date firstDate,
			Date lastDate, BigInteger serial, String algoritmo,
			String provider, CertificadoConfig certProfile) throws Exception {

		final String sigAlg = algoritmo;
		X509CertificateImpl certgen = new X509CertificateImpl();
		X509Principal subjectPrincipal = new X509Principal(subjectDN);
		certgen.setSerialNumber(serial);
		certgen.setNotBefore(firstDate);
		certgen.setNotAfter(lastDate);
		certgen.setSignatureAlgorithm(sigAlg);
		certgen.setSubject(subjectPrincipal);
		certgen.setPublicKey(publicKey);
		X509Principal caname = this.esteCert.getSubject();
		certgen.setIssuer(caname);

		// Basic constranits, all subcerts are NOT CAs
		X509Extensions extensions = new X509Extensions();
		certgen.setExtensions(extensions);

		if (certProfile.getUseBasicConstraints() == true) {
			boolean isCA = false;
			if ((certProfile.getType() == CertificadoConfig.TYPE_SUBCA) || (certProfile.getType() == CertificadoConfig.TYPE_ROOTCA))
				isCA = true;
			X509BasicConstraintsExtension bc = new X509BasicConstraintsExtension(isCA, new Integer(0), certProfile.getBasicConstraintsCritical());
			extensions.addExtension(bc);
		}
		// Key usage
		if ((certProfile.getUseKeyUsage() == true)) {
			X509KeyUsageExtension newKeyUsage = sunKeyUsageToBC(certProfile.getKeyUsage());
			extensions.addExtension(newKeyUsage);
		}
		// Extended Key usage
		if (certProfile.getUseExtendedKeyUsage() == true) {
			X509ExtendedKeyUsageExtension extendedKu = new X509ExtendedKeyUsageExtension(false);
			// Get extended key usage from certificate profile
			Collection c = certProfile.getExtendedKeyUsageAsOIDStrings();
			Iterator iter = c.iterator();
			boolean hasElement = false;
			while (iter.hasNext()) {
				extendedKu.addUsage((String) iter.next());
				hasElement = true;
			}
			if (hasElement) {
				extensions.addExtension(extendedKu);
			}
		}
		// Subject key identifier
		if (certProfile.getUseSubjectKeyIdentifier() == true) {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] kIdS = md.digest(publicKey.getEncoded());
			X509ExtensionObject subjectKeyIdentifier = new X509SubjectKeyIdentifierExtension(kIdS, false);
			extensions.addExtension(subjectKeyIdentifier);
		}
		// Authority key identifier
		if (certProfile.getUseAuthorityKeyIdentifier() == true) {
			X509Extensions extns = esteCert.getExtensions();
			if(extns.hasExtension(X509SubjectKeyIdentifierExtension.OID)){
				X509SubjectKeyIdentifierExtension sub = (X509SubjectKeyIdentifierExtension)extns.getExtension(X509SubjectKeyIdentifierExtension.OID);				
				X509ExtensionObject authKeyIdentifier = new X509AuthorityKeyIdentifierExtension(sub.getKeyId(), false);
				extensions.addExtension(authKeyIdentifier);
			}
			
		}
		// Subject Alternative name
		if ((certProfile.getUseSubjectAlternativeName() == true)) {

			boolean hasAltName = false;
			X509SubjectAltNameExtension subAltName = new X509SubjectAltNameExtension(false);

			if ((subjectAltName != null) && (subjectAltName.length() > 0)) {
				String email = CertTools.getEmailFromDN(subjectAltName);
				if (email != null && email != "") {
					X509RFC822Name rfc = new X509RFC822Name(email);
					subAltName.getNames().add(rfc);
					hasAltName = true;
				}
			}

			// ICP Brasil
			// ------------------------------------------------------------------------
			ArrayList vec = new ArrayList();
			if (certProfile instanceof UsuarioFinal) {
				((UsuarioFinal) certProfile).nomeAlternativoICPBrasil(vec);
			}

			for (int i = 0; i < vec.size(); i++) {
				hasAltName = true;
				subAltName.getNames().add((X509GeneralName) vec.get(i));
			}
			if (hasAltName) {
				extensions.addExtension(subAltName);
			}
		}

		// Certificate Policies
		if (certProfile.getUseCertificatePolicies() == true) {
			X509CertificatePoliciesExtension policy = new X509CertificatePoliciesExtension(certProfile.getCertificatePoliciesCritical());
			PolicyInformation pInfo = new PolicyInformation();
			pInfo.setPolicyIdentifier(new ASN1ObjectIdentifier(certProfile.getCertificatePolicyId()));
			X509PolicyInformation policyInfo = new X509PolicyInformation(pInfo);
			policy.addPolicy(policyInfo);
			extensions.addExtension(policy);
		}

		// CRL Distribution point URI
		if (certProfile.getUseCRLDistributionPoint() == true) {

			// Multiple CDPs are spearated with the ';' sign
			StringTokenizer tokenizer = new StringTokenizer(certProfile.getCRLDistributionPointURI(), ";", false);
			X509CRLDistributionPointsExtension crlDist = new X509CRLDistributionPointsExtension(certProfile.getCRLDistributionPointCritical());
			int lenPoints = 0;
			while (tokenizer.hasMoreTokens()) {
				lenPoints++;
				// 6 is URI
				String uri = removeAspas(tokenizer.nextToken());

				X509GeneralNameList gNames = new X509GeneralNameList();
				gNames.add(new X509StringGeneralName(X509StringGeneralName.URI_NAME, uri));
				X509DistributionPoint point = new X509DistributionPoint();
				point.setDistributionPoint(gNames);
				crlDist.addDistributionPoint(point);
			}
			if (lenPoints > 0) {
				extensions.addExtension(crlDist);
			}
		}
		// Authority Information Access (OCSP url)
		if (certProfile.getUseOCSPServiceLocator() == true) {
			String ocspUrl = certProfile.getOCSPServiceLocatorURI();
			X509AuthorityInfoAccessSyntaxExtension authInfoExt = new X509AuthorityInfoAccessSyntaxExtension(false);
			X509URIName gName = new X509URIName(ocspUrl);
			authInfoExt.addLocation(X509AuthorityInfoAccessSyntaxExtension.OCSP_OID, gName);
			extensions.addExtension(authInfoExt);
		}

		certgen.sign(getPrivateKey(), provider);

		// Verify before returning
		certgen.verify(getPublicKey());

		return certgen;
	}

	private X509KeyUsageExtension sunKeyUsageToBC(boolean[] sku) {
		X509KeyUsageExtension ku = new X509KeyUsageExtension(true);
		if (sku[0] == true)
			ku.setDigitalSignature(true);
		if (sku[1] == true)
			ku.setNonRepudiation(true);
		if (sku[2] == true)
			ku.setKeyEncipherment(true);
		if (sku[3] == true)
			ku.setDataEncipherment(true);
		if (sku[4] == true)
			ku.setKeyAgreement(true);
		if (sku[5] == true)
			ku.setKeyCertSign(true);
		if (sku[6] == true)
			ku.setCrlSign(true);
		if (sku[7] == true)
			ku.setEncipherOnly(true);
		if (sku[8] == true)
			ku.setDecipherOnly(true);
		return ku;
	}

	public boolean getAuthorityKeyIdentifierCritical() {
		return authorityKeyIdentifierCritical;
	}
	
	private static String removeAspas(String s){
		if(s.charAt(0) == '"'){
			s = s.substring(1);
		}
		if(s.charAt(s.length()-1) == '"'){
			s = s.substring(0, s.length()-1);
		}
		return s;
	}
}

