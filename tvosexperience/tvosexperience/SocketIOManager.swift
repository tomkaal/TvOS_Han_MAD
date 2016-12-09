//
//  SocketIOManager.swift
//  tvosexperience
//
//  Created by HAN IVS on 09-12-16.
//  Copyright © 2016 HAN IVS. All rights reserved.
//

import Foundation
import SocketIO

class SocketIOManager {
    static let sharedInstance = SocketIOManager()
    var socket = SocketIOClient(socketURL: URL(string: "http://localhost:3000")!, config: [.log(true), .forcePolling(true)])
    
    func establishConnection() {
        socket.connect()
    }
    
    func closeConnection() {
        socket.disconnect()
    }
}
