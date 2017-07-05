 
import java.util.Arrays;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Zack
 */
public class PacketTester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        byte[] data = {102, 74, -5, 49, 98, -28, -99};
        short da;
        short sa;
        short sn;
//        
//        
        byte[] packet;
//        
//        System.out.println(Arrays.toString(packet));
//        extractData = Packet.extract(packet);
//        seqNum = Packet.extractSeqNum(packet);
//        destAddr = Packet.extractDestAddr(packet);
//        sourceAddr = Packet.extractSourceAddr(packet);
//        
//        System.out.println("expected: {102,74,-5,49,98,-28,-99}, actual: "+Arrays.toString(extractData));
//        System.out.println("Expected: 2046, Actual: "+seqNum);
//        System.out.println("Expected: 720, Actual: "+sourceAddr);
//        System.out.println("Expected: 550, Actual: "+destAddr);

        Random r = new Random();
        int ds;
        byte[] tdata;
        short tda;
        short tsa;
        short tsn;
//        System.out.println(Long.MAX_VALUE*0.10);
//        System.out.println((Long.MAX_VALUE*0.10)/1000000);
//        System.out.println(922*25/60);
        for (long i = 0; i < 1000000; i++) {
            //System.out.println(i);
//            if(i == Long.MAX_VALUE*0.10)
//                System.out.println("10%");
//            
//            
            
            
            ds = r.nextInt(2039);//create random size for byte array
            data = new byte[ds];//create the array
            r.nextBytes(data);//populate it with random values

            da = (short) r.nextInt();//random dest addr
            sa = (short) r.nextInt();//random source addr
            sn = (short) r.nextInt(4096);//random seq num

            packet = Packet.toDataPacket(sn, sa, da, data);//create packet

            tdata = Packet.extract(packet);
            tsa = Packet.extractSourceAddr(packet);
            tda = Packet.extractDestAddr(packet);
            tsn = Packet.extractSeqNum(packet);

            if (tsa != sa) {
                System.err.println("source address broke");
                System.err.println("iteration " + i);
                System.err.println("Expected: " + sa + ", Actual: " + tsa);
                break;
            }
//            else{
//                System.out.print("source address--- ");
//                System.out.println(tsa+" "+sa);
//            }
            if (tda != da) {
                System.err.println("destination address broke");
                System.err.println("iteration " + i);
                System.err.println("Expected: " + da + ", Actual: " + tda);
                break;
            }
//            else{
//                System.out.print("dest address--- ");
//                System.out.println(tda+" "+da);
//            }
            if (tsn != sn) {
                System.err.println("seq number broke");
                System.err.println("iteration " + i);
                System.err.println("Expected: " + sn + ", Actual: " + tsn);
                break;
            }
//            else{
//                System.out.print("seq number--- ");
//                System.out.println(tsn+" "+sn);
//            }
            for (int j = 0; j < data.length; j++) {
                if (data[j] != tdata[j]) {
                    System.err.println("extract data broke--- byte number "+j);
                    System.err.println("iteration " + i);
                    System.err.println("Expected: " + data[j] + ", Actual: " + tdata[j]);
                    break;
                }
            }

        }

    }

}
