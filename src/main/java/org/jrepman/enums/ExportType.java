package org.jrepman.enums;

/**
 * Enumerate the possible report types
 * that the application can handle.
 */
public enum ExportType {
    PDF("application/pdf", "pdf");

    private String mediaType;
    private String extension;

    private ExportType(String mediaType, String extension){
        this.mediaType = mediaType;
        this.extension = extension;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getExtension() {
        return extension;
    }

    public String getFileWithExtension(String fileName){
        return fileName + "." + extension;
    }
}
