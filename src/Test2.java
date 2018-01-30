import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class Test2 {
    public static void main(String[] args) throws Exception {
        FileOutputStream comfos = new FileOutputStream(new File("files/test.txt"));
        long blockSize=50000000;
        byte[] blockContent = new byte[(int)blockSize];
        comfos.write(blockContent);
        comfos.close();

        RandomAccessFile raf = new RandomAccessFile("files/test.txt", "r");
        byte[] buf = new byte[(int)blockSize];
        long startTime = System.currentTimeMillis(); //程序开始记录时间
        raf.read(buf);
        long endTime = System.currentTimeMillis(); //程序开始记录时间
        System.out.println("1: "+(endTime-startTime));
        raf.close();


        int[] loop = {1,10,20,50,100,500,1000,5000,10000,20000,50000,100000,200000,500000,1000000};
        //int[] loop = {1000};
        for(int k=0;k<loop.length;k++) {
            RandomAccessFile raf2 = new RandomAccessFile("files/test.txt", "r");
            byte[] buf2 = new byte[(int) blockSize / loop[k]];
            long startTime2 = System.currentTimeMillis(); //程序开始记录时间
            for (int i = 0; i < loop[k]; i++) {
                raf2.read(buf2);
                //System.out.println(raf2.getFilePointer());
            }
            long endTime2 = System.currentTimeMillis(); //程序开始记录时间
            System.out.println(loop[k]+","+(endTime2-startTime2));
            raf2.close();
        }
    }
}
