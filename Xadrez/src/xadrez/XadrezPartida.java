package xadrez;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import peca.Bispo;
import peca.Cavalo;
import peca.Peao;
import peca.Rainha;
import peca.Rei;
import peca.Torre;
import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;

public class XadrezPartida {

	private int turn;
	private Color jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	private XadrezPeca enPassantVulnerable;
	private XadrezPeca promoted;
	
	private List<Peca> pecaTabuleiro = new ArrayList<>();
	private List<Peca> pecaCaptura = new ArrayList<>();
	
	public XadrezPartida() {
		tabuleiro = new Tabuleiro(8, 8);
		turn = 1;
		jogadorAtual = Color.WHITE;
		iniciarPartida();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getJogadorAtual() {
		return jogadorAtual;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public XadrezPeca getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public XadrezPeca getPromoted() {
		return promoted;
	}
	
	public XadrezPeca[][] getPecas() {
		XadrezPeca[][] mat = new XadrezPeca[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (XadrezPeca) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}
	
	public boolean[][] possivelMovimentos(XadrezPosicao posicaoOrigem) {
		Posicao posicao = posicaoOrigem.toPosicao();
		validarPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).possivelMovimentos();
	}
	
	public XadrezPeca performaXadrezMovimento(XadrezPosicao posicaoOrigem, XadrezPosicao posicaoDestino) {
		Posicao origem = posicaoOrigem.toPosicao();
		Posicao destino = posicaoDestino.toPosicao();
		validarPosicaoOrigem(destino);
		validarPosicaoDestino(origem, destino);
		Peca pecaCapturada = fazMovimento(origem, destino);
		
		if (testCheck(jogadorAtual)) {
			desfazMovimento(origem, destino, pecaCapturada);
			throw new XadrezExcecao("You can't put yourself in check");
		}
		
		XadrezPeca movePeca = (XadrezPeca)tabuleiro.peca(destino);
		
		// #specialmove promotion
		promoted = null;
		if (movePeca instanceof Peao) {
			if ((movePeca.getColor() == Color.WHITE && destino.getLinha() == 0) || (movePeca.getColor() == Color.BLACK && destino.getLinha() == 7)) {
				promoted = (XadrezPeca)tabuleiro.peca(destino);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		check = (testCheck(oponente(jogadorAtual))) ? true : false;

		if (testCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		}
		else {
			nextTurn();
		}
		
		// #specialmove en passant
		if (movePeca instanceof Peao && (destino.getLinha() == origem.getLinha() - 2 || destino.getLinha() == origem.getLinha() + 2)) {
			enPassantVulnerable = movePeca;
		}
		else {
			enPassantVulnerable = null;
		}
		
		return (XadrezPeca)pecaCapturada;
	}

	public XadrezPeca replacePromotedPiece(String tipo) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!tipo.equals("B") && !tipo.equals("N") && !tipo.equals("R") & !tipo.equals("Q")) {
			throw new InvalidParameterException("Invalid type for promotion");
		}
		
		Posicao pos = promoted.getXadrezPosicao().toPosicao();
		Peca p = tabuleiro.removePeca(pos);
		pecaTabuleiro.remove(p);
		
		XadrezPeca novaPeca = novaPeca(tipo, promoted.getColor());
		tabuleiro.lugarPeca(novaPeca, pos);
		pecaTabuleiro.add(novaPeca);
		
		return novaPeca;
	}
	
	private XadrezPeca novaPeca(String tipo, Color color) {
		if (tipo.equals("B")) return new Bispo(tabuleiro, color);
		if (tipo.equals("C")) return new Cavalo(tabuleiro, color);
		if (tipo.equals("Q")) return new Rainha(tabuleiro, color);
		return new Torre(tabuleiro, color);
	}
	
	private Peca fazMovimento(Posicao origem, Posicao destino) {
		XadrezPeca p = (XadrezPeca)tabuleiro.removePeca(origem);
		p.incrementaContagem();
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.lugarPeca(p, destino);
		
		if (pecaCapturada != null) {
			pecaTabuleiro.remove(pecaCapturada);
			pecaCaptura.add(pecaCapturada);
		}
		
		
		// #specialmove castling kingside rook
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			XadrezPeca torre = (XadrezPeca)tabuleiro.removePeca(origemT);
			tabuleiro.lugarPeca(torre, destinoT);
			torre.incrementaContagem();
		}

		// #specialmove castling queenside rook
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			XadrezPeca torre = (XadrezPeca)tabuleiro.removePeca(origemT);
			tabuleiro.lugarPeca(torre, destinoT);
			torre.incrementaContagem();
		}		
		
		// #specialmove en passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Posicao(destino.getLinha() + 1, destino.getColuna());
				}
				else {
					pawnPosition = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				pecaCapturada = tabuleiro.removePeca(pawnPosition);
				pecaCaptura.add(pecaCapturada);
				pecaTabuleiro.remove(pecaCapturada);
			}
		}
		
		return pecaCapturada;
	}
	
	private void desfazMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		XadrezPeca p = (XadrezPeca)tabuleiro.removePeca(destino);
		p.decrementaContagem();
		tabuleiro.lugarPeca(p, origem);
		
		if (pecaCapturada != null) {
			tabuleiro.lugarPeca(pecaCapturada, destino);
			pecaCaptura.remove(pecaCapturada);
			pecaTabuleiro.add(pecaCapturada);
		}

		// #specialmove castling kingside rook
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			XadrezPeca torre = (XadrezPeca)tabuleiro.removePeca(destinoT);
			tabuleiro.lugarPeca(torre, origemT);
			torre.decrementaContagem();
		}

		// #specialmove castling queenside rook
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			XadrezPeca torre = (XadrezPeca)tabuleiro.removePeca(destinoT);
			tabuleiro.lugarPeca(torre, origemT);
			torre.decrementaContagem();
		}
		
		// #specialmove en passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == enPassantVulnerable) {
				XadrezPeca pawn = (XadrezPeca)tabuleiro.removePeca(destino);
				Posicao pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Posicao(3, destino.getColuna());
				}
				else {
					pawnPosition = new Posicao(4, destino.getColuna());
				}
				tabuleiro.lugarPeca(pawn, pawnPosition);
			}
		}
	}
	
	private void validarPosicaoOrigem(Posicao posicao) {
		if (!tabuleiro.pecaExiste(posicao)) {
			throw new XadrezExcecao("There is no piece on source position");
		}
		if (jogadorAtual != ((XadrezPeca)tabuleiro.peca(posicao)).getColor()) {
			throw new XadrezExcecao("The chosen piece is not yours");
		}
		if (!tabuleiro.peca(posicao).algumMovimento()) {
			throw new XadrezExcecao("There is no possible moves for the chosen piece");
		}
	}
	
	private void validarPosicaoDestino(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).possivelMovimento(destino)) {
			throw new XadrezExcecao("The chosen piece can't move to target position");
		}
	}
	
	private void nextTurn() {
		turn++;
		jogadorAtual = (jogadorAtual == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private Color oponente(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private XadrezPeca rei(Color color) {
		List<Peca> list = pecaTabuleiro.stream().filter(x -> ((XadrezPeca)x).getColor() == color).collect(Collectors.toList());
		for (Peca p : list) {
			if (p instanceof Rei) {
				return (XadrezPeca)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board");
	}
	
	private boolean testCheck(Color color) {
		Posicao kingPosition = rei(color).getXadrezPosicao().toPosicao();
		List<Peca> pecaAdversaria = pecaTabuleiro.stream().filter(x -> ((XadrezPeca)x).getColor() == oponente(color)).collect(Collectors.toList());
		for (Peca p : pecaAdversaria) {
			boolean[][] mat = p.possivelMovimentos();
			if (mat[kingPosition.getLinha()][kingPosition.getColuna()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Peca> list = pecaTabuleiro.stream().filter(x -> ((XadrezPeca)x).getColor() == color).collect(Collectors.toList());
		for (Peca p : list) {
			boolean[][] mat = p.possivelMovimentos();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((XadrezPeca)p).getXadrezPosicao().toPosicao();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = fazMovimento(origem, destino);
						boolean testCheck = testCheck(color);
						desfazMovimento(origem, destino, pecaCapturada);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}	
	
	private void lugarPecaNova(char coluna, int linha, XadrezPeca peca) {
		tabuleiro.lugarPeca(peca, new XadrezPosicao(coluna, linha).toPosicao());
		pecaTabuleiro.add(peca);
	}
	
	private void iniciarPartida() {
		lugarPecaNova('a', 1, new Torre(tabuleiro, Color.WHITE));
		lugarPecaNova('b', 1, new Cavalo(tabuleiro, Color.WHITE));
		lugarPecaNova('c', 1, new Bispo(tabuleiro, Color.WHITE));
		lugarPecaNova('d', 1, new Rainha(tabuleiro, Color.WHITE));
		lugarPecaNova('e', 1, new Rei(tabuleiro, Color.WHITE, this));
		lugarPecaNova('f', 1, new Bispo(tabuleiro, Color.WHITE));
		lugarPecaNova('g', 1, new Cavalo(tabuleiro, Color.WHITE));
		lugarPecaNova('h', 1, new Torre(tabuleiro, Color.WHITE));
		lugarPecaNova('a', 2, new Peao(tabuleiro, Color.WHITE, this));
		lugarPecaNova('b', 2, new Peao(tabuleiro, Color.WHITE, this));
		lugarPecaNova('c', 2, new Peao(tabuleiro, Color.WHITE, this));
		lugarPecaNova('d', 2, new Peao(tabuleiro, Color.WHITE, this));
		lugarPecaNova('e', 2, new Peao(tabuleiro, Color.WHITE, this));
		lugarPecaNova('f', 2, new Peao(tabuleiro, Color.WHITE, this));
		lugarPecaNova('g', 2, new Peao(tabuleiro, Color.WHITE, this));
		lugarPecaNova('h', 2, new Peao(tabuleiro, Color.WHITE, this));

		lugarPecaNova('a', 8, new Torre(tabuleiro, Color.BLACK));
		lugarPecaNova('b', 8, new Cavalo(tabuleiro, Color.BLACK));
		lugarPecaNova('c', 8, new Bispo(tabuleiro, Color.BLACK));
		lugarPecaNova('d', 8, new Rainha(tabuleiro, Color.BLACK));
		lugarPecaNova('e', 8, new Rei(tabuleiro, Color.BLACK, this));
		lugarPecaNova('f', 8, new Bispo(tabuleiro, Color.BLACK));
		lugarPecaNova('g', 8, new Cavalo(tabuleiro, Color.BLACK));
        lugarPecaNova('h', 8, new Torre(tabuleiro, Color.BLACK));
        lugarPecaNova('a', 7, new Peao(tabuleiro, Color.BLACK, this));
        lugarPecaNova('b', 7, new Peao(tabuleiro, Color.BLACK, this));
        lugarPecaNova('c', 7, new Peao(tabuleiro, Color.BLACK, this));
        lugarPecaNova('d', 7, new Peao(tabuleiro, Color.BLACK, this));
        lugarPecaNova('e', 7, new Peao(tabuleiro, Color.BLACK, this));
        lugarPecaNova('f', 7, new Peao(tabuleiro, Color.BLACK, this));
        lugarPecaNova('g', 7, new Peao(tabuleiro, Color.BLACK, this));
        lugarPecaNova('h', 7, new Peao(tabuleiro, Color.BLACK, this));
	}
}