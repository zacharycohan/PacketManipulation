
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This class provides various methods to help in the construction and
 * deconstruction of Link Layer frames
 *
 * @author Zachary Cohan
 *
 */
public class Packet {

//maximum size of data that a packet can hold
    protected static final int MAX_DATA_SIZE = 2038;

//describes different packet types
    public enum Type {
        ACK, BEACON, DATA, CTS, RTS, CORRUPT;
    }

    /**
     * takes a frame and extracts the destination address and returns it as a
     * short
     *
     * @param t the entire frame
     * @return the destination address within the frame
     */
    public static short extractDestAddr(byte[] t) {

        int a = t[2];
        int b = t[3];
        
        a = a & 0x000000FF;
        a = a << 8;
        b = b & 0x000000FF;
        a += b;
        return (short)a;
    }

    /**
     * takes a frame and extracts the source address and returns it as a short
     *
     * @param t the entire frame
     * @return the source address within the frame
     */
    public static short extractSourceAddr(byte[] t) {

        int a = t[4];
        int b = t[5];
        
        a = a & 0x000000FF;
        a = a << 8;
        b = b & 0x000000FF;
        a += b;
        return (short)a;
    }

    /**
     * extracts the data from a frame and returns it as a byte array
     *
     * @param t the entire frame
     * @return the data contained within the frame
     */
    public static byte[] extract(byte[] t) {
        int dlength = t.length - 10;
        byte[] data = new byte[dlength];

        for (int i = 6; i < t.length - 4; i++) {
            data[i - 6] = t[i];
        }

        return data;
    }

    /**
     * extracts the sequence number from a frame
     *
     * @param t the entire frame
     * @return the sequence number
     */
    public static short extractSeqNum(byte[] t) {
        int a = t[0];
        int b = t[1];
// System.out.println("init A representation as int bs: "+Integer.toBinaryString(a));
// System.out.println("init B representation as int bs: "+Integer.toBinaryString(b));

        a = a << 8;
        a = a & 0x00000F00;
        b = b & 0x000000FF;
// System.out.println("after shift, A representation as int bs: "+Integer.toBinaryString(a));
// System.out.println("after shift, B representation as int bs: "+Integer.toBinaryString(b));

        int c = a + b;

// System.out.println("c val after adding: "+c);
// System.out.println("c bs after adding: "+Integer.toBinaryString(c));
        return (short) c;
    }

    /**
     * creates a data packet from the arguments provided
     *
     *
     *
     * @param seqNum the sequence number of the packet
     * @param sourceAddr the source from which the packet will be sent
     * @param destAddr the destination to which the packet will be sent
     * @param data the data contained within the packet
     * @return the built data packet
     */
    public static byte[] toDataPacket(short seqNum, short sourceAddr, short destAddr,
            byte[] data) {
        ByteBuffer buf;
// allocate based on the size of the data
        if (data.length < MAX_DATA_SIZE) {
            buf = ByteBuffer.allocate(data.length + 10);
        } else {
            buf = ByteBuffer.allocate(MAX_DATA_SIZE + 10);
        }
        if (seqNum >= 4096 || seqNum < 0) {
            throw new IllegalArgumentException();//check to make sure sequence number is within the appropriate bounds
        }
        buf.order(ByteOrder.BIG_ENDIAN);
//put control byte.
        int a = seqNum >>> 8;//shift over 8 bits to get the first byte
        int b = seqNum;
        b = b & 0x000000FF;
        buf.put((byte) a);
        buf.put((byte) b);
        buf.putShort(destAddr);
        buf.putShort(sourceAddr);

// if the data is less than the max data size
        if (data.length < MAX_DATA_SIZE) {
            for (int i = 0; i < data.length; i++) {
                buf.put(data[i]);
            }
        } else// otherwise, go from 0-MAX_DATA_SIZE
        {
            for (int i = 0; i < MAX_DATA_SIZE; i++) {
                buf.put(data[i]);
            }
        }
        buf.putInt(-1);
        return buf.array();
    }

    /**
     * builds an ACK packet out the the arguments provided
     *
     * @param seqNum the sequence number of the packet being acknowledged
     * @param sourceAddr the source from which the packet will be sent
     * @param destAddr the source to which the packet will be sent
     * @return the build ACK packet
     */
    public static byte[] toACKPacket(short seqNum, short sourceAddr, short destAddr) {

        ByteBuffer buf;
        buf = ByteBuffer.allocate(10);
        buf.order(ByteOrder.BIG_ENDIAN);
//put control byte.
        int a = seqNum >>> 8;//shift over 8 bits to get the first byte
        a += 32;
        int b = seqNum;
        b = b & 0x000000FF;
        buf.put((byte) a);
        buf.put((byte) b);
        buf.putShort(destAddr);
        buf.putShort(sourceAddr);
        buf.putInt(-1);
        return buf.array();
    }

    /**
     * determines whether or not a packet is a retransmission
     *
     * @param t the entire packet
     * @return whether the packet is a retransmission
     */
    public static boolean isRetry(byte[] t) {
        int a = t[0];
        a = a & 0x00000010;
        a = a >>> 4;

        return (a == 1);
    }

    /**
     * determines the type of packet that has passed in
     *
     * @param t the entire packet
     * @return the type of packet (See the Packet.Type enumerated type)
     */
    public static Type getType(byte[] t) {

        int a = t[0];
        a = a & 0x000000F0;
        a = a >>> 5;
        if (a == 0) {
            return Packet.Type.DATA;
        } else if (a == 1) {
            return Packet.Type.ACK;
        } else if (a == 2) {
            return Packet.Type.BEACON;
        } else if (a == 4) {
            return Packet.Type.CTS;
        } else if (a == 5) {
            return Packet.Type.RTS;
        } else {
            return Packet.Type.CORRUPT;
        }

    }

    /**
     * sets the retry bit of a packet
     *
     * @param t the entire packet that does not have the retry bit set
     * @return the modified packet
     */
    public static byte[] setRetry(byte[] t) {
        int a = t[0];
        a = a & 0x00000010;
        if (a == 1) {
            return t;
        } else {
            t[0] += 16;
        }
        return t;
    }

    /**
     * for testing purposes
     *
     * @param myString
     * @return
     */
    public static int toInt(String myString) {
        int n = 0;
        for (int j = 0; j < myString.length(); j++) {
            n *= 2;
            n += myString.charAt(j) == '0' ? 0 : 1;
        }
        return n;
    }

}
