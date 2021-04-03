package files;

public class File {
    //public Path filePath;
    public String filename;

    public File(String filename){
        //this.filePath = Paths.get(filename);
        this.filename = filename;
    }





/*
    private void generateFileId() {
        try {
            BasicFileAttributes fileMetadata = Files.readAttributes(filePath, BasicFileAttributes.class);
            String hashMetadata = filename + fileMetadata.creationTime() + fileMetadata.lastModifiedTime() + fileMetadata.getOwner();
        } catch (Exception e) {
            //TODO: handle exception
        }

    }
    */
}
