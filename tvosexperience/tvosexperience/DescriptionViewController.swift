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
    var json: JSON?
    
    var descriptionTxt: String? {
        didSet {
            labelDescription.text = descriptionTxt
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        DispatchQueue.global(qos: .userInitiated).async {
            let tvDescription = BackendApi().getTvDescription()
            DispatchQueue.main.async {
                self.descriptionTxt = tvDescription
            }
        }
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
        socket.off("team_nearby")
    }
    
    func addHandlers() {
        socket.on("connect") {data, ack in
            //Join tv room on server
            self.socket.emit("tvJoinRoom", "")
        }
        
        socket.on("team_nearby") { [weak self] data, ack in
            self?.json = JSON(data.first as! String) //teamId
            self?.performSegue(withIdentifier: "Show question", sender: self)
        }
        
        socket.on("chat") { [weak self] data, ack in
            if let value = data.first as? String {
                if value == "question" {
                    self?.performSegue(withIdentifier: "Show question", sender: self)
                } else {
                    print("Kaas: \(value)")
                    self?.labelDescription.text = value
                }
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        var destination = segue.destination
        if let uinc = destination as? UINavigationController {
            destination = uinc.visibleViewController!
        }
        if let qvc = destination as? QuestionViewController {
            if let identifier = segue.identifier {
                switch identifier {
                case "Show question":
                    //currently test id, so things can be tested
                    qvc.teamId = "dsdsad353erfsddsf4ds" //json?["teamId"].stringValue
                default:
                    break
                }
            }
        }
    }
    
}

