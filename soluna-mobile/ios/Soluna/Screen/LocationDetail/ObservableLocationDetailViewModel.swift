import SwiftUI
import Shared

class ObservableLocationDetailViewModel: ObservableViewModel<LocationDetailViewModel.State, LocationDetailViewModel.Event, LocationDetailViewModel.Action> {

    @Published
    var goBack = false

    init(id: Int64) {
        super.init(SwiftKotlinBridge().getLocationDetailViewModel(id: id))
    }
    
    override func onEvent(_ event: LocationDetailViewModel.Event) {
        switch event {
        case is LocationDetailViewModel.EventExit:
            goBack = true
        default:
            NSLog("Received unknown event \(event)")
        }
    }
    
    override func reset() {
        goBack = false
    }
    
    func setLabel(label: String) {
        performAction(action: LocationDetailViewModel.ActionSetLabel(label: label))
    }
    
    func toggleSelected() {
        performAction(action: LocationDetailViewModel.ActionToggleSelected())
    }
    
    func deleteLocation() {
        performAction(action: LocationDetailViewModel.ActionDelete())
    }
}
