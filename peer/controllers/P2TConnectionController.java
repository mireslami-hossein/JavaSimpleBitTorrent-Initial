package peer.controllers;

import common.models.Message;
import common.utils.FileUtils;
import peer.app.P2TConnectionThread;
import peer.app.PeerApp;

import java.util.HashMap;

public class P2TConnectionController {
	public static Message handleCommand(Message message) {
		// 1. Parse command from message
		String command = message.getFromBody("command");
		// 2. Call appropriate handler (status, get_files_list, get_sends, get_receives)
		// 3. Return response message
		return switch (command) {
            case "status" -> status();
            case "get_files_list" -> getFilesList();
            case "get_sends" -> getSends();
			default -> getReceives();
        };

    }

	private static Message getReceives() {
		HashMap<String, Object> body = new HashMap<>();
		body.put("command", "get_receives");
		body.put("response", "ok");
		body.put("received_files", PeerApp.getReceivedFiles());
		return new Message(body, Message.Type.response);
	}

	private static Message getSends() {
		HashMap<String, Object> body = new HashMap<>();
		body.put("command", "get_sends");
		body.put("response", "ok");
		body.put("sent_files", PeerApp.getSentFiles());
		return new Message(body, Message.Type.response);
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
		// 1. Build request message
		HashMap<String, Object> body  = new HashMap<>();
		body.put("name", fileName);

		// 2. Send message and wait for response
		Message fileRequest = new Message(body, Message.Type.file_request);
		Message response = tracker.sendAndWaitForResponse(fileRequest, PeerApp.TIMEOUT_MILLIS);
		// 3. raise exception if error or return response
		if (response.getFromBody("response").equals("error")) {
			throw new Exception((String) response.getFromBody("error"));
		} else {
			return response;
		}
	}
}
