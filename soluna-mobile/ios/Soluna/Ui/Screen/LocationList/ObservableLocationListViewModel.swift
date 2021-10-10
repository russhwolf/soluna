import SwiftUI
import Shared

class ObservableLocationListViewModel : ObservableViewModel<LocationListViewModel.State, LocationListViewModel.Event, LocationListViewModel.Action> {
 
    let addLocationTrigger = NavigationTrigger<Bool>()
    let locationDetailsTrigger = NavigationTrigger<Int64>()

    init() {
        super.init(SwiftKotlinBridge().getLocationListViewModel())
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: LocationListViewModel.Event) {
        switch event {
        case is LocationListViewModel.EventAddLocation:
            addLocationTrigger.navigate()
        case is LocationListViewModel.EventLocationDetails:
            let event = event as! LocationListViewModel.EventLocationDetails
            locationDetailsTrigger.navigate(event.locationId)
        default:
            NSLog("Received unknown event \(event)")
        }
    }
    
    override func reset() {
        addLocationTrigger.reset()
        locationDetailsTrigger.reset()
    }
    
    func onAddLocationClick() {
        performAction(action: LocationListViewModel.ActionAddLocation())
    }
    
    func onRemoveLocationClick(_ locationId: Int64) {
        performAction(action: LocationListViewModel.ActionRemoveLocation(locationId: locationId))
    }
    
    func onSelectLocationClick(_ locationId: Int64) {
        performAction(action: LocationListViewModel.ActionToggleLocationSelected(locationId: locationId))
    }
    
    func onLocationDetailClick(_ locationId: Int64) {
        performAction(action: LocationListViewModel.ActionLocationDetails(locationId: locationId))
    }
}
