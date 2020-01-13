package peca;

import xadrez.XadrezPeca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.Color;

public class Rainha extends XadrezPeca {

	public Rainha(Tabuleiro board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "Q";
	}
	
	@Override
	public boolean[][] possivelMovimentos (){
		boolean[][] mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];
		
		Posicao p = new Posicao(0, 0);
		
		// above
		p.setValores(posicao.getLinha() - 1, posicao.getColuna());
		while (getTabuleiro().pecaExiste(p) && !getTabuleiro().pecaExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setLinha(p.getLinha() - 1);
		}
		if (getTabuleiro().pecaExiste(p) && pecaOponenteExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// left
		p.setValores(posicao.getLinha(), posicao.getColuna() - 1);
		while (getTabuleiro().pecaExiste(p) && !getTabuleiro().pecaExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setColuna(p.getColuna() - 1);
		}
		if (getTabuleiro().pecaExiste(p) && pecaOponenteExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// right
		p.setValores(posicao.getLinha(), posicao.getColuna() + 1);
		while (getTabuleiro().pecaExiste(p) && !getTabuleiro().pecaExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setColuna(p.getColuna() + 1);
		}
		if (getTabuleiro().pecaExiste(p) && pecaOponenteExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// below
		p.setValores(posicao.getLinha() + 1, posicao.getColuna());
		while (getTabuleiro().pecaExiste(p) && !getTabuleiro().pecaExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setLinha(p.getLinha() + 1);
		}
		if (getTabuleiro().pecaExiste(p) && pecaOponenteExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// nw
		p.setValores(posicao.getLinha() - 1, posicao.getColuna() - 1);
		while (getTabuleiro().pecaExiste(p) && !getTabuleiro().pecaExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setValores(p.getLinha() - 1, p.getColuna() - 1);
		}
		if (getTabuleiro().pecaExiste(p) && pecaOponenteExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// ne
		p.setValores(posicao.getLinha() - 1, posicao.getColuna() + 1);
		while (getTabuleiro().pecaExiste(p) && !getTabuleiro().pecaExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setValores(p.getLinha() - 1, p.getColuna() + 1);
		}
		if (getTabuleiro().pecaExiste(p) && pecaOponenteExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// se
		p.setValores(posicao.getLinha() + 1, posicao.getColuna() + 1);
		while (getTabuleiro().pecaExiste(p) && !getTabuleiro().pecaExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setValores(p.getLinha() + 1, p.getColuna() + 1);
		}
		if (getTabuleiro().pecaExiste(p) && pecaOponenteExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// sw
		p.setValores(posicao.getLinha() + 1, posicao.getColuna() - 1);
		while (getTabuleiro().pecaExiste(p) && !getTabuleiro().pecaExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
			p.setValores(p.getLinha() + 1, p.getColuna() - 1);
		}
		if (getTabuleiro().pecaExiste(p) && pecaOponenteExiste(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		return mat;
	}
}
