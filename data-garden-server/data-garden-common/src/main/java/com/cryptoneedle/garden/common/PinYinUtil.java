package com.cryptoneedle.garden.common;

import com.hankcs.hanlp.HanLP;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2026-03-17
 */
public class PinYinUtil {
    
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4E00-\\u9FA5]+");
    
    public static String convertChinese(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        Matcher matcher = CHINESE_PATTERN.matcher(input);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String chineseText = matcher.group();
            String pinyin = HanLP.convertToPinyinString(chineseText, "", false);
            pinyin = pinyin.toLowerCase();
            
            // 获取中文片段的位置
            int start = matcher.start();
            int end = matcher.end();
            
            // 判断是否需要添加前置下划线
            boolean needPrefixUnderscore = start > 0 && input.charAt(start - 1) != '_';
            // 判断是否需要添加后置下划线
            boolean needSuffixUnderscore = end < input.length() && input.charAt(end) != '_';
            
            // 构建替换字符串
            StringBuilder replacement = new StringBuilder();
            if (needPrefixUnderscore) {
                replacement.append("_");
            }
            replacement.append(pinyin);
            if (needSuffixUnderscore) {
                replacement.append("_");
            }
            
            matcher.appendReplacement(result, replacement.toString());
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
}