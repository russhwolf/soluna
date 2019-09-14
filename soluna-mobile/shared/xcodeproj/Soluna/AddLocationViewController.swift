//
//  AddLocationViewController.swift
//  Soluna
//
//  Created by Russell Wolf on 8/2/19.
//  Copyright © 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

class AddLocationViewController: BaseViewController<AddLocationViewModel, AddLocationViewState> {
    let viewModelInit = AddLocationViewModel(repository: SwiftKotlinBridgeKt.repository, dispatcher: MainThreadKt.mainDispatcher)

    override var viewModel: AddLocationViewModel! {
        get { return viewModelInit }
        set { super.viewModel = newValue }
    }

    @IBOutlet var labelInput: UITextField!
    @IBOutlet var latitudeInput: UITextField!
    @IBOutlet var longitudeInput: UITextField!
    @IBOutlet var timeZoneInput: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel.setViewStateListener { (state: AddLocationViewState) in
            state.geocodeTrigger.consume { geocodeResult in
                self.latitudeInput.text = String(geocodeResult.latitude)
                self.longitudeInput.text = String(geocodeResult.longitude)
                self.timeZoneInput.text = geocodeResult.timeZone
            }
            state.exitTrigger.consume { unit in
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
        viewModel.geocodeLocation(location: labelInput.text ?? "")
    }
    
}
