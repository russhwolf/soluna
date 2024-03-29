import SwiftUI
import Combine
import Shared

class ObservableAddLocationViewModel : ObservableViewModel<AddLocationViewModel.State, AddLocationViewModel.Event, AddLocationViewModel.Action> {
    
    let geocodeSubject = PassthroughSubject<AddLocationViewModel.EventShowGeocodeData, Never>()
    let goBack = BackNavigationTrigger()

    init() {
        super.init(SwiftKotlinBridge().getAddLocationViewModel())
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: AddLocationViewModel.Event) {
        switch event {
        case is AddLocationViewModel.EventExit:
            goBack.navigate()
        case is AddLocationViewModel.EventShowGeocodeData:
            let geocodeEvent = event as! AddLocationViewModel.EventShowGeocodeData
            geocodeSubject.send(geocodeEvent)
        default:
            NSLog("Received unknown event \(event)")
        }
    }

    override func reset() {
        goBack.reset()
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
