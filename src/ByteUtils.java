import java.nio.ByteBuffer;

public class ByteUtils {
    //private static ByteBuffer longBuffer = ByteBuffer.allocate(Long.SIZE/8);
    //private static ByteBuffer intBuffer = ByteBuffer.allocate(Integer.SIZE/8);

    public static byte[] longToBytes(long x) {
    	ByteBuffer longBuffer = ByteBuffer.allocate(Long.SIZE/8);
        longBuffer.putLong(0, x);
        return longBuffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
    	ByteBuffer longBuffer = ByteBuffer.allocate(Long.SIZE/8);
        longBuffer.put(bytes, 0, bytes.length);
        longBuffer.flip();//need flip 
        return longBuffer.getLong();
    }
    
    public static byte[] intToBytes(int x) {
    	ByteBuffer intBuffer = ByteBuffer.allocate(Integer.SIZE/8);
    	intBuffer.putInt(0, x);
    	return intBuffer.array();
    }
    
    public static int bytesToInt(byte[] bytes) {
    	ByteBuffer intBuffer = ByteBuffer.allocate(Integer.SIZE/8);
    	intBuffer.put(bytes, 0, bytes.length);
    	intBuffer.flip();
    	return intBuffer.getInt();
    }
    
    public static String bytesToIntArray (byte[] bytes) {
    	String ints = "";
    	for (int i = 0; i < bytes.length; i++) {
    		ints += bytes[i] + " ";
    	}
    	
    	return ints;
    }
}