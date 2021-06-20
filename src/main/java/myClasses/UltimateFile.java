package myClasses;

import java.util.Objects;

public class UltimateFile {
    private int file_id;
    private String file_name;
    private byte[] file_data;

    public UltimateFile(int file_id, String file_name, byte[] file_data) {
        this.file_id = file_id;
        this.file_name = file_name;
        this.file_data = file_data;
    }

    public int getFile_id() {
        return file_id;
    }

    public void setFile_id(int file_id) {
        this.file_id = file_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public byte[] getFile_data() {
        return file_data;
    }

    public void setFile_data(byte[] file_data) {
        this.file_data = file_data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UltimateFile that = (UltimateFile) o;
        return file_id == that.file_id && Objects.equals(file_name, that.file_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file_id, file_name);
    }
}
