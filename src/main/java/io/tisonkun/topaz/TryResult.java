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

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.function.Function;

public final class TryResult {
    public static final StructLayout LAYOUT =
            MemoryLayout.structLayout(ValueLayout.ADDRESS.withName("val"), ValueLayout.ADDRESS.withName("exp"));
    private static final VarHandle VAR_VAL = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("val"));
    private static final VarHandle VAR_EXP = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("exp"));

    public static <T> T unwrap(MemorySegment result, Function<MemorySegment, T> fn) {
        final MemorySegment exp = (MemorySegment) VAR_EXP.get(result, 0L);
        if (!MemorySegment.NULL.equals(exp)) {
            throw TopazException.create(exp);
        }
        final MemorySegment val = (MemorySegment) VAR_VAL.get(result, 0L);
        return MemorySegment.NULL.equals(val) ? null : fn.apply(val);
    }
}
