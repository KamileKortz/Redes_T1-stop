import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {

        Socket s = new Socket("127.0.0.1", 9002);
        OutputStream out = s.getOutputStream();
        InputStream in = s.getInputStream();
        Scanner LER = new Scanner(System.in);

        System.out.print("Seu nome: ");
        out.write(LER.nextLine().getBytes());

        // Recebe as mensagens do servidor
        new Thread(() -> {
            try {
                byte[] buf = new byte[1024];
                int n;
                while ((n = in.read(buf)) != -1) {
                    System.out.println(new String(buf, 0, n));
                }
            } catch (Exception e) {
            }
        }).start();

        System.out.println("Aguardando início...");

        // Digita e envia as respostas pao servidor
        while (true) {
            String resp = LER.nextLine();
            out.write(resp.getBytes());
        }
    }
}