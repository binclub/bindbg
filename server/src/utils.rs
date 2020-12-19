#![allow(dead_code)]

use std::ptr::null_mut;
use std::borrow::BorrowMut;
use std::os::raw::c_void;
use jvm_rs::jni::{JNIEnv, JavaVM, JNI_OK};
use jvm_rs::jvmti::{JVMTI_VERSION, jvmtiEnv};
use std::io::Read;

pub unsafe fn get_vm(env: *mut JNIEnv) -> *mut JavaVM {
	let mut vm: *mut JavaVM = null_mut();
	assert_eq!((**env).GetJavaVM.unwrap()(env, vm.borrow_mut()), JNI_OK as i32, "Couldn't fetch vm instance");
	vm
}

pub unsafe fn get_jvmti(vm: *mut JavaVM) -> *mut jvmtiEnv {
	let mut jvmti_ptr: *mut c_void = null_mut();
	assert_eq!((**vm).GetEnv.unwrap()(vm, jvmti_ptr.borrow_mut(), JVMTI_VERSION as i32), JNI_OK as i32, "Couldn't fetch jvmti instance");
	jvmti_ptr as *mut jvmtiEnv
}

pub trait ReadLine {
	fn read_line(&mut self, buf: &mut Vec<u8>) -> std::io::Result<usize>;
	fn read_string_line(&mut self, str: &mut String) -> std::io::Result<usize>;
}

impl <T: Read> ReadLine for T {
	/// Reads a line to a vector, stopping when it encounters an ascii new line
	/// The new line will NOT be included in the buffer
	fn read_line(&mut self, buf: &mut Vec<u8>) -> std::io::Result<usize> {
		let mut amount_read = 0;
		loop {
			let mut slice = [0u8];
			let read = self.read(&mut slice)?;
			if read == 1{
				amount_read += 1;
				
				let byte = slice[0];
				if byte == b'\n' {
					return Ok(amount_read);
				} else {
					buf.push(byte);
				}
			} else {
				return Err(std::io::Error::new(std::io::ErrorKind::UnexpectedEof, "failed to fill whole buffer"))
			}
		}
	}
	
	fn read_string_line(&mut self, str: &mut String) -> std::io::Result<usize> {
		append_to_string(str, |b| self.read_line(b))
	}
}


fn append_to_string<F>(buf: &mut String, f: F) -> std::io::Result<usize>
	where
		F: FnOnce(&mut Vec<u8>) -> std::io::Result<usize>,
{
	struct Guard<'a> {
		buf: &'a mut Vec<u8>,
		len: usize,
	}
	
	impl Drop for Guard<'_> {
		fn drop(&mut self) {
			unsafe {
				self.buf.set_len(self.len);
			}
		}
	}
	
	unsafe {
		let mut g = Guard { len: buf.len(), buf: buf.as_mut_vec() };
		let ret = f(g.buf);
		if std::str::from_utf8(&g.buf[g.len..]).is_err() {
			ret.and_then(|_| {
				Err(std::io::Error::new(std::io::ErrorKind::InvalidData, "stream did not contain valid UTF-8"))
			})
		} else {
			g.len = g.buf.len();
			ret
		}
	}
}
