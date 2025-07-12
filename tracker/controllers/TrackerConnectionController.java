package tracker.controllers;

import common.models.Message;
import tracker.app.PeerConnectionThread;
import tracker.app.TrackerApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackerConnectionController {
	public static Message handleCommand(Message message) {

		// 1. Validate message type and content
		if (message.getType() != Message.Type.file_request) return null;
		String fileName = message.getFromBody("name");
		// 2. Find peers having the requested file
		List<PeerConnectionThread> peerConnections = new ArrayList<>();
		String hashOfFile = null;

		HashMap<String, Object> body  = new HashMap<>();
		for (PeerConnectionThread connection : TrackerApp.getConnections()) {
			String hashOfPeerFile = connection.getFileAndHashes().get(fileName);
			if (hashOfPeerFile == null) continue;

			peerConnections.add(connection);
			if (hashOfFile == null) hashOfFile = hashOfPeerFile;
			else {
				// 3. Check for hash consistency
				if (!hashOfFile.equals(hashOfPeerFile)) {
					// Error : HashConflict
					body.put("response", "error");
					body.put("error", "multiple_hash");
					return new Message(body, Message.Type.response);
				} else {
					peerConnections.add(connection);
				}
			}
		}

		if (hashOfFile == null) {
			// Error : FileNotFound
			body.put("response", "error");
			body.put("error", "not_found");
			return new Message(body, Message.Type.response);
		}

		// 4. Return peer information
		PeerConnectionThread randomConnection = peerConnections.get((int)(Math.random() * peerConnections.size()));
		String peerIP = randomConnection.getOtherSideIP();
		int peerPort = randomConnection.getOtherSidePort();

		// if ok
		body.put("response", "peer_found");
		body.put("md5", hashOfFile);
		body.put("peer_have", peerIP);
		body.put("peer_port", peerPort);

		return new Message(body, Message.Type.response);
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
