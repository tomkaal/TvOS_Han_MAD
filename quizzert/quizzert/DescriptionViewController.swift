//
//  ViewController.swift
//  quizzert
//
//  Created by HAN IVS on 06-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import UIKit
//import SocketIO

class DescriptionViewController: UIViewController {

    @IBOutlet weak var label: UILabel!
    
    @IBAction func question(_ sender: UIButton) {
        performSegue(withIdentifier: "Show question", sender: nil)
    }
    
    private let Api = BackendApi()
    
    private func refresh() {
//        let groups = BackendApi().getGroups()
//        
//        label.text = ""
//        for group in groups {
//            label.text! += "Groupname: " + group.name + " Owner: " + group.owner._id! + "\r\n"
//        }
        
        DispatchQueue.global(qos: .userInitiated).async {
            let tvDescription = self.Api.getTvDescription()
            // Bounce back to the main thread to update the UI
            DispatchQueue.main.async {
                self.label.text = tvDescription
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        refresh()
        
//        let socket = SocketIOClient(socketURL: URL(string: "http://localhost:3000")!, config: [.log(true), .forcePolling(true)])
//        
//        socket.on("connect") {data, ack in
//            print("socket connected")
//            self.label.text = "Socket connected"
//        }
//        
//        socket.on("bla") {data, ack in
//            print("Bla received")
//            self.label.text = "Bla received"
//        }
//        
//        socket.on("currentAmount") {data, ack in
//            if let cur = data[0] as? Double {
//                socket.emitWithAck("canUpdate", cur).timingOut(after: 0) {data in
//                    socket.emit("update", ["amount": cur + 2.50])
//                }
//                
//                ack.with("Got your currentAmount", "dude")
//            }
//        }
//        
//        socket.connect()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

