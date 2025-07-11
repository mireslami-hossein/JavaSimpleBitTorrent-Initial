package tracker.app;

import common.models.ConnectionThread;
import common.models.Message;
import tracker.controllers.TrackerConnectionController;

import java.io.IOException;
import java.net.ConnectException;
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
			// Refresh peer statusCommand (IP and port), Get peer's file list, Add connection to tracker's connection list

			refreshStatus();
			refreshFileList();

			TrackerApp.addPeerConnection(this);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void refreshStatus() throws ConnectException {
		// Send status command and update peer's IP and port and wait for response
		// then update peer's IP and port

		HashMap<String, Object> body = new HashMap<>();
		body.put("command", "status");
		Message statusCommand = new Message(body, Message.Type.command);
		Message statusOfPeer = sendAndWaitForResponse(statusCommand, TrackerApp.TIMEOUT_MILLIS);
		if (statusOfPeer == null) throw new ConnectException("timeout");
		this.setOtherSideIP(statusOfPeer.getFromBody("peer"));
		this.setOtherSidePort(statusOfPeer.getIntFromBody("listen_port"));
	}

	public void refreshFileList() {
		// Request and update peer's file list

		HashMap<String, Object> body = new HashMap<>();
		body.put("command", "get_files_list");
		Message getFiles = new Message(body, Message.Type.command);
		Message filesListResponse = sendAndWaitForResponse(getFiles, TrackerApp.TIMEOUT_MILLIS);

		Map<String,String> filesList = filesListResponse.getFromBody("files");
		this.fileAndHashes = new HashMap<>(filesList);
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
