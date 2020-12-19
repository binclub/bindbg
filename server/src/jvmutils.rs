use jvm_rs::jvmti::{jvmtiEnv, jthread, jvmtiError_JVMTI_ERROR_NONE};
use std::ptr::null_mut;
use jvm_rs::jni::jint;

static mut SUSPENDED_THREADS: Option<Vec<jthread>> = None;

unsafe fn suspend_jvm(jvmti: *mut jvmtiEnv) {
	let mut threads: *mut jthread = null_mut();
	let mut num_threads: jint = 0;
	assert_eq!((**jvmti).GetAllThreads.unwrap()(jvmti, &mut num_threads, &mut threads), jvmtiError_JVMTI_ERROR_NONE);
	
	let suspended_threads = match &SUSPENDED_THREADS {
		None => {
			let
		},
		Some(x) => *x
	};
	for i in 0..num_threads {
		let thread: jthread = *threads.offset(num_threads as isize);
		let err = (**jvmti).SuspendThread.unwrap()(jvmti, thread);
		if err == jvmtiError_JVMTI_ERROR_NONE {
		
		} else {
			println!("Couldnt suspend thread {}: {:p} ({})", i, thread, err);
		}
	}
}
