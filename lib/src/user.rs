// Copyright 2024 tison <wander4096@gmail.com>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

use std::ffi::c_char;

use nix::unistd::User;
use snafu::ResultExt;

use crate::{string_from_raw, string_into_raw, TryResult};
use crate::error::NixSnafu;

#[no_mangle]
pub unsafe extern "C" fn TOPAZ_nix_unistd_user_from_name(name: *const c_char) -> TryResult<User> {
    let name = string_from_raw(name);
    User::from_name(&name).context(NixSnafu { desc: "cannot load user" }).into()
}

#[no_mangle]
pub unsafe extern "C" fn TOPAZ_nix_unistd_user_dir(user: *mut User) -> *mut c_char {
    let user = &*user;
    string_into_raw(user.dir.to_string_lossy().into_owned())
}

#[no_mangle]
pub unsafe extern "C" fn TOPAZ_deinit_user(user: *mut User) {
    drop(Box::from_raw(user));
}
