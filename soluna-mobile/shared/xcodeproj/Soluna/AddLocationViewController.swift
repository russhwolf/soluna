//
//  AddLocationViewController.swift
//  Soluna
//
//  Created by Russell Wolf on 8/2/19.
//  Copyright © 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

class AddLocationViewController: UIViewController {
    
    let viewModel = AddLocationViewModel(repository: SwiftKotlinBridgeKt.repository, dispatcher: BackgroundKt.mainDispatcher)
    
    @IBOutlet var labelInput: UITextField!
    @IBOutlet var latitudeInput: UITextField!
    @IBOutlet var longitudeInput: UITextField!
    @IBOutlet var timeZoneInput: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel.setViewStateListener { (state: AddLocationViewState) in
            if (state.exitTrigger.consume() != nil) {
                self.navigationController?.popViewController(animated: true)
            }
        }
    }
    
    @IBAction func onSubmitClick(_ sender: Any) {
        viewModel.addLocation(
            label: labelInput.text ?? "",
            latitude: latitudeInput.text ?? "",
            longitude: longitudeInput.text ?? "",
            timeZone: timeZoneInput.text ?? ""
        )
    }
    
    @IBAction func onGeocodeClick(_ sender: Any) {
        // TODO
    }
    
}
