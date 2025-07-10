package tracker.app;

import common.models.ConnectionThread;
import common.models.Message;
import tracker.controllers.TrackerConnectionController;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class PeerConnectionThread extends ConnectionThread {
	private HashMap<String, String> fileAndHashes;

	public PeerConnectionThread(Socket socket) throws IOException {
		super(socket);
	}

	@Override
	public boolean initialHandshake() {
		try {
			// TODO: Implement initial handshake
			// Refresh peer statusCommand (IP and port), Get peer's file list, Add connection to tracker's connection list
			HashMap<String, Object> body = new HashMap<>();
			body.put("command", "status");
			Message statusCommand = new Message(body, Message.Type.command);
			Message statusOfPeer = sendAndWaitForResponse(statusCommand, TrackerApp.TIMEOUT_MILLIS);
			if (statusOfPeer == null) {
				System.err.println("Peer did not respond to the status command. Handshake failed.");
				return false;
			}

			body = new HashMap<>();
			body.put("command", "get_files_list");
			Message getFiles = new Message(body, Message.Type.command);
			Message filesListResponse = sendAndWaitForResponse(getFiles, TrackerApp.TIMEOUT_MILLIS);
			if (filesListResponse == null) {
				System.err.println("Peer did not respond to the filesList command. Handshake failed.");
				return false;
			}

			String ipOfPeer = statusOfPeer.getFromBody("peer");
			int portOfPeer = statusOfPeer.getIntFromBody("listen_port");

			Map<String,String> filesList = filesListResponse.getFromBody("files");
			this.fileAndHashes = new HashMap<>(filesList);

			this.setOtherSideIP(ipOfPeer);
			this.setOtherSidePort(portOfPeer);
			TrackerApp.addPeerConnection(this);
			// TODO : delete
//			System.out.println("Peer connected: " + this.getOtherSideIP() + ":" + this.getOtherSidePort());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void refreshStatus() {
		// TODO: Implement status refresh
		// Send status command and update peer's IP and port and wait for response
		// then update peer's IP and port
		throw new UnsupportedOperationException("Status refresh not implemented yet");
	}

	public void refreshFileList() {
		// TODO: Implement file list refresh
		// Request and update peer's file list
		throw new UnsupportedOperationException("File list refresh not implemented yet");
	}

	@Override
	protected boolean handleMessage(Message message) {
		if (message.getType() == Message.Type.file_request) {
			sendMessage(TrackerConnectionController.handleCommand(message));
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		super.run();
		TrackerApp.removePeerConnection(this);
	}

	public Map<String, String> getFileAndHashes() {
		return Map.copyOf(fileAndHashes);
	}
}
