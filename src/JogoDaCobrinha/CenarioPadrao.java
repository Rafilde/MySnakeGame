package JogoDaCobrinha;

import java.awt.Graphics2D;

public abstract class CenarioPadrao {
	private static RankingInterface rankinginterface = new RankingInterface();
	private static int frutinhasComidas;
	protected int altura, largura;

	public CenarioPadrao(int largura, int altura) {
		this.altura = altura;
		this.largura = largura;
	}

	public abstract void carregar();

	public abstract void descarregar();

	public abstract void atualizar();

	public abstract void desenhar(Graphics2D g);
	
	public int getFrutinhasComidas() {
		return frutinhasComidas;
	}

	public void setFrutinhasComidas(int frutinhasComidas) {
		this.frutinhasComidas = frutinhasComidas;
	}

	public void addFrutinhasComidas(int frutinhasComidas) {
		this.frutinhasComidas += frutinhasComidas;
	}
	
	public static void addRanking(int pontuacao) {
		rankinginterface.adicionarJogador(pontuacao);
		rankinginterface.setVisible(true);
	}
}