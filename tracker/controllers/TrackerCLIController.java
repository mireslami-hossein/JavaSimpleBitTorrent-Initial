package tracker.controllers;

import common.models.CLICommands;
import common.utils.FileUtils;
import tracker.app.PeerConnectionThread;
import tracker.app.TrackerApp;

import java.util.*;

public class TrackerCLIController {
	public static String processCommand(String command) {
		// TODO: Process tracker CLI commands
		if (TrackerCommands.END.matches(command)) {
			return endProgram();
		} else if (TrackerCommands.LIST_PEERS.matches(command)) {
			return listPeers();
		} else if (TrackerCommands.GET_RECEIVES.matches(command)) {
			return getReceives(command);
		} else if (TrackerCommands.GET_SENDS.matches(command)) {
			return getSends(command);
		} else if (TrackerCommands.LIST_FILES.matches(command)) {
			return listFiles(command);
		} else if (TrackerCommands.REFRESH_FILES.matches(command)) {
			return refreshFiles();
		} else if (TrackerCommands.RESET_CONNECTIONS.matches(command)) {
			return resetConnections();
		}
		else {
			return CLICommands.invalidCommand;
		}
	}

	private static String getReceives(String command) {
		String IP = TrackerCommands.GET_RECEIVES.getGroup(command, "IP");
		int port = Integer.parseInt(TrackerCommands.GET_RECEIVES.getGroup(command, "port"));
		PeerConnectionThread connectionToPeer = TrackerApp.getConnectionByIpPort(IP, port);
		if (connectionToPeer == null)
			return "Peer not found.";
		Map<String, List<String>> receivedFiles = TrackerConnectionController.getReceives(connectionToPeer);
		if (receivedFiles == null || receivedFiles.isEmpty())
			return "No files received by " + IP + ":" + port;

		StringBuilder result = new StringBuilder();
		// Sorting lists
		for (String address : receivedFiles.keySet()) {
			List<String> filesSorted = receivedFiles.get(address);
			Collections.sort(filesSorted);
		}

		// TODO : check sort
		TreeMap<String, List<String>> sortedReceivedFiles = new TreeMap<>(receivedFiles);
		for (Map.Entry<String, List<String>> entry : sortedReceivedFiles.entrySet()) {
			for (String fileData : entry.getValue()) {
				result.append(fileData);
				result.append(" - ");
				result.append(entry.getKey() + "\n");
			}
		}
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	private static String getSends(String command) {
		String IP = TrackerCommands.GET_SENDS.getGroup(command, "IP");
		int port = Integer.parseInt(TrackerCommands.GET_SENDS.getGroup(command, "port"));
		PeerConnectionThread connectionToPeer = TrackerApp.getConnectionByIpPort(IP, port);
		if (connectionToPeer == null)
			return "Peer not found.";

		Map<String, List<String>> sentFiles = TrackerConnectionController.getSends(connectionToPeer);
		if (sentFiles == null || sentFiles.isEmpty())
			return "No files sent by " + IP + ":" + port;

		StringBuilder result = new StringBuilder();
		// Sorting lists
		for (String address : sentFiles.keySet()) {
			List<String> filesSorted = sentFiles.get(address);
			Collections.sort(filesSorted);
		}

		TreeMap<String, List<String>> sortedSentFiles = new TreeMap<>(sentFiles);
		for (Map.Entry<String, List<String>> entry : sortedSentFiles.entrySet()) {
			for (String fileData : entry.getValue()) {
				result.append(fileData);
				result.append(" - ");
				result.append(entry.getKey() + "\n");
			}
		}
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	private static String listFiles(String command) {
		String IP = TrackerCommands.LIST_FILES.getGroup(command, "IP");
		int port = Integer.parseInt(TrackerCommands.LIST_FILES.getGroup(command, "port"));

		PeerConnectionThread connection = TrackerApp.getConnectionByIpPort(IP, port);
		if (connection == null) return "Peer not found.";
		return FileUtils.getSortedFileList(connection.getFileAndHashes());
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
		// Refresh status and file list for each peer
		List<PeerConnectionThread> connectionsCopyList = TrackerApp.getConnections();
		for (PeerConnectionThread connection : connectionsCopyList) {
			try {
				connection.refreshStatus();
				connection.refreshFileList();
			} catch (Exception e) {
				TrackerApp.removePeerConnection(connection);
			}
		}
		return "";
	}

	private static String refreshFiles() {
		for (PeerConnectionThread connection : TrackerApp.getConnections()) {
			connection.refreshFileList();
		}
		return "";
	}

	private static String endProgram() {
		TrackerApp.endAll();
		return "";
	}
}
