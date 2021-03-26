package peer;


public class PutchunkMessage extends Message{
    //public String header;
    //public String body;

    public PutchunkMessage(Header header, String body, String address, int port){
        //this.type = "PUTCHUNK";

        //TO DO: Parse message 

        //this.header = header;
        //this.body = body;
        super(header, body, address, port);
    }

    

   /*
    public String getHeader(){
        return this.header;
    }
    */

    //parsing message to split between header and body
    

    public void action(){
        //TO DO: Actual backup protocol
        BackupTask task = new BackupTask(this.header, this.body, this.address, this.port);
    }


}

