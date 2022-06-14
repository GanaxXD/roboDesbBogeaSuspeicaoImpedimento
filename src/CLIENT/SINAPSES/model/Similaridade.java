package CLIENT.SINAPSES.model;

public class Similaridade {

		private String cluster;
		private String id;
		private Object similaridade;
		private float x;
		private float y;
		private String conteudo;
		public String getCluster() {
			return cluster;
		}
		public void setCluster(String cluster) {
			this.cluster = cluster;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Object getSimilaridade() {
			return similaridade;
		}
		public void setSimilaridade(Object similaridade) {
			this.similaridade = similaridade;
		}
		public float getX() {
			return x;
		}
		public void setX(float x) {
			this.x = x;
		}
		public float getY() {
			return y;
		}
		public void setY(float y) {
			this.y = y;
		}
		public String getConteudo() {
			return conteudo;
		}
		public void setConteudo(String conteudo) {
			this.conteudo = conteudo;
		}
		@Override
		public String toString() {
			return "[cluster=" + cluster + ", id=" + id + "]";
		}
		
		
}
