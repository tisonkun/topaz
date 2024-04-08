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

use std::ffi::{c_char, CStr, CString};
use crate::error::{Error, Exception};

pub mod error;
pub mod user;

#[repr(C)]
pub struct TryResult<T> {
    val: *mut T,
    exp: *mut Exception,
}

impl<T> From<Result<T, Error>> for TryResult<T> {
    fn from(result: Result<T, Error>) -> Self {
        match result {
            Ok(val) => TryResult {
                val: make_value(val),
                exp: std::ptr::null_mut(),
            },
            Err(err) => TryResult {
                val: std::ptr::null_mut(),
                exp: make_exception(err),
            }
        }
    }
}

impl<T> From<Result<Option<T>, Error>> for TryResult<T> {
    fn from(result: Result<Option<T>, Error>) -> Self {
        match result {
            Ok(None) => TryResult {
                val: std::ptr::null_mut(),
                exp: std::ptr::null_mut(),
            },
            Ok(Some(val)) => TryResult {
                val: make_value(val),
                exp: std::ptr::null_mut(),
            },
            Err(err) => TryResult {
                val: std::ptr::null_mut(),
                exp: make_exception(err),
            }
        }
    }
}

pub fn string_from_raw(raw: *const c_char) -> String {
    let c_str = unsafe { CStr::from_ptr(raw) };
    c_str.to_string_lossy().into_owned()
}

pub fn string_into_raw(string: impl ToString) -> *mut c_char {
    let string = string.to_string();
    unsafe { CString::from_vec_unchecked(string.into_bytes()).into_raw() }
}

pub fn make_value<T>(value: T) -> *mut T {
    Box::into_raw(Box::new(value))
}

pub fn make_exception(error: Error) -> *mut Exception {
    let msg = string_into_raw(error);
    Box::into_raw(Box::new(Exception::new(msg)))
}
