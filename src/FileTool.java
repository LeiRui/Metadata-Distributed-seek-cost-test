import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/*
    主要工作类
 */
public class FileTool {
    private Parameter parameter;
    public StripesIndex stripesIndex;
    private String comFilePath;
    private String disFilePath;

    FileTool(Parameter parameter) {

        this.parameter=parameter;
        stripesIndex = new StripesIndex();
    }

    /*
        根据parameter生成两种文件并写入到磁盘中，同时分散模式下附带实例化stripeIndex索引对象

        comPath:完整模式文件路径
        disPath:分散模式文件路径
     */
    public void fileGenerator(String comPath, String disPath) throws IOException {
        this.comFilePath = comPath;
        this.disFilePath = disPath;

        File comFile = new File(comPath);
        File disFile = new File(disPath);
        if(comFile.exists()){
            comFile.delete();
        }
        if(disFile.exists()){
            disFile.delete();
        }
        long stripeNum = parameter.getStripeNum();
        long stripeSize = parameter.getStripeSize();
        long intervalSize = parameter.getIntervalSize();

        byte[] nonblockContent;
        byte[] blockContent;

        //写完整模式的文件
        long nonblockSize = intervalSize*50;
        long blockSize = stripeNum*stripeSize;
        nonblockContent = new byte[(int)(nonblockSize)]; // 默认全0
        blockContent = new byte[(int)(blockSize)]; // 默认全0
        for(int i=0;i<nonblockSize;i++) {
            nonblockContent[i] = "h".getBytes()[0];
        }
        for(int i=0; i < blockSize; i++) {
            blockContent[i] = "h".getBytes()[0];
        }
        FileOutputStream comfos = new FileOutputStream(comFile);
        comfos.write(nonblockContent);
        comfos.write(blockContent);
        comfos.close();

        //写分散模式的文件,同时实例化索引对象
        System.out.println("开始写分散模式文件...");
        long index = 0;
        long stripeNumCount = 0;
        FileOutputStream disfos = new FileOutputStream(disFile);
        while(stripeNumCount < stripeNum) {
            disfos.write(Arrays.copyOfRange(nonblockContent, 0, (int)intervalSize));
            index+=intervalSize;
            this.stripesIndex.addPair(index, stripeSize);
            disfos.write(Arrays.copyOfRange(blockContent, 0, (int)stripeSize));
            index+=stripeSize;
            stripeNumCount++;
        }
        disfos.close();

    }

    /*
        测试从磁盘中读取完整模式文件的block的时间代价
     */
    public long testComplete() throws IOException{

        long blockSize = parameter.getBlockSize();

        RandomAccessFile raf = new RandomAccessFile(comFilePath, "r");
        raf.seek(raf.length()); // note here!


        long startTime = System.currentTimeMillis(); //程序开始记录时间

        raf.seek(raf.length()-blockSize);
        byte[] buf = new byte[(int)blockSize];
        raf.read(buf);

        long endTime = System.currentTimeMillis(); //程序开始记录时间

        raf.close();

        return endTime-startTime;


    }

    /*
        测试从磁盘中读取完整模式文件的block的时间代价，不包括读取数据的时间
    */
    public long testCompleteOnlySeek() throws IOException{
        RandomAccessFile raf = new RandomAccessFile(comFilePath, "r");
        raf.seek(raf.length());

        long blockSize = parameter.getBlockSize();
        long startTime = System.currentTimeMillis(); //程序开始记录时间
        raf.seek(raf.length()-blockSize);
        long endTime = System.currentTimeMillis(); //程序开始记录时间

        raf.close();

        return endTime-startTime;

    }

    /*
        测试从磁盘中读取分散模式文件的block的时间代价，
        其中在内存中的stripeIndex是辅助读取的索引对象。
     */
    public long[] testDistributed() throws Exception {
        long[] res= new long[2];
        RandomAccessFile raf = new RandomAccessFile(disFilePath, "r");
        long offset,len;

        raf.seek(raf.length()); // note here
        stripesIndex.pointer=0; // note here

        long length = 0;
        boolean firstflag = false;
        long startTime = System.currentTimeMillis(); //程序开始记录时间
        while(stripesIndex.hasNext()) {
            offset = stripesIndex.getNextOffset();
            len = stripesIndex.getNextLen();
            stripesIndex.pointer++; // note here
            raf.seek(offset);
            if(!firstflag){
                res[0]=System.currentTimeMillis()-startTime; // 从文件末尾seek到第一个位置的时间
                firstflag=true;
            }
            byte[] buf = new byte[(int)len];
            raf.read(buf);
            length += len;
            //System.out.println(offset);
        }
        long endTime = System.currentTimeMillis(); //程序开始记录时间
        res[1]=endTime-startTime;

        raf.close();

        return res;
    }

    /*
    测试从磁盘中读取分散模式文件的block的时间代价，不包括读取数据的时间
    其中在内存中的stripeIndex是辅助读取的索引对象。
 */
    public long testDistributedOnlySeek() throws IOException {
        //long[] res= new long[2];
        RandomAccessFile raf = new RandomAccessFile(disFilePath, "r");
        long offset,len;
        boolean firstflag=false;

        long startTime=0;

        stripesIndex.pointer=0; // note here

        while(stripesIndex.hasNext()) {
            offset = stripesIndex.getNextOffset();
            stripesIndex.pointer++; // note here
            raf.seek(offset);
            if(!firstflag){
                firstflag=true;
                startTime = System.currentTimeMillis(); //程序开始记录时间
            }
        }
        long endTime = System.currentTimeMillis(); //程序开始记录时间
        raf.close();
        return endTime-startTime;
    }


}
