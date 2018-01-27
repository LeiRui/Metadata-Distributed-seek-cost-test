public class Parameter {
    private long stripeNum;
    private long stripeSize;
    private long intervalSize;

    public long getStripeNum() {
        return stripeNum;
    }
    public long getStripeSize() {
        return stripeSize;
    }
    public long getIntervalSize(){
        return intervalSize;
    }

    public long getBlockSize() {
        return stripeSize*stripeNum;
    }
    Parameter(long stripeNum, long stripSize, long intervalSize){
        this.stripeNum = stripeNum;
        this.stripeSize=stripSize;
        this.intervalSize=intervalSize;
    }
    public String toString() {
        return new String("stripeNum="+stripeNum
                +", stripeSize="+stripeSize+" (blockSize="+getBlockSize()+"), intervalSize="+intervalSize);
    }
}
