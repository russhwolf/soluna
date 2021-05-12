import SwiftUI
import Combine
import Shared

class ObservableAddLocationViewModel : ObservableViewModel<AddLocationViewModel.State, AddLocationViewModel.Event, AddLocationViewModel.Action> {
    
    @Published
    var goBack = false
    
    let geocodeSubject = PassthroughSubject<AddLocationViewModel.EventShowGeocodeData, Never>()
    
    init() {
        super.init(SwiftKotlinBridge().getAddLocationViewModel())
    }
    
    override func onEvent(_ event: AddLocationViewModel.Event) {
        switch event {
        case is AddLocationViewModel.EventExit:
            goBack = true
        case is AddLocationViewModel.EventShowGeocodeData:
            let geocodeEvent = event as! AddLocationViewModel.EventShowGeocodeData
            geocodeSubject.send(geocodeEvent)
        default:
            NSLog("Received unknown event \(event)")
        }
    }
    
    override func reset() {
        goBack = false
    }
    
    func createLocation(label: String, latitude: String, longitude: String, timeZone: String) {
        performAction(
            action: AddLocationViewModel.ActionCreateLocation(
                label: label,
                latitude: latitude,
                longitude: longitude,
                timeZone: timeZone
            )
        )
    }
    
    func geocodeLocation(_ label: String) {
        performAction(action: AddLocationViewModel.ActionGeocodeLocation(location: label))
    }
    
    func useGpsLocation() {
        performAction(action: AddLocationViewModel.ActionDeviceLocation())
    }
}
