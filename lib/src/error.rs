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

use std::ffi::{c_char, CString};

use derive_new::new;
use snafu::{Location, Snafu};

#[derive(Snafu, Debug)]
#[snafu(visibility(pub))]
pub enum Error {
    #[snafu(display("[nix-rust] {} (loc: {}, source: {})", desc, location, source))]
    NixError {
        desc: String,
        #[snafu(source)]
        source: nix::Error,
        #[snafu(implicit)]
        location: Location,
    },
}

#[repr(C)]
#[derive(new)]
pub struct Exception {
    msg: *mut c_char,
}

#[no_mangle]
pub unsafe extern "C" fn TOPAZ_deinit_exception(exception: *mut Exception) {
    let exception = Box::from_raw(exception);
    drop(CString::from_raw(exception.msg));
}
