package peer.controllers;

import common.models.CLICommands;
import common.models.Message;
import common.utils.FileUtils;
import peer.app.P2TConnectionThread;
import peer.app.PeerApp;

import java.util.Map;

public class PeerCLIController {
	public static String processCommand(String command) {
		// TODO: Process Peer CLI commands
		// 1. Check command type (END_PROGRAM, DOWNLOAD, LIST)
		if (PeerCommands.END.matches(command)) {
			return endProgram();
		} else if (PeerCommands.LIST.matches(command)) {
			return handleListFiles();
		} else if (PeerCommands.DOWNLOAD.matches(command)) {
			return handleDownload(command);
		}
		else {
			return CLICommands.invalidCommand;
		}
		// 2. Call appropriate handler
		// 3. Return result or error message
	}

	private static String handleListFiles() {
		Map<String, String> files =  FileUtils.listFilesInFolder(PeerApp.getSharedFolderPath());
		return FileUtils.getSortedFileList(files);
	}

	private static String handleDownload(String command) {
		// TODO: Handle download command
		// Send file request to tracker
		String fileName = PeerCommands.DOWNLOAD.getGroup(command, "name");
		P2TConnectionThread connection = PeerApp.getP2TConnection();
		try {
			Message requestFile = P2TConnectionController.sendFileRequest(connection, fileName);
			// File is correct
			if (requestFile.getFromBody("response").equals("peer_found")) {
				String IP = requestFile.getFromBody("peer_have");
				int port = requestFile.getIntFromBody("peer_port");
				String fileHash = requestFile.getFromBody("md5");

				PeerApp.requestDownload(IP, port, fileName, fileHash);

				return "File downloaded successfully: " + fileName;
			}
		} catch (NullPointerException e) {
			return "Some Error occurred in getting message! message is null";
		}
		catch (Exception e) {
			if (e.getMessage().equals("file_exists"))
				return "You already have the file!";
			else if (e.getMessage().equals("not_found"))
				return "No peer has the file!";
			else if (e.getMessage().equals("multiple_hash"))
				return "Multiple hashes found!";
			else if (e.getMessage().equals("conflict"))
				return "The file has been downloaded from peer but is corrupted!";

			else // TODO : delete maybe
				return e.getMessage();
		}

		// Get peer info and file hash
		// Request file from peer
		// Return success or error message
		throw new UnsupportedOperationException("handleDownload not implemented yet");
	}

	public static String endProgram() {
		PeerApp.endAll();
		return "";
	}
}
