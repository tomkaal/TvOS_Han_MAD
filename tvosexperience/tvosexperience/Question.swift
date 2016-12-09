//
//  Question.swift
//  quizzert
//
//  Created by HAN IVS on 07-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import Foundation

class Question: BaseModel {
    var answers: [Answer]?
    var correctAnswer: Answer?
    var text: String?
    var score: Int?
    
    
}
