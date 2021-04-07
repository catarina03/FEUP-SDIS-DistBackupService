package peer;

import files.BackupChunk;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class Task implements Runnable{
    protected Peer peer;
    protected Message message;
    protected Header header;
    protected BackupChunk chunk;
    protected ScheduledThreadPoolExecutor scheduler;
    protected final int NUMBER_OF_WORKERS = 5;

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
