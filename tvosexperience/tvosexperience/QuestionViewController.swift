//
//  QuestionViewController.swift
//  tvosexperience
//
//  Created by HAN IVS on 09-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import UIKit

class QuestionViewController: UIViewController {

    @IBOutlet weak var labelQuestion: UILabel!
    
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
            print("Kaas:Ham socket connected")
        }
        
        socket.on("chat") { [weak self] data, ack in
            if let value = data.first as? String {
                print("Kaas:Ham \(value)")
                self?.labelQuestion.text = value
            }
        }
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
