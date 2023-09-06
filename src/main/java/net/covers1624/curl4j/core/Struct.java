package net.covers1624.curl4j.core;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by covers1624 on 16/8/23.
 */
public class Struct {

    private static final int ALIGN = OperatingSystem.CURRENT == OperatingSystem.WINDOWS ? 8 : 0x4000_0000;

    private final List<Member<?>> members = new ArrayList<>();
    private boolean finished;
    private int sizeof;
    private int align = ALIGN;

    private <T> Member<T> addMember(Member<T> member) {
        if (finished) throw new IllegalArgumentException("Finish has already been called.");
        members.add(member);
        sizeof = member.offset + member.size();
        align = Math.max(align, member.alignment());
        return member;
    }

    public Member<Integer> intMember(String name) {
        return addMember(new IntMember(sizeof, name));
    }

    public Member<@Nullable String> stringMember(String name) {
        return addMember(new StringMember(sizeof, name));
    }

    public Member<Long> longMember(String name) {
        return addMember(new CLongMember(sizeof, name));
    }

    public Member<Set<String>> stringListMember(String name) {
        return addMember(new StringListMember(sizeof, name));
    }

    public Member<Pointer> pointerMember(String name) {
        return addMember(new PointerMember(sizeof, name));
    }

    public <T extends Pointer> Member<@Nullable T> structPointerMember(String name, Function<Pointer, T> func) {
        return addMember(new Member<T>(sizeof, name) {
            @Override
            public T read(long struct) {
                long addr = Memory.getAddress(struct + offset);
                return addr != Memory.NULL ? func.apply(new Pointer(addr)) : null;
            }

            @Override
            public int size() {
                return NativeTypes.POINTER_SIZE;
            }
        });
    }

    public void finish() {
        if (finished) return;
        sizeof = align(sizeof, align);
    }

    public int getSize() {
        finish();
        return sizeof;
    }

    public int getAlign() {
        finish();
        return align;
    }

    private static int align(int o, int a) {
        return ((o - 1) | (a - 1)) + 1;
    }

    public static abstract class Member<T> {

        public final int offset;
        public final String name;

        private Member(int offset, String name) {
            this.offset = align(offset, alignment());
            this.name = name;
        }

        public T read(Pointer pointer) {
            return read(pointer.address);
        }

        public abstract T read(long struct);

        public abstract int size();
        // TODO, this should probably be exposed as a creation property
        public int alignment() { return size(); }
    }

    // @formatter:off
    private static class IntMember extends Member<Integer> {
        private IntMember(int offset, String name) { super(offset, name); }
        @Override public Integer read(long struct) { return Memory.getInt(struct + offset); }
        @Override public int size() { return NativeTypes.CINT_SIZE; }
    }
    private static class StringMember extends Member<@Nullable String> {
        private StringMember(int offset, String name) { super(offset, name); }
        @Override public String read(long struct) { return Memory.readUtf8(Memory.getAddress(struct + offset)); }
        @Override public int size() { return NativeTypes.POINTER_SIZE; }
    }
    private static class CLongMember extends Member<Long> {
        private CLongMember(int offset, String name) { super(offset, name); }
        @Override public Long read(long struct) { return Memory.getCLong(struct + offset); }
        @Override public int size() { return NativeTypes.CLONG_SIZE; }
    }
    private static class PointerMember extends Member<Pointer> {
        private PointerMember(int offset, String name) { super(offset, name); }
        @Override public Pointer read(long struct) { return new Pointer(Memory.getAddress(struct + offset)); }
        @Override public int size() { return NativeTypes.POINTER_SIZE; }
    }
    // @formatter:on

    private static class StringListMember extends Member<Set<String>> {

        private StringListMember(int offset, String name) {
            super(offset, name);
        }

        @Override
        public Set<String> read(long struct) {
            Set<String> protocols = new LinkedHashSet<>();
            long ptr = Memory.getAddress(struct + offset);
            while (Memory.getByte(ptr) != '\0') {
                protocols.add(Memory.readUtf8(Memory.getAddress(ptr)));
                ptr += NativeTypes.POINTER_SIZE;
            }

            return protocols;
        }

        @Override
        public int size() {
            return NativeTypes.CLONG_SIZE;
        }
    }
}
