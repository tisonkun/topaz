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

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import lombok.SneakyThrows;

public final class User extends NativeObject {
    private static final MethodHandle DEINIT =
            NativeLibrary.createDowncallHandle("TOPAZ_deinit_user", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

    private static final MethodHandle fnUserFromName = NativeLibrary.createDowncallHandle(
            "TOPAZ_nix_unistd_user_from_name", FunctionDescriptor.of(TryResult.LAYOUT, ValueLayout.ADDRESS));
    private static final MethodHandle fnUserDir = NativeLibrary.createDowncallHandle(
            "TOPAZ_nix_unistd_user_dir", FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS));

    @SneakyThrows
    public static User of(String name, Arena arena) {
        final MemorySegment result = (MemorySegment) fnUserFromName.invoke(arena, arena.allocateFrom(name));
        final User user = TryResult.unwrap(result, User::new);
        if (user != null) {
            user.pointer.reinterpret(arena, user::dispose);
        }
        return user;
    }

    private User(MemorySegment user) {
        super(user);
    }

    @SneakyThrows
    public String getHomeDir() {
        final MemorySegment dir = (MemorySegment) fnUserDir.invoke(pointer);
        return dir.reinterpret(Long.MAX_VALUE).getString(0L);
    }

    @SneakyThrows
    @Override
    protected void deinit(MemorySegment handle) {
        DEINIT.invoke(handle);
    }
}
