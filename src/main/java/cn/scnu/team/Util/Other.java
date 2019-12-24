package cn.scnu.team.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Other {
    public static String timeStamp2Date(String seconds,String format) {//时间格式转换
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds)));
    }
}
