package net.covers1624.curl4j.util;

import net.covers1624.curl4j.util.CLikeSymbolResolver.NameTypePair;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 12/31/24.
 */
public class CLikeStructParser {

    private final CLikeSymbolResolver symbolResolver;

    public CLikeStructParser(CLikeSymbolResolver symbolResolver) {
        this.symbolResolver = symbolResolver;
    }

    /**
     * Parses a very C like struct definition into a StructLayout.
     * <p>
     * Structs must be formatted over multiple lines, a single line for the struct keyword, name and opening brace,
     * each struct field must be on a new line with a semicolon terminator, and a single tailing closing brace and semicolon on a new line.
     * Comments are allowed using double slash or slash star syntax, Slash star comments may not span multiple lines.
     * <p>
     * Example:
     * <pre>
     * struct my_struct {
     *     int someField; // Some comment
     *     char * someOtherField; /* Some other comment &#42;&#47;
     * };
     * </pre>
     *
     * @param str The string.
     * @return The struct layout.
     */
    public StructLayout parseStruct(String str) {
        StructDef def = parseStructDef(str);
        record Pair(MemoryLayout layout, String name) { }

        long alignment = 1;
        // Parse each entry's type and figure out the struct alignment.
        List<Pair> entries = new ArrayList<>();
        for (NameTypePair entry : def.entries) {
            MemoryLayout layout = symbolResolver.resolveType(entry.type());
            entries.add(new Pair(layout, entry.name()));
            alignment = Math.max(alignment, layout.byteAlignment());
        }

        // Build the struct.
        List<MemoryLayout> built = new ArrayList<>();
        for (Pair entry : entries) {
            built.add(entry.layout.withName(entry.name));
            long r = entry.layout.byteSize() % alignment;
            if (r != 0) {
                built.add(MemoryLayout.paddingLayout(alignment - r));
            }
        }

        return MemoryLayout.structLayout(built.toArray(MemoryLayout[]::new));
    }

    private StructDef parseStructDef(String str) {
        List<String> lines = str.lines()
                .map(this::stripComment)
                .map(String::trim)
                .filter(e -> !e.isEmpty())
                .toList();
        if (lines.size() < 3) throw new IllegalArgumentException("Expected at least 3 lines.");

        String structName = parseStructHeader(lines.getFirst());
        if (!lines.getLast().equals("};")) throw new IllegalArgumentException("Expected '};' after the struct.");

        List<NameTypePair> entries = new ArrayList<>();
        for (String element : lines.subList(1, lines.size() - 1)) {
            if (!element.endsWith(";")) throw new IllegalArgumentException("Expected element '" + element + "' to end with a semicolon.");
            entries.add(symbolResolver.parseNamePair(element.substring(0, element.length() - 1)));
        }

        return new StructDef(structName, entries);
    }

    private String parseStructHeader(String line) {
        if (!line.startsWith("struct ")) throw new IllegalArgumentException("Struct does not start with 'struct'");
        line = line.substring(7);

        int braceIdx = line.indexOf("{");
        if (braceIdx == -1) throw new IllegalArgumentException("Struct header does not contain an opening brace.");

        return line.substring(0, braceIdx).trim();
    }

    private String stripComment(String line) {
        int slashSlash = line.indexOf("//");
        int slashStar = line.indexOf("/*");
        int commentIdx;
        if (slashSlash != -1 && slashStar != -1) {
            commentIdx = Math.min(slashSlash, slashStar);
        } else if (slashSlash != -1) {
            commentIdx = slashSlash;
        } else if (slashStar != -1) {
            commentIdx = slashStar;
        } else {
            return line;
        }
        return line.substring(0, commentIdx);
    }

    public record StructDef(String name, List<NameTypePair> entries) { }
}
