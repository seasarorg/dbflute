package org.seasar.dbflute.helper.io.compress;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.7A (2012/07/15 Sunday)
 */
public class DfZipArchiver {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final File _zipFile;
    protected boolean _suppressSubDir;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfZipArchiver(File zipFile) {
        _zipFile = zipFile;
    }

    // ===================================================================================
    //                                                                            Compress
    //                                                                            ========
    public void compress(File targetFile, FileFilter filter) {
        if (targetFile == null) {
            throw new IllegalArgumentException("The argument 'targetFile' should not be null.");
        }
        if (!targetFile.exists()) {
            throw new IllegalArgumentException("The targetFile was not found in the file system: " + targetFile);
        }
        OutputStream out = null;
        ZipArchiveOutputStream archive = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(_zipFile));
            archive = new ZipArchiveOutputStream(out);
            archive.setEncoding("UTF-8");

            final File topDir = targetFile.isDirectory() ? targetFile : targetFile.getParentFile();
            addAll(archive, topDir, targetFile, filter);

            archive.finish();
            archive.flush();
            out.flush();
        } catch (IOException e) {
            String msg = "Failed to compress the files to " + _zipFile.getPath();
            throw new IllegalStateException(msg, e);
        } finally {
            if (archive != null) {
                try {
                    archive.close();
                } catch (IOException ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    protected void addAll(ArchiveOutputStream archive, File topDir, File targetFile, FileFilter filter)
            throws IOException {
        if (_suppressSubDir && isSubDir(topDir, targetFile)) {
            return;
        }
        if (!filter.accept(targetFile)) {
            return;
        }
        if (targetFile.isDirectory()) {
            final File[] listFiles = targetFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                addDir(archive, topDir, targetFile);
            } else {
                for (File elementFile : listFiles) {
                    addAll(archive, topDir, elementFile, filter);
                }
            }
        } else {
            addFile(archive, topDir, targetFile);
        }
    }

    protected boolean isSubDir(File topFile, File targetFile) {
        return targetFile.isDirectory() && !topFile.equals(targetFile);
    }

    protected void addDir(ArchiveOutputStream archive, File topDir, File targetDir) throws IOException {
        final String name = buildEntryName(topDir, targetDir, true);
        archive.putArchiveEntry(new ZipArchiveEntry(name));
        archive.closeArchiveEntry();
    }

    protected void addFile(ArchiveOutputStream archive, File topDir, File targetFile) throws IOException {
        final String name = buildEntryName(topDir, targetFile, false);
        archive.putArchiveEntry(new ZipArchiveEntry(name));
        FileInputStream ins = null;
        try {
            ins = new FileInputStream(targetFile);
            IOUtils.copy(ins, archive);
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignored) {
                }
            }
        }
        archive.closeArchiveEntry();
    }

    protected String buildEntryName(File topDir, File targetFile, boolean dir) {
        final String path = resolveAbsolutePath(targetFile);
        String name = path.substring(resolveAbsolutePath(topDir).length());
        if (name.startsWith("/")) {
            name = name.substring("/".length());
        }
        if (dir) {
            name = name + "/";
        }
        return name;
    }

    protected String resolveAbsolutePath(File file) {
        return Srl.replace(file.getAbsolutePath(), "\\", "/");
    }

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    public void extract(File baseDir, FileFilter filter) {
        if (baseDir == null) {
            throw new IllegalArgumentException("The argument 'baseDir' should not be null.");
        }
        if (baseDir.exists() && !baseDir.isDirectory()) {
            throw new IllegalArgumentException("The baseDir was not directory: " + baseDir);
        }
        baseDir.mkdirs();
        final String baseDirPath = baseDir.getPath();
        InputStream ins = null;
        ZipArchiveInputStream archive = null;
        try {
            ins = new FileInputStream(_zipFile);
            archive = new ZipArchiveInputStream(ins, "UTF-8", true);
            ZipArchiveEntry entry;
            while ((entry = archive.getNextZipEntry()) != null) {
                final File file = new File(baseDirPath + "/" + entry.getName());
                if (!filter.accept(file)) {
                    continue;
                }
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                        IOUtils.copy(archive, out);
                        out.close();
                    } catch (IOException e) {
                        String msg = "Failed to IO-copy the file: " + file.getPath();
                        throw new IllegalStateException(msg, e);
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            String msg = "Failed to extract the files from " + _zipFile.getPath();
            throw new IllegalArgumentException(msg, e);
        } finally {
            if (archive != null) {
                try {
                    archive.close();
                } catch (IOException ignored) {
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public DfZipArchiver suppressSubDir() {
        _suppressSubDir = true;
        return this;
    }
}
