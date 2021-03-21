public class File {
    public Path filePath;

    public File(String filename){
        this.filePath = Paths.get(filename);
    }






    private void generateFileId() {
        try {
            BasicFileAttributes fileMetadata = Files.readAttributes(filePath, BasicFileAttributes.class);
            String hashMetadata = filename + fileMetadata.creationTime() + fileMetadata.
        } catch (Exception e) {
            //TODO: handle exception
        }

    }
}
