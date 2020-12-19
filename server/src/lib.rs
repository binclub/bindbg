use crate::connection::Client;
use std::sync::{Mutex, Arc};

mod utils;
mod protocol;
mod options;
mod connection;
mod jvm;
mod jvmutils;

use crate::options::Options;
use std::time::Duration;
use std::borrow::BorrowMut;

static mut BINDBG: Option<Box<BinDbg>> = None;

pub fn startup() {
	connection::startup(get_bindbg());
}

pub fn get_bindbg() -> &'static mut BinDbg {
	unsafe {
		if let None = BINDBG {
			BINDBG = Some(Box::new(BinDbg::new()));
		}
		
		#[allow(mutable_transmutes)]
		std::mem::transmute(BINDBG.as_ref().unwrap().as_ref())
	}
}

#[derive(Debug)]
pub struct BinDbg {
	options: Options,
	clients: Vec<Arc<Mutex<Client>>>
}

impl BinDbg {
	fn new() -> Self {
		BinDbg {
			options: Options::from_env(),
			clients: Vec::with_capacity(1)
		}
	}
}
