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
		// TODO: Implement file transfer

		try {
			// 1. Open file input stream
			FileInputStream fileInput = new FileInputStream(file);
			// 2. Read file in chunks and send to peer
			byte[] buffer = new byte[1024];
			int bytesRead;

			DataOutputStream socketOutStream = new DataOutputStream(socket.getOutputStream());
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
			socket.close();
		} catch (Exception e) {
			e.printStackTrace(); // TODO : delete
		}

		PeerApp.removeTorrentP2PThread(this);
	}

	public void end() {
		try {
			dataOutputStream.close();
			socket.close();
		} catch (Exception e) {}
	}
}
