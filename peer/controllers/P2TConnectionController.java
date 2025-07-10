package peer.controllers;

import common.models.Message;
import common.utils.FileUtils;
import peer.app.P2TConnectionThread;
import peer.app.PeerApp;

import java.util.HashMap;

public class P2TConnectionController {
	public static Message handleCommand(Message message) {
		// TODO: Handle incoming tracker-to-peer commands
		// 1. Parse command from message
		// 2. Call appropriate handler (status, get_files_list, get_sends, get_receives)
		// 3. Return response message
		throw new UnsupportedOperationException("handleCommand not implemented yet");
	}

	private static Message getReceives() {
		// TODO: Return information about received files
		throw new UnsupportedOperationException("getReceives not implemented yet");
	}

	private static Message getSends() {
		// TODO: Return information about sent files
		throw new UnsupportedOperationException("getSends not implemented yet");
	}

	public static Message getFilesList() {
		// Return list of files in shared folder
		HashMap<String, Object> messageBody = new HashMap<>();
		messageBody.put("command", "get_files_list");
		messageBody.put("response",	"ok");
		messageBody.put("files", FileUtils.listFilesInFolder(PeerApp.getSharedFolderPath()));
		return new Message(messageBody, Message.Type.response);
	}

	public static Message status() {
		// Return peer status information
		HashMap<String, Object> messageBody  = new HashMap<>();
		messageBody.put("command", "status");
		messageBody.put("response", "ok");
		messageBody.put("peer", PeerApp.getPeerIP());
		messageBody.put("listen_port", PeerApp.getPeerPort());

		return new Message(messageBody, Message.Type.response);
	}

	public static Message sendFileRequest(P2TConnectionThread tracker, String fileName) throws Exception {
		// TODO: Send file request to tracker and handle response
		// 1. Build request message
		// 2. Send message and wait for response
		// 3. raise exception if error or return response
		throw new UnsupportedOperationException("sendFileRequest not implemented yet");
	}
}
