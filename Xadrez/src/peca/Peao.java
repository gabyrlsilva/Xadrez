package peca;

import xadrez.XadrezPartida;
import xadrez.XadrezPeca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.Color;

public class Peao extends XadrezPeca {

	private XadrezPartida chessMatch;

	public Peao(Tabuleiro board, Color color, XadrezPartida chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public boolean[][] possivelMovimentos() {
		boolean[][] mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];

		Posicao p = new Posicao(0, 0);

		if (getColor() == Color.WHITE) {
			p.setValores(posicao.getLinha() - 1, posicao.getColuna());
			if (getTabuleiro().posicaoExiste(p) && !getTabuleiro().pecaExiste(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() - 2, posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() - 1, posicao.getColuna());
			if (getTabuleiro().posicaoExiste(p) && !getTabuleiro().pecaExiste(p) && getTabuleiro().posicaoExiste(p2)
					&& !getTabuleiro().pecaExiste(p2) && getContagem() == 0) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() - 1, posicao.getColuna() - 1);
			if (getTabuleiro().posicaoExiste(p) && pecaOponenteExiste(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() - 1, posicao.getColuna() + 1);
			if (getTabuleiro().posicaoExiste(p) && pecaOponenteExiste(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}

			// #specialmove en passant white
			if (posicao.getLinha() == 3) {
				Posicao left = new Posicao(posicao.getLinha(), posicao.getColuna() - 1);
				if (getTabuleiro().posicaoExiste(left) && pecaOponenteExiste(left)
						&& getTabuleiro().peca(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getLinha() - 1][left.getColuna()] = true;
				}
				Posicao right = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				if (getTabuleiro().posicaoExiste(right) && pecaOponenteExiste(right)
						&& getTabuleiro().peca(right) == chessMatch.getEnPassantVulnerable()) {
					mat[right.getLinha() - 1][right.getColuna()] = true;
				}
			}
		} else {
			p.setValores(posicao.getLinha() + 1, posicao.getColuna());
			if (getTabuleiro().posicaoExiste(p) && !getTabuleiro().pecaExiste(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() + 2, posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() + 1, posicao.getColuna());
			if (getTabuleiro().posicaoExiste(p) && !getTabuleiro().pecaExiste(p) && getTabuleiro().posicaoExiste(p2)
					&& !getTabuleiro().pecaExiste(p2) && getContagem() == 0) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() + 1, posicao.getColuna() - 1);
			if (getTabuleiro().posicaoExiste(p) && pecaOponenteExiste(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() + 1, posicao.getColuna() + 1);
			if (getTabuleiro().posicaoExiste(p) && pecaOponenteExiste(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}

			// #specialmove en passant black
			if (posicao.getLinha() == 4) {
				Posicao left = new Posicao(posicao.getLinha(), posicao.getColuna() - 1);
				if (getTabuleiro().posicaoExiste(left) && pecaOponenteExiste(left)
						&& getTabuleiro().peca(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getLinha() + 1][left.getColuna()] = true;
				}
				Posicao right = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				if (getTabuleiro().posicaoExiste(right) && pecaOponenteExiste(right)
						&& getTabuleiro().peca(right) == chessMatch.getEnPassantVulnerable()) {
					mat[right.getLinha() + 1][right.getColuna()] = true;
				}
			}
		}
		return mat;
	}

	@Override
	public String toString() {
		return "P";
	}

}
