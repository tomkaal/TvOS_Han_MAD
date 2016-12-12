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
    @IBOutlet weak var labelAnswerA: UILabel!
    @IBOutlet weak var labelAnswerB: UILabel!
    @IBOutlet weak var labelAnswerC: UILabel!
    @IBOutlet weak var labelAnswerD: UILabel!
    @IBOutlet weak var labelTeamInformation: UILabel!
    
    var socket = SocketIOManager.sharedInstance.socket
    var json: JSON?
    var question: Question?
    var teamId: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        DispatchQueue.global(qos: .userInitiated).async {
            self.question = BackendApi().getQuestion()
            DispatchQueue.main.async {
                self.labelQuestion.text = self.question?.text
                self.labelAnswerA.text = "A: " + (self.question?.answers?[0].text)!
                self.labelAnswerB.text = "B: " + (self.question?.answers?[1].text)!
                self.labelAnswerC.text = "C: " + (self.question?.answers?[2].text)!
                self.labelAnswerD.text = "D: " + (self.question?.answers?[3].text)!
                
                if self.question != nil {
                    var answerIds = [String]()
                    for answer in self.question!.answers! {
                        answerIds.append(answer._id!)
                    }
                    let socketBody: JSON =  ["teamId": self.teamId!, "questionId": self.question!._id!, "answerIds": answerIds]
                    self.socket.emit("tv_is_ready", "\(socketBody)")
                }
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
//        socket.off("chat")
//        socket.off("connect")
    }
    
    func addHandlers() {
//        socket.on("connect") {data, ack in
//            print("Kaas:Ham socket connected")
//        }
//        
//        socket.on("chat") { [weak self] data, ack in
//            if let value = data.first as? String {
//                print("Kaas:Ham \(value)")
//                self?.labelQuestion.text = value
//            }
//        }
        socket.on("team_has_answered") { [weak self] data, ack in
            self?.json = JSON(data.first as! String) //teamId, questionId
            //do stuff to get score from api and show on screen
            
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
