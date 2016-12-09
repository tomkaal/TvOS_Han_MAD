//
//  User.swift
//  quizzert
//
//  Created by HAN IVS on 07-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import Foundation

class User: BaseModel {
    var name: String?
    var team: Team?
    
    init(_id: String) {
        super.init()
        self._id = _id
    }
    
    init(_id: String, name: String) {
        self.name = name
        super.init()
        self._id = _id
    }
    
    init(_id: String, name: String, team: Team) {
        self.name = name
        self.team = team
        super.init()
        self._id = _id
    }
}
