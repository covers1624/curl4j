package net.covers1624.curl4j.util;

import org.jetbrains.annotations.Nullable;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.*;

/**
 * @author covers1624
 */
public final class CLikeSymbolLinker {

    private static final Linker LINKER = Linker.nativeLinker();
    private static final FunctionDescriptor VOID_FUN = FunctionDescriptor.ofVoid();

    private final SymbolLookup lookup;
    private final Map<String, String> typeAliases = new HashMap<>();

    public CLikeSymbolLinker(SymbolLookup lookup) {
        this.lookup = lookup;
    }

    public CLikeSymbolLinker addAlias(String from, String to) {
        typeAliases.put(from, to);
        return this;
    }

    public MethodHandle link(String prototype, String... varArgs) {
        MethodHandle handle = linkOptionally(prototype, varArgs);

        if (handle == null) throw new NoSuchElementException();

        return handle;
    }

    public @Nullable MethodHandle linkOptionally(String prototype, String... varArgs) {
        MethodSig sig = parse(prototype, String.join(",", varArgs));
        Optional<MemorySegment> addr = lookup.find(sig.mName);

        if (addr.isEmpty()) return null;

        Linker.Option[] options = new Linker.Option[0];
        if (!sig.varArgs.isEmpty()) {
            options = new Linker.Option[] { Linker.Option.firstVariadicArg(sig.arguments.size() - 1) };
        }
        return LINKER.downcallHandle(addr.get(), resolveFunction(sig), options);
    }

    private FunctionDescriptor resolveFunction(MethodSig sig) {
        FunctionDescriptor func = VOID_FUN;
        if (!sig.retType.equals("void")) {
            func = func.changeReturnLayout(getType(sig.retType));
        }

        for (String argument : sig.arguments) {
            func = func.appendArgumentLayouts(getType(argument));
        }

        for (String argument : sig.varArgs) {
            func = func.appendArgumentLayouts(getType(argument));
        }

        return func;
    }

    private MemoryLayout getType(String type) {
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

    private static MethodSig parse(String prototype, String varArgsPrototype) {
        int startBrace = prototype.indexOf("(");
        int endBrace = prototype.indexOf(")");
        if (startBrace == -1 || endBrace == -1) {
            throw new RuntimeException("Expected opening and closing brace to exist.");
        }

        NameTypePair retAndFunc = parseNamePair(prototype.substring(0, startBrace));
        if (retAndFunc.name == null) throw new RuntimeException("Expected function name.");

        boolean hasVarArgs = false;
        List<String> arguments = new ArrayList<>();
        String args = prototype.substring(startBrace + 1, endBrace);
        if (!args.trim().equals("void")) {
            for (String arg : args.split(",")) {
                if (arg.isEmpty()) continue;
                if (hasVarArgs) throw new RuntimeException("Expected no more arguments after varargs.");

                NameTypePair pair = parseNamePair(arg);
                if (pair.type.equals("...")) {
                    hasVarArgs = true;
                    continue;
                }
                arguments.add(pair.type);
            }
        }

        List<String> varArgs = new ArrayList<>();
        for (String arg : varArgsPrototype.split(",")) {
            if (arg.isEmpty()) continue;
            varArgs.add(parseNamePair(arg).type);
        }

        if (!hasVarArgs && !varArgs.isEmpty()) {
            throw new RuntimeException("Expected var args prototypes to be empty, method sig does not contain ...");
        }

        return new MethodSig(retAndFunc.name, retAndFunc.type, arguments, varArgs);
    }

    private static NameTypePair parseNamePair(String str) {
        int starPos = str.lastIndexOf("*");
        int spacePos = str.lastIndexOf(' ');

        String type;
        String name = null;
        if (starPos != -1) {
            type = str.substring(0, starPos + 1).replaceAll(" ", "");
            name = str.substring(starPos + 1).trim();
        } else if (spacePos != -1) {
            type = str.substring(0, spacePos).trim();
            if (type.equals("struct")) {
                // Last space was the only space in 'struct myStruct', which means
                // the entire String is just the type, there is no name component.
                type = str;
            } else {
                name = str.substring(spacePos + 1);
            }
        } else {
            type = str;
        }

        return new NameTypePair(type, name);
    }

    private record NameTypePair(String type, @Nullable String name) { }

    private record MethodSig(
            String mName,
            String retType,
            List<String> arguments,
            List<String> varArgs
    ) { }
}
