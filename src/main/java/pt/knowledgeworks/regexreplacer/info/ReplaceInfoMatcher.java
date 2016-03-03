/*
 * Copyright 2015 by KnowledgeWorks. All rights reserved.
 * 
 * This software is the proprietary information of KnowledgeWorks
 * Use is subject to license terms.
 * 
 * http://www.knowledgeworks.pt
 */
package pt.knowledgeworks.regexreplacer.info;

import java.util.regex.Pattern;
import pt.knowledgeworks.regexreplacer.config.ReplaceInfo;

/**
 *
 * @author Ivo Lucas <ivo.lucas@knowledgeworks.pt>
 */
public class ReplaceInfoMatcher {

    ReplaceInfo replaceInfo;
    Pattern pattern;

    public ReplaceInfoMatcher(ReplaceInfo replaceInfo) {
        this.replaceInfo = replaceInfo;
        pattern = Pattern.compile(replaceInfo.getRegex());
    }

    public ReplaceInfo getReplaceInfo() {
        return replaceInfo;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String replace(String in) {
        return pattern.matcher(in).replaceAll(replaceInfo.getReplacement());
    }
    
    public boolean find(String in) {
        return pattern.matcher(in).find();
    }

    @Override
    public String toString() {
        return "ReplaceInfoMatcher{" + "replaceInfo=" + replaceInfo + ", pattern=" + pattern + '}';
    }
    
    
}

