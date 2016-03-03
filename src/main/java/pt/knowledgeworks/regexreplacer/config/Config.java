/*
 * Copyright 2015 by KnowledgeWorks. All rights reserved.
 * 
 * This software is the proprietary information of KnowledgeWorks
 * Use is subject to license terms.
 * 
 * http://www.knowledgeworks.pt
 */
package pt.knowledgeworks.regexreplacer.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivo Lucas <ivo.lucas@knowledgeworks.pt>
 */
public class Config implements Serializable{
    private static final long serialVersionUID = 5391678725622185423L;
    String fileEnconding;
    String fileNameRegex;
    boolean zipFiles;
    String zipfileNameRegex="";
    List<ReplaceInfo> list =new ArrayList<>();

    public String getFileEnconding() {
        return fileEnconding;
    }

    public void setFileEnconding(String fileEnconding) {
        this.fileEnconding = fileEnconding;
    }

    public List<ReplaceInfo> getList() {
        return list;
    }

    public void setList(List<ReplaceInfo> list) {
        this.list = list;
    }

    public String getFileNameRegex() {
        return fileNameRegex;
    }

    public void setFileNameRegex(String fileNameRegex) {
        this.fileNameRegex = fileNameRegex;
    }

    public boolean isZipFiles() {
        return zipFiles;
    }

    public void setZipFiles(boolean zipFiles) {
        this.zipFiles = zipFiles;
    }

    public String getZipfileNameRegex() {
        return zipfileNameRegex;
    }

    public void setZipfileNameRegex(String zipfileNameRegex) {
        this.zipfileNameRegex = zipfileNameRegex;
    }
        
    
}
