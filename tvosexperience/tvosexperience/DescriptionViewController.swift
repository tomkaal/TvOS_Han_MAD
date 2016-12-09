//
//  ViewController.swift
//  tvosexperience
//
//  Created by HAN IVS on 09-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import UIKit
import SocketIO

class DescriptionViewController: UIViewController {

    @IBOutlet weak var labelDescription: UILabel!
    
    var socket = SocketIOManager.sharedInstance.socket
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func viewDidAppear(_ animated: Bool) {
        addHandlers()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        removeHandlers()
    }
    
    func removeHandlers() {
        socket.off("chat")
        socket.off("connect")
    }
    
    func addHandlers() {
        socket.on("connect") {data, ack in
            print("Kaas: socket connected")
        }
        
        socket.on("chat") { [weak self] data, ack in
            if let value = data.first as? String {
                if value == "question" {
                    self?.performSegue(withIdentifier: "Show question", sender: value)
                } else {
                    print("Kaas: \(value)")
                    self?.labelDescription.text = value
                }
            }
        }
    }
    
}

