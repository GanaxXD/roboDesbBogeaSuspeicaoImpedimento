package DAO;

import java.util.Iterator;
import java.util.List;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import MODEL.Documento;
import MODEL.Processo;

public class DAOMongoDB {

	private String host;
	private String database;
	private String collection;
	private int port;

	public DAOMongoDB(String host, String database, String collection, int port) {
		super();
		this.host = host;
		this.database = database;
		this.collection = collection;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void persistirProcessoMongoDB(Processo p) {

		try {

			DBObject doc = createDBObject(p);
			MongoClient mongo = new MongoClient(getHost(), getPort());
			DB db = mongo.getDB(getDatabase());
			DBCollection col = db.getCollection(getCollection());
System.out.println();
			try {

				WriteResult result = col.insert(doc);
				mongo.close();

			} catch (Exception e) {
				List<Documento> documentos = p.getDocumentos();
				for (Iterator iterator = documentos.iterator(); iterator.hasNext();) {
					Documento documento = (Documento) iterator.next();
					DBObject query = BasicDBObjectBuilder.start().add("_id", documento.getIdProcessoDocumento()).get();
					col.update(query, doc);
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Não foi possível persistir o documento no banco de dados");

		}

	}

	public void obterProcessoMongo(Processo p) {
		try {
			MongoClient mongo = new MongoClient(getHost(), getPort());
			DB db = mongo.getDB(getDatabase());
			DBCollection col = db.getCollection(getCollection());

			DBObject query = BasicDBObjectBuilder.start().add("nr_processo", p.getNumeroProcesso()).get();
			DBCursor cursor = col.find(query);
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Não foi possível obter o processo");
		}
	}

	private DBObject createDBObject(Processo p) {

		BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

		List<Documento> documentos = p.getDocumentos();
		for (Iterator iterator = documentos.iterator(); iterator.hasNext();) {
			Documento documento = (Documento) iterator.next();
			docBuilder.append("_id", documento.getIdProcessoDocumento());
			
			
			break;
		}

		docBuilder.append("nr_processo", p.getNumeroProcesso());
		docBuilder.append("jurisdicao", p.getJurisdicao());
		docBuilder.append("ds_orgao_julgador", p.getOrgaoJulgador());
		docBuilder.append("id_classe_judicial", p.getClasse());
		docBuilder.append("dt_autuacao", p.getDataAutuacao());
		
		//221;JULGADA PROCEDENTE EM PARTE A AÇÃO;44922557;28/09/16 16:19:28,127000;CONCLUSÃO;/2015/479/3220152501154/ARQUIVO42670191.P7S
		if(p.getJulgamento()!=null) {
			String[] julgamento = p.getJulgamento().split(";");
			if(julgamento.length==6) {
				docBuilder.append("codJulgamento", julgamento[0]);
				docBuilder.append("JULGAMENTO", julgamento[1]);
				docBuilder.append("codArquivoSentenca", julgamento[2]);
				docBuilder.append("dataSentenca", julgamento[3]);
				docBuilder.append("caminhoArquivoSentenca", julgamento[5]);
				docBuilder.append("linkDownloadSentenca", "https://projudi.tjba.jus.br/projudi/listagens/DownloadArquivo?arquivo=" + julgamento[2]);
			}
			
		}
		
		docBuilder.append("linkProcesso", p.getLinkProcesso());
		docBuilder.append("tema", p.getEtiqueta());
		docBuilder.append("CONTEUDO_TEXTO", p.getDocumentoAto());

		return docBuilder.get();
	}

}
