package peer.app;

import common.models.Message;
import common.utils.FileUtils;
import common.utils.JSONUtils;
import common.utils.MD5Hash;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class PeerApp {
	public static final int TIMEOUT_MILLIS = 500;

	// static fields for peer's ip, port, shared folder path, sent files, received files,
	//  tracker connection thread, p2p listener thread, torrent p2p threads
	private static String myIP;
	private static int myPort;
	private static String sharedFolderPath;
	private static Map<String, List<String>> sentFilesHashes = new HashMap<>();
	private static Map<String, List<String>> receivedFilesHashes =  new HashMap<>();

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
//		System.out.println("PeerApp ended");
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
		if (torrentP2PThreads.contains(torrentP2PThread))
			torrentP2PThreads.remove(torrentP2PThread);
	}

	public static void addTorrentP2PThread(TorrentP2PThread torrentP2PThread) {
		// 1. Check if thread is valid
		if (torrentP2PThread == null) return;
		// 2. Check if already exists
		if (torrentP2PThreads.contains(torrentP2PThread)) return;

		// 3. Add to list
		torrentP2PThreads.add(torrentP2PThread);
	}

	public static String getSharedFolderPath() {
		return sharedFolderPath;
	}

	public static void addSentFile(String receiver, String fileNameAndHash) {
		List<String> files = sentFilesHashes.get(receiver);
		if (files == null) {
			files = new ArrayList<>();
		}
		files.add(fileNameAndHash);
	}

	public static void addReceivedFile(String sender, String fileNameAndHash) {
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

	public static void requestDownload(String ip, int port, String fileName, String md5) throws Exception {
		// TODO: Implement file download from peer
		// 1. Check if file already exists
		Map<String, String> files = FileUtils.listFilesInFolder(sharedFolderPath);
		if (files.containsKey(fileName)) {
			throw new Exception("file_exists");
		}

		// 2. Create download request message
		HashMap<String, Object> body = new HashMap<>();
		body.put("name", fileName);
		body.put("md5", md5);
		body.put("receiver_ip", myIP);
		body.put("receiver_port", myPort);
		Message requestMessage = new Message(body, Message.Type.download_request);

		// 3. Connect to peer
		try (Socket peerSocket = new Socket(ip, port)) {
			peerSocket.setSoTimeout(TIMEOUT_MILLIS);

			// 4. Send request
			DataOutputStream outToPeer = new DataOutputStream(peerSocket.getOutputStream());
			outToPeer.writeUTF(JSONUtils.toJson(requestMessage));
			outToPeer.flush();

			// 5. Receive file data
			DataInputStream inFromPeer = new DataInputStream(peerSocket.getInputStream());

			// 6. Save file
			String path = sharedFolderPath + File.separator + fileName;
			File newFile = new File(path);
			BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(newFile));
			byte[] bytes = new byte[1024];
			int bytesRead;
//			System.out.println("reading file form peer: path: " + path + " file exists:" + newFile.exists());
			while ((bytesRead = inFromPeer.read(bytes)) != -1) {
//				System.out.println(new String(bytes, 0, bytesRead));
				fileOutput.write(bytes, 0, bytesRead);
			}
			fileOutput.flush();
			fileOutput.close();
			inFromPeer.close();

			// 7. Verify file integrity
			String md5OfReceivedFile = MD5Hash.HashFile(sharedFolderPath + File.separator + fileName);
			if (!md5OfReceivedFile.equals(md5))
				throw new Exception("conflict");

			// 8. Update received files list
			addReceivedFile(ip + ":" + port, fileName + " " + md5);
		}

	}
}
