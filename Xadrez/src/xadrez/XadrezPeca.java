package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;

public abstract class XadrezPeca extends Peca {

	private Color color;
	private int contagem;

	public XadrezPeca(Tabuleiro board, Color color) {
		super(board);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
	
	public int getContagem() {
		return contagem;
	}
	
	public void incrementaContagem() {
		contagem++;
	}

	public void decrementaContagem() {
		contagem--;
	}

	public XadrezPosicao getXadrezPosicao() {
		return XadrezPosicao.fromPosicao(posicao);
	}
	
	protected boolean pecaOponenteExiste(Posicao posicao) {
		XadrezPeca p = (XadrezPeca)getTabuleiro().peca(posicao);
		return p != null && p.getColor() != color;
	}
}