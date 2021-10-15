import SwiftUI
import Shared

class ObservableLocationDetailViewModel: ObservableViewModel<LocationDetailViewModel.State, LocationDetailViewModel.Event, LocationDetailViewModel.Action> {
    
    let goBack = BackNavigationTrigger()

    init(id: Int64) {
        super.init(SwiftKotlinBridge().getLocationDetailViewModel(id: id))
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: LocationDetailViewModel.Event) {
        switch event {
        case is LocationDetailViewModel.EventExit:
            goBack.navigate()
        default:
            NSLog("Received unknown event \(event)")
        }
    }

    override func reset() {
        goBack.reset()
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
