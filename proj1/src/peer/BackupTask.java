package peer;

public class BackupTask implements Runnable{

    public Header header;
    public String messageBody;
    public String address;
    public int port;

    public BackupTask(Header header, String messageBody, String address, int port){
        this.header = header;
        this.messageBody=messageBody;
        this.address=address;
        this.port=port;
    }


    public void run(){

        System.out.println("TO DO: Running backup task");

        //Ve se o ficheiro nao é seu, se não for:
        //Esperar 0 - 400 ms
        //Guarda o chunk se o replication degree ainda nao for suficiente
        //Manda o STORED
    }


}
