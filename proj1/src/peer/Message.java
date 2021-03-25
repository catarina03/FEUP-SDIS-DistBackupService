package peer;

public class Message {
    

    public String type = "NONE";
    public String address;
    public int port;

    public Message(String m, String address, int port){
        this.type = "PUTCHUNK";
        this.address = address;
        this.port = port;
    };

    /*
    abstract public String getHeader();
    abstract public void action(String message);
    */


    public String getHeader(){
        return "PUTCHUNK";
    };
    public void action(String message){
        //só a ver
    };
    
}
