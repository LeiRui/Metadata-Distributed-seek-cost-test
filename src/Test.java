import java.io.File;
import java.io.PrintStream;

public class Test {
    public static void main(String[] args) throws Exception {
        File result = new File("files/newresults2.csv");
        if(result.exists()){
            result.delete();
        }
        PrintStream out = new PrintStream(result);
        out.println("stripeNum,stripeSize,blockSize,intervalSize,,completeTime" +
                ",distributedTime(total),distributedTimefirstSeek" +
                ",comSeekTime" +
                ",disSeekTime,seek length(include),seek length(exclude)");

        //第一组实验：改变stripeSize
        /*
        long[] stripeNumTest = new long[]{500,1000,5000,10000,50000,       50000,50000, 50000, 50000, 50000, 50000};
        long[] stripeSizeTest = new long[]{100000,50000,10000,5000,1000,   1000, 1000, 1000, 1000, 1000, 1000};
        long[] intervalSizeTest = new long[]{10000,10000,10000,10000,10000,  100, 500, 1000, 5000, 20000, 50000};
        */
        long[] stripeNumTest = new long[]{50000, 100000, 150000, 200000,      50000, 50000,50000,50000,       50000,50000,50000,50000   };
        long[] stripeSizeTest = new long[]{1000, 1000, 1000, 1000,          5000, 8000,10000,20000,           1000,1000,1000,1000};
        long[] intervalSizeTest = new long[]{10000, 10000, 10000, 10000,     10000, 10000,10000,10000,        10000,15000,20000,50000};
        for (int k = 0; k < stripeSizeTest.length; k++) {
            for(int loop = 0; loop <1; loop++) {
                //step 1 构造参数对象
                long stripeNum = stripeNumTest[k];
                long stripeSize = stripeSizeTest[k];
                long intervalSize = intervalSizeTest[k];
                Parameter parameter = new Parameter(stripeNum, stripeSize, intervalSize);

                //step 2 构造FileTool对象
                FileTool fileTool = new FileTool(parameter);

                //step 3 生成并写文件
                String comPath = "files/complete.txt";
                String disPath = "files/distributed.txt";
                fileTool.fileGenerator(comPath, disPath);
                Thread.sleep(1000);

                File f = new File(disPath);
                long seekLengthInclude = fileTool.stripesIndex.costInclude(f.length());// note 这一步得在此时执行 因为后面会把index清空
                long seekLengthExclude = fileTool.stripesIndex.costExclude();// note 这一步得在此时执行 因为后面会把index清空

                //step 4 文件读取代价测试
                long com = fileTool.testComplete();
                Thread.sleep(1000);
                long dis[] = fileTool.testDistributed();
                Thread.sleep(1000);
                long comOnly = fileTool.testCompleteOnlySeek();
                Thread.sleep(1000);
                long disOnly = fileTool.testDistributedOnlySeek();
                Thread.sleep(1000);

                //step 5 返回结果

                out.println(stripeNum+","+stripeSize + "," +stripeNum*stripeSize+","+ intervalSize + ",," + com + ","
                        + dis[1] + "," + dis[0] + "," + comOnly + "," + disOnly + "," + seekLengthInclude + "," + seekLengthExclude);
            }
            out.println("");
        }

        /*
        //第二组实验：改变intervalSize
        //long[] intervalSizeTest = new long[]{10, 100, 500, 1000, 10000, 100000, 500000, 1000000, 5000000, 10000000, 50000000};
        long[] intervalSizeTest = new long[]{10000};
        for (int k = 0; k < intervalSizeTest.length; k++) {
            //step 1 构造参数对象
            long blockSize = 50000000;
            long nonblockSize = 500000000;
            long stripeSize = 1000;
            long intervalSize = intervalSizeTest[k];
            Parameter parameter = new Parameter(blockSize, nonblockSize, stripeSize, intervalSize);

            //step 2 构造FileTool对象
            FileTool fileTool = new FileTool(parameter);

            //step 3 生成并写文件
            String comPath = "files/complete.txt";
            String disPath = "files/distributed.txt";
            fileTool.fileGenerator(comPath, disPath);
            Thread.sleep(10000);

            File f = new File(disPath);
            long seekLengthInclude = fileTool.stripesIndex.costInclude(f.length());// note 这一步得在此时执行 因为后面会把index清空
            long seekLengthExclude = fileTool.stripesIndex.costExclude();// note 这一步得在此时执行 因为后面会把index清空


            //step 4 文件读取代价测试
            Thread.sleep(10000);
            long com = fileTool.testComplete();
            Thread.sleep(10000);
            long []dis = fileTool.testDistributed();
            Thread.sleep(10000);
            long comOnly = fileTool.testCompleteOnlySeek();
            Thread.sleep(10000);
            long disOnly = fileTool.testDistributedOnlySeek();
            Thread.sleep(10000);

            //step 5 返回结果
            out.println(blockSize + "," + nonblockSize + "," + stripeSize + "," + intervalSize + ",," + com + ","
                    + dis[1] +","+dis[0]+ "," + comOnly + "," + disOnly + "," + seekLengthInclude + "," + seekLengthExclude);
        }
        */
        out.close();

               /*
            System.out.println(parameter);
            System.out.println("com: " + com);
            System.out.println("dis: " + dis);
            System.out.println("com only seek: " + comOnly);
            System.out.println("dis only seek: " + disOnly);
            System.out.println("dis/com = " + (double) dis / com);
            System.out.println("dis/com = " + (double) disOnly / comOnly);
            System.out.println("seek length(include) =" + seekLengthInclude);
            System.out.println("seek length(exclude) =" + seekLengthExclude);
            */
    }
}


