package net.covers1624.curl4j.util;

import net.covers1624.curl4j.util.CLikeSymbolResolver.NameTypePair;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author covers1624
 */
public final class CLikeSymbolLinker {

    private static final Linker LINKER = Linker.nativeLinker();
    private static final FunctionDescriptor VOID_FUN = FunctionDescriptor.ofVoid();

    private final SymbolLookup lookup;
    private final CLikeSymbolResolver symbolResolver;

    public CLikeSymbolLinker(SymbolLookup lookup, CLikeSymbolResolver symbolResolver) {
        this.lookup = lookup;
        this.symbolResolver = symbolResolver;
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
            options = new Linker.Option[] { Linker.Option.firstVariadicArg(sig.arguments.size()) };
        }
        return LINKER.downcallHandle(addr.get(), resolveFunction(sig), options);
    }

    private FunctionDescriptor resolveFunction(MethodSig sig) {
        FunctionDescriptor func = VOID_FUN;
        if (!sig.retType.equals("void")) {
            func = func.changeReturnLayout(symbolResolver.resolveType(sig.retType));
        }

        for (String argument : sig.arguments) {
            func = func.appendArgumentLayouts(symbolResolver.resolveType(argument));
        }

        for (String argument : sig.varArgs) {
            func = func.appendArgumentLayouts(symbolResolver.resolveType(argument));
        }

        return func;
    }

    private MethodSig parse(String prototype, String varArgsPrototype) {
        int startBrace = prototype.indexOf("(");
        int endBrace = prototype.indexOf(")");
        if (startBrace == -1 || endBrace == -1) {
            throw new RuntimeException("Expected opening and closing brace to exist.");
        }

        NameTypePair retAndFunc = symbolResolver.parseNamePair(prototype.substring(0, startBrace));
        if (retAndFunc.name() == null) throw new RuntimeException("Expected function name.");

        boolean hasVarArgs = false;
        List<String> arguments = new ArrayList<>();
        String args = prototype.substring(startBrace + 1, endBrace);
        if (!args.trim().equals("void")) {
            for (String arg : args.split(",")) {
                arg = arg.trim();
                if (arg.isEmpty()) continue;
                if (hasVarArgs) throw new RuntimeException("Expected no more arguments after varargs.");

                NameTypePair pair = symbolResolver.parseNamePair(arg);
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
            varArgs.add(symbolResolver.parseNamePair(arg).type());
        }

        if (!hasVarArgs && !varArgs.isEmpty()) {
            throw new RuntimeException("Expected var args prototypes to be empty, method sig does not contain ...");
        }

        return new MethodSig(retAndFunc.name(), retAndFunc.type(), arguments, varArgs);
    }

    private record MethodSig(
            String mName,
            String retType,
            List<String> arguments,
            List<String> varArgs
    ) { }
}
