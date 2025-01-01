package net.covers1624.curl4j.util;

import org.jetbrains.annotations.Nullable;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 12/31/24.
 */
public class CLikeSymbolResolver {

    private static final Linker LINKER = Linker.nativeLinker();
    private static final FunctionDescriptor VOID_FUN = FunctionDescriptor.ofVoid();

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

    public FunctionDescriptor resolveFunction(MethodSig sig) {
        FunctionDescriptor func = VOID_FUN;
        if (!sig.retType.equals("void")) {
            func = func.changeReturnLayout(resolveType(sig.retType));
        }

        for (String argument : sig.arguments) {
            func = func.appendArgumentLayouts(resolveType(argument));
        }

        for (String argument : sig.varArgs) {
            func = func.appendArgumentLayouts(resolveType(argument));
        }

        return func;
    }

    public MethodSig parse(String prototype, String varArgsPrototype) {
        int startBrace = prototype.lastIndexOf("(");
        int endBrace = prototype.lastIndexOf(")");
        if (startBrace == -1 || endBrace == -1) {
            throw new RuntimeException("Expected opening and closing brace to exist.");
        }

        String funcRetAndName = prototype.substring(0, startBrace);
        if (funcRetAndName.contains("(*") && funcRetAndName.contains(")")) {
            funcRetAndName = funcRetAndName.replace("(*", "").replace(")", "");
        }
        NameTypePair retAndFunc = parseNamePair(funcRetAndName);
        if (retAndFunc.name() == null) throw new RuntimeException("Expected function name.");

        boolean hasVarArgs = false;
        List<String> arguments = new ArrayList<>();
        String args = prototype.substring(startBrace + 1, endBrace);
        if (!args.trim().equals("void")) {
            for (String arg : args.split(",")) {
                arg = arg.trim();
                if (arg.isEmpty()) continue;
                if (hasVarArgs) throw new RuntimeException("Expected no more arguments after varargs.");

                NameTypePair pair = parseNamePair(arg);
                if (pair.type().equals("...")) {
                    hasVarArgs = true;
                    continue;
                }
                arguments.add(pair.type());
            }
        }

        List<String> varArgs = new ArrayList<>();
        for (String arg : varArgsPrototype.split(",")) {
            arg = arg.trim();
            if (arg.isEmpty()) continue;
            varArgs.add(parseNamePair(arg).type());
        }

        if (!hasVarArgs && !varArgs.isEmpty()) {
            throw new RuntimeException("Expected var args prototypes to be empty, method sig does not contain ...");
        }

        return new MethodSig(retAndFunc.name(), retAndFunc.type(), arguments, varArgs);
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

    public record MethodSig(
            String mName,
            String retType,
            List<String> arguments,
            List<String> varArgs
    ) { }
}
