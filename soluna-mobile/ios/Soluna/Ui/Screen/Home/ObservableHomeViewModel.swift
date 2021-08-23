import SwiftUI
import Shared

class ObservableHomeViewModel: ObservableViewModel<HomeViewModel.State, HomeViewModel.Event, HomeViewModel.Action> {

    @Published
    var navigateToSettings: Bool = false

    init() {
        super.init(SwiftKotlinBridge().getHomeViewModel())
        _ = self.objectWillChange.append(super.objectWillChange)
    }
    
    override func onEvent(_ event: HomeViewModel.Event) {
        switch event {
        case is HomeViewModel.EventSettings:
            navigateToSettings = true
        default:
            NSLog("Received unknown event \(event)")
        }
    }
    
    override func reset() {
        navigateToSettings = false
    }
    
    func onNavigateToSettings() {
        performAction(action: HomeViewModel.ActionSettings())
    }
}
