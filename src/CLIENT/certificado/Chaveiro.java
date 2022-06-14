package CLIENT.certificado;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Chaveiro {
	
	private ByteArrayInputStream arquivoChaveiro;
	private KeyStore chaveiro;
	private String senha;
	private String alias;
	
	private Chaveiro(){
		super();
	}
	
	public Chaveiro(ByteArrayInputStream arquivoChaveiro, String senha) 
	throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, IOException {
		this();
		this.senha = senha;
		init(arquivoChaveiro);
	}

	private void init(ByteArrayInputStream arquivoChaveiro)
	throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
		this.arquivoChaveiro = arquivoChaveiro;
		if(this.arquivoChaveiro == null ){
			throw new KeyStoreException("O chaveiro informado não existe.");
		}
		this.chaveiro = carregaChaveArquivo();
		validaChavePrivada();
		validaCadeiaCertificado();
		validaEmissor();
	}	

    private KeyStore carregaChaveArquivo() 
    throws KeyStoreException, NoSuchAlgorithmException, CertificateException {
    	try{
    		KeyStore keyStore = KeyStore.getInstance("PKCS12");
    		ByteArrayInputStream keyStoreStream = arquivoChaveiro;
    		char[] password = senha.toCharArray();
    		keyStore.load(keyStoreStream, password);
    		return keyStore;
    	}catch(IOException e){
    		throw new KeyStoreException("Ocorreu um erro ao abrir o arquivo de certificado.");
    	}
    }       
	
    public boolean validaChavePrivada() 
    throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
    	PrivateKey privateKey = getChavePrivada();
    	if (privateKey == null) {
    		throw new KeyStoreException("Não foi possí­vel encontrar a chave privada no arquivo/token/smartCard informado.");
    	}    
    	return true;
    }
    
    public boolean validaCadeiaCertificado() throws KeyStoreException{
		Certificate[] cadeiaCertificado = getCadeiaCertificado();
		if (cadeiaCertificado == null) {
			throw new KeyStoreException("Não foi possí­vel encontrar um certificado nem uma cadeia de certificação no arquivo/token/smartCard informado.");
		}
		return true;
    }
	
    public boolean validaEmissor() throws KeyStoreException {
		X509Certificate emissor = getCertificadoEmissor();
		if (emissor == null) {
			throw new KeyStoreException("Não foi possí­vel encontrar um certificado nem uma cadeia de certificação no arquivo/token/smartCard informado.");
		}
		return true;
    }
    
    public String getProvider(){
    	if(alias == null || alias.equals("")){
    		return "BC";
    	}else{
    		return "SunMSCAPI";
    	}
    }
    
    public CertStore getCertStore(String provider) throws InvalidAlgorithmParameterException, 
    NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException{    	
    	return CertStore.getInstance("Collection", new CollectionCertStoreParameters(mergeCadeiaCertificadoComEmissor()), "BC");
    }
    
    public ArrayList<Certificate> mergeCadeiaCertificadoComEmissor() throws KeyStoreException{
		Security.addProvider(new BouncyCastleProvider());
		ArrayList<Certificate> certList = new ArrayList<Certificate>();

		Certificate[] cadeiaCertificado = getCadeiaCertificado();
		X509Certificate emissor = getCertificadoEmissor();
		
		if (cadeiaCertificado != null && cadeiaCertificado.length != 0) {
			for(Certificate atual : cadeiaCertificado){
				certList.add(atual);
			}
		} else {
			certList.add(emissor);
		}
		return certList;
    }
    
	public Certificate[] getCadeiaCertificado() throws KeyStoreException {
		if(this.alias != null && !this.alias.isEmpty())
		{
			return chaveiro.getCertificateChain(this.alias);	
		}
		else
		{
			Enumeration<String> aliasesEnum = chaveiro.aliases();
			while (aliasesEnum.hasMoreElements()) {
				String alias = (String) aliasesEnum.nextElement();
				return chaveiro.getCertificateChain(alias);
			}
		}
		return null;
	}
	
	public X509Certificate getCertificadoEmissor() throws KeyStoreException{
		if(this.alias != null && !this.alias.isEmpty())
		{
			return (X509Certificate) chaveiro.getCertificate(this.alias);	
		}
		else
		{
			Enumeration<String> aliasesEnum = chaveiro.aliases();
			while (aliasesEnum.hasMoreElements()) {
				String alias = (String) aliasesEnum.nextElement();
				return (X509Certificate) chaveiro.getCertificate(alias);
			}
		}
		return null;
	}
	
	public PrivateKey getChavePrivada() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException{
		char[] password = senha.toCharArray();
		if(this.alias != null && !this.alias.isEmpty())
		{
			return (PrivateKey) chaveiro.getKey(alias, password);	
		}
		else
		{
			Enumeration<String> aliasesEnum = chaveiro.aliases();
			while (aliasesEnum.hasMoreElements()) {
				String alias = (String) aliasesEnum.nextElement();
				return (PrivateKey) chaveiro.getKey(alias,password);
			}
		}
		return null;
	}

	public String getAlias() {
		return alias;
	}
}