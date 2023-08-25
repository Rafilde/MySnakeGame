package JogoDaCobrinha;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RankingInterface extends JFrame {
	private static final String Arquivo = "C:\\Users\\Rafael Silva\\Videos\\Captures\\ranking.txt";
    private ArrayList<Jogador> jogadores;
    private JList<String> rankingList;

    public RankingInterface() {
        jogadores = new ArrayList<>();

        setTitle("Ranking de Jogadores");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Ranking de Jogadores");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        rankingList = new JList<>();
        rankingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(rankingList), BorderLayout.CENTER);
        
        atualizarRanking();
    }

    public void adicionarJogador(int pontuacao) {
        String nome = JOptionPane.showInputDialog("Digite o nome do jogador:");

        Jogador jogador = new Jogador(nome, pontuacao);
        jogadores.add(jogador);

        JOptionPane.showMessageDialog(this, "Jogador adicionado com sucesso!");
        
        try {
        	 FileWriter writer = new FileWriter(Arquivo, true);
        	 writer.write(jogador.getNome() + "," + pontuacao + "\n");
        	 writer.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        atualizarRanking(); 
    }

    private void atualizarRanking() {
    	
    	 jogadores.clear();

         try {
             BufferedReader reader = new BufferedReader(new FileReader(Arquivo));
             String linha;

             while ((linha = reader.readLine()) != null) {
                 String[] dados = linha.split(",");
                 if (dados.length == 2) {
                     String nome = dados[0];
                     int pontuacao = Integer.parseInt(dados[1]);
                     jogadores.add(new Jogador(nome, pontuacao));
                 }
             }

             reader.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
    	
        Collections.sort(jogadores, new Comparator<Jogador>() {
            @Override
            public int compare(Jogador jogador1, Jogador jogador2) {
                return Integer.compare(jogador2.getPontuacao(), jogador1.getPontuacao());
            }
        });

        // Cria uma lista de strings com o nome e pontuação dos 10 melhores jogadores
        ArrayList<String> ranking = new ArrayList<>();
        int tamanhoRanking = Math.min(jogadores.size(), 10); // Limita o tamanho do ranking aos 10 primeiros jogadores
        for (int i = 0; i < tamanhoRanking; i++) {
            Jogador jogador = jogadores.get(i);
            ranking.add(jogador.getNome() + " - " + jogador.getPontuacao());
        }

        // Atualiza o conteúdo da JList com o ranking
        rankingList.setListData(ranking.toArray(new String[0]));
    }

    private class Jogador {
        private String nome;
        private int pontuacao;

        public Jogador(String nome, int pontuacao) {
            this.nome = nome;
            this.pontuacao = pontuacao;
        }

        public String getNome() {
            return nome;
        }

        public int getPontuacao() {
            return pontuacao;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                RankingInterface rankingInterface = new RankingInterface();
                rankingInterface.setVisible(true);
            }
        });
    }
}
