use jvm_rs::jvmti::{jvmtiEnv, jvmtiCapabilities, jvmtiError_JVMTI_ERROR_NONE, jvmtiEventCallbacks, jthread, jvmtiEventMode_JVMTI_ENABLE, jvmtiEvent_JVMTI_EVENT_VM_INIT, jvmtiEvent_JVMTI_EVENT_METHOD_ENTRY, jvmtiEvent_JVMTI_EVENT_VM_START};
use jvm_rs::jni::{JavaVM, JNI_OK, JNIEnv, jmethodID, jclass};
use std::os::raw::{c_char, c_void, c_int};
use std::mem::{zeroed, size_of};
use std::ffi::CString;
use std::ptr::null_mut;
use crate::{connection, utils, get_bindbg, startup};

static mut JVMTI: Option<*mut jvmtiEnv> = None;
static mut CAPABILITIES: Option<jvmtiCapabilities> = None;
static mut CALLBACKS: Option<jvmtiEventCallbacks> = None;

#[no_mangle]
pub unsafe extern "system" fn Agent_OnLoad(vm: *mut JavaVM, _options: *const c_char, _reserved: &mut c_void) -> c_int {
	println!("Loaded BinDbg server on PID {}", std::process::id());
	
	startup();
	
	let jvmti: *mut jvmtiEnv = utils::get_jvmti(vm);
	JVMTI = Some(jvmti);
	
	
	let mut capabilities: jvmtiCapabilities = zeroed();
	capabilities.set_can_generate_method_entry_events(1);
	assert_eq!((**jvmti).AddCapabilities.unwrap()(jvmti, &capabilities), jvmtiError_JVMTI_ERROR_NONE);
	CAPABILITIES = Some(capabilities);
	
	
	let mut callbacks: jvmtiEventCallbacks = zeroed();
	callbacks.MethodEntry = Some(method_entry);
	callbacks.VMInit = Some(vm_init);
	callbacks.VMStart = Some(vm_start);
	assert_eq!((**jvmti).SetEventCallbacks.unwrap()(jvmti, &callbacks, size_of::<jvmtiEventCallbacks>() as i32), jvmtiError_JVMTI_ERROR_NONE);
	CALLBACKS = Some(callbacks);
	
	
	assert_eq!((**jvmti).SetEventNotificationMode.unwrap()(jvmti, jvmtiEventMode_JVMTI_ENABLE, jvmtiEvent_JVMTI_EVENT_METHOD_ENTRY, null_mut()), jvmtiError_JVMTI_ERROR_NONE);
	assert_eq!((**jvmti).SetEventNotificationMode.unwrap()(jvmti, jvmtiEventMode_JVMTI_ENABLE, jvmtiEvent_JVMTI_EVENT_VM_INIT, null_mut()), jvmtiError_JVMTI_ERROR_NONE);
	assert_eq!((**jvmti).SetEventNotificationMode.unwrap()(jvmti, jvmtiEventMode_JVMTI_ENABLE, jvmtiEvent_JVMTI_EVENT_VM_START, null_mut()), jvmtiError_JVMTI_ERROR_NONE);
	
	
	JNI_OK as i32
}

unsafe extern "C" fn method_entry(
	jvmti: *mut jvmtiEnv,
	_jni: *mut JNIEnv,
	_thread: jthread,
	method: jmethodID,
) {
	let mut class: jclass = null_mut();
	let mut class_sig: *mut c_char = null_mut();
	let mut name: *mut c_char = null_mut();
	let mut sig: *mut c_char = null_mut();
	
	assert_eq!((**jvmti).GetMethodDeclaringClass.unwrap()(jvmti, method, &mut class), jvmtiError_JVMTI_ERROR_NONE);
	assert_eq!((**jvmti).GetClassSignature.unwrap()(jvmti, class, &mut class_sig, null_mut()), jvmtiError_JVMTI_ERROR_NONE);
	assert_eq!((**jvmti).GetMethodName.unwrap()(jvmti, method, &mut name, &mut sig, null_mut()), jvmtiError_JVMTI_ERROR_NONE);
	
	{
		let class_sig = CString::from_raw(class_sig);
		let class_sig = class_sig.to_str().unwrap();
		let name = CString::from_raw(name);
		let name = name.to_str().unwrap();
		let sig = CString::from_raw(sig);
		let sig = sig.to_str().unwrap();
		if name == "main" {
			println!("Enter method: {} {} {}", class_sig, name, sig);
		}
	}
	
	//assert_eq!((**jvmti).Deallocate.unwrap()(jvmti, class_sig as *mut c_uchar), jvmtiError_JVMTI_ERROR_NONE);
	//assert_eq!((**jvmti).Deallocate.unwrap()(jvmti, name as *mut c_uchar), jvmtiError_JVMTI_ERROR_NONE);
	//assert_eq!((**jvmti).Deallocate.unwrap()(jvmti, sig as *mut c_uchar), jvmtiError_JVMTI_ERROR_NONE);
}

unsafe extern "C" fn vm_init(
	_jvmti: *mut jvmtiEnv,
	_jni: *mut JNIEnv,
	_thread: jthread
) {
	println!("-------------- VM INIT --------------")
}

unsafe extern "C" fn vm_start(
	_jvmti_env: *mut jvmtiEnv, _jni_env: *mut JNIEnv
) {
	println!("-------------- VM START --------------")
}
