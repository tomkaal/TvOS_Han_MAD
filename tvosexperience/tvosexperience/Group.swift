//
//  Group.swift
//  quizzert
//
//  Created by HAN IVS on 07-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import Foundation

class Group: BaseModel {
    var name: String
    var owner: User
    
    init(_id: String, name: String, owner: User) {
        self.name = name
        self.owner = owner
        super.init()
        self._id = _id
        
    }
}
