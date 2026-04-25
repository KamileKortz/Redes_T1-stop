import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("127.0.0.1", 9002);
        OutputStream out = s.getOutputStream();
        InputStream in = s.getInputStream();
        Scanner teclado = new Scanner(System.in);

        System.out.print("Seu nome: ");
        out.write(teclado.nextLine().getBytes());

        // Thread para ficar ouvindo o servidor (Letra e resultados)
        new Thread(() -> {
            try {
                byte[] buf = new byte[1024];
                int n;
                while ((n = in.read(buf)) != -1) {
                    System.out.println(new String(buf, 0, n));
                }
            } catch (Exception e) {}
        }).start();

        // Loop para enviar as respostas quando o servidor pedir
        while (true) {
            out.write(teclado.nextLine().getBytes());
        }
    }
}