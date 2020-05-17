//
//  KoinModulesTest.swift
//  
//
//  Created by Russell Wolf on 5/17/20.
//

import XCTest
import KoinTest

class KoinModulesTestSwift: XCTestCase {

    func testCheckModules() {
        do {
            try KoinModulesTestIosKt.testCheckModules()
        } catch {
            XCTFail()
        }
    }
}

