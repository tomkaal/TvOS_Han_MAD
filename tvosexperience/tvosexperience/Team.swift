//
//  Team.swift
//  quizzert
//
//  Created by HAN IVS on 07-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import Foundation

class Team: BaseModel {
    var name: String
    var group: Group
    
    init(name: String, group: Group) {
        self.name = name
        self.group = group
    }
}
