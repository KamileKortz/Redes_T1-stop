import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.text.SimpleDateFormat;

public class Server {

    // Pirncipais informações do jogo
    static int n_jogadores = 2;
    static int n_rodadas = 2;
    static String[] categorias = { "Nome", "CEP", "Animal", "Objeto" };
    static List<Handler> jogadores = new ArrayList<>();

    // Controla o envio de mensagens, só um jogador pode mandar mensagem por vez
    static Semaphore semaforo = new Semaphore(1);

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9002);

        // Espera até todos os jogadores entrarem
        while (jogadores.size() < n_jogadores) {
            Socket s = server.accept();
            Handler h = new Handler(s); // Cria um objeto Handler, que vai ser responsável por cuidar daquele jogador
            h.start();
            
            while (h.nome == null) { // Espera os jogadores colocarem o nome pra iniciar o jogo
                Thread.sleep(100);
            }
            jogadores.add(h);
            System.out.println(h.nome + " entrou");
        }

        // for que controla as rodadas
        for (int r = 1; r <= n_rodadas; r++) {

            // Sorteia uma letra
            String letra = String.valueOf((char) ('A' + new Random().nextInt(26)));
            enviarParaTodos("\nRodada " + r + " | Letra: " + letra);
            enviarParaTodos("Categorias: " + String.join(", ", categorias));

            // Espera até todos os jogadores responderem
            while (true) {
                int prontos = 0;
                for (Handler h : jogadores) 
                    if (h.resposta != null)
                        prontos++;
                if (prontos == n_jogadores)
                    break;
                Thread.sleep(500);
            }

            // Calcula os pontos e limpa as respostas para a próxima rodada
            processarRodada();
            for (Handler h : jogadores)
                h.resposta = null;
        }
        enviarPlacarFinal();
    }

    // Esse método envia uma mensagem para todos os jogadores
    static void enviarParaTodos(String msg) throws Exception {
        semaforo.acquire(); // Bloqueia o semáforo

        // Envia para todos os jogadores
        for (Handler h : jogadores) {
            h.out.write((msg + "\n").getBytes());
        }
        semaforo.release(); // Abre o semáforo de novo
    }

    // Calcula a pontuação de cada jogador
    static void processarRodada() throws Exception {
        String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());

        // Conta os pontos de um jogador por vez
        for (Handler h : jogadores) {
            String[] respostasCli = h.resposta.split(",");
            int pontosDaRodada = 0;

            System.out.println("[" + hora + "] " + h.nome + " (" + h.ip + ") enviou: " + h.resposta);

            // Percorre as categorias e compara a resposta do jogador com as dos outros
            // jogadores
            for (int i = 0; i < categorias.length; i++) {
                if (i < respostasCli.length) {
                    String respAtual = respostasCli[i].trim();
                    int pts = 3;

                    // Compara a resposta com as dos outros jogadores
                    for (Handler outro : jogadores) {
                        if (outro != h && outro.resposta != null) {
                            String[] respOutro = outro.resposta.split(",");
                            
                            if (i < respOutro.length && respAtual.equalsIgnoreCase(respOutro[i].trim())) {
                                pts = 1;
                            }
                        }
                    }
                    pontosDaRodada += pts;
                }
            }
            h.pontos += pontosDaRodada;
            enviarParaTodos(h.nome + " fez " + pontosDaRodada + " pontos nesta rodada.");
        }
    }

    // Monta o placar final e mostra pros jogadores
    static void enviarPlacarFinal() throws Exception {
        String placar = "\nPlacar final:\n";
        for (Handler h : jogadores)
            placar += h.nome + ": " + h.pontos + " pts\n";
        enviarParaTodos(placar);
    }
}

class Handler extends Thread {
    Socket s;
    OutputStream out;
    String nome, ip, resposta;
    int pontos = 0;

    public Handler(Socket s) {
        this.s = s;
        this.ip = s.getInetAddress().getHostAddress();
    }

    public void run() {
        try {
            InputStream in = s.getInputStream();
            this.out = s.getOutputStream();
            byte[] buf = new byte[1024];
            int n = in.read(buf);
            this.nome = new String(buf, 0, n);
            while ((n = in.read(buf)) != -1) {
                this.resposta = new String(buf, 0, n);
            }
        } catch (Exception e) {
        }
    }
}
