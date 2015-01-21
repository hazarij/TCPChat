/*
 * Message Format
 * DATA:   [0x01] [unameLength] [uname]       [messageLength] [message]       [timeInMillis] [0x02]
 * LENGTH: [1]    [4]           [unameLength] [4]             [messageLength] [8]            [1]
 */
public class Message {
	private String username;
	private String message;
	private long timestamp;
	private byte[] bytes;
	
	public Message (String username, String message, long timestamp) {
		this.username = username;
		this.message = message;
		this.timestamp = timestamp;
		dataToBytes();
	}
	
	public Message (byte[] bytes) {
		this.bytes = bytes;
		bytesToData();
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getMessage() {
		return message;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	private void dataToBytes() {
		byte[] unameBytes = username.getBytes();
		byte[] unameLengthBytes = ByteUtils.intToBytes(unameBytes.length);
		byte[] messageBytes = message.getBytes();
		byte[] messageLengthBytes = ByteUtils.intToBytes(messageBytes.length);
		byte[] timestampBytes = ByteUtils.longToBytes(timestamp);
		
		byte[] result = new byte[1+4+unameBytes.length+4+messageBytes.length+8+1];
		
		result[0] = 0x01;
		
		int i = 1;
		for (int j = 0; j < 4; j++) {
			result[i] = unameLengthBytes[j];
			i++;
		}
		
		for (int j = 0; j < unameBytes.length; j++) {
			result[i] = unameBytes[j];
			i++;
		}
		
		for (int j = 0; j < 4; j++) {
			result[i] = messageLengthBytes[j];
			i++;
		}
		
		for (int j = 0; j < messageBytes.length; j++) {
			result[i] = messageBytes[j];
			i++;
		}
		
		for (int j = 0; j < 8; j++) {
			result[i] = timestampBytes[j];
			i++;
		}
		
		result[i] = 0x02;
		
		this.bytes = result;
	}
	
	private void bytesToData() {
		int i = 1;
		
		byte[] unameLengthBytes = new byte[4];
		for (int j = 0; j < 4; j++) {
			unameLengthBytes[j] = this.bytes[i];
			i++;
		}
		int unameLength = ByteUtils.bytesToInt(unameLengthBytes);
		
		byte[] unameBytes = new byte[unameLength];
		for (int j = 0; j < unameLength; j++) {
			unameBytes[j] = this.bytes[i];
			i++;
		}
		this.username = new String(unameBytes);
		
		byte[] messageLengthBytes = new byte[4];
		for (int j = 0; j < 4; j++) {
			messageLengthBytes[j] = this.bytes[i];
			i++;
		}
		int messageLength = ByteUtils.bytesToInt(messageLengthBytes);
		
		byte[] messageBytes = new byte[messageLength];
		for (int j = 0; j < messageLength; j++) {
			messageBytes[j] = this.bytes[i];
			i++;
		}
		this.message = new String(messageBytes);
		
		byte[] timestampBytes = new byte[8];
		for (int j = 0; j < 8; j++) {
			timestampBytes[j] = this.bytes[i];
			i++;
		}
		this.timestamp = ByteUtils.bytesToLong(timestampBytes);
	}
}
