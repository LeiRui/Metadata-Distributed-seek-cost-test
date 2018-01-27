import java.util.ArrayList;
import java.util.Iterator;

/*
    used to index stripes in a file
 */
public class StripesIndex {
    private ArrayList<Long> offset; // 按照从小到大的顺序
    private ArrayList<Long> len;
    public long pointer;
    StripesIndex(){
        offset = new ArrayList<>();
        len = new ArrayList<>();
        pointer = 0;
    }

    public void addPair(long offset, long len){
        this.offset.add(offset);
        this.len.add(len);
    }


    public boolean hasNext() {

        //return offset.size()>0 ? true : false;
        if(pointer < offset.size())
            return true;
        else
            return false;
    }

    public long getNextOffset() { // 该函数在hasNext()为true之后调用
        /*
        Long res = offset.get(0);
        offset.remove(0);
        return res;
        */
        Long res = offset.get((int)pointer);
        return  res;
    }
    public long getNextLen() { // 该函数在hasNext()为true之后调用
        /*
        Long res = len.get(0);
        len.remove(0);
        return res;
        */
        Long res = len.get((int)pointer);
        return  res;
    }


    /*
        计算seek长度
        移动到最前面第一个offset的seek长度不包括在内
        于是seek长度等于stripes之间间隔的长度，两个stripes之间的间隔长度等于(offset2-(offset1+len1)-1)
    */
    public long costExclude() {
        long res=0;
        if(this.offset.size() <= 1) {
            return 0;
        }
        Iterator offset_iter = offset.iterator();
        Iterator len_iter = len.iterator();
        long old_offset = (long)offset_iter.next();
        long old_len = (long)len_iter.next();
        long new_offset,new_len;
        while(offset_iter.hasNext()){
            new_offset = (long)offset_iter.next();
            new_len = (long)len_iter.next();

            long tmp = (new_offset-(old_offset+old_len-1)) >0 ? new_offset-(old_offset+old_len-1)-1:0 ;
            res += tmp;
            //System.out.println(tmp);
            old_offset = new_offset;
            old_len = new_len;
        }
        return res;

    }

    /*
        计算seek长度
        移动到最前面第一个offset的seek长度也计算在内，等于(fileSzie-offset)
        这种cost计算方式限制默认了读取顺序：从文件末尾读取fileMetadata之后，从最接近文件开头的位置读起
     */
    public long costInclude(long fileSize) {
        long res = costExclude();
        if(offset.size()<=0) {
            return res;
        }
        res += fileSize-offset.get(0);
        return res;

    }

}
