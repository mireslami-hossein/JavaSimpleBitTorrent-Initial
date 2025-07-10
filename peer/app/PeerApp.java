package peer.app;

import tracker.app.ListenerThread;

import java.net.Socket;
import java.util.*;

public class PeerApp {
	public static final int TIMEOUT_MILLIS = 500;

	// static fields for peer's ip, port, shared folder path, sent files, received files,
	//  tracker connection thread, p2p listener thread, torrent p2p threads
	private static String myIP;
	private static int myPort;
	private static String sharedFolderPath;
	private static Map<String, List<String>> sentFilesHashes;
	private static Map<String, List<String>> receivedFilesHashes;

	// Threads
	private static P2TConnectionThread trackerConnectionThread;
	private static P2PListenerThread peerListenerThread;
	private static List<TorrentP2PThread> torrentP2PThreads = new ArrayList<>();


	private static boolean exitFlag = false;

	public static boolean isEnded() {
		return exitFlag;
	}

	public static void initFromArgs(String[] args) throws Exception {
		// 1. Parse self address (ip:port)
		String[] selfAddress = args[0].split(":");
		myIP = selfAddress[0];
		myPort = Integer.parseInt(selfAddress[1]);
		// 2. Parse tracker address (ip:port)
		String[] trackerAddress = args[1].split(":");
		String trackerIP = trackerAddress[0];
		int trackerPort = Integer.parseInt(trackerAddress[1]);

		// 3. Set shared folder path
		sharedFolderPath = args[2];

		// 4. Create tracker connection thread
		trackerConnectionThread = new P2TConnectionThread(new Socket(trackerIP, trackerPort));

		// 5. Create peer listener thread
		peerListenerThread = new P2PListenerThread(myPort);
	}

	public static void endAll() {
		exitFlag = true;
		// 1. End tracker connection
		trackerConnectionThread.end();
		// 2. End all torrent threads
		for (TorrentP2PThread thread : torrentP2PThreads) {
			thread.end();
		}
		//TODO: 3. Clear file lists

		throw new UnsupportedOperationException("Cleanup not implemented yet");
	}

	public static void connectTracker() {
		// Check if thread exists and not running, then Start thread
		if (trackerConnectionThread != null && !trackerConnectionThread.isAlive())
			trackerConnectionThread.start();
	}

	public static void startListening() {
		// Check if thread exists and not running, then Start thread
		if (peerListenerThread != null && !peerListenerThread.isAlive())
			peerListenerThread.start();
	}

	public static void removeTorrentP2PThread(TorrentP2PThread torrentP2PThread) {
		// TODO: Remove and cleanup torrent thread
		throw new UnsupportedOperationException("Torrent P2P thread not implemented yet");
	}

	public static void addTorrentP2PThread(TorrentP2PThread torrentP2PThread) {
		// TODO: Add new torrent thread
		// 1. Check if thread is valid
		// 2. Check if already exists
		// 3. Add to list
		torrentP2PThreads.add(torrentP2PThread);
		throw new UnsupportedOperationException("Torrent P2P thread not implemented yet");
	}

	public static String getSharedFolderPath() {
		return sharedFolderPath;
	}

	public static void addSentFile(String receiver, String fileNameAndHash) {
//		TODO : call in download file
		List<String> files = sentFilesHashes.get(receiver);
		if (files == null) {
			files = new ArrayList<>();
		}
		files.add(fileNameAndHash);
	}

	public static void addReceivedFile(String sender, String fileNameAndHash) {
		//		TODO : call in download file
		List<String> files = receivedFilesHashes.get(sender);
		if (files == null) {
			files = new ArrayList<>();
		}
		files.add(fileNameAndHash);
	}

	public static String getPeerIP() {
		return myIP;
	}

	public static int getPeerPort() {
		return myPort;
	}

	public static Map<String, List<String>> getSentFiles() {
		return new HashMap<>(sentFilesHashes);
	}

	public static Map<String, List<String>> getReceivedFiles() {
		return new HashMap<>(receivedFilesHashes);
	}

	public static P2TConnectionThread getP2TConnection() {
		return trackerConnectionThread;
	}

	public static void requestDownload(String ip, int port, String filename, String md5) throws Exception {
		// TODO: Implement file download from peer
		// 1. Check if file already exists
		// 2. Create download request message
		// 3. Connect to peer
		// 4. Send request
		// 5. Receive file data
		// 6. Save file
		// 7. Verify file integrity
		// 8. Update received files list
		throw new UnsupportedOperationException("File download not implemented yet");
	}
}
