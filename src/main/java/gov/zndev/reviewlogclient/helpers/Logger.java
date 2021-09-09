package gov.zndev.reviewlogclient.helpers;

public class Logger {

    public static void print(String tag, String method, String msg, Exception ex){
        System.out.println(tag+" | "+method+" | Msg: "+msg);
        if(ex!=null){
           ex.printStackTrace();
        }
    }
}
