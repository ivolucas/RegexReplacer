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

/**
 *
 * @author Ivo Lucas <ivo.lucas@knowledgeworks.pt>
 */
public class ReplaceInfo implements Serializable{
    private static final long serialVersionUID = 2524768840633480774L;
    
    String name="";
    String regex="";
    String replacement="";
    Boolean enable = Boolean.TRUE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    
    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "ReplaceInfo{" + "name=" + name + ", regex=" + regex + ", replacement=" + replacement + ", enable=" + enable + '}';
    }

   
    
    
    
}
