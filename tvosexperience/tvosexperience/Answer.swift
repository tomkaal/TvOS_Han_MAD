//
//  Answer.swift
//  quizzert
//
//  Created by HAN IVS on 07-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import Foundation

class Answer: BaseModel {
    var text: String
    
    init(text: String) {
        self.text = text
    }
}
