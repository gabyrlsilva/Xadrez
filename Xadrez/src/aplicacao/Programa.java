
package aplicacao;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.XadrezExcecao;
import xadrez.XadrezPartida;
import xadrez.XadrezPeca;
import xadrez.XadrezPosicao;

public class Programa {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		XadrezPartida xp = new XadrezPartida();
		List<XadrezPeca> captura = new ArrayList<>();
		
		while (!xp.getCheckMate()) {
			try {
				UI.limparTela();
				UI.printPartida(xp, captura);
				System.out.println();
				System.out.print("Origem: ");
				XadrezPosicao origem = UI.leiaPosicaoXadrez(sc);
				
				boolean[][] possivelMovimentos = xp.possivelMovimentos(origem);
				UI.limparTela();
				UI.printTabuleiro(xp.getPecas(), possivelMovimentos);
				System.out.println();
				System.out.print("Destino: ");
				XadrezPosicao destino = UI.leiaPosicaoXadrez(sc);
				
				XadrezPeca pecaCapturada = xp.performaXadrezMovimento(origem, destino);
				
				if (pecaCapturada != null) {
					captura.add(pecaCapturada);
				}
				
				if (xp.getPromoted() != null) {
					System.out.print("Enter piece for promotion (B/N/R/Q): ");
					String tipo = sc.nextLine();
					xp.replacePromotedPiece(tipo);
				}
			}
			catch (XadrezExcecao e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
			catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		UI.limparTela();
		UI.printPartida(xp, captura);
	}
}
