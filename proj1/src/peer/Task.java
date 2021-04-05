package peer;

import files.Chunk;

public abstract class Task implements Runnable{
    protected Peer peer;
    protected Message message;
    protected Header header;
    protected Chunk chunk;

    //BACKUP TASK
    public Task(Message message) {
        this.message = message;
    }

    public Task(Peer peer, Header header, Chunk chunk) {
        this.peer = peer;
        this.header = header;
        this.chunk = chunk;
    }



    public Task(Peer peer, Message message) {
        this.peer = peer;
        this.message = message;
    }

    public Task(){}


}
