/*
 * Copyright 2015 by KnowledgeWorks. All rights reserved.
 * 
 * This software is the proprietary information of KnowledgeWorks
 * Use is subject to license terms.
 * 
 * http://www.knowledgeworks.pt
 */
package pt.knowledgeworks.regexreplacer;

import com.sun.nio.zipfs.ZipPath;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;
import pt.knowledgeworks.regexreplacer.config.ReplaceInfo;
import pt.knowledgeworks.regexreplacer.info.MatchFound;
import pt.knowledgeworks.regexreplacer.info.ReplaceInfoMatcher;

/**
 *
 * @author Ivo Lucas <ivo.lucas@knowledgeworks.pt>
 */
public class FinderUtil {

    private static final Logger LOGGER = Logger.getLogger(FinderUtil.class.getName());

    public FinderUtil(String fileEnconding, String fileRegex, boolean replaceZip, String zipRegex, List<ReplaceInfo> replaceInfos, List<MatchFound> filesFound) {
        if (fileEnconding != null && !fileEnconding.isEmpty()) {
            this.fileEnconding = Charset.forName(fileEnconding);
        }
        this.replaceZip = replaceZip;
        if (this.replaceZip) {
            this.zipRegex = Pattern.compile(zipRegex);
        }
        this.fileRegex = Pattern.compile(fileRegex);
        for (ReplaceInfo replaceInfo : replaceInfos) {
            replaceInfoMatchers.add(new ReplaceInfoMatcher(replaceInfo));
        }
        this.filesFound = filesFound;

    }

    protected Charset fileEnconding = null;
    protected Pattern fileRegex;
    protected boolean replaceZip = false;
    protected Pattern zipRegex;
    protected List<MatchFound> filesFound;
    protected List<ReplaceInfoMatcher> replaceInfoMatchers = new ArrayList<>();

    public void process(Path x) {
        if (Files.isDirectory(x)) {
            try {
                for (Path f : Files.newDirectoryStream(x)) {
                    if (Files.isDirectory(f)) {
                        process(f);
                    } else if (replaceZip && zipRegex.matcher(f.getFileName().toString()).matches()) {
                        processZipFile(f);
                    } else if (fileRegex.matcher(f.getFileName().toString()).matches()) {
                        processFile(f);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(FinderUtil.class.getName()).log(Level.SEVERE, "Error processing " + x, ex);
            }
        } else {
            processFile(x);
        }
    }

    public void processZipPath(Path x) {
        if (Files.isDirectory(x)) {
            try {
                for (Path f : Files.newDirectoryStream(x)) {
                    if (Files.isDirectory(f)) {
                        processZipPath(f);
                    } else if (fileRegex.matcher(f.getFileName().toString()).matches()) {
                        processFile(f);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(FinderUtil.class.getName()).log(Level.SEVERE, "Error processing " + x, ex);
            }
        } else {
            processFile(x);
        }
    }

    protected void processZipFile(Path file) {
        try (FileSystem fs = FileSystems.newFileSystem(file, null)) {
            for (Path t : fs.getRootDirectories()) {
                processZipPath(t);
            }

        } catch (IOException e) {
            Logger.getLogger(FinderUtil.class.getName()).log(Level.WARNING, "message", e);
        }
    }

    protected void processFile(Path file) {
        MatchFound matchFound = new MatchFound(file);
        try {
            StringBuilder info = new StringBuilder("FileSystem:");
            info.append(file.getFileSystem());
            info.append("File:");
            info.append(file);
            String fileContent = readFile(file);
            for (ReplaceInfoMatcher replaceInfoMatcher : replaceInfoMatchers) {
                if (replaceInfoMatcher.getReplaceInfo().getEnable() && replaceInfoMatcher.find(fileContent)) {
                    fileContent = replaceInfoMatcher.replace(fileContent);
                    matchFound.getMatchs().put(replaceInfoMatcher.getReplaceInfo(), Boolean.TRUE);
                    info.append("\n\t");
                    info.append(replaceInfoMatcher.getReplaceInfo());
                } else {
                    matchFound.getMatchs().put(replaceInfoMatcher.getReplaceInfo(), Boolean.FALSE);
                }
            }
            LOGGER.info(info.toString());

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            matchFound.setIgnore(Boolean.TRUE);
        }
        if (!matchFound.getMatchs().isEmpty()) {
            filesFound.add(matchFound);
        }

    }

    public void apply() {
        FileSystem fs = null;
        for (MatchFound matchFound : filesFound) {

            if (!matchFound.getIgnore() && matchFound.hasMatch()) {
                Path path = matchFound.getFile();
                if (matchFound.isZipFileEntry()) {
                    if (fs != null && fs.toString().equals(path.getFileSystem().toString())) {
                        //same false sistem
                    } else if (fs != null && !fs.toString().equals(path.getFileSystem().toString())) {
                        try {
                            fs.close();
                        } catch (IOException ex) {
                            Logger.getLogger(FinderUtil.class.getName()).log(Level.SEVERE, "Closing " + fs, ex);
                        }
                        fs = null;
                    }
                    if (fs == null) {
                        try {
                            fs = FileSystems.newFileSystem((new File(path.getFileSystem().toString())).toPath(), null);
                        } catch (IOException ex) {
                            Logger.getLogger(FinderUtil.class.getName()).log(Level.SEVERE, "Openning " + path.getFileSystem(), ex);
                        }
                    }
                    path = fs.getPath(path.toString());
                }
                try {
                    String fileContent = readFile(path);
                    for (ReplaceInfoMatcher replaceInfoMatcher : replaceInfoMatchers) {
                        try {
                            if (matchFound.getMatchs().get(replaceInfoMatcher.getReplaceInfo())) {
                                fileContent = replaceInfoMatcher.replace(fileContent);
                            }
                        } catch (Exception e) {
                            throw new IOException("Erro com o replace " + replaceInfoMatcher, e);
                        }
                    }
                    writeFile(path, fileContent);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Erro a processar " + matchFound + " causado por: " + ex.getMessage(), ex);
                }

            }
        }
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException ex) {
                Logger.getLogger(FinderUtil.class.getName()).log(Level.SEVERE, "Closing " + fs, ex);
            }
        }
    }

    private String readFile(Path path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, fileEnconding);
    }

    private void writeFile(Path path, String content)
            throws IOException {
        Logger.getLogger(FinderUtil.class.getName()).log(Level.INFO, "Writing  " + path);
        byte[] encoded = content.getBytes(fileEnconding);
        Files.write(path, encoded, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
