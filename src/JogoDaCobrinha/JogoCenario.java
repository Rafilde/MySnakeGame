package JogoDaCobrinha;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JPanel;

public class JogoCenario extends CenarioPadrao {

	enum Estado {
		JOGANDO, GANHOU, PERDEU
	}

	private static final int _LARG = 25;

	private static final int RASTRO_INICIAL = 5;

	private int dx, dy;

	private boolean moveu;

	private int temporizador = 0;

	private int contadorRastro = RASTRO_INICIAL;
	
	private int cont = 0; //frutinhas que ele comeu até morrer ou ganhar
	
	private boolean flat = true; //operador lógico

	private Elemento fruta;

	private Elemento serpente;

	private Elemento[] nivel;

	private Elemento[] rastros;

	private Texto texto = new Texto(new Font("Arial", Font.PLAIN, 25));

	private Random rand = new Random();

	// Frutas para finalizar o level
	private int dificuldade = 10;

	private int contadorNivel = 0;

	private Estado estado = Estado.JOGANDO;

	public JogoCenario(int largura, int altura) {
		super(largura, altura);
	}

	@Override
	public void carregar() {

		// define direcao inicial
		dy = 1;
		rastros = new Elemento[dificuldade + RASTRO_INICIAL];

		fruta = new Elemento(0, 0, _LARG, _LARG);
		fruta.setCor(Color.RED);

		serpente = new Elemento(0, 0, _LARG, _LARG);
		serpente.setAtivo(true);
		serpente.setCor(Color.ORANGE); //serpente cabeça
		serpente.setVel(Jogo.velocidade);

		for (int i = 0; i < rastros.length; i++) {
			rastros[i] = new Elemento(serpente.getPx(), serpente.getPy(), _LARG, _LARG);
			rastros[i].setCor(Color.BLUE); //corpo serpente
			rastros[i].setAtivo(true);
		}
		//gera mundo
		char[][] nivelSelecionado = Nivel.niveis[Jogo.nivel];
		nivel = new Elemento[nivelSelecionado.length * 2];

		for (int linha = 0; linha < nivelSelecionado.length; linha++) {
			for (int coluna = 1; coluna < nivelSelecionado[0].length; coluna++) {
				if (nivelSelecionado[linha][coluna] != ' ') {

					Elemento e = new Elemento();
					e.setAtivo(true);
					e.setCor(new Color(45, 180, 0)); //cor das barras

					e.setPx(_LARG * coluna);
					e.setPy(_LARG * linha);

					e.setAltura(_LARG);
					e.setLargura(_LARG);

					nivel[contadorNivel++] = e;
				}
			}
		}
	}

	@Override
	public void descarregar() {
		fruta = null;
		rastros = null;
		serpente = null;
	}

