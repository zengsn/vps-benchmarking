package com.kang.estimate.util;

import com.kang.estimate.core.error.BussinessException;
import com.kang.estimate.core.error.EmBussinessError;
import com.kang.estimate.module.monitor.entity.Stats;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 叶兆康
 */
public class Common {

    static Pattern p = Pattern.compile("\\s*|\t|\r|\n");

    public static boolean ping(String ipAddress) throws Exception {
        int  timeOut =  3000 ;
        boolean status = InetAddress.getByName(ipAddress).isReachable(timeOut);
        // 当返回值是true时，说明host是可用的，false则不可。
        return status;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static int binarySearch(int target,int[] array){
        int low = 0;
        int high = array.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (array[mid] > target) {
                high = mid - 1;
            }
            else if (array[mid] < target){
                low = mid + 1;
            } else{
                return mid;
            }
        }
        return low;
    }

    public static int[] bubbleSort(int[] array){
        for(int i=0;i<array.length;i++){
            for(int j=0;j<array.length-i-1;j++){
                if(array[j+1]<array[j]){
                    int temp=array[j+1];
                    array[j+1]=array[j];
                    array[j]=temp;
                }
            }
        }
        return array;
    }

    /**
     * 计算两个时间差，返回为分钟。
     * @param time1
     * @param time2
     * @return
     */
    public static long calTime(String time1, String time2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long minutes = 0L;
        try {
            Date d1 = df.parse(time1);
            Date d2 = df.parse(time2);
            // 这样得到的差值是微秒级别
            long diff = d1.getTime() - d2.getTime();
            minutes = diff / (1000 * 60);
        } catch (ParseException e) {
            System.out.println("抱歉，。");
            throw new BussinessException(EmBussinessError.TIME_FORMATE_ERROR);
        }
        return Math.abs(minutes);
    }

    /**
     * 获得当前时间的前N小时
     * @param hour
     * @return
     */
    public static String getBeforeByHourTime(int hour){
        String returnStr = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hour);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        returnStr = df.format(calendar.getTime());
        return returnStr;
    }

    /**
     * 将Ftp的输出转为行
     * @param raw
     * @return
     */
    public static List<String> ftpOutPutToRow(StringBuilder raw){
        return Arrays.asList(raw.toString().split("\n"));
    }

    /**
     * 检查实体类是否全都有值
     * @param obj
     * @return
     */
    public static boolean isAllFieldNotNull(Object obj){
        // 得到类对象
        Class stuCla = (Class) obj.getClass();
        // 得到属性集合
        Field[] fs=stuCla.getDeclaredFields();
        boolean flag =true;
        for(Field f : fs) {
            // 设置属性是可以访问的(私有的也可以)
            f.setAccessible(true);
            // 得到此属性的值
            Object val= null;
            try {
                val = f.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new BussinessException(EmBussinessError.UNKNOW_ERROR);
            }
            // 只要有1个属性为空,返回
            if(val==null){
                flag=false;
                break;
            }
        }
        return flag;
    }

    /**
     * 将Ftp输出的结果转换为Stats类
     * @param rawData
     * @return
     */
    public static List<Stats> convertToStats(StringBuilder rawData){
        List<String> rows=ftpOutPutToRow(rawData);
        List<Stats> statsList=new ArrayList<>();
        for(String row:rows){
            String[] items=row.split("&");
            Stats stats=new Stats();
            for(String item:items){
                String[] keyValue=item.split("~");
                switch (keyValue[0]){
                    case "Time":
                        stats.setTime(keyValue[1]);
                        break;
                    case "CPU Usage":
                        stats.setCpuUsage(keyValue[1]);
                        break;
                    case "MEMORY Usage":
                        stats.setMemoryUsage(keyValue[1]);
                        break;
                    case "DISK Usage":
                        stats.setDiskUsage(keyValue[1]);
                        break;
                    default:
                        break;
                }
            }
            if(isAllFieldNotNull(stats)){
                statsList.add(stats);
            }
        }
        return statsList;
    }

    public static BigDecimal convertMemoryUsage(String raw){
        String[] item=raw.split("/");
        BigDecimal available=new BigDecimal(item[0]);
        BigDecimal total=new BigDecimal(item[1]);
        BigDecimal used=total.subtract(available);
        BigDecimal usage=used.divide(total,2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
        return usage;
    }

    public static void main(String[] args) throws Exception {
//        int[] array={1,8,2,1,7,3,2,6,7,5,8};
        List<List<String>> sheet1 = ExcelUtil.readExcel("C:\\Users\\hasee\\Desktop\\客户.xls",0);
        List<List<String>> sheet2 = ExcelUtil.readExcel("C:\\Users\\hasee\\Desktop\\客户.xls",1);
        Map<String,String> companyName2Id=new HashMap<>();
        for(List<String> list:sheet1){
            companyName2Id.put(list.get(2),list.get(0));
        }
        for(List<String> list:sheet2){
            list.set(0,companyName2Id.get(list.get(0)));
        }
        ExcelUtil.writeExcel(sheet2, "C:\\Users\\hasee\\Desktop\\customer_contact.xls");
    }
}
