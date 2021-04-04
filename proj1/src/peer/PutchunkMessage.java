package peer;


import files.Chunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PutchunkMessage extends Message{
    //public String header;
    //public String body;

    public PutchunkMessage(Header header, Chunk body, String address, int port){
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
        System.out.println("REACHED PUTCHUNK ACTION\n");
        //TO DO: Actual backup protocol
        //BackupTask task = new BackupTask(this.header, this.body, this.address, this.port);
    }



    public byte[] convertToBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(header.toString().getBytes());
        stream.write(doubleCRLF.getBytes());
        stream.write(body.body);
        return stream.toByteArray();
    }


}

