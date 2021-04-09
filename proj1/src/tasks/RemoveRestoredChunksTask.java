package tasks;

import files.BackupChunk;
import files.BackupFile;
import peer.Header;
import peer.Peer;

public class RemoveRestoredChunksTask extends Task{
    public RemoveRestoredChunksTask(Peer peer, Header header, BackupChunk chunk) {
        super(peer, header, chunk);
    }

    @Override
    public void run() {
        BackupFile backupFile = this.peer.storage.files.get(this.header.fileId);
        int chunkNumber = this.peer.storage.getMaxNumberOfFileChunks(backupFile);

        for (int i = 0; i < chunkNumber; i++){
            String chunkId = backupFile.fileId + i;
            this.peer.storage.toBeRestoredChunks.remove(chunkId);
        }
    }
}
