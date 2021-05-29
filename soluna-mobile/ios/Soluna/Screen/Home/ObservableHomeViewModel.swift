import SwiftUI
import Shared

class ObservableHomeViewModel: ObservableViewModel<HomeViewModel.State, HomeViewModel.Event, HomeViewModel.Action> {

    @Published
    var navigateToLocationList: Bool = false
    
    init() {
        super.init(SwiftKotlinBridge().getHomeViewModel())
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: HomeViewModel.Event) {
        switch event {
        case is HomeViewModel.EventLocations:
            navigateToLocationList = true
        default:
            NSLog("Received unknown event \(event)")
        }
    }
    
    override func reset() {
        navigateToLocationList = false
    }
    
    func onNavigateToLocationList() {
        performAction(action: HomeViewModel.ActionLocations())
    }
}
