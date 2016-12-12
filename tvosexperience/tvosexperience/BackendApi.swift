//
//  BackendApi.swift
//  quizzert
//
//  Created by HAN IVS on 07-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import Foundation

class BackendApi {
    
    private struct Api {
        static let Url = "http://localhost:3000/api"
        static let beaconId = "243sad-343fg-t5t5r-3eg6"
    }
    
    func getGroups() -> [Group] {
        var groups = [Group]()
        if let url = URL(string: Api.Url + "/group") {
            if let data = try? Data(contentsOf: url) {
                let json = JSON(data: data)
                
                let jsonGroups = json["doc"].arrayValue
                
                for group in jsonGroups {
                    groups.append(
                        Group(_id: group["_id"].stringValue,
                              name: group["name"].stringValue,
                              owner: User(_id: group["owner"].stringValue)
                        )
                    )
                }
            }
        }
        
        return groups
    }
    
    func getQuestion() -> Question? {
        let question = Question()
        
        if let url = URL(string: Api.Url + "/tv/" + Api.beaconId + "/question") {
            if let data = try? Data(contentsOf: url) {
                let json = JSON(data: data)
                
                question._id = json["doc"]["_id"].stringValue
                question.text = json["doc"]["text"].stringValue
                question.score = json["doc"]["score"].intValue
                question.answers = []
                question.correctAnswer = Answer(_id: json["doc"]["correctAnswer"]["_id"].stringValue, text: json["doc"]["correctAnswer"]["text"].stringValue)
                let jsonAnswers = json["doc"]["answers"].arrayValue
                for answer in jsonAnswers {
                    question.answers!.append(Answer(_id: answer["_id"].stringValue, text: answer["text"].stringValue))
                }
                
            }
        }
        
        return question
    }
    
    func getTvDescription() -> String? {
        var description: String? = nil
        
        if let url = URL(string: Api.Url + "/tv/" + Api.beaconId) {
            if let data = try? Data(contentsOf: url) {
                let json = JSON(data: data)
                
                description = json["doc"].arrayValue[0]["description"].stringValue
            }
        }
        
        return description
    }
    
}