	@Override
	public void atualizar() {

		if (estado != Estado.JOGANDO) {
			return;
		}

		if (!moveu) {
			if (dy != 0) {
				if (Jogo.controleTecla[Jogo.Tecla.ESQUERDA.ordinal()]) {
					dx = -1;

				} else if (Jogo.controleTecla[Jogo.Tecla.DIREITA.ordinal()]) {
					dx = 1;
				}

				if (dx != 0) {
					dy = 0;
					moveu = true;
				}

			} else if (dx != 0) {
				if (Jogo.controleTecla[Jogo.Tecla.CIMA.ordinal()]) {
					dy = -1;
				} else if (Jogo.controleTecla[Jogo.Tecla.BAIXO.ordinal()]) {
					dy = 1;
				}

				if (dy != 0) {
					dx = 0;
					moveu = true;
				}
			}
		}

		if (temporizador >= 20) {
			temporizador = 0;
			moveu = false;

			int x = serpente.getPx();
			int y = serpente.getPy();

			serpente.setPx(serpente.getPx() + _LARG * dx);
			serpente.setPy(serpente.getPy() + _LARG * dy);

			if (Util.saiu(serpente, largura, altura)) {
				serpente.setAtivo(false);
				estado = Estado.PERDEU;

			} else {

				// colisao com cenario
				for (int i = 0; i < contadorNivel; i++) {
					if (Util.colide(serpente, nivel[i])) {
						serpente.setAtivo(false);
						estado = Estado.PERDEU;
						break;
					}
				}

				// colisao com o rastro
				for (int i = 0; i < contadorRastro; i++) {
					if (Util.colide(serpente, rastros[i])) {
						serpente.setAtivo(false);
						estado = Estado.PERDEU;
						break;
					}
				}
			}

			if (Util.colide(fruta, serpente)) {
				// Adiciona uma pausa
				temporizador = -10;
				contadorRastro++; //Aqui ele vai aumentando o tamanho da cobrinha (deve ser o contador de pontos)
				cont++; //frutinhas que ele tá comendo
				fruta.setAtivo(false);

				if (contadorRastro == rastros.length) { //se a cobra conseguir o tamanho exatdo ela ganha
					//serpente.setAtivo(false); //apagar 
					//estado = Estado.GANHOU; //apagar
					
					Jogo.nivel++; // Incrementa o número do nível

				    if (Jogo.nivel < Nivel.niveis.length) {	
				    	nivel = null;

				    	char[][] nivelSelecionado = Nivel.niveis[Jogo.nivel];
				    	nivel = new Elemento[nivelSelecionado.length * 2];

				    	contadorNivel = 0;

				    	for (int linha = 0; linha < nivelSelecionado.length; linha++) {
				    		for (int coluna = 1; coluna < nivelSelecionado[0].length; coluna++) {
				    			if (nivelSelecionado[linha][coluna] != ' ') {

				    				Elemento e = new Elemento();
				    				e.setAtivo(true);
				    				e.setCor(new Color(45, 180, 0));

				    				e.setPx(_LARG * coluna);
				    				e.setPy(_LARG * linha);

				    				e.setAltura(_LARG);
				    				e.setLargura(_LARG);

				    				nivel[contadorNivel++] = e;
				    			}
				    		}
				    	}
				    	contadorRastro = RASTRO_INICIAL;
				    	serpente.setAtivo(true);
				    	fruta.setAtivo(false);
				    	serpente.posicao(0, 0);
				    } else {
				        estado = Estado.GANHOU;
				    }
				}

			}

			for (int i = 0; i < contadorRastro; i++) {
				Elemento rastro = rastros[i];
				int tx = rastro.getPx();
				int ty = rastro.getPy();

				rastro.setPx(x);
				rastro.setPy(y);

				x = tx;
				y = ty;
			}

		} else {
			temporizador += serpente.getVel();
		}

		// Adicionando frutas
		if (estado == Estado.JOGANDO && !fruta.isAtivo()) {
			int x = rand.nextInt(largura / _LARG);
			int y = rand.nextInt(altura / _LARG);

			fruta.setPx(x * _LARG);
			fruta.setPy(y * _LARG);
			fruta.setAtivo(true);

			// colisao com a serpente
			if (Util.colide(fruta, serpente)) {
				fruta.setAtivo(false);
				return;
			}

			// colisao com rastro
			for (int i = 0; i < contadorRastro; i++) {
				if (Util.colide(fruta, rastros[i])) {
					fruta.setAtivo(false);
					return;
				}
			}

			// colisao com cenario
			for (int i = 0; i < contadorNivel; i++) {
				if (Util.colide(fruta, nivel[i])) {
					fruta.setAtivo(false);
					return;
				}
			}

		}

	}

	@Override
	public void desenhar(Graphics2D g) {
		//mudar cor de fundo dos níveis
		g.setColor(Color.GREEN); 
		g.fillRect(0, 0, largura, altura); 
		//--------------------------------

		if (fruta.isAtivo()) {
			fruta.desenha(g);
		}

		for (Elemento e : nivel) {
			if (e == null)
				break;

			e.desenha(g);
		}

		for (int i = 0; i < contadorRastro; i++) {
			rastros[i].desenha(g);
		}

		serpente.desenha(g);

		texto.desenha(g, String.valueOf(rastros.length - contadorRastro), largura - 35, altura);

		if (estado != Estado.JOGANDO) {

			if (estado == Estado.GANHOU) {
				if (flat) {
					addFrutinhasComidas(cont);
					flat = false;
				}
				Jogo.changeToRusso();
			} else {
				
				if (flat) {
					addFrutinhasComidas(cont);
					addRanking(getFrutinhasComidas());
					flat = false;
				}
				texto.desenha(g, "Vixe!", 180, 180);
			}
		}

		if (Jogo.pausado)
			Jogo.textoPausa.desenha(g, "PAUSA", largura / 2 - Jogo.textoPausa.getFonte().getSize(), altura / 2);
	}
}
