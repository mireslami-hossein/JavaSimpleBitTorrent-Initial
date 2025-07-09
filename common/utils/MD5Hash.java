package common.utils;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5Hash {
	public static String HashFile(String filePath) {
		// 1. Open file input stream
		try (FileInputStream fileIn = new FileInputStream(filePath)) {
			// 2. Create message digest
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			// 3. Read file in chunks and update digest
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = fileIn.read(buffer)) != -1) {
				messageDigest.update(buffer, 0, bytesRead);
			}

			// 4. Convert digest to hex string
			byte[] digest = messageDigest.digest();
			StringBuilder fileHash = new StringBuilder();
			for (byte b : digest) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) fileHash.append('0');
				fileHash.append(hex);
			}

			return fileHash.toString();
		// 5. Handle errors
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
