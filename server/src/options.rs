use std::env;

#[derive(Clone, Debug)]
pub struct Options {
	pub pause_on_startup: bool,
	pub bind_addr: String,
}

impl Options {
	pub fn from_env() -> Self {
		let pause_on_startup = match env::var("BINDBG_PAUSE_ON_STARTUP") {
			Ok(x) => x.parse::<bool>().unwrap_or(true),
			Err(_) => true
		};
		let bind_addr = match env::var("BINDBG_BIND_ADDR") {
			Ok(x) => x,
			Err(_) => String::from("127.0.0.1:762")
		};
		println!("Addr: {}", bind_addr);
		Options {
			pause_on_startup,
			bind_addr
		}
	}
}
