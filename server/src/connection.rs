use std::net::{TcpListener, TcpStream};
use crate::BinDbg;
use crate::utils::ReadLine;
use std::sync::{Mutex, Arc};
use std::thread;

pub fn startup(bindbg: &mut BinDbg) {
	let socket = TcpListener::bind(bindbg.options.bind_addr.clone())
		.expect("Unable to open socket");
	
	println!("Listening for connection on {}", bindbg.options.bind_addr);
	let (client_stream, client_addr) = socket.accept()
		.expect("Unable to establish client connection");
	
	println!("Established connection with {}", client_addr);
	
	let mut client = Client { id: 0, connection: client_stream };
	
	let arc = Arc::new(Mutex::new(client));
	let arc_clone = Arc::clone(&arc);
	
	bindbg.clients.push(arc);
	println!("Spawning...");
	
	thread::spawn(move || {
		client_connection(arc_clone);
	});
	
	println!("Thread spawned");
}

#[derive(Debug)]
pub struct Client {
	pub id: i32,
	pub connection: TcpStream
}

fn client_connection(client: Arc<Mutex<Client>>) {
	let connection = {
		&mut client.lock().unwrap().connection
	};
	loop {
		let mut str = String::new();
		connection.read_string_line(&mut str).unwrap();
		if str.len() > 0 {
			println!("Received: {}", str);
		}
	}
}
