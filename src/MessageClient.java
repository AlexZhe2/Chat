import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class MessageClient {

    public static void main(String[] args) {
        Socket socketFromClient = null;
        try {
            socketFromClient = new Socket("127.0.0.1", 8888);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Socket finalSocketFromClient = socketFromClient;
        Scanner scanner = new Scanner(System.in);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectInputStream in3= null;
                try {
                    in3 = new ObjectInputStream(finalSocketFromClient.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (true) {
                    try {
                        Message ms_3 =  (Message) in3.readObject();
                        System.out.println("прочитанное сообщение от сервера "+ms_3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        ObjectOutputStream out3 = null;
        try {
            out3 = new ObjectOutputStream(socketFromClient.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Введите имя");
        String name = scanner.nextLine();
        String messageText;
        while (true) {
            System.out.println("Введите сообщение");
            messageText = scanner.nextLine();
            try {
                out3.writeObject(new Message(name, messageText));
                out3.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}






