import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.text.SimpleDateFormat;

public class Server {
    static int n_jogadores = 2; // Altere para quantos quiser
    static int n_rodadas = 2;
    static List<Handler> jogadores = new ArrayList<>();
    static Semaphore semaforo = new Semaphore(1); // Protege a lista/envios

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9002);
        System.out.println("Aguardando jogadores...");

        // 1. Conexão: Espera todos os nomes antes de começar
        while (jogadores.size() < n_jogadores) {
            Socket s = server.accept();
            Handler h = new Handler(s);
            h.start();
            while (h.nome == null) { Thread.sleep(100); } // Trava até o nome chegar
            jogadores.add(h);
            System.out.println(h.nome + " entrou!");
        }

        // 2. Rodadas
        for (int r = 1; r <= n_rodadas; r++) {
            String letra = String.valueOf((char) ('A' + new Random().nextInt(26)));
            enviarParaTodos("\n--- RODADA " + r + " | LETRA: " + letra + " ---");

            // Espera todos enviarem a resposta
            while (true) {
                int prontos = 0;
                for (Handler h : jogadores) if (h.resposta != null) prontos++;
                if (prontos == n_jogadores) break;
                Thread.sleep(500);
            }

            processarRodada();
            for (Handler h : jogadores) h.resposta = null; // Reseta para a próxima
        }
        enviarParaTodos("FIM DE JOGO!");
    }

    static void enviarParaTodos(String msg) throws Exception {
        semaforo.acquire(); // Usa semáforo para garantir que ninguém interrompa o envio
        for (Handler h : jogadores) {
            h.out.write((msg + "\n").getBytes());
        }
        semaforo.release();
    }

    static void processarRodada() throws Exception {
        String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
        for (Handler h : jogadores) {
            // Regra de pontos: 3 se for único, 1 se for repetido
            int pontosNestaRodada = 3;
            for (Handler outro : jogadores) {
                if (outro != h && h.resposta.equalsIgnoreCase(outro.resposta)) {
                    pontosNestaRodada = 1;
                }
            }
            h.pontos += pontosNestaRodada;
            
            // Aqui o servidor apresenta os dados: NOME, IP e HORA
            enviarParaTodos("[" + hora + "] " + h.nome + " (" + h.ip + ") enviou: " + h.resposta);
        }
    }
}

class Handler extends Thread {
    Socket s;
    OutputStream out;
    String nome, ip, resposta;
    int pontos = 0;

    public Handler(Socket s) {
        this.s = s;
        this.ip = s.getInetAddress().getHostAddress(); // O servidor pega o IP aqui
    }

    public void run() {
        try {
            InputStream in = s.getInputStream();
            this.out = s.getOutputStream();
            byte[] buf = new byte[1024];
            
            // Primeira leitura é sempre o nome
            int n = in.read(buf);
            this.nome = new String(buf, 0, n);

            // Leituras seguintes são as respostas do jogo
            while ((n = in.read(buf)) != -1) {
                this.resposta = new String(buf, 0, n);
            }
        } catch (Exception e) {}
    }
}