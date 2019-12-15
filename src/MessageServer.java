import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageServer {


    public static void main(String[] args) {
        CopyOnWriteArraySet<ObjectOutputStream> setOfObjOut = new CopyOnWriteArraySet<>();
        LinkedBlockingDeque<Message> linkedBlockForMess = new LinkedBlockingDeque(1);
        LinkedBlockingDeque<ObjectOutputStream> linkedBlockForClient = new LinkedBlockingDeque(1);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    if (setOfObjOut.size() != 0 && linkedBlockForMess.size() != 0) {

                            for (ObjectOutputStream obj : setOfObjOut) {
                                if (!linkedBlockForClient.getFirst().equals(obj)) {
                                    try {
                                        obj.writeObject(linkedBlockForMess.getFirst());
                                        obj.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        linkedBlockForMess.removeFirst();
                        linkedBlockForClient.removeFirst();
                    }
                }
            }
        }).start();

        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("Server started...");

            while (true) {
                Socket socket = serverSocket.accept();

                ObjectOutputStream objOutStreamForCheck = new ObjectOutputStream(socket.getOutputStream());
                setOfObjOut.add(objOutStreamForCheck);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ObjectInputStream in4 = null;
                        try {
                            in4 = new ObjectInputStream(socket.getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        while (true) {

                            try {
                                Message ms4 = (Message) in4.readObject();
                                linkedBlockForMess.putFirst(ms4);
                                linkedBlockForClient.putFirst(objOutStreamForCheck);
                                System.out.println("прочитаное сообщение " + ms4);
                            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                                //   e.printStackTrace();
                                System.out.println("Client disconnected ");
                                setOfObjOut.remove(objOutStreamForCheck);
                                break;
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


