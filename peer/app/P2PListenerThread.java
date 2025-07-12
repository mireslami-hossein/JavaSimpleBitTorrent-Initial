package peer.app;

import common.models.Message;
import common.utils.JSONUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static peer.app.PeerApp.TIMEOUT_MILLIS;

public class P2PListenerThread extends Thread {
    private final ServerSocket serverSocket;

    public P2PListenerThread(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    private void handleConnection(Socket socket) throws Exception {
        // 1. Set socket timeout
        socket.setSoTimeout(TIMEOUT_MILLIS);

        // 2. Read incoming message
        DataInputStream dataInput = new DataInputStream(socket.getInputStream());
        // 3. Parse message type
        Message gottenMessage = JSONUtils.fromJson(dataInput.readUTF());

        String fileName = gottenMessage.getFromBody("name");
        String fileHash = gottenMessage.getFromBody("md5");
        String peerIP = gottenMessage.getFromBody("receiver_ip");
        int peerPort = gottenMessage.getIntFromBody("receiver_port");

        // 4. Handle download requests by starting a new TorrentP2PThread
        String path = PeerApp.getSharedFolderPath() + File.separator + fileName;
        File file = new File(path);

        TorrentP2PThread fileDownloader = new TorrentP2PThread(socket, file, peerIP + ":" + peerPort);
        fileDownloader.start();
        fileDownloader.join();

        // 5. Close socket for other message types (EOF)
    }

    @Override
    public void run() {
        while (!PeerApp.isEnded()) {
            try {
                Socket socket = serverSocket.accept();
                handleConnection(socket);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            serverSocket.close();
        } catch (Exception ignored) {
        }
    }
}
