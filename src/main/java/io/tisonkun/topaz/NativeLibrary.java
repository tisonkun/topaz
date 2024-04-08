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
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NativeLibrary {
    private static final Linker LINKER;
    private static final SymbolLookup LOOKUP;

    static {
        System.loadLibrary("topaz");
        LINKER = Linker.nativeLinker();
        LOOKUP = SymbolLookup.loaderLookup();
    }

    public static MethodHandle createDowncallHandle(String functionName, FunctionDescriptor descriptor) {
        return LINKER.downcallHandle(LOOKUP.find(functionName).orElseThrow(), descriptor);
    }
}
