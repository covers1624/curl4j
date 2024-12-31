package net.covers1624.curl4j.util;

import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 12/31/24.
 */
public class CLikeSymbolResolver {

    private static final Linker LINKER = Linker.nativeLinker();

    private final Map<String, String> typeAliases = new HashMap<>();

    public CLikeSymbolResolver addAlias(String from, String to) {
        typeAliases.put(from, to);
        return this;
    }

    public MemoryLayout resolveType(String type) {
        String resolvedType = type;
        String prevType;
        do {
            prevType = resolvedType;
            resolvedType = resolveTypeAlias(resolvedType);
        }
        while (!prevType.equals(resolvedType));

        MemoryLayout layout = LINKER.canonicalLayouts().get(resolvedType);
        if (layout == null) {
            throw new RuntimeException("Unable to resolve type " + type + " (" + resolvedType + ")");
        }
        return layout;
    }

    private String resolveTypeAlias(String type) {
        if (!type.equals("void*") && type.endsWith("*")) {
            return "void*";
        }

        return typeAliases.getOrDefault(type, type);
    }

    public NameTypePair parseNamePair(String str) {
        str = stripKeywords(str, "struct", "const", "unsigned", "signed");
        int starPos = str.lastIndexOf("*");
        int spacePos = str.lastIndexOf(' ');

        String type;
        String name = null;
        if (starPos != -1) {
            type = str.substring(0, starPos + 1).replaceAll(" ", "");
            name = str.substring(starPos + 1).trim();
        } else if (spacePos != -1) {
            type = str.substring(0, spacePos).trim();
            name = str.substring(spacePos + 1);
        } else {
            type = str;
        }

        return new NameTypePair(type, name);
    }

    private static String stripKeywords(String str, String... keywords) {
        String prev;
        do {
            prev = str;
            for (String keyword : keywords) {
                if (str.startsWith(keyword)) {
                    str = str.substring(keyword.length());
                    break;
                }
            }
        }
        while (!prev.equals(str));
        return str;
    }

    public record NameTypePair(String type, @Nullable String name) { }
}