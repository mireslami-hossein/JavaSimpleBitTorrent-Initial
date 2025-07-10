package tracker.controllers;

import tracker.app.PeerConnectionThread;
import tracker.app.TrackerApp;

import java.util.List;

public class TrackerCLIController {
	public static String processCommand(String command) {
		// TODO: Process tracker CLI commands
		if (TrackerCommands.END.matches(command)) {
			return endProgram();
		} else if (TrackerCommands.LIST_PEERS.matches(command)) {
			return listPeers();
		} else {
			return "invalid command";
		}
	}

	private static String getReceives(String command) {
		// TODO: Get list of files received by a peer
		throw new UnsupportedOperationException("getReceives not implemented yet");
	}

	private static String getSends(String command) {
		// TODO: Get list of files sent by a peer
		throw new UnsupportedOperationException("getSends not implemented yet");
	}

	private static String listFiles(String command) {
		// TODO: List files of a peer (do not send command, use the cached list)
		throw new UnsupportedOperationException("listFiles not implemented yet");
	}

	private static String listPeers() {
		List<PeerConnectionThread> connections = TrackerApp.getConnections();
		if (connections.isEmpty()) {
			return "No peers connected.";
		}
		StringBuilder result = new StringBuilder();
		for (PeerConnectionThread connection : connections) {
			result.append(connection.getOtherSideIP() + ":" + connection.getOtherSidePort()).append("\n");
		}
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	private static String resetConnections() {
		// TODO: Reset all peer connections
		// Refresh status and file list for each peer
		throw new UnsupportedOperationException("resetConnections not implemented yet");
	}

	private static String refreshFiles() {
		// TODO: Refresh file lists for all peers
		throw new UnsupportedOperationException("refreshFiles not implemented yet");
	}

	private static String endProgram() {
		TrackerApp.endAll();
		return "";
	}
}
