/*
 * Copyright 2015 by KnowledgeWorks. All rights reserved.
 * 
 * This software is the proprietary information of KnowledgeWorks
 * Use is subject to license terms.
 * 
 * http://www.knowledgeworks.pt
 */
package pt.knowledgeworks.regexreplacer.info;

import com.sun.nio.zipfs.ZipPath;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import pt.knowledgeworks.regexreplacer.config.ReplaceInfo;

/**
 *
 * @author Ivo Lucas <ivo.lucas@knowledgeworks.pt>
 */
public class MatchFound {
    Path file;
    Map<ReplaceInfo,Boolean> matchs = new HashMap<>(10);
    Boolean ignore= Boolean.FALSE;

    public MatchFound(Path file) {
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    public String getLocation(){
        if(file instanceof ZipPath){
            return file.getFileSystem().toString()+"#"+file.toString();
        } else {
            return file.toString();
        }
    }
    
    public boolean isZipFileEntry(){
        return (file instanceof ZipPath);       
    }
    
    public void setFile(Path file) {
        this.file = file;
    }

    public Map<ReplaceInfo, Boolean> getMatchs() {
        return matchs;
    }

    public void setMatchs(Map<ReplaceInfo, Boolean> matchs) {
        this.matchs = matchs;
    }

    public boolean hasMatch() {
        
        for(Boolean v:this.matchs.values()){
            if(v!=null && v){
                return true;
            }
        }
        return false;
    }
    
    public Boolean getIgnore() {
        return ignore;
    }

    public void setIgnore(Boolean ignore) {
        this.ignore = ignore;
    }

    @Override
    public String toString() {
        return "MatchFound{" + "file=" + file + ", matchs=" + matchs.size() + ", ignore=" + ignore + '}';
    }
    
    
}
