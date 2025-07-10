package peer.controllers;

import common.models.CLICommands;
import common.utils.FileUtils;
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
		} else {
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
