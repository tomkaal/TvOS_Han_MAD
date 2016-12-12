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
    
    init(_id: String, text: String) {
        self.text = text
        super.init()
        self._id = _id
    }
}
