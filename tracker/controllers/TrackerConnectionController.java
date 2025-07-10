package tracker.controllers;

import common.models.Message;
import peer.app.PeerApp;
import tracker.app.PeerConnectionThread;
import tracker.app.TrackerApp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackerConnectionController {
	public static Message handleCommand(Message message) {
		// TODO: Handle incoming peer-to-tracker commands
		// 1. Validate message type and content
		if (message.getType() != Message.Type.file_request) return null;
		String fileName = message.getFromBody("name");
		// 2. Find peers having the requested file
		// 3. Check for hash consistency
		// 4. Return peer information or error
		throw new UnsupportedOperationException("handleCommand not implemented yet");
	}

	public static Map<String, List<String>> getSends(PeerConnectionThread connection) {
		// 1. Build command message
		HashMap<String, Object> body = new HashMap<>();
		body.put("command", "get_sends");
		Message message = new Message(body, Message.Type.command);

		// 2. Send message and wait for response
		Message response = connection.sendAndWaitForResponse(message, TrackerApp.TIMEOUT_MILLIS);
		if (response == null) return null;

		// 3. Parse and return sent files map
		return new HashMap<>(response.getFromBody("sent_files"));
	}

	public static Map<String, List<String>> getReceives(PeerConnectionThread connection) {
		// 1. Build command message
		HashMap<String, Object> body = new HashMap<>();
		body.put("command", "get_receives");
		Message message = new Message(body, Message.Type.command);

		// 2. Send message and wait for response
		Message response = connection.sendAndWaitForResponse(message, TrackerApp.TIMEOUT_MILLIS);
		if (response == null) return null;

		// 3. Parse and return received files map
		return new HashMap<>(response.getFromBody("received_files"));
	}
}
