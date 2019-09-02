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
    
    let viewModel = AddLocationViewModel(repository: SwiftKotlinBridgeKt.repository, dispatcher: MainThreadKt.mainDispatcher)
    
    @IBOutlet var labelInput: UITextField!
    @IBOutlet var latitudeInput: UITextField!
    @IBOutlet var longitudeInput: UITextField!
    @IBOutlet var timeZoneInput: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel.setViewStateListener { (state: AddLocationViewState) in
            let geocodeResult = state.geocodeTrigger.consume()
            if (geocodeResult != nil) {
                self.latitudeInput.text = String(geocodeResult!.latitude)
                self.longitudeInput.text = String(geocodeResult!.longitude)
                self.timeZoneInput.text = geocodeResult!.timeZone
            }
            if (state.exitTrigger.consume() != nil) {
                self.navigationController?.popViewController(animated: true)
            }
        }
        viewModel.setErrorListener { (throwable) in
            throwable.printStackTrace()
        }
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        viewModel.clearScope()
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
        viewModel.geocodeLocation(location: labelInput.text ?? "")
    }
    
}
