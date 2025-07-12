package peer.app;

import common.utils.MD5Hash;

import java.io.*;
import java.net.Socket;

public class TorrentP2PThread extends Thread {
	private final Socket socket;
	private final File file;
	private final String receiver;
	private final BufferedOutputStream dataOutputStream;

	public TorrentP2PThread(Socket socket, File file, String receiver) throws IOException {
		this.socket = socket;
		this.file = file;
		this.receiver = receiver;
		this.dataOutputStream = new BufferedOutputStream(socket.getOutputStream());
		PeerApp.addTorrentP2PThread(this);
	}

	@Override
	public void run() {
		try {
			if (!file.exists() || !file.isFile() || !file.canRead()) {
				socket.close();
				return;
			}

			// 1. Open file input stream
			FileInputStream fileInput = new FileInputStream(file);

			// 2. Read file in chunks and send to peer
			DataOutputStream socketOutStream = new DataOutputStream(socket.getOutputStream());

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = fileInput.read(buffer)) != -1) {
				socketOutStream.write(buffer, 0, bytesRead);
			}
			// 3. Flush and close output stream
			socketOutStream.flush();
			socketOutStream.close();

			fileInput.close();
			// 4. Update sent files list with file name and MD5 hash
			PeerApp.addSentFile(receiver, file.getName() + " " +
					MD5Hash.HashFile(file.getPath()));
		} catch (Exception e) {
			System.err.println("Error during file transfer to " + receiver + ": " + e.getMessage());//TODO delete
		} finally {
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {

			}
			PeerApp.removeTorrentP2PThread(this);
		}
	}

	public void end() {
		try {
			dataOutputStream.close();
			socket.close();
		} catch (Exception e) {}
	}
}
