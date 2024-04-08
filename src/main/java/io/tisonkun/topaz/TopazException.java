/*
 * Copyright 2024 tison <wander4096@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.tisonkun.topaz;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import lombok.SneakyThrows;

public class TopazException extends RuntimeException {
    private static final MethodHandle DEINIT = NativeLibrary.createDowncallHandle(
            "TOPAZ_deinit_exception", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final StructLayout LAYOUT = MemoryLayout.structLayout(ValueLayout.ADDRESS.withName("msg"));
    private static final VarHandle VAR_MSG = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("msg"));

    @SneakyThrows
    static TopazException create(MemorySegment exp) {
        try {
            final MemorySegment msg = (MemorySegment) VAR_MSG.get(exp.reinterpret(LAYOUT.byteSize()), 0L);
            final String message = msg.reinterpret(Long.MAX_VALUE).getString(0L);
            return new TopazException(message);
        } finally {
            DEINIT.invoke(exp);
        }
    }

    private TopazException(String message) {
        super(message);
    }
}
