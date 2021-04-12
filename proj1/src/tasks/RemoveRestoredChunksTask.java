package tasks;

import files.BackupChunk;
import files.BackupFile;
import peer.Header;
import peer.Peer;

public class RemoveRestoredChunksTask extends Task {
    /**
     * RemoveRestoredTask Constructor
     * 
     * @param peer   Peer that starts the task
     * @param header Header of message received
     * @param chunk  Chunk of the message received
     */
    public RemoveRestoredChunksTask(Peer peer, Header header, BackupChunk chunk) {
        super(peer, header, chunk);
    }

    /**
     * Removes the restored file
     */
    @Override
    public void run() {
        BackupFile backupFile = this.peer.storage.files.get(this.header.fileId);
        int chunkNumber = this.peer.storage.getMaxNumberOfFileChunks(backupFile);

        for (int i = 0; i < chunkNumber; i++) {
            String chunkId = backupFile.fileId + i;
            this.peer.storage.toBeRestoredChunks.remove(chunkId);
        }
    }
}
