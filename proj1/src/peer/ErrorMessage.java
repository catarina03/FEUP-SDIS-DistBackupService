package peer;

import java.io.IOException;

public class ErrorMessage extends Message {
    public ErrorMessage(){
        
    }



    public void action(){
        
    }

    @Override
    public byte[] convertToBytes() throws IOException {
        return new byte[0];
    }


}
