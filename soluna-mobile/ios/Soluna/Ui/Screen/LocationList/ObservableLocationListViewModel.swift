import SwiftUI
import Shared

class ObservableLocationListViewModel : ObservableViewModel<LocationListViewModel.State, LocationListViewModel.Event, LocationListViewModel.Action> {
 
    @Published
    var navigateToAddLocation: Bool = false
    
    @Published
    var navigateToLocationDetails: Int64? = nil

    init() {
        super.init(SwiftKotlinBridge().getLocationListViewModel())
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: LocationListViewModel.Event) {
        switch event {
        case is LocationListViewModel.EventAddLocation:
            navigateToAddLocation = true
        case is LocationListViewModel.EventLocationDetails:
            let event = event as! LocationListViewModel.EventLocationDetails
            navigateToLocationDetails = event.locationId
        default:
            NSLog("Received unknown event \(event)")
        }
    }
    
    override func reset() {
        navigateToAddLocation = false
        navigateToLocationDetails = nil
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
