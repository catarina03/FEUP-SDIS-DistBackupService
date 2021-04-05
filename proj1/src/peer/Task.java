package peer;

import files.BackupChunk;

public abstract class Task implements Runnable{
    protected Peer peer;
    protected Message message;
    protected Header header;
    protected BackupChunk chunk;

    //BACKUP TASK
    public Task(Message message) {
        this.message = message;
    }

    public Task(Peer peer, Header header, BackupChunk chunk) {
        this.peer = peer;
        this.header = header;
        this.chunk = chunk;
    }



    public Task(Peer peer, Message message) {
        this.peer = peer;
        this.message = message;
        this.header = message.header;
    }

    public Task(){}


}
