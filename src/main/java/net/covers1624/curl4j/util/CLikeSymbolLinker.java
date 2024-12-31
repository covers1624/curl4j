package net.covers1624.curl4j.util;

import net.covers1624.curl4j.util.CLikeSymbolResolver.MethodSig;
import org.jetbrains.annotations.Nullable;

import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author covers1624
 */
public final class CLikeSymbolLinker {

    private static final Linker LINKER = Linker.nativeLinker();

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
        MethodSig sig = symbolResolver.parse(prototype, String.join(",", varArgs));
        Optional<MemorySegment> addr = lookup.find(sig.mName());

        if (addr.isEmpty()) return null;

        Linker.Option[] options = new Linker.Option[0];
        if (!sig.varArgs().isEmpty()) {
            options = new Linker.Option[] { Linker.Option.firstVariadicArg(sig.arguments().size()) };
        }
        return LINKER.downcallHandle(addr.get(), symbolResolver.resolveFunction(sig), options);
    }
}
