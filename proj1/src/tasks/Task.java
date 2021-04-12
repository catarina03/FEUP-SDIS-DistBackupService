package tasks;

import files.BackupChunk;
import messages.Message;
import peer.Header;
import peer.Peer;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class Task implements Runnable{
    protected Peer peer;
    protected Message message;
    protected Header header;
    protected BackupChunk chunk;
    protected ScheduledThreadPoolExecutor scheduler;
    protected final int NUMBER_OF_WORKERS = 5;

    /**
     * Task Constructor
     * @param message  Message received
     */
    public Task(Message message) {
        this.message = message;
    }

    /**
     * Task Constructor
     * @param peer  Peer who received the message
     * @param header  Header of message received
     * @param chunk  Chunk of message received
     */
    public Task(Peer peer, Header header, BackupChunk chunk) {
        this.peer = peer;
        this.header = header;
        this.chunk = chunk;
    }

    /**
     * Task Constructor
     * @param peer  Peer that received the message
     * @param message  Message received
     */
    public Task(Peer peer, Message message) {
        this.peer = peer;
        this.message = message;
        this.header = message.header;
    }

    /**
     * Task Empty Constructor
     */
    public Task(){}
}