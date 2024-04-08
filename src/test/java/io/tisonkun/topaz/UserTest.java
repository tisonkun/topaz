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

import static org.assertj.core.api.Assertions.assertThat;
import java.lang.foreign.Arena;
import org.junit.jupiter.api.Test;

class UserTest {
    @Test
    public void testGetHomeDir() {
        try (Arena arena = Arena.ofConfined()) {
            final User root = User.of("root", arena);
            assertThat(root).isNotNull();
            assertThat(root.getHomeDir()).contains("root");
            final User nonexistent = User.of("nonexistent", arena);
            assertThat(nonexistent).isNull();
        }
    }
}
