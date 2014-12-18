package util;

import play.mvc.Http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * FileUploader
 *
 * @author maasdianto
 *         create on 12/14/2014
 */
public class FileUploader {

    private String directory = "";
    private String fileExtension = "";
    private String fileName = "";
    private Http.MultipartFormData.FilePart file;

    public FileUploader(Http.MultipartFormData.FilePart file) {
        assert file != null;
        this.file = file;
        this.fileName = file.getFilename();
        this.fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public Http.MultipartFormData.FilePart getFile() {
        return file;
    }

    public void setFile(Http.MultipartFormData.FilePart file) {
        this.file = file;
    }

    public boolean upload() {
        boolean upload = Boolean.FALSE;

        File fileDirectory = new File(getDirectory());
        if (!fileDirectory.isDirectory()) {
            fileDirectory.mkdirs();
        }

        File newFile = new File(getDirectory() + File.separator + getFileName());
        if (newFile.exists()) {
            newFile.delete();
        }
        upload = file.getFile().renameTo(newFile);

        return upload;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName + "." + getFileExtension();
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
