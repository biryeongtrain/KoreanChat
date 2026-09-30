package org.karn.koreanchat.util;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ChatConversion {
    private final static KoreanConvertorUtil englishToKorean = new KoreanConvertorUtil();
    private static final Pattern URL = Pattern.compile("https?://\\S+");

    public static TextNode convertChat(TextNode message, ParserContext context) {
        if (hasKorAndEng(message.toComponent(context).getString())) {
            return message;
        }
        return convertNode(message, new boolean[1]);
    }

    private static TextNode convertNode(TextNode node, boolean[] quoted) {
        if (node instanceof LiteralNode literal) {
            return new LiteralNode(convertLiteral(literal.value(), quoted));
        }
        if (node instanceof ParentTextNode parent) {
            return parent.copyWith(Arrays.stream(parent.getChildren())
                    .map(child -> convertNode(child, quoted)).toArray(TextNode[]::new));
        }
        return node;
    }

    private static boolean hasKorAndEng(String string){
        boolean hasKorean = false;
        boolean hasEnglish = false;

        for (char c : string.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO) {
                hasKorean = true;
            } else if (Character.isLetter(c)) {
                hasEnglish = true;
            }

            if (hasKorean && hasEnglish) {
                break;
            }
        }

        return hasKorean && hasEnglish;
    };

    public static String convertChat(String string){
        if(hasKorAndEng(string))
            return string;

        return convertLiteral(string, new boolean[1]);
    }

    private static String convertLiteral(String string, boolean[] quoted) {
        var parts = string.split("\"", -1);
        var result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) quoted[0] = !quoted[0];
            result.append(quoted[0] ? parts[i] : convertUnquoted(parts[i]));
        }
        return result.toString();
    }

    private static String convertUnquoted(String string) {
        var urls = URL.matcher(string);
        var result = new StringBuilder();
        int end = 0;
        while (urls.find()) {
            result.append(englishToKorean.engToKor(string.substring(end, urls.start())));
            result.append(urls.group());
            end = urls.end();
        }
        return result.append(englishToKorean.engToKor(string.substring(end))).toString();
    }
}
