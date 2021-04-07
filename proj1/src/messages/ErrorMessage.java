package messages;

import java.io.IOException;

public class ErrorMessage extends Message {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ErrorMessage(){
        
    }



    public void action(){
        
    }

    @Override
    public byte[] convertToBytes() throws IOException {
        return new byte[0];
    }


}
